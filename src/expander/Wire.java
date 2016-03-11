package expander;

public class Wire {
  
    int fd;
    
    public Wire() {
        fd = -1;
    }
    
    /**
     * Initiate the library. (Only as a master)
     * This should normally be called only once.
     * It maps the BSC0 (0x7E20_5000) registers.
     */
    public native int begin(byte channel);

    /**
     * Unmap the BSC0 registers.
     */
    public native void stop();

    /**
     * Begin a transmission to the I2C slave device with the given 
     * address. Subsequently, queue bytes for transmission with the 
     * write() function and transmit them by calling endTransmission().
     * 
     * @param address               The device address.
     */
    public native void beginTransmission(int address);

    /**
     * Ends a transmission to a slave device that was begun by 
     * beginTransmission() and transmits the bytes that were queued by 
     * write().
     * 
     * @return                      Nothing for now.
     */
    public native byte endTransmission();

    /**
     * Used to request bytes from a slave device. The bytes may then be 
     * retrieved with the available() and read() functions.
     * 
     * @param address               The slave address.
     * @param len                   The length of data. Need be <= 16 
     *                              due the FIFO limits.
     */
    public native byte requestFrom(int address, int len);

    /**
     * Queues bytes for transmission to slave device (in-between calls 
     * to beginTransmission() and endTransmission()).
     * 
     * @param buf                   The bytes to be queued.
     * @param len                   The number of byte to be queued.
     * @return                      The number of accepted bytes.
     */
    public native int writeBytes(byte[] buf, int len);

    /**
     * Returns 1 if there is one or more bytes to be read.
     * 
     * @return                      0 if there is no bytes to be read in
     *                              the internal FIFO.
     */
    public native int available();

    /**
     * Reads a byte that was transmitted from a slave device to a master
     * after a call to requestFrom()
     *
     * @return                      The byte read.
     */
    public native int readByte();

    /**
     * For now, does nothing.
     */
    public native void flush();
}
    
