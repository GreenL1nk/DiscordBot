package greenlink.music.buttons;

import global.buttons.IButton;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

/**
 * @author t.me/GreenL1nk
 * 18.01.2024
 */
public class CancelButton implements IButton {
    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (event.getGuild() == null) return;
        if (!memberCanPerformIfVoice(event.getMember(), event)) return;

        event.deferEdit().queue();
        event.getMessage().delete().queue();
    }

    @Override
    public String getButtonID() {
        return "cancelselecttrack";
    }
}
