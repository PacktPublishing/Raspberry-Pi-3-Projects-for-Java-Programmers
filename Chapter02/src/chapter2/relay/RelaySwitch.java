/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chapter2.relay;

import chapter2.lcd.LcdHandler;
import chapter2.lcd.LcdSetupException;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinMode;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Class to switch a relay on and off.
 * @author John Sirach <john.sirach@pidome.org>
 */
public class RelaySwitch {
    
    /**
     * The instance controller for the GPIO pins.
     */
    private final GpioController gpioController = GpioFactory.getInstance();
    
    /**
     * The pin where we want to detect the PIN state to read the light intensity.
     */
    private final Pin gpioPin = RaspiPin.GPIO_06;
    
    /**
     * The pin we are able to manipulate,
     */
    private final GpioPinDigitalOutput relayPin;
 
    /**
     * The lcd handler used to display data on the LCD display.
     */
    private final LcdHandler lcd;
    
    /**
     * The constructor.
     * The constructor sets the pin mode to output and sets the state to low.
     */
    public RelaySwitch(){
        relayPin = gpioController.provisionDigitalOutputPin(gpioPin);
        relayPin.setMode(PinMode.DIGITAL_OUTPUT);
        relayPin.setState(PinState.LOW);

        LcdHandler localLcd;
        try {
            localLcd = LcdHandler.getInstance();
        } catch (LcdSetupException ex) {
            System.err.println("No LCD output available: " + ex.getMessage());
            localLcd = null;
        }
        lcd = localLcd;
    }
 
    /**
     * Runs the relay example.
     * This method opens the relay for two seconds and closes it again. In other words set's the pin high and low.
     */
    public final void runExample(){

        if (lcd!=null) { lcd.clear(); lcd.setHome(); }

        relayPin.setState(PinState.HIGH);

        if (lcd!=null) lcd.write("Relay closed");

        ScheduledExecutorService closeRelay = Executors.newSingleThreadScheduledExecutor();
        ScheduledFuture<Boolean> future = closeRelay.schedule(() -> {
            relayPin.setState(PinState.LOW);
            if (lcd!=null){ lcd.setCursor(0, 0); lcd.write("Relay open  "); }
            return true;
        }, 2, TimeUnit.SECONDS);
        try {
            future.get(3, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException ex) {
            System.err.println("An execution error occured: " + ex.getMessage() + " try to put the pin to low.");
            relayPin.setState(PinState.LOW);
            if (lcd!=null){
                lcd.setCursor(0, 0);
                lcd.write("Relay open  ");
                lcd.setCursor(1, 0);
                lcd.write("from error");
            }
        }
        closeRelay.shutdownNow();
    }

    /**
     * Closes the relay circuit.
     */
    public final void closeRelay(){
        this.relayPin.setState(PinState.HIGH);
    }
    
    /**
     * Closes the relay circuit.
     */
    public final void openRelay(){
        this.relayPin.setState(PinState.LOW);
    }
    
}