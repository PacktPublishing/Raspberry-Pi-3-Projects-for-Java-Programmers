package chapter2.ldrreader;

import chapter2.lcd.LcdHandler;
import chapter2.lcd.LcdSetupException;
import chapter2.relay.RelaySwitch;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinMode;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Main class to read the light intensity
 * @author John Sirach <john.sirach@pidome.org>
 */
public class LdrReader {
    
    /**
     * The instance controller for the GPIO pins.
     */
    private final GpioController gpioController = GpioFactory.getInstance();
    
    /**
     * The pin where we want to detect the PIN state to read the light intensity.
     */
    private final Pin gpioPin = RaspiPin.GPIO_05;
    
    /**
     * The pin we are able to manipulate,
     */
    private final GpioPinDigitalOutput ldrPin;
    
    /**
     * The amount of milliseconds we would like to wait before we define it is dark.
     * Adjust this to the amount of milliseconds a recharge of the capacitor should take before you think it is dark enough.
     */
    private final long darkThreshold = 40;
    
    /**
     * Scheduled executor for detecting environment light intensity.
     */
    private ScheduledExecutorService intensityCheckScheduler;
    
    /**
     * Single executor to create a FIFO like execution of display writes.
     */
    private ExecutorService displayWriter;
    
    /**
     * Constructor.
     */
    public LdrReader(){
        ldrPin = gpioController.provisionDigitalOutputPin(gpioPin);
    }
    
    /**
     * A one shot darkness level detect.
     * @param singleOutput Set to true when you want both measurement and time taken to measure displayed on the LCD screen..
     * @return true when dark, else false when still light enough;
     */
    public final boolean singleDarkDetect(boolean singleOutput){

        long measure = 0;
        boolean dark = false;

        setPinDrain();
        Date startMeasure = new Date();
        setPinToDetect();

        while(!pinIsHigh()){
            Date stopMeasure = new Date();
            measure = stopMeasure.getTime() - startMeasure.getTime();
            if(measure > darkThreshold){
                dark = true;
                break;
            }
        }

        if(singleOutput){
            try {
                LcdHandler lcdHandler = LcdHandler.getInstance();
                lcdHandler.clear();
                lcdHandler.setHome();
                lcdHandler.write("Dark: " + dark);
                lcdHandler.setCursor(1, 0);
                lcdHandler.write("Took " + measure + " ms");
            } catch (LcdSetupException ex) {
                System.err.println("Failed to write to LCD: " + ex.getMessage());
                System.out.println("It is dark: " + dark + ", took " + measure + " ms");
            }
        }
        return dark;

    }
    
    /**
     * Runs the automatic light switch.
     * This method binds listeners to the 
     * @param relaySwitch The relay switch to switch.
     */
    public final void runAutomaticLightSwitching(final RelaySwitch relaySwitch){
        
        final LcdHandler lcdHandler;
        LcdHandler lcdCheckHandler;
        try {
            lcdCheckHandler = LcdHandler.getInstance();
        } catch (LcdSetupException ex) {
            System.err.println("Unable to output to display.");
            lcdCheckHandler = null;
        }
        lcdHandler = lcdCheckHandler;
        
        intensityCheckScheduler = Executors.newScheduledThreadPool(2);
        
        if(lcdHandler!=null){
            lcdHandler.clear();
            
            final SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
            intensityCheckScheduler.scheduleAtFixedRate(() -> {
                writeLcd(lcdHandler, 0, 0, formatter.format(new Date()));
            }, 0, 60, TimeUnit.SECONDS);
        
        }
        
        intensityCheckScheduler.scheduleAtFixedRate(() -> {
            if(singleDarkDetect(false) == true){
                relaySwitch.closeRelay();
                writeLcd(lcdHandler, 0, 13, "On ");
            } else {
                relaySwitch.openRelay();
                writeLcd(lcdHandler, 0, 13, "Off");
            }
        }, 0, 10, TimeUnit.SECONDS);
    }
    
    /**
     * Write text on the lcd at the desired location.
     * @param lcdHandler The lcd handler.
     * @param row The row the cursor should be placed.
     * @param col The column the cursor should be placed.
     * @param text The text to write.
     */
    private void writeLcd(final LcdHandler lcdHandler, final int row, final int col, final String text){
        if(lcdHandler!=null){
            if(displayWriter == null){
                displayWriter = Executors.newSingleThreadExecutor();
            }
            displayWriter.submit(() -> {  
                lcdHandler.setCursor(row, col);
                lcdHandler.write(text);
            });
        }
    }
    
    /**
     * Returns if the pin is low or high.
     * @return true when the pin is high, otherwise false.
     */
    private boolean pinIsHigh(){
        return ldrPin.isState(PinState.HIGH);
    }
    
    /**
     * Change the pin to digital out and to low.
     * This causes to connect this pin to ground.
     */
    private void setPinDrain(){
        ldrPin.setMode(PinMode.DIGITAL_OUTPUT);
        ldrPin.setState(PinState.LOW);
        try {
            //// Give the capacitor some time to get drained.
            Thread.sleep(500);
        } catch (InterruptedException ex) {
            System.err.println("Not waiting 500 ms to drain, result not guaranteed.");
        }
    }
    
    /**
     * Changes the pin state to be able to detect an high value.
     * This causes the pin to do a READ and detect LOW or HIGH state.
     */
    private void setPinToDetect(){
        ldrPin.setMode(PinMode.DIGITAL_INPUT);
    }
    
}
