package greenlink.moderation.events.role;

import greenlink.moderation.EventLogger;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class RoleCreateEvent extends ListenerAdapter implements EventLogger {
    @Override
    public MessageEmbed embedData(String... payload) {
        EmbedBuilder embedBuilder = new EmbedBuilder();

        embedBuilder.setDescription(String.format("""
                            <@%s> создал роль <@&%s> \s
                            *Изменения:* %s
                            """, payload[0], payload[1], getChanges(payload[2])));

        return embedBuilder.build();
    }

    @Override
    public void onGuildAuditLogEntryCreate(@NotNull GuildAuditLogEntryCreateEvent event) {
        String maker = event.getEntry().getUserId();
        String target = event.getEntry().getTargetId();

        if (event.getEntry().getType() == ActionType.ROLE_CREATE) {
            sendLog(embedData(maker, target, event.getEntry().getChanges().toString()));
        }
    }
}
