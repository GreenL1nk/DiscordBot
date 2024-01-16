package greenlink.music.buttons;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import global.buttons.IButton;
import greenlink.music.GuildMusicManager;
import greenlink.music.PlayerManager;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author t.me/GreenL1nk
 * 16.01.2024
 */
public class ShuffleButton implements IButton {
    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (event.getGuild() == null) return;

        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
        ArrayList<AudioTrack> trackList = new ArrayList<>(musicManager.trackScheduler.queue);
        Collections.shuffle(trackList);
        musicManager.trackScheduler.queue.clear();
        musicManager.trackScheduler.queue.addAll(trackList);
    }

    @Override
    public String getButtonID() {
        return "shufflestracks";
    }
}
