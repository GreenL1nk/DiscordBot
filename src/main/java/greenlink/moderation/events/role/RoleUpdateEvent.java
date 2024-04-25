package greenlink.moderation.events.role;

import global.BotMain;
import greenlink.moderation.EventLogger;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class RoleUpdateEvent extends ListenerAdapter implements EventLogger {
    @Override
    public MessageEmbed embedData(String... payload) {
        EmbedBuilder embedBuilder = new EmbedBuilder();

        embedBuilder.setDescription(String.format("""
                            <@%s> обновил роль <@&%s> \s
                            *Изменения:* %s
                            """, payload[0], payload[1], getChanges(payload[2])));

        return embedBuilder.build();
    }

    @Override
    public void onGuildAuditLogEntryCreate(@NotNull GuildAuditLogEntryCreateEvent event) {
//        BotMain.logger.info(String.valueOf(event.getEntry()));
//        BotMain.logger.info(String.valueOf(event.getEntry().getChanges()));
//        String maker = event.getEntry().getUserId();
//        String target = event.getEntry().getTargetId();
//        BotMain.logger.info(event.getEntry().getUserId());

        String maker = event.getEntry().getUserId();
        String target = event.getEntry().getTargetId();

        if (event.getEntry().getType() == ActionType.ROLE_UPDATE) {
            sendLog(embedData(maker, target, event.getEntry().getChanges().toString()));
        }
    }
}
