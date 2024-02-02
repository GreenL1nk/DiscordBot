package global.modals;

import global.buttons.IButton;

import java.util.ArrayList;
import java.util.List;

/**
 * @author t.me/GreenL1nk
 * 02.02.2024
 */
public class ModalManager {

    private static ModalManager instance;

    private final List<IModal> modalsMap = new ArrayList<>();

    private ModalManager() {
    }

    public void addModals(IModal ... iModals) {
        getModals().addAll(List.of(iModals));
    }

    public List<IModal> getModals() { return modalsMap; }


    public static synchronized ModalManager getInstance() {
        if (instance == null) {
            instance = new ModalManager();
        }
        return instance;
    }

}
