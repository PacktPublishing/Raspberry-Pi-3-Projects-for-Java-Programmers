/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chapter2.lcd;

/**
 * The exception used when the initialization of the LCD fails.
 * @author John Sirach <john.sirach@pidome.org>
 */
public class LcdSetupException extends Exception {

    /**
     * Creates a new instance of <code>LcdSetupException</code> without detail message.
     */
    public LcdSetupException() {
    }

    /**
     * Constructs an instance of <code>LcdSetupException</code> with the specified detail message.
     *
     * @param msg the detail message.
     */
    public LcdSetupException(String msg) {
        super(msg);
    }
}
