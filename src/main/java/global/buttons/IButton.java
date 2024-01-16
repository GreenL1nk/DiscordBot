package global.buttons;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

/**
 * @author t.me/GreenL1nk
 * 15.01.2024
 */
public interface IButton {

    void onButtonInteraction(ButtonInteractionEvent event);
    String getButtonID();

}
