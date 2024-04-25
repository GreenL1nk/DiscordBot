package greenlink.moderation;

import global.BotMain;
import global.config.Config;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.util.ArrayList;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author t.me/GreenL1nk
 * 22.03.2024
 */
public interface EventLogger {

    MessageEmbed embedData(String ... payload);
    String pattern = "(?<=AuditLogChange:)([^{,}]+)";
    Pattern regex = Pattern.compile(pattern);

    default void sendLog(MessageEmbed embed) {

        TextChannel logChannel = null;

        Optional<Guild> guild = BotMain.getInstance().getJda().getGuilds().stream().filter(g -> g.getTextChannelById(Config.getInstance().getLogChannelId()) != null).findFirst();
        if (guild.isPresent()) logChannel = guild.get().getTextChannelById(Config.getInstance().getLogChannelId());

        if (logChannel == null) {
            BotMain.logger.error("Лог сообщение не может быть отправлено, из-за неверного id канала в конфиге!");
            return;
        }

        logChannel.sendMessage(MessageCreateData.fromEmbeds(embed)).queue();
    }

    default String getChanges(String input) {
        Matcher matcher = regex.matcher(input);

        StringBuilder changes = new StringBuilder();
        while (matcher.find()) {
            changes.append("\n* `").append(matcher.group()).append("`");
        }
        return changes.toString();
    }
}
