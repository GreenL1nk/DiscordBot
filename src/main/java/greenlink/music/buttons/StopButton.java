package greenlink.music.buttons;

import global.buttons.IButton;
import greenlink.music.PlayerManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

/**
 * @author t.me/GreenL1nk
 * 16.01.2024
 */
public class StopButton implements IButton {
    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        Guild guild = event.getGuild();
        if (guild == null) return;
        if (!memberCanPerformIfVoice(event.getMember(), event)) return;

        PlayerManager.getInstance().kickBot(guild);
    }

    @Override
    public String getButtonID() {
        return "stoptracks";
    }
}
