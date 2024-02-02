package greenlink.music.buttons;

import global.buttons.IButton;
import greenlink.music.GuildMusicManager;
import greenlink.music.PlayerManager;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;

/**
 * @author t.me/GreenL1nk
 * 16.01.2024
 */
public class RepeatTrackButton implements IButton {
    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (event.getGuild() == null) return;
        if (!memberCanPerformIfVoice(event.getMember(), event)) return;

        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
        boolean repeatTrack = musicManager.trackScheduler.repeatTrack;
        if (repeatTrack) {
            musicManager.trackScheduler.repeatTrack = false;
            Button button = Button.of(ButtonStyle.SECONDARY, getButtonID(), "Включить цикл трека",  Emoji.fromUnicode("🔂"));
            event.getInteraction().editButton(button).queue();
        }
        else {
            musicManager.trackScheduler.repeatTrack = true;
            Button button = Button.of(ButtonStyle.DANGER, getButtonID(), "Выключить цикл трека",  Emoji.fromUnicode("🔂"));
            event.getInteraction().editButton(button).queue();
        }
    }

    @Override
    public String getButtonID() {
        return "repeatrack";
    }
}
