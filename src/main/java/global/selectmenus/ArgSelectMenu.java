package global.selectmenus;

import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;

/**
 * @author t.me/GreenL1nk
 * 29.01.2024
 */
public abstract class ArgSelectMenu implements ISelectMenu {

    public String[] getArgs(StringSelectInteractionEvent event) {
        String substring = event.getComponentId().substring(event.getComponentId().indexOf("-") + 1);
        return substring.split("-");
    }

}
