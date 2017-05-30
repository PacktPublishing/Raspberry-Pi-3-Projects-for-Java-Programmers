package chapter2;

import chapter2.app.App;
import chapter2.bluetooth.BluetoothDevices;
import chapter2.bluetooth.DiscoveryFailedException;
import chapter2.lcd.LcdExample;
import chapter2.lcd.LcdSetupException;
import chapter2.ldrreader.LdrReader;
import chapter2.relay.RelaySwitch;
import com.pi4j.io.gpio.GpioFactory;

/**
 * Chapter 2 program.
 * 
 * @author John Sirach <john.sirach@pidome.org>
 */
public class Chapter2 {
    
    /**
     * Main application entrypoint.
     * @param args Arguments passed to the application via the command libne.
     */
    public static void main(String[] args) {
        /// Leave this uncommented because we need to stup the GPIO interface before we are able to interact with it.

        //runLcdExample();
        //runLdrExample();

        //relayExample();

        //lightDependentSwitching();

        //runBluetoothDetectionExample();
            
        try {
            runApp();
        } catch (LcdSetupException ex) {
            System.err.println("Unable to run the app: " + ex.getMessage());
        }
    }
    
    /**
     * Runs the LCD example to make sure the connectd LCD works.
     */
    private static void runLcdExample(){
        try {
            LcdExample example = new LcdExample();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                System.err.println(ex.getMessage());
            }
            example.runExample();
        } catch (LcdSetupException ex) {
            System.err.println(ex.getMessage());
        }
    }
    
    /**
     * Runs the LDR example.
     */
    private static void runLdrExample(){
        LdrReader ldrReader = new LdrReader();
        ldrReader.singleDarkDetect(true);
    }
    
    /**
     * Runs the relay example code.
     */
    private static void relayExample(){
        RelaySwitch switchRelay = new RelaySwitch();
        switchRelay.runExample();
    }
    
    /**
     * Runs the application with automatic switching.
     */
    private static void lightDependentSwitching(){
        LdrReader ldrReader = new LdrReader();
        RelaySwitch relaySwitch = new RelaySwitch();
        ldrReader.runAutomaticLightSwitching(relaySwitch);
    }
    
    /**
     * Runs the Bluetooth device discovery example.
     */
    private static void runBluetoothDetectionExample(){
        BluetoothDevices example = new BluetoothDevices();
        example.init();
        try {
            System.out.println("Starting device search, please wait.");
            example.runExample();
            System.out.println("Search done");
        } catch (DiscoveryFailedException ex) {
            System.err.println(ex.getMessage() + " because of " + ex.getCause().getMessage());
        }
        example.destroy();
    }
    
    /**
     * Runs the application.
     * @throws LcdSetupException When the LCD is not able to be initialized.
     */
    public static void runApp() throws LcdSetupException{
        final App app = new App();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                app.destroy();
                GpioFactory.getInstance().shutdown();
            }
        });
        app.init();
        app.run();
    }
    
}