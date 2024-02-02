package global.buttons;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

/**
 * @author t.me/GreenL1nk
 * 01.02.2024
 */
public abstract class ArgButton implements IButton {

    public String[] getArgs(ButtonInteractionEvent event) {
        String substring = event.getComponentId().substring(event.getComponentId().indexOf("-") + 1);
        return substring.split("-");
    }

}
