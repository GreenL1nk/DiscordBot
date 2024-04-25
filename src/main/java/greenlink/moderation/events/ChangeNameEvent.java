package greenlink.moderation.events;

import greenlink.moderation.EventLogger;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateNicknameEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class ChangeNameEvent extends ListenerAdapter implements EventLogger {
    @Override
    public MessageEmbed embedData(String... payload) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setDescription(String.format("""
                            у <@%s> изменился никнейм\s
                            с `%s` -> `%s`
                            """, payload[0], payload[1], payload[2]));

        return embedBuilder.build();
    }

    @Override
    public void onGuildMemberUpdateNickname(@NotNull GuildMemberUpdateNicknameEvent event) {
        sendLog(embedData(event.getMember().getId(), event.getOldNickname(), event.getNewNickname()));
    }
}
