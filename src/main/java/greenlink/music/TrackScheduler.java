package greenlink.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import global.BotMain;
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
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author t.me/GreenL1nk
 * 12.01.2024
 */
public class TrackScheduler extends AudioEventAdapter {
    public final AudioPlayer audioPlayer;
    public BlockingDeque<AudioTrack> queue;
    public final BlockingDeque<AudioTrack> history;
    public boolean repeatTrack = false;
    public boolean repeatPlayList = false;
    public boolean saveHistory = true;
    public InteractionHook message;
    public AudioTrack currentTrack;
    public HashMap<Long, ArrayList<AudioTrack>> chooseTrack = new HashMap<>();
    public boolean needAddButtons = true;

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

    public void playQueuedTrackById(String ... ids) {
        BlockingDeque<AudioTrack> newQueue = new LinkedBlockingDeque<>();
        ArrayList<AudioTrack> copyQueue = new ArrayList<>(queue);
        for (String id : ids) {
            AudioTrack queueTrackById = getQueueTrackById(id);
            if (queueTrackById == null) return;
            saveHistory = true;
            if (!audioPlayer.startTrack(queueTrackById, true)) {
                newQueue.offer(queueTrackById);
            }
            copyQueue.remove(queueTrackById);
        }
        newQueue.addAll(copyQueue);
        this.queue.clear();
        this.queue = newQueue;
        updateMessage();
    }

    @Nullable
    public AudioTrack getQueueTrackById(String id) {
        ArrayList<AudioTrack> tracks = new ArrayList<>(queue);
        return tracks.stream().filter(audioTrack -> audioTrack.getIdentifier().equals(id)).findFirst().orElse(null);
    }

    public void onQueueUpdate() {
        updateMessage();
    }

    public void updateMessage() {
        EmbedBuilder embedBuilder = getEmbedBuilder(currentTrack);
        message.editOriginalEmbeds(embedBuilder.build()).setComponents(getSituationalRow()).queue();
    }

