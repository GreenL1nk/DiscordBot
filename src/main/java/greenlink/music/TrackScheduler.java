package greenlink.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import global.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * @author t.me/GreenL1nk
 * 12.01.2024
 */
public class TrackScheduler extends AudioEventAdapter {
    public final AudioPlayer audioPlayer;
    public final BlockingDeque<AudioTrack> queue;
    public final BlockingDeque<AudioTrack> history;
    public boolean repeatTrack = false;
    public boolean repeatPlayList = false;
    public boolean saveHistory = true;
    public InteractionHook message;
    public AudioTrack currentTrack;

    public TrackScheduler(AudioPlayer audioPlayer) {
        this.audioPlayer = audioPlayer;
        this.queue = new LinkedBlockingDeque<>();
        this.history = new LinkedBlockingDeque<>();
    }

    public void queue(AudioTrack track) {
        if (!audioPlayer.startTrack(track, true)) {
            this.queue.offer(track);
            onQueueUpdate();
        }
    }

    public void nextTrack(boolean addToHistory) {
        if (addToHistory) saveHistory = true;
        this.audioPlayer.startTrack(this.queue.poll(), false);
        if (this.queue.isEmpty() && !this.history.isEmpty() && repeatPlayList) {
            this.queue.addAll(history);
            this.history.clear();
            saveHistory = true;
        }
    }
    public void previousTrack() {
        AudioTrack previous = this.history.pollLast();
        if (previous == null) return;
        saveHistory = false;
        this.queue.offerFirst(currentTrack.makeClone());
        this.queue.offerFirst(previous.makeClone());
        nextTrack(false);
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        currentTrack = track;
        updateMessage();
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (saveHistory) history.offer(track);
        if (endReason.mayStartNext) {
            if (repeatTrack) {
                this.audioPlayer.startTrack(track.makeClone(), false);
                return;
            }
            nextTrack(true);
        }
    }

    public void onQueueUpdate() {
        updateMessage();
    }

    public void updateMessage() {
        EmbedBuilder embedBuilder = getEmbedBuilder(currentTrack);
        message.editOriginalEmbeds(embedBuilder.build()).queue();
    }

    @NotNull
    private EmbedBuilder getEmbedBuilder(AudioTrack currentTrack) {
        AudioTrack nextTrack = getNextTrack();
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setThumbnail(currentTrack.getInfo().artworkUrl);
        embedBuilder.setColor(Color.decode("#e32a2a"));
        if (nextTrack != null) {
            embedBuilder.setFooter(String.format("%s - %s" +
                                    "\n (В очереди ещё: %d)",
                    nextTrack.getInfo().title, Utils.formatTime(nextTrack.getDuration()), queue.size()),
                    nextTrack.getInfo().artworkUrl);

        }
        embedBuilder.addField("💽 " + "Сейчас играет | Громкость - " + audioPlayer.getVolume(),
                String.format("[%s](%s) - %s",
                        currentTrack.getInfo().title, currentTrack.getInfo().uri, Utils.formatTime(currentTrack.getDuration())), false);
        return embedBuilder;
    }

    @Nullable
    public AudioTrack getNextTrack() {
        List<AudioTrack> tracks = new ArrayList<>(queue.stream().toList());
        if (tracks.isEmpty()) return null;
        else return tracks.get(0);
    }

    @Nullable
    public AudioTrack getPreviousTrack() {
        return history.isEmpty() ? null : history.peekLast().makeClone();
    }

    public void deleteMessage() {
        audioPlayer.stopTrack();
        queue.clear();
        message.deleteOriginal().queue();
        message = null;
    }

    public ActionRow[] getActionRow() {
        List<ActionRow> actionRows = new ArrayList<>();

        List<Button> row1Buttons = new ArrayList<>();
        row1Buttons.add(Button.of(ButtonStyle.PRIMARY, "previoustrack", "Предыдущий", Emoji.fromUnicode("⏪")));
        row1Buttons.add(Button.of(ButtonStyle.PRIMARY, "nexttrack", "Следующий", Emoji.fromUnicode("⏩")));
        actionRows.add(ActionRow.of(row1Buttons));

        List<Button> row2Buttons = new ArrayList<>();
        row2Buttons.add(Button.of(ButtonStyle.SUCCESS, "decresevolume", "Уменьшить", Emoji.fromUnicode("🔉")));
        row2Buttons.add(Button.of(ButtonStyle.SUCCESS, "increasevolume", "Увеличить", Emoji.fromUnicode("🔊")));
        actionRows.add(ActionRow.of(row2Buttons));

        List<Button> row3Buttons = new ArrayList<>();
        row3Buttons.add(Button.of(ButtonStyle.DANGER, "stoptracks", "Остановить", Emoji.fromUnicode("⏹️")));
        row3Buttons.add(Button.of(ButtonStyle.SECONDARY, "pausetrack", "Пауза", Emoji.fromUnicode("⏸️")));
        actionRows.add(ActionRow.of(row3Buttons));

        List<Button> row4Buttons = new ArrayList<>();
        row4Buttons.add(Button.of(ButtonStyle.SECONDARY, "repeatrack", "Включить цикл трека", Emoji.fromUnicode("🔂")));
        row4Buttons.add(Button.of(ButtonStyle.SECONDARY, "repeaplaylist", "Включить цикл очереди", Emoji.fromUnicode("🔁")));
        actionRows.add(ActionRow.of(row4Buttons));

        List<Button> row5Buttons = new ArrayList<>();
        row5Buttons.add(Button.of(ButtonStyle.SECONDARY, "shufflestracks", "Перемешать", Emoji.fromUnicode("🔀")));
        row5Buttons.add(Button.of(ButtonStyle.SECONDARY, "viewqueue", "Посмотреть очередь", Emoji.fromUnicode("📃")));
        actionRows.add(ActionRow.of(row5Buttons));

        return actionRows.toArray(new ActionRow[0]);
    }
}
