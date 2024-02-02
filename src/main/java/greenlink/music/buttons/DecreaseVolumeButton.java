package greenlink.music.buttons;

import global.buttons.IButton;
import greenlink.music.GuildMusicManager;
import greenlink.music.PlayerManager;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

/**
 * @author t.me/GreenL1nk
 * 16.01.2024
 */
public class DecreaseVolumeButton implements IButton {
    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (event.getGuild() == null) return;
        if (!memberCanPerformIfVoice(event.getMember(), event)) return;

        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
        musicManager.audioPlayer.setVolume(musicManager.audioPlayer.getVolume() - 10);
        musicManager.trackScheduler.updateMessage();
        event.deferEdit().queue();
    }

    @Override
    public String getButtonID() {
        return "decresevolume";
    }
}