    @NotNull
    private EmbedBuilder getEmbedBuilder(AudioTrack currentTrack) {
        AudioTrack nextTrack = getNextTrack();
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setThumbnail(currentTrack.getInfo().artworkUrl);
        embedBuilder.setColor(Color.decode("#e32a2a"));
        if (nextTrack != null) {
            embedBuilder.setFooter(String.format("%s - %s" +
                                    "\n (В очереди ещё: %d)" +
                                    "\n (Общее время: %s)",
                            nextTrack.getInfo().title,
                            Utils.formatTime(nextTrack.getDuration()),
                            queue.size(),
                            Utils.formatTime(queue.stream().mapToLong(AudioTrack::getDuration).sum())
                    ),
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
        message.deleteOriginal().queue();
        message = null;
        needAddButtons = true;
    }

    public void stop() {
        currentTrack = null;
        repeatTrack = false;
        repeatPlayList = false;
        saveHistory = true;
        deleteMessage();
        audioPlayer.stopTrack();
        queue.clear();
        history.clear();
        audioPlayer.setPaused(false);
        audioPlayer.destroy();
    }

//    public ActionRow[] getActionRow() {
//        List<Button> buttons = new ArrayList<>();
//
//        buttons.add(Button.of(ButtonStyle.PRIMARY, "previoustrack", "Предыдущий", Emoji.fromUnicode("⏪")));
//        buttons.add(Button.of(ButtonStyle.PRIMARY, "nexttrack", "Следующий", Emoji.fromUnicode("⏩")));
//
//        buttons.add(Button.of(ButtonStyle.SUCCESS, "decresevolume", "Уменьшить", Emoji.fromUnicode("🔉")));
//        buttons.add(Button.of(ButtonStyle.SUCCESS, "increasevolume", "Увеличить", Emoji.fromUnicode("🔊")));
//
//        buttons.add(Button.of(ButtonStyle.DANGER, "stoptracks", "Остановить", Emoji.fromUnicode("⏹️")));
//        buttons.add(Button.of(ButtonStyle.SECONDARY, "pausetrack", "Пауза", Emoji.fromUnicode("⏸️")));
//
//        buttons.add(Button.of(ButtonStyle.SECONDARY, "repeatrack", "Включить цикл трека", Emoji.fromUnicode("🔂")));
//        buttons.add(Button.of(ButtonStyle.SECONDARY, "repeaplaylist", "Включить цикл очереди", Emoji.fromUnicode("🔁")));
//
//        buttons.add(Button.of(ButtonStyle.SECONDARY, "shufflestracks", "Перемешать", Emoji.fromUnicode("🔀")));
//        buttons.add(Button.of(ButtonStyle.SECONDARY, "viewqueue", "Посмотреть очередь", Emoji.fromUnicode("📃")));
//
//        return processButtons(buttons).toArray(new ActionRow[0]);
//    }

//    public ActionRow[] getActionRowDisabled() {
//        List<Button> buttons = new ArrayList<>();
//
//        buttons.add(Button.of(ButtonStyle.PRIMARY, "previoustrack", "Предыдущий", Emoji.fromUnicode("⏪")).asDisabled());
//        buttons.add(Button.of(ButtonStyle.PRIMARY, "nexttrack", "Следующий", Emoji.fromUnicode("⏩")).asDisabled());
//
//        buttons.add(Button.of(ButtonStyle.SUCCESS, "decresevolume", "Уменьшить", Emoji.fromUnicode("🔉")).asDisabled());
//        buttons.add(Button.of(ButtonStyle.SUCCESS, "increasevolume", "Увеличить", Emoji.fromUnicode("🔊")).asDisabled());
//
//        buttons.add(Button.of(ButtonStyle.DANGER, "stoptracks", "Остановить", Emoji.fromUnicode("⏹️")).asDisabled());
//        buttons.add(Button.of(ButtonStyle.SECONDARY, "pausetrack", "Возобновить",  Emoji.fromUnicode("▶️")));
//
//        buttons.add(Button.of(ButtonStyle.SECONDARY, "repeatrack", "Включить цикл трека", Emoji.fromUnicode("🔂")).asDisabled());
//        buttons.add(Button.of(ButtonStyle.SECONDARY, "repeaplaylist", "Включить цикл очереди", Emoji.fromUnicode("🔁")).asDisabled());
//
//        buttons.add(Button.of(ButtonStyle.SECONDARY, "shufflestracks", "Перемешать", Emoji.fromUnicode("🔀")).asDisabled());
//        buttons.add(Button.of(ButtonStyle.SECONDARY, "viewqueue", "Посмотреть очередь", Emoji.fromUnicode("📃")).asDisabled());
//
//        return processButtons(buttons).toArray(new ActionRow[0]);
//    }

    public List<ActionRow> getSituationalRow() {
        List<Button> buttons = new ArrayList<>();

        if (getPreviousTrack() == null || audioPlayer.isPaused()) buttons.add(Button.of(ButtonStyle.PRIMARY, "previoustrack", "Предыдущий", Emoji.fromUnicode("⏪")).asDisabled());
        else buttons.add(Button.of(ButtonStyle.PRIMARY, "previoustrack", "Предыдущий", Emoji.fromUnicode("⏪")).asEnabled());
        if (getNextTrack() == null || audioPlayer.isPaused()) buttons.add(Button.of(ButtonStyle.PRIMARY, "nexttrack", "Следующий", Emoji.fromUnicode("⏩")).asDisabled());
        else buttons.add(Button.of(ButtonStyle.PRIMARY, "nexttrack", "Следующий", Emoji.fromUnicode("⏩")).asEnabled());

        if (audioPlayer.isPaused()) buttons.add(Button.of(ButtonStyle.SUCCESS, "decresevolume", "Уменьшить", Emoji.fromUnicode("🔉")).asDisabled());
        else buttons.add(Button.of(ButtonStyle.SUCCESS, "decresevolume", "Уменьшить", Emoji.fromUnicode("🔉")).asEnabled());
        if (audioPlayer.isPaused()) buttons.add(Button.of(ButtonStyle.SUCCESS, "increasevolume", "Увеличить", Emoji.fromUnicode("🔊")).asDisabled());
        else buttons.add(Button.of(ButtonStyle.SUCCESS, "increasevolume", "Увеличить", Emoji.fromUnicode("🔊")).asEnabled());

        buttons.add(Button.of(ButtonStyle.DANGER, "stoptracks", "Остановить", Emoji.fromUnicode("⏹️")).asEnabled());
        if (audioPlayer.isPaused()) {
            buttons.add(Button.of(ButtonStyle.SECONDARY, "pausetrack", "Возобновить", Emoji.fromUnicode("▶️")));
        }
        else {
            buttons.add(Button.of(ButtonStyle.SECONDARY, "pausetrack", "Пауза", Emoji.fromUnicode("⏸️")));
        }

        if (audioPlayer.isPaused()) buttons.add(Button.of(ButtonStyle.SECONDARY, "repeatrack", "Включить цикл трека", Emoji.fromUnicode("🔂")).asDisabled());
        else buttons.add(Button.of(ButtonStyle.SECONDARY, "repeatrack", "Включить цикл трека", Emoji.fromUnicode("🔂")).asEnabled());
        if (audioPlayer.isPaused()) buttons.add(Button.of(ButtonStyle.SECONDARY, "repeaplaylist", "Включить цикл очереди", Emoji.fromUnicode("🔁")).asDisabled());
        else buttons.add(Button.of(ButtonStyle.SECONDARY, "repeaplaylist", "Включить цикл очереди", Emoji.fromUnicode("🔁")).asEnabled());

        if (this.queue.isEmpty() || this.queue.size() == 1) buttons.add(Button.of(ButtonStyle.SECONDARY, "shufflestracks", "Перемешать", Emoji.fromUnicode("🔀")).asDisabled());
        else buttons.add(Button.of(ButtonStyle.SECONDARY, "shufflestracks", "Перемешать", Emoji.fromUnicode("🔀")).asEnabled());
        if (this.queue.isEmpty()) buttons.add(Button.of(ButtonStyle.SECONDARY, "viewqueue", "Посмотреть очередь", Emoji.fromUnicode("📃")).asDisabled());
        else buttons.add(Button.of(ButtonStyle.SECONDARY, "viewqueue", "Посмотреть очередь", Emoji.fromUnicode("📃")).asEnabled());

        return processButtons(buttons);
    }

    public List<ActionRow> processButtons(List<Button> buttons) {
        List<ArrayList<Button>> buttonLists = IntStream.range(0, buttons.size())
                .filter(i -> i % 2 == 0)
                .mapToObj(i -> new ArrayList<>(buttons.subList(i, Math.min(i + 2, buttons.size()))))
                .toList();

        return buttonLists.stream()
                .map(this::createActionRow)
                .collect(Collectors.toList());
    }
    private ActionRow createActionRow(ArrayList<Button> buttonList) {
        return ActionRow.of(buttonList);
    }
}
