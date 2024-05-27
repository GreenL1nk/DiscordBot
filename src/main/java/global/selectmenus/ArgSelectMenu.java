package global.selectmenus;

import global.BotMain;
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

    public String[] getValues(StringSelectInteractionEvent event) {
        String value = event.getValues().get(0);
        String substring = value.substring(value.indexOf("-") + 1);
        return substring.split("-");
    }
}
