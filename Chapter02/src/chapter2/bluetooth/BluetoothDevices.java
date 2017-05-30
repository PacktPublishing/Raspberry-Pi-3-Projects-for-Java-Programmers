/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chapter2.bluetooth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.RemoteDevice;

/**
 * Bluetooth devices.
 * @author John Sirach <john.sirach@pidome.org>
 */
public class BluetoothDevices implements DiscoveryServiceListener {

    /**
     * The discovery service used for searching for bluetoooth devices.
     */
    private final DiscoveryService discovery = new DiscoveryService();
    
    /**
     * List if found devices.
     */
    private List<RemoteDevice> foundDevices = new ArrayList<>();

    /**
     * Constructor.
     */
    public BluetoothDevices(){}
    
    /**
     * Initializes where needed and set's the listener for search done.
     */
    public final void init(){
        discovery.addListener(this);
    }
    
    /**
     * Destroy's any variables and members and removes listeners.
     */
    public final void destroy(){
        discovery.removeListener(this);
    }
    
    /**
     * Executes the example.
     * @throws DiscoveryFailedException 
     */
    public final void runExample() throws DiscoveryFailedException {
        startSearch();
        Iterator<RemoteDevice> iter = discovery.getDiscoveredDevices().iterator();
        while(iter.hasNext()){
            RemoteDevice device = iter.next();
            try {
                System.out.println("Found: " + device.getBluetoothAddress() + " - " + device.getFriendlyName(true));
            } catch (IOException ex) {
                System.err.println("Unable to ask for device name for address: " + device.getBluetoothAddress() + " because of: " + ex.getMessage());
            }
        }
    }
    
    /**
     * Starts device search.
     * @throws DiscoveryFailedException 
     */
    public final void startSearch() throws DiscoveryFailedException {
        try {
            discovery.startSearch();
        } catch (InterruptedException | BluetoothStateException ex) {
            throw new DiscoveryFailedException("Could not complete discovery" + ex.getMessage(), ex);
        }
    }
    
    /**
     * Returns a list of found devices.
     * @return List of the found devices.
     */
    public final List<RemoteDevice> getFoundDevices(){
        return this.foundDevices;
    }
    
    /**
     * Called when device discovery has finished.
     */
    @Override
    public void serviceDiscoveryDone() {
        this.foundDevices = discovery.getDiscoveredDevices();
    }
    
    /**
     * Clears the list of found devices.
     */
    public final void clearFoundDevicesList(){
        this.foundDevices.clear();
    }
    
}