import com.zxrasp.emulator.core.impl.SimpleULA;
import com.zxrasp.emulator.core.impl.Z80;
import com.zxrasp.emulator.core.impl.z80internals.UnknownOperationException;

public class Main {

    public static void main(String[] args) {
        Z80 cpu = new Z80(new SimpleULA());

        while (true) {
            try {
                System.out.println("Perform operation during " + cpu.clock() + " ticks");
            } catch (UnknownOperationException e) {
                e.printStackTrace();
                return;
            }
        }
    }
}
