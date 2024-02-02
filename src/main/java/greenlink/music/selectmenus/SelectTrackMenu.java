package greenlink.music.selectmenus;

import global.selectmenus.ISelectMenu;
import greenlink.music.GuildMusicManager;
import greenlink.music.PlayerManager;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;

/**
 * @author t.me/GreenL1nk
 * 17.01.2024
 */
public class SelectTrackMenu implements ISelectMenu {
    @Override
    public void onSelectMenuInteraction(StringSelectInteractionEvent event) {
        if (event.getGuild() == null) return;
        if (!memberCanPerformIfVoice(event.getMember(), event)) return;

        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
        String trackID = event.getValues().get(0);
        event.deferEdit().queue();

        if (musicManager.trackScheduler.message == null) {
            musicManager.trackScheduler.message = event.getHook();
            musicManager.trackScheduler.message.editOriginalComponents(musicManager.trackScheduler.getSituationalRow()).queue();
        }
        else {
            event.getMessage().delete().queue();
        }
        musicManager.trackScheduler.chooseTrack.get(event.getMember().getIdLong())
                .stream()
                .filter(audioTrack -> audioTrack.getIdentifier().equals(trackID)
                ).findFirst()
                .ifPresent(musicManager.trackScheduler::queue);
    }

    @Override
    public String getMenuID() {
        return "choose-track";
    }
}
