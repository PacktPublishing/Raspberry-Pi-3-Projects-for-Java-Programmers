/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chapter2.bluetooth;

/**
 * Exception used when bluetooth discovery fails.
 * @author John Sirach <john.sirach@pidome.org>
 */
public class DiscoveryFailedException extends Exception {

    /**
     * Creates a new instance of <code>DiscoveryFailedException</code> without detail message.
     */
    public DiscoveryFailedException() {
    }

    /**
     * Constructs an instance of <code>DiscoveryFailedException</code> with the specified detail message.
     *
     * @param msg the detail message.
     */
    public DiscoveryFailedException(String msg) {
        super(msg);
    }
    
    /**
     * Constructs an instance of <code>DiscoveryFailedException</code> with the specified detail message and <code>throwable</code>.
     *
     * @param msg the detail message.
     * @param exception A <code>Throwable</code> to be used as cause for this exception.
     */
    public DiscoveryFailedException(String msg, Throwable exception){
        super(msg,exception);
    }
    
}
