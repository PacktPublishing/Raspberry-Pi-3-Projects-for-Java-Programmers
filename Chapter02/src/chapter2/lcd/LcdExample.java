/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chapter2.lcd;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Class showing a the demo as mentioned in the book.
 * @author John Sirach <john.sirach@pidome.org>
 */
public final class LcdExample {
    
    /**
     * The lcd wrapper handler.
     */
    private LcdHandler handler;
    
    /**
     * The Lcd example constructor.
     * @throws LcdSetupException 
     */
    public LcdExample() throws LcdSetupException {
        handler = LcdHandler.getInstance();
    }
    
    /**
     * Run the LCD example.
     */
    public final void runExample(){
        /// Clear the display
        handler.clear();
        /// Cursor to home position (0,0)
        handler.setHome();
        /// Write to the first line.
        handler.write("-- RASPI3JAVA --"); 
        /// Create a time format for output
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        /// Sets the cursor on the second line at the first position.
        handler.setCursor(1, 0); 
        /// Write the current time in the set format.
        handler.write("--- " + formatter.format(new Date()) + " ---");     
    }
    
    /**
     * Run the second LCD example.
     */
    public final void runSecondExample(){
        /// Clear the display
        handler.clear();
        /// Cursor to home position (0,0)
        handler.setHome();
        /// Write to the first line.
        handler.write("Hello");
    }
    
}