package greenlink.moderation.events;

import greenlink.moderation.EventLogger;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class DeleteMessageEvent extends ListenerAdapter implements EventLogger {
    @Override
    public MessageEmbed embedData(String... payload) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        if (payload.length == 1) {
            embedBuilder.setTitle("Удалённого сообщения не было в кэше");
            embedBuilder.setDescription(
                    String.format("""
                            Канал: <#%s>
                            """, payload[0]));
        }
        else {
            embedBuilder.setDescription(String.format("""
                            <@%s> удалил сообщение\s
                            Канал: <#%s>
                            """, payload[0], payload[1]));
        }

        return embedBuilder.build();
    }

    @Override
    public void onMessageDelete(@NotNull MessageDeleteEvent event) {
        sendLog(embedData(event.getChannel().getId()));
    }
}
