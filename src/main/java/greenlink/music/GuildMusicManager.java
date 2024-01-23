package greenlink.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import global.BotMain;
import global.config.Config;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author t.me/GreenL1nk
 * 12.01.2024
 */
public class GuildMusicManager {

    public AudioPlayer audioPlayer;
    public TrackScheduler trackScheduler;
    public AudioPlayerSendHandler sendHandler;
    public ScheduledExecutorService executorService;

    public GuildMusicManager(AudioPlayerManager audioPlayerManager) {
        this.audioPlayer = audioPlayerManager.createPlayer();
        this.trackScheduler = new TrackScheduler(audioPlayer);
        this.audioPlayer.addListener(trackScheduler);
        this.sendHandler = new AudioPlayerSendHandler(audioPlayer);
    }

    public void startScheduler(Guild guild) {
        executorService = Executors.newScheduledThreadPool(1);
        executorService.schedule(() -> {
            GuildVoiceState botVoiceState = guild.getSelfMember().getVoiceState();

            if (botVoiceState == null) return;
            if (!botVoiceState.inAudioChannel()) return;
            AudioChannelUnion channel = botVoiceState.getChannel();
            if (channel == null) return;
            trackScheduler.stop();
            guild.getAudioManager().closeAudioConnection();
            audioPlayer = null;
            trackScheduler = null;
            sendHandler = null;
            BotMain.logger.debug("Бот отключился по истечению таймаута из " + guild.getIdLong());
        }, Config.getInstance().getBotVoiceTimeout(), TimeUnit.MINUTES);
    }

    public void removeScheduler(Guild guild) {
        if (executorService == null) return;
        executorService.shutdownNow();
        BotMain.logger.debug("У гильдии " + guild.getIdLong() + " были отключены executro'ы");
    }

    public AudioPlayerSendHandler getSendHandler() {
        return sendHandler;
    }
}
