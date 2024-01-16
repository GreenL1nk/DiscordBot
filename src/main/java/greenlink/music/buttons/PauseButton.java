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
public class PauseButton implements IButton {
    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (event.getGuild() == null) return;

        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
        if (musicManager.trackScheduler.audioPlayer.isPaused()) {
            musicManager.trackScheduler.audioPlayer.setPaused(false);
            Button button = Button.of(ButtonStyle.SECONDARY, getButtonID(), "Пауза", Emoji.fromUnicode("⏸️"));
            event.getInteraction().editButton(button).queue();
        }
        else {
            musicManager.trackScheduler.audioPlayer.setPaused(true);
            Button button = Button.of(ButtonStyle.SECONDARY, getButtonID(), "Возобновить",  Emoji.fromUnicode("▶️"));
            event.getInteraction().editButton(button).queue();
        }

    }

    @Override
    public String getButtonID() {
        return "pausetrack";
    }
}
