
import java.io.IOException;


class IOExpander {

    private int device;

    private final Wire wire;

    public enum SequentialOperationMode {
      
        SEQUENTIAL_MODE_ENABLE((byte) 0x00),
        SEQUENTIAL_MODE_DISABLE((byte) 0xff);

        byte mode;

        SequentialOperationMode(byte mode) {
            this.mode = mode;
        }
        
        byte getMode() {
            return mode;
        }
    };

    public enum Register {
      
        IODIRA((byte) 0x00),
        IODIRB((byte) 0x01),
        IPOLA((byte) 0x02),
        IPOLB((byte) 0x03),
        GPINTENA((byte) 0x04),
        GPINTENB((byte) 0x05),
        DEFVALA((byte) 0x06),
        DEFVALB((byte) 0x07),
        INTCONA((byte) 0x08),
        INTCONB((byte) 0x09),
        IOCON((byte) 0x0a),
        GPPUA((byte) 0x0c),
        GPPUB((byte) 0x0d),
        INTFA((byte) 0x0e),
        INTFB((byte) 0x0f),
        INTCAPA((byte) 0x10),
        INTCAPB((byte) 0x11),
        GPIOA((byte) 0x12),
        GPIOB((byte) 0x13),
        OLATA((byte) 0x14),
        OLATB((byte) 0x15);
        
        byte address;
        
        Register(byte address) {
            this.address = address;
        }
        
        byte getAddress() {
            return address;
        }
    };

    public enum Port {
        
        PORT_A(Register.GPIOA, Register.IODIRA, Register.GPPUA, Register.IPOLA, Register.GPINTENA), 
        PORT_B(Register.GPIOB, Register.IODIRB, Register.GPPUB, Register.IPOLB, Register.GPINTENB);
        
        Register ioRegister;
        Register directionRegister;
        Register pullUpRegister;
        Register pinPolarityRegister;
        Register pinInterruptRegister;
        
        Port(Register ioRegister, Register directionRegister, Register pullUpRegister, Register pinPolarityRegister, Register pinInterruptRegister) {
          this.ioRegister = ioRegister;
          this.directionRegister = directionRegister;
          this.pullUpRegister = pullUpRegister;
          this.pinPolarityRegister = pinPolarityRegister;
          this.pinInterruptRegister = pinInterruptRegister;
        }
        
        Register getIORegister() {
            return ioRegister;
        }

        Register getDirectionRegister() {
            return directionRegister;
        }

        Register getPullUpRegister() {
            return pullUpRegister;
        }
        
        Register getPinPolarityRegister() {
            return pinPolarityRegister;
        }
        
        Register getPinInterruptRegister() {
            return pinInterruptRegister;
        }
    };

    public enum Direction {
      
        OUT((byte) 0x00), 
        IN((byte) 0xff);
        
        byte direction;
        
        Direction(byte direction) {
            this.direction = direction;
        }
        
        byte getDirection() {
            return direction;
        }
    };

    public enum Mask {
      
        IOCON_BANK((byte) 0x80),
        IOCON_MIRROR((byte) 0x40),
        IOCON_SEQOP((byte) 0x20),
        IOCON_DISSLW((byte) 0x10),
        IOCON_HAEN((byte) 0x08),
        IOCON_ODR((byte) 0x04),
        IOCON_INTPOL((byte) 0x02);
        
        byte value;
        
        Mask(byte value) {
            this.value = value;
        }
        
        byte getValue() {
            return value;
        }
    };

    public enum Pin {
      
        PIN_A0((byte) 0x00),
        PIN_A1((byte) 0x01),
        PIN_A2((byte) 0x02),
        PIN_A3((byte) 0x03),
        PIN_A4((byte) 0x04),
        PIN_A5((byte) 0x05),
        PIN_A6((byte) 0x06),
        PIN_A7((byte) 0x07),
        PIN_B0((byte) 0x08),
        PIN_B1((byte) 0x09),
        PIN_B2((byte) 0x0a),
        PIN_B3((byte) 0x0b),
        PIN_B4((byte) 0x0c),
        PIN_B5((byte) 0x0d),
        PIN_B6((byte) 0x0e),
        PIN_B7((byte) 0x0f);
        
        byte number;
        
        Pin(byte number) {
            this.number = number;
        }
        
        byte getNumber() {
            return number;
        }
    };

    IOExpander(Wire wire) {
        this.wire = wire;
        this.device = 0x20;
    }

    /**
     * Begins the IO expander divice.
     *
     * @param device            The device address, or just the last 3 pins combination;
     */
    void begin(int device) {
        this.device |= device & 0x07;
    }

