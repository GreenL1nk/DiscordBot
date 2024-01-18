package greenlink.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import global.BotMain;
import global.utils.Utils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.dv8tion.jda.api.interactions.components.selections.SelectOption.LABEL_MAX_LENGTH;

/**
 * @author t.me/GreenL1nk
 * 12.01.2024
 */
public class PlayerManager {

    private static PlayerManager instance;
    private final Map<Long, GuildMusicManager> musicManagers;
    private final AudioPlayerManager audioPlayerManager;

    public PlayerManager() {
        this.musicManagers = new HashMap<>();
        this.audioPlayerManager = new DefaultAudioPlayerManager();

        AudioSourceManagers.registerRemoteSources(this.audioPlayerManager);
        AudioSourceManagers.registerLocalSource(this.audioPlayerManager);
    }

    public GuildMusicManager getMusicManager(Guild guild) {
        return musicManagers.computeIfAbsent(guild.getIdLong(), (guildId) -> {
            final GuildMusicManager guildMusicManager = new GuildMusicManager(this.audioPlayerManager);

            guild.getAudioManager().setSendingHandler(guildMusicManager.getSendHandler());

            return guildMusicManager;
        });
    }

    public void loadAndPlay(TextChannel channel, String trackUrl, boolean fromUrl, SlashCommandInteractionEvent event) {
        GuildMusicManager musicManager = this.getMusicManager(channel.getGuild());
        TrackScheduler trackScheduler = musicManager.trackScheduler;
        if (trackScheduler.message == null) {
            if (fromUrl) {
                trackScheduler.message = event.getHook();
                trackScheduler.message.editOriginalComponents(trackScheduler.getSituationalRow()).queue();
            }
        }
//        else event.getHook().deleteOriginal().queue();
        trackScheduler.chooseTrack.computeIfAbsent(event.getMember().getIdLong(), key -> new ArrayList<>());
        this.audioPlayerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                trackScheduler.queue(track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                List<AudioTrack> tracks = playlist.getTracks();
                if (fromUrl) {
                    tracks.forEach(trackScheduler::queue);
                }
                else {
                    if (playlist.getTracks().size() == 1) trackScheduler.queue(tracks.get(0));
                    StringSelectMenu.Builder dropdown = StringSelectMenu.create("choose-track");
                    for (AudioTrack track : playlist.getTracks()) {
                        String title = track.getInfo().title + " " + Utils.formatTime(track.getDuration());
                        String minTitle = title.length() <= LABEL_MAX_LENGTH ? title : title.substring(0, LABEL_MAX_LENGTH);
                        if (dropdown.getOptions().stream().anyMatch(selectOption -> selectOption.getValue().equals(minTitle))) continue;
                        dropdown.addOptions(SelectOption.of(minTitle, track.getIdentifier()).withDescription(track.getInfo().author));
                        trackScheduler.chooseTrack.get(event.getMember().getIdLong()).add(track);
                        if (dropdown.getOptions().size() == 25) break;
                    }

                    ActionRow actionRow = ActionRow.of(dropdown.build());
                    if (!event.isAcknowledged() && musicManager.trackScheduler.message != null) {
                        event.replyComponents(actionRow).setEphemeral(true).queue();
                    }
                    else {
                        event.replyComponents(actionRow)
                                .addActionRow(
                                        Button.of(ButtonStyle.DANGER, "cancelselecttrack", "Отменить", Emoji.fromUnicode("🛑"))
                                ).setEphemeral(false).queue();
                    }

//                    channel.sendMessageComponents(actionRow).queue();
                }
            }

            @Override
            public void noMatches() {

            }

            @Override
            public void loadFailed(FriendlyException exception) {

            }
        });
    }

    public static PlayerManager getInstance() {
        if (instance == null) {
            instance = new PlayerManager();
        }
        return instance;
    }

}
