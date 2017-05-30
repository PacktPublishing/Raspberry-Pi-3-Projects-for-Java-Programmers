package chapter2.lcd;

import com.pi4j.component.lcd.impl.GpioLcdDisplay;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.RaspiPin;

/**
 * The main LCD handler class.
 * @author John Sirach <john.sirach@pidome.org>
 */
public class LcdHandler {

    /**
     * The amount of rows on this display.
     */
    private final static int LCD_ROWS = 2;
    /**
     * The amount of columns per row.
     */
    private final static int LCD_COLUMNS = 16;
    
    /**
     * GPIO controller.
     */
    final GpioController gpio = GpioFactory.getInstance();
    
    /**
     * The LCD handle we will be using to communicate with the display.
     */
    final GpioLcdDisplay lcdHandle;
    
    /**
     * For singleton use.
     */
    private static LcdHandler handler;
    
    /**
     * LcdHandler constructor.
     * @throws LcdSetupException When init of the LCD fails.
     */
    private LcdHandler() throws LcdSetupException {
        // initialize LCD
        lcdHandle = new GpioLcdDisplay(LCD_ROWS,          // Rows
                                       LCD_COLUMNS,       // Columns
                                       RaspiPin.GPIO_01,  // RS
                                       RaspiPin.GPIO_04,  // Enable
                                       RaspiPin.GPIO_26,  // Bit 1
                                       RaspiPin.GPIO_27,  // Bit 2
                                       RaspiPin.GPIO_28,  // Bit 3
                                       RaspiPin.GPIO_29); // Bit 4
    }
    
    /**
     * We use an singleton because we need to make sure only one instance lives.
     * @return The lcd handler.
     * @throws chapter2.lcd.LcdSetupException When the setup of the LCD display fails.
     */
    public static LcdHandler getInstance() throws LcdSetupException {
        if(handler == null){
            handler = new LcdHandler();
        }
        return handler;
    }
    
    /**
     * Clears the LCD display.
     */
    public final void clear(){
        lcdHandle.clear();
    }
    
    /**
     * Places the cursor at row 0 and column 0.
     */
    public final void setHome(){
        lcdHandle.setCursorHome();
    }
    
    /**
     * Sets the cursor at a specific position.
     * The row count starts at 0. This means the first line is row 0, the second line is row 1.
     * The column count starts at 0, this means the first column is 0, the second column is 1.
     * @param row Sets the cursor on the defined row.
     * @param column Sets the cursor on the defined column.
     */
    public final void setCursor(int row, int column){
        lcdHandle.setCursorPosition(row, column);
    }
    
    /**
     * Write a text on the cursor position.
     * @param text The text to write to the LCD.
     */
    public final void write(String text){
        lcdHandle.write(text);
    }
    
}