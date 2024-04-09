package global.logger;

import global.BotMain;
import global.config.Config;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

/**
 * @author t.me/GreenL1nk
 * 22.03.2024
 */
public interface EventLogger {

    TextChannel logChannel = BotMain.getInstance().getJda().getTextChannelById(Config.getInstance().logChannelId);

    void EmbedData(String ... info);

    default void sendLog(MessageEmbed embed) {

        if (logChannel == null) {
            BotMain.logger.error("Лог сообщение не может быть отправлено, из-за неверного id канала в конфиге!");
            return;
        }
        logChannel.sendMessage(MessageCreateData.fromEmbeds(embed)).queue();
    }
}
