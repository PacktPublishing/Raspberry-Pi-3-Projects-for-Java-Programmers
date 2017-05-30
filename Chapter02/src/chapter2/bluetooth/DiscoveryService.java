/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chapter2.bluetooth;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;

/**
 * Small class to scan for devices.
 * @author John Sirach <john.sirach@pidome.org>
 */
public class DiscoveryService implements DiscoveryListener {

    /**
     * List of discovered devices.
     */
    private final List<RemoteDevice> discoveredDevices = new ArrayList();
    
    /**
     * Devices discovery listener.
     * This listeners are notified when the service is done listening.
     */
    private final List<DiscoveryServiceListener> listeners = new ArrayList<>();
    
    /**
     * Object to use for acquiring a lock.
     */
    final Object searchLock = new Object();
    
    /**
     * Adds a listener for this class.
     * @param listener 
     */
    public final void addListener(DiscoveryServiceListener listener){
        if(!listeners.contains(listener)){
            listeners.add(listener);
        }
    }
    
    /**
     * Removes a listener for this class.
     * @param listener 
     */
    public final void removeListener(DiscoveryServiceListener listener){
        listeners.remove(listener);
    }
    
    /**
     * Returns a list of discovered devices.
     * @return 
     */
    public final List<RemoteDevice> getDiscoveredDevices(){
        return this.discoveredDevices;
    }
    
    /**
     * Starts discovery search.
     * @throws InterruptedException When the search is interrupted
     * @throws BluetoothStateException When are not able to use the Discovery Agent.
     */
    public final void startSearch() throws InterruptedException, BluetoothStateException {
        synchronized(searchLock) {
            boolean started = LocalDevice.getLocalDevice().getDiscoveryAgent().startInquiry(DiscoveryAgent.GIAC, this);
            if (started) {
                searchLock.wait();
                notifyListeners();
            }
        }
    }
    
    @Override
    public void deviceDiscovered(RemoteDevice btDevice, DeviceClass btDeviceClass) {
        discoveredDevices.add(btDevice);
    }

    @Override
    public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
        /// Not used.
    }

    @Override
    public void serviceSearchCompleted(int transID, int respCode) {
        /// Not used.
    }

    /**
     * Called when the search is done.
     * @param i 
     */
    @Override
    public void inquiryCompleted(int i) {
        synchronized(searchLock) {
            searchLock.notify();
        }
    }
    
    /**
     * Notify the listeners we are done.
     */
    private void notifyListeners(){
        Iterator<DiscoveryServiceListener> iter = listeners.iterator();
        while(iter.hasNext()){
            iter.next().serviceDiscoveryDone();
        }
    }
    
}
