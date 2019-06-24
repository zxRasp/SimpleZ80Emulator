import com.zxrasp.emulator.core.EmulationCore;
import com.zxrasp.emulator.platform.SwingScreen;

public class Main {

    public static final int FRAME_TIME = 1000 / 50;
    public static final String WINDOW_TITLE = "Simple Z80 Emulator";

    public static void main(String[] args) {
       EmulationCore core = new EmulationCore();
       core.init(new SwingScreen(WINDOW_TITLE));

       core.doEmulation(FRAME_TIME);
    }
}
