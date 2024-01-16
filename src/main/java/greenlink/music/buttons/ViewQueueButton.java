package greenlink.music.buttons;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import global.buttons.IButton;
import global.utils.Utils;
import greenlink.music.GuildMusicManager;
import greenlink.music.PlayerManager;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * @author t.me/GreenL1nk
 * 16.01.2024
 */
public class ViewQueueButton implements IButton {
    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (event.getGuild() == null) return;

        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
        List<AudioTrack> trackList = new ArrayList<>(musicManager.trackScheduler.queue);
        StringBuilder message = new StringBuilder("```\n");
        int size = Math.min(trackList.size(), 15);
        for (int i = 1; i < size + 1; i++) {
            AudioTrack audioTrack = trackList.get(i - 1);
            message.append(i).append(". ").append(Utils.capitalizeFirstLetter(audioTrack.getInfo().title)).append(" - ").append(Utils.formatTime(audioTrack.getDuration())).append("\n");
        }
        if (trackList.size() > 15) {
            message.append("\nИ ещё ").append(trackList.size() - 15).append("...");
        }
        message.append("```");
        event.reply(String.valueOf(message)).setEphemeral(true).queue();
    }

    @Override
    public String getButtonID() {
        return "viewqueue";
    }
}
