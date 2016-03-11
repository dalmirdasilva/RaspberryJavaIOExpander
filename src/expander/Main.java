package expander;

import java.io.IOException;

public class Main {

    static {
        System.load(Main.class.getResource("libwire.o").getPath());
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
