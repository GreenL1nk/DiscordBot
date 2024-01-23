package greenlink.music.buttons;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import global.buttons.IButton;
import global.utils.Utils;
import greenlink.music.GuildMusicManager;
import greenlink.music.PlayerManager;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;

import java.util.ArrayList;
import java.util.List;

import static net.dv8tion.jda.api.interactions.components.selections.SelectOption.LABEL_MAX_LENGTH;

/**
 * @author t.me/GreenL1nk
 * 16.01.2024
 */
public class ViewQueueButton implements IButton {
    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (event.getGuild() == null) return;
        if (!memberCanPerform(event.getMember(), event)) return;

        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
        List<AudioTrack> trackList = new ArrayList<>(musicManager.trackScheduler.queue);

        StringSelectMenu.Builder dropdown = StringSelectMenu.create("set-track");
        if (trackList.isEmpty()) {
            event.deferEdit().queue();
            return;
        }
        dropdown.setMaxValues(Math.min(25, trackList.size()));
        for (AudioTrack track : trackList) {
            String title = track.getInfo().title + " " + Utils.formatTime(track.getDuration());
            String minTitle = title.length() <= LABEL_MAX_LENGTH ? title : title.substring(0, LABEL_MAX_LENGTH);
            String uuid = "~~~" + dropdown.getOptions().size();
            dropdown.addOptions(SelectOption.of(minTitle, track.getIdentifier() + uuid).withDescription(track.getInfo().author));
            if (dropdown.getOptions().size() == 25) break;
        }

        ActionRow actionRow = ActionRow.of(dropdown.build());
        event.replyComponents(actionRow).setEphemeral(true).queue();
    }

    @Override
    public String getButtonID() {
        return "viewqueue";
    }
}