    /**
     * Configures the specified pin to behave either as an input or an output.
     *
     * @param pin               The pin number.
     * @param Direction         1 means input, 0 means output.
     */
    void pinMode(Pin pin, Direction direction) throws IOException {
        Port port = pinToPort(pin);
        configureRegisterBits(port.getDirectionRegister(), (byte) (1 << (pin.getNumber() % 8)), (byte) direction.getDirection());
    }

    /**
     * Configures a port to behave either as an input or an output.
     *
     * @param port              The port.
     * @param mode              The mode.
     */
    void portMode(Port port, int mode) throws IOException {  
        Register reg = port.getDirectionRegister();
        writeRegister(reg, (byte) mode);
    }

    /**
     * Write a HIGH or a LOW value to a pin.
     *
     * @param pin               The pin number.
     * @param value             LOW or WRITE.
     */
    void digitalWrite(Pin pin, boolean value) throws IOException {
        Port port = pinToPort(pin);
        configureRegisterBits(port.getIORegister(), (byte) (1 << (pin.getNumber() % 8)), (byte) ((value) ? 0xff : 0x00));
    }

    /**
     * Reads the value from a specified pin, either HIGH or LOW.
     *
     * @param pin               The pin number.
     */
    boolean digitalRead(Pin pin) throws IOException {    
        Port port = pinToPort(pin);
        return (readRegister(port.getIORegister()) & (1 << (pin.getNumber() % 8))) > 0;  
    }

    /**
     * Writes value to a port.
     *
     * @param port              The port to write.
     * @param value             The value to write to the port.
     */
    void portWrite(Port port, byte value) throws IOException {
        writeRegister(port.getIORegister(), value);
    }

    /**
     * Reads a port.
     *
     * @param port              The port to write.
     * @return                  The value associated with the port.
     */
    int portRead(Port port) throws IOException {
        return readRegister(port.getIORegister());
    }

    /**
     * Configures the pullup resistor for a given pin.
     *
     * @param pin               The pin number.
     * @param pullUp            0 means with, 1 means witout pullup.
     */
    void setPinPullUp(Pin pin, boolean pullUp) throws IOException {
        Port port = pinToPort(pin);
        configureRegisterBits(port.getPullUpRegister(), (byte) (1 << (pin.getNumber() % 8)), (byte) ((pullUp) ? 0xff : 0x00));
    }

    /**
     * Configures the polarity for a given pin.
     *
     * @param pin               The pin number.
     * @param pullUp            0 means normal, 1 means inverted polarity.
     */
    void setPinPolarity(Pin pin, boolean polarity) throws IOException {
        Port port = pinToPort(pin);
        configureRegisterBits(port.getPinPolarityRegister(), (byte) (1 << (pin.getNumber() % 8)), (byte) ((polarity) ? 0xff : 0x00));
    }

    /**
     * Configures a pin to clear or set the interrupt.
     *
     * @param pin               The pin number.
     * @param pullUp            0 means interrupt disable, 1 means interrupt enable.
     */
    void setPinInterrupt(Pin pin, boolean interrupt) throws IOException {
        Port port = pinToPort(pin);
        configureRegisterBits(port.getPinInterruptRegister(), (byte) (1 << (pin.getNumber() % 8)), (byte) ((interrupt) ? 0xff : 0x00));
    }

    /**
     * Configures the sequential/continuous operation mode.
     *
     * @param mode              The operation mode.
     */
    void setSequentialOperationMode(SequentialOperationMode mode) {
    }

    /**
     * Configures a registers.
     *
     * @param reg           The register number.
     * @param mask          The mask to be used.
     * @param v             The value to be used.
     */
    void configureRegisterBits(Register reg, byte mask, byte value) throws IOException {
        byte n;
        n = (byte) readRegister(reg);
        n &= ~(mask);
        n |= value & mask;
        writeRegister(reg, n);
    }

    /**
     * Writes a value to a register.
     *
     * @param reg           The register number.
     * @param v             The value to be used.
     */
    void writeRegister(Register reg, byte value) throws IOException {
        byte[] buf = new byte[] { reg.getAddress(), value };
        wire.beginTransmission(device);
        if (wire.writeBytes(buf, 2) != 2) {
            throw new IOException("Cannot write to device.");
        }
        wire.endTransmission();
    }

    /**
     * Reads a value from a register.
     *
     * @param reg           The register number.
     * @return              The register value.
     */
    int readRegister(Register reg) throws IOException {      
        wire.beginTransmission(device);
        if (wire.writeBytes(new byte[] { reg.getAddress() }, 1) != 1) {
            throw new IOException("Cannot read from device.");
        }
        wire.endTransmission();
        wire.requestFrom(device, 1);
        while(wire.available() <= 0) {
        }
        return wire.readByte();
    }
    
    private Port pinToPort(Pin pin) {
        return pin.getNumber() < 8 ? Port.PORT_A : Port.PORT_B;
    }
}
