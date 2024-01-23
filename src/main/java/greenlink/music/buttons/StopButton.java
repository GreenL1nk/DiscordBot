package greenlink.music.buttons;

import global.buttons.IButton;
import greenlink.music.PlayerManager;
import greenlink.music.GuildMusicManager;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

/**
 * @author t.me/GreenL1nk
 * 16.01.2024
 */
public class StopButton implements IButton {
    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (event.getGuild() == null) return;
        if (!memberCanPerform(event.getMember(), event)) return;


        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
        musicManager.trackScheduler.stop();
    }

    @Override
    public String getButtonID() {
        return "stoptracks";
    }
}
