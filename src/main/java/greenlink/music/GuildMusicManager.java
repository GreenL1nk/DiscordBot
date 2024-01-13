package greenlink.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;

/**
 * @author t.me/GreenL1nk
 * 12.01.2024
 */
public class GuildMusicManager {

    public final AudioPlayer audioPlayer;
    public final TrackScheduler trackScheduler;
    private final AudioPlayerSendHandler sendHandler;

    public GuildMusicManager(AudioPlayerManager audioPlayerManager) {
        this.audioPlayer = audioPlayerManager.createPlayer();
        this.trackScheduler = new TrackScheduler(audioPlayer);
        this.audioPlayer.addListener(trackScheduler);
        this.sendHandler = new AudioPlayerSendHandler(audioPlayer);
    }

    public AudioPlayerSendHandler getSendHandler() {
        return sendHandler;
    }
}
