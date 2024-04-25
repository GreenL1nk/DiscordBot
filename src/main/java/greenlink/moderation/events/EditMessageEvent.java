package greenlink.moderation.events;

import greenlink.moderation.EventLogger;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class EditMessageEvent extends ListenerAdapter implements EventLogger {
    @Override
    public MessageEmbed embedData(String... payload) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setDescription(String.format("""
                            <@%s> изменил сообщение\s
                            Канал: <#%s>
                            Заменил на: `%s`
                            """, payload[0], payload[1], payload[2]));

        return embedBuilder.build();
    }
    @Override
    public void onMessageUpdate(@NotNull MessageUpdateEvent event) {
        sendLog(embedData(event.getAuthor().getId(), event.getChannel().getId(), event.getMessage().getContentRaw()));
    }
}
