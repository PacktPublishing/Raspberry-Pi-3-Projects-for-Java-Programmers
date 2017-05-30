/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chapter2.bluetooth;

/**
 * Lister for classes wanting to know if a defined bluetooth device is present or not.
 * @author John Sirach <john.sirach@pidome.org>
 */
public interface BluetoothDevicesListener {
    
    public void deviceDetected(String deviceAddress);
    
}