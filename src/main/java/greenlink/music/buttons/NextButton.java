package greenlink.music.buttons;

import global.buttons.IButton;
import greenlink.music.GuildMusicManager;
import greenlink.music.PlayerManager;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

/**
 * @author t.me/GreenL1nk
 * 15.01.2024
 */
public class NextButton implements IButton {
    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (event.getGuild() == null) return;
        if (!memberCanPerformIfVoice(event.getMember(), event)) return;
        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
        musicManager.trackScheduler.nextTrack(true);
        event.deferEdit().queue();
    }

    @Override
    public String getButtonID() {
        return "nexttrack";
    }
}
