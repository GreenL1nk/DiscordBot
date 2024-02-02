package greenlink.music.selectmenus;

import global.selectmenus.ISelectMenu;
import greenlink.music.GuildMusicManager;
import greenlink.music.PlayerManager;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;

/**
 * @author t.me/GreenL1nk
 * 17.01.2024
 */
public class SetTrackMenu implements ISelectMenu {
    @Override
    public void onSelectMenuInteraction(StringSelectInteractionEvent event) {
        if (event.getGuild() == null) return;
        if (!memberCanPerformIfVoice(event.getMember(), event)) return;
        event.deferEdit().queue();
        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
        musicManager.audioPlayer.stopTrack();
        musicManager.trackScheduler.playQueuedTrackById(event.getValues().toArray(new String[0]));
        event.getMessage().delete().queue();
    }

    @Override
    public String getMenuID() {
        return "set-track";
    }
}
