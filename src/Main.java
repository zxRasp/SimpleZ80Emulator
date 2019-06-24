import com.zxrasp.emulator.core.EmulationCore;
import com.zxrasp.emulator.platform.SwingScreen;

import java.awt.*;

public class Main {

    public static final String WINDOW_TITLE = "Simple Z80 Emulator";

    public static void main(String[] args) {
       EmulationCore core = new EmulationCore();
       core.init(new SwingScreen(WINDOW_TITLE, new Dimension(800, 600)));

       core.doEmulation();
    }
}
