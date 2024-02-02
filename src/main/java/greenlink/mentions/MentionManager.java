package greenlink.mentions;

import global.BotMain;
import global.commands.ICommand;
import global.commands.SlashCommandsManager;
import greenlink.databse.DatabaseConnector;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author t.me/GreenL1nk
 * 28.01.2024
 */
public class MentionManager {
    private static MentionManager instance;
    public ArrayList<ICommand> mentionableCommands = new ArrayList<>();
    public HashMap<ICommand, ScheduledExecutorService> commandExecutors = new HashMap<>();

    public void runScheduleIfNotExist(ICommand command) {
        if (commandExecutors.containsKey(command)) return;
        ScheduledExecutorService service = Executors.newScheduledThreadPool(1);
        MentionObject mentionObject = getLowestMentionTime(command);
        if (mentionObject == null) return;
        commandExecutors.put(command, service);
        long scheduleTime = mentionObject.timeMention - System.currentTimeMillis();
        BotMain.logger.debug("Запуск для - " + command.getName());
        BotMain.logger.debug("Время: " + scheduleTime);
        service.schedule(() -> {
            mentionUser(mentionObject.user, mentionObject.mentionType, mentionObject.channelId, command, mentionObject.guildId);
            commandExecutors.remove(command);
            DatabaseConnector.getInstance().deleteMention(command, mentionObject.user);
            runScheduleIfNotExist(command);
        }, scheduleTime, TimeUnit.MILLISECONDS);
    }

    @Nullable
    public MentionObject getLowestMentionTime(ICommand command) {
        return DatabaseConnector.getInstance().getMentionObject(command);
    }

    public void mentionUser(User user, MentionType mentionType, String channelId, ICommand command, String guildId) {
        Long commandIdByName = SlashCommandsManager.getInstance().getCommandIdByName(command.getName());
        if (mentionType == MentionType.PRIVATE_CHANNEL) {
            user.openPrivateChannel()
                    .flatMap(channel -> channel.sendMessage(
                            String.format(
                                    "Вам снова доступна </%s:%s> в <#%s>",
                                    command.getName(), commandIdByName, channelId
                            )
                    ))
                    .queue();
        }
        if (mentionType == MentionType.GUILD_CHANNEL) {
            Guild guild = BotMain.getInstance().getJda().getGuildById(guildId);
            if (guild == null) return;
            guild.getChannels(true).stream()
                    .filter(guildChannel -> guildChannel.getId().equalsIgnoreCase(channelId)).findFirst()
                    .ifPresent(guildChannel -> {
                        if (guildChannel instanceof MessageChannel messageChannel) {
                            messageChannel.sendMessage(String.format(
                                    "<@%s> вам снова доступна </%s:%s>",
                                    user.getId(), command.getName(), commandIdByName
                            )).queue();
                        }
                    });

        }
    }

    private MentionManager() {
        for (ICommand command : SlashCommandsManager.getInstance().getCommands()) {
            if (command instanceof Mentionable) {
                mentionableCommands.add(command);
            }
        }
        mentionableCommands.forEach(this::runScheduleIfNotExist);
    }

    public static synchronized MentionManager getInstance() {
        if (instance == null) {
            instance = new MentionManager();
        }
        return instance;
    }
}