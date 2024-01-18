package greenlink.music.buttons;

import global.buttons.IButton;
import greenlink.music.GuildMusicManager;
import greenlink.music.PlayerManager;
import greenlink.music.TrackScheduler;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Component;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author t.me/GreenL1nk
 * 16.01.2024
 */
public class PauseButton implements IButton {
    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (event.getGuild() == null) return;
        if (!memberCanPerform(event.getMember(), event)) return;


        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
        TrackScheduler trackScheduler = musicManager.trackScheduler;
        event.deferEdit().queue();
        if (trackScheduler.audioPlayer.isPaused()) {
            trackScheduler.audioPlayer.setPaused(false);
            event.getMessage().editMessageComponents(trackScheduler.getSituationalRow()).queue();
        }
        else {
            trackScheduler.audioPlayer.setPaused(true);
            event.getMessage().editMessageComponents(trackScheduler.getSituationalRow()).queue();
        }

    }

    @Override
    public String getButtonID() {
        return "pausetrack";
    }
}
