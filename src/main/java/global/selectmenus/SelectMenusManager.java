package global.selectmenus;

import java.util.ArrayList;
import java.util.List;

/**
 * @author t.me/GreenL1nk
 * 17.01.2024
 */
public class SelectMenusManager {
    private static SelectMenusManager instance;
    private final List<ISelectMenu> menuMap = new ArrayList<>();

    private SelectMenusManager() {
    }

    public void addMenus(ISelectMenu ... iSelectMenus) {
        getMenus().addAll(List.of(iSelectMenus));
    }

    public List<ISelectMenu> getMenus() { return menuMap; }

    public static synchronized SelectMenusManager getInstance() {
        if (instance == null) {
            instance = new SelectMenusManager();
        }
        return instance;
    }
}