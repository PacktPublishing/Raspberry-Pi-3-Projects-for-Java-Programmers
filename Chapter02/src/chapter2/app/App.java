package chapter2.app;


import chapter2.bluetooth.BluetoothDevices;
import chapter2.bluetooth.DiscoveryFailedException;
import chapter2.lcd.LcdHandler;
import chapter2.lcd.LcdSetupException;
import chapter2.ldrreader.LdrReader;
import chapter2.relay.RelaySwitch;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.bluetooth.RemoteDevice;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Switch a relay based on ambient light and presence
 * @author John Sirach <john.sirach@pidome.org>
 */
public final class App {
    
    /**
     * The LCD handler to write to the display.
     */
    private final LcdHandler lcdHandler;
    
    /**
     * The ldrReader for detecting ambient light.
     */
    private final LdrReader ldrReader;
    
    /**
     * A relay switch to power an higher rated device then the raspberry pi
     */
    private final RelaySwitch relaySwitch;
    
    /**
     * Bluetooth devices scanning.
     */
    private final BluetoothDevices blDevices;
    
    /**
     * Run three scheduled threads.
     * These two threads will be the ambient light intensity check and the time updater.
     */
    ScheduledExecutorService servicesScheduler = Executors.newScheduledThreadPool(2);
    
    /**
     * Run a single thread for the display writer.
     */
    ExecutorService displayWriter = Executors.newSingleThreadExecutor();
    
    /**
     * Run a single thread for the display writer.
     */
    ExecutorService deviceSearch = Executors.newSingleThreadExecutor();
    
    /**
     * Boolean to keep track of if we are running or not.
     */
    private boolean running = false;
    
    /**
     * If it currently is dark or not.
     */
    private boolean isDark = false;
    
    /**
     * Boolean if a device is found.
     */
    private boolean deviceFound = false;
    
    /**
     * Device allowed to change the relay status.
     */
    private String allowedDevice = "";
    
    /**
     * Constructor.
     * @throws LcdSetupException When the lcd init fails.
     */
    public App() throws LcdSetupException{
        lcdHandler  = LcdHandler.getInstance();
        ldrReader   = new LdrReader();
        relaySwitch = new RelaySwitch();
        blDevices   = new BluetoothDevices();
        
    }
    
    /**
     * Initializes all components in need of initialization.
     */
    public final void init(){
        blDevices.init();
    }
    
    /**
     * Run the application.
     */
    public final void run(){
        running = true;
        setTimeUpdater();
        setLdrScheduler();
        setBluetoothDetectionScheduler();
    }
    
    /**
     * Set's the bluetooth scheduler.
     */
    private void setBluetoothDetectionScheduler(){
        deviceSearch.submit(() -> {
            while(running){
                try {
                    /// Because the search is blocking we can keep on looping.
                    this.blDevices.startSearch();
                    for(RemoteDevice device:this.blDevices.getFoundDevices()){
                        if(allowedDevice.equals(device.getBluetoothAddress()) && isDark){
                            writeLcd(1, 0, "Detected        ");
                            this.relaySwitch.closeRelay();
                            deviceFound = true;
                            break;
                        }
                    }
                    if(deviceFound == false){
                        writeLcd(1, 0, "No device       ");
                        this.relaySwitch.openRelay();
                    }
                    deviceFound = false;
                    this.blDevices.clearFoundDevicesList();
                } catch (DiscoveryFailedException ex) {
                    System.err.println("Could not correctly search for devices: " + ex.getMessage());
                }
            }
        });
    }
    
    /**
     * Schedule the LDR service.
     */
    private void setLdrScheduler(){
        servicesScheduler.scheduleAtFixedRate(() -> {
            if(ldrReader.singleDarkDetect(false) == true){
                isDark = true;
                writeLcd(0, 12, "Dark ");
            } else {
                isDark = false;
                writeLcd( 0, 12, "Light");
                this.relaySwitch.openRelay();
            }
        }, 0, 10, TimeUnit.SECONDS);
    }
    
    /**
     * Schedule the time updater to write the time to the display.
     */
    private void setTimeUpdater(){
        final SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        servicesScheduler.scheduleAtFixedRate(() -> {
            writeLcd(0, 0, formatter.format(new Date()));
        }, 0, 60, TimeUnit.SECONDS);
    }
    
    /**
     * Write text on the lcd at the desired location.
     * @param lcdHandler The lcd handler.
     * @param row The row the cursor should be placed.
     * @param col The column the cursor should be placed.
     * @param text The text to write.
     */
    private void writeLcd(final int row, final int col, final String text){
        displayWriter.submit(() -> {  
            lcdHandler.setCursor(row, col);
            lcdHandler.write(text);
        });
    }
    
    /**
     * Destroy any resources.
     */
    public final void destroy(){
        blDevices.destroy();
        running = false;
        servicesScheduler.shutdown();
        displayWriter.shutdown();
        deviceSearch.shutdown();
    }
    
}
