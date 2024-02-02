package global.buttons;

import java.util.ArrayList;
import java.util.List;

/**
 * @author t.me/GreenL1nk
 * 15.01.2024
 */
public class ButtonManager {
    private static ButtonManager instance;

    private final List<IButton> buttonsMap = new ArrayList<>();

    private ButtonManager() {
    }

    public void addButtons(IButton ... iButtons) {
        getButtons().addAll(List.of(iButtons));
    }

    public List<IButton> getButtons() { return buttonsMap; }


    public static synchronized ButtonManager getInstance() {
        if (instance == null) {
            instance = new ButtonManager();
        }
        return instance;
    }
}