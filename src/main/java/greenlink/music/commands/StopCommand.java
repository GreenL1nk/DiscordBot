package greenlink.music.commands;

import global.commands.SlashCommand;
import greenlink.music.GuildMusicManager;
import greenlink.music.PlayerManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * @author t.me/GreenL1nk
 * 13.01.2024
 */
public class StopCommand extends SlashCommand {

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (event.getMember() == null) return;
        if (event.getMember().getVoiceState() == null) return;
        if (event.getGuild() == null) return;

        if(!event.getGuild().getSelfMember().getVoiceState().inAudioChannel()) return;
        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
        musicManager.trackScheduler.audioPlayer.stopTrack();
        musicManager.trackScheduler.queue.clear();

        event.deferReply().queue();
        event.getChannel().sendMessage("Проигрывание остановлено, очередь очищена").queue();
    }

    @Override
    public String getName() {
        return "stop";
    }

    @Override
    public String getDescription() {
        return "Останавливает воспроизведение аудио";
    }
}
