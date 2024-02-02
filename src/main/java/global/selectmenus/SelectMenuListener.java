package global.selectmenus;

import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

/**
 * @author t.me/GreenL1nk
 * 17.01.2024
 */
public class SelectMenuListener extends ListenerAdapter {

    @Override
    public void onStringSelectInteraction(@NotNull StringSelectInteractionEvent event) {
        SelectMenusManager.getInstance().getMenus().stream()
                .filter(menu -> {
                    if (menu instanceof ArgSelectMenu) {
                        return event.getComponentId().startsWith(menu.getMenuID());
                    }
                    return menu.getMenuID().equals(event.getComponentId());
                })
                .findFirst()
                .ifPresent((menu) -> menu.onSelectMenuInteraction(event));
    }

}
