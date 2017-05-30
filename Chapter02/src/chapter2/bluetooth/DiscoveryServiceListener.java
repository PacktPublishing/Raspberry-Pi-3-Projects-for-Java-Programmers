/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chapter2.bluetooth;

/**
 * A listener interface for when the discovery of bluetooth devices has ended.
 * @author John Sirach <john.sirach@pidome.org>
 */
public interface DiscoveryServiceListener {
    
    /**
     * Called when the discovery service is done.
     * This callback is called only when the discovery succeeded.
     */
    public void serviceDiscoveryDone();
    
}