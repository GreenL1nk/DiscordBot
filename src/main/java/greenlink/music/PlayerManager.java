package greenlink.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import global.BotMain;
import global.config.Config;
import global.utils.Utils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static net.dv8tion.jda.api.interactions.components.selections.SelectOption.LABEL_MAX_LENGTH;

/**
 * @author t.me/GreenL1nk
 * 12.01.2024
 */
public class PlayerManager {

    private static PlayerManager instance;
    private final Map<Long, GuildMusicManager> musicManagers;
    private final AudioPlayerManager audioPlayerManager;
    public HashMap<Long, ScheduledExecutorService> executorService = new HashMap<>();

    public PlayerManager() {
        this.musicManagers = new HashMap<>();
        this.audioPlayerManager = new DefaultAudioPlayerManager();

        AudioSourceManagers.registerRemoteSources(this.audioPlayerManager);
        AudioSourceManagers.registerLocalSource(this.audioPlayerManager);
    }

    public GuildMusicManager getMusicManager(Guild guild) {
        return musicManagers.computeIfAbsent(guild.getIdLong(), (guildId) -> {
            final GuildMusicManager guildMusicManager = new GuildMusicManager(this.audioPlayerManager);
            removeScheduler(guild);
            guild.getAudioManager().setSendingHandler(guildMusicManager.getSendHandler());

            return guildMusicManager;
        });
    }

    public void loadAndPlay(GuildMessageChannel channel, String trackUrl, boolean fromUrl, SlashCommandInteractionEvent event) {
        if (event.getMember() == null) return;
        GuildMusicManager musicManager = this.getMusicManager(channel.getGuild());
        TrackScheduler trackScheduler = musicManager.trackScheduler;
        if (trackScheduler.message != null && trackScheduler.message.isExpired()) {
            trackScheduler.message.retrieveOriginal().queue(message -> message.delete().queue());
            trackScheduler.message = null;
        }
        trackScheduler.chooseTrack.computeIfAbsent(event.getMember().getIdLong(), key -> new ArrayList<>());
        this.audioPlayerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                if (trackScheduler.message == null) {
                    event.deferReply().queue();
                    trackScheduler.message = event.getHook();
                    trackScheduler.message.editOriginalComponents(trackScheduler.getSituationalRow()).queue();
                }
                else {
                    event.deferReply().queue(m -> m.deleteOriginal().queue());
                }
                trackScheduler.queue(track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                List<AudioTrack> tracks = playlist.getTracks();
                if (fromUrl) {
                    if (trackScheduler.message == null) {
                        event.deferReply().queue();
                        trackScheduler.message = event.getHook();
                        trackScheduler.message.editOriginalComponents(trackScheduler.getSituationalRow()).queue();
                    }
                    else {
                        event.deferReply().queue(m -> m.deleteOriginal().queue());
                    }
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

    public void startScheduler(Guild guild) {
        ScheduledExecutorService service = Executors.newScheduledThreadPool(1);
        executorService.put(guild.getIdLong(), service);
        service.schedule(() -> {
            kickBot(guild);
            BotMain.logger.debug("Бот отключился по истечению таймаута из " + guild.getIdLong());
        }, Config.getInstance().getBotVoiceTimeout(), TimeUnit.MINUTES);
    }

    public void removeScheduler(Guild guild) {
        ScheduledExecutorService service = executorService.get(guild.getIdLong());
        if (service == null) return;
        service.shutdownNow();
        executorService.remove(guild.getIdLong());
        BotMain.logger.debug("У гильдии " + guild.getIdLong() + " были отключены executro'ы");
    }

    public void kickBot(Guild guild) {
        long idLong = guild.getIdLong();
        if (musicManagers.get(idLong).trackScheduler != null) musicManagers.get(idLong).trackScheduler.stop();
        GuildVoiceState botVoiceState = guild.getSelfMember().getVoiceState();
        if (botVoiceState == null) return;
        if (!botVoiceState.inAudioChannel()) return;
        AudioChannelUnion channel = botVoiceState.getChannel();
        if (channel == null) return;
        guild.getAudioManager().closeAudioConnection();
        musicManagers.remove(idLong);
        executorService.remove(guild.getIdLong());
    }

    public static PlayerManager getInstance() {
        if (instance == null) {
            instance = new PlayerManager();
        }
        return instance;
    }

}
