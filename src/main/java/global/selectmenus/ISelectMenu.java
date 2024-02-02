package global.selectmenus;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;

/**
 * @author t.me/GreenL1nk
 * 17.01.2024
 */
public interface ISelectMenu {

    void onSelectMenuInteraction(StringSelectInteractionEvent event);
    String getMenuID();

    default boolean memberCanPerformIfVoice(Member member, StringSelectInteractionEvent event) {
        Guild guild = event.getGuild();
        if (guild == null) return false;
        if (member == null) return false;
        GuildVoiceState botMember = guild.getSelfMember().getVoiceState();
        if (botMember == null) return false;
        if (!botMember.inAudioChannel()) return false;

        GuildVoiceState userVoiceState = member.getVoiceState();
        if (userVoiceState == null) return false;
        if (!userVoiceState.inAudioChannel()) return false;
        AudioChannelUnion userChannel = userVoiceState.getChannel();
        if (userChannel == null) return false;
        return userChannel.equals(botMember.getChannel());
    }

    default boolean memberCanPerform(Member member, StringSelectInteractionEvent event) {
        Guild guild = event.getGuild();
        if (guild == null) return false;
        if (member == null) return false;

        return event.getGuild().getIdLong() == member.getGuild().getIdLong();
    }
}
