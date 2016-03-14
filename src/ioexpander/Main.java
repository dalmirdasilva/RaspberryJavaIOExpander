package ioexpander;

import java.io.IOException;

public class Main {

    static {
        try {
            System.loadLibrary("wire");
        } catch (UnsatisfiedLinkError e) {
            String javaLibPath = System.getProperty("java.library.path");
            System.out.println("Cannot find libwire.so in any of the following paths: " + javaLibPath);
            System.exit(1);
        }
    }
    
    public static void main(String[] args) throws IOException, InterruptedException {
        Wire wire = new Wire();
        wire.begin((byte) 0);
        IOExpander expander = new IOExpander(wire);
        expander.begin(0);
        expander.pinMode(IOExpander.Pin.PIN_A0, IOExpander.Direction.OUT);
        expander.pinMode(IOExpander.Pin.PIN_A1, IOExpander.Direction.IN);
        while (true) {
            expander.digitalWrite(IOExpander.Pin.PIN_A0, expander.digitalRead(IOExpander.Pin.PIN_A1));
            Thread.sleep(100);
        }
    }
}
