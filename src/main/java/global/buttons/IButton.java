package global.buttons;

import global.config.Config;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

/**
 * @author t.me/GreenL1nk
 * 15.01.2024
 */
public interface IButton {

    void onButtonInteraction(ButtonInteractionEvent event);
    String getButtonID();

    default boolean memberCanPerformIfVoice(Member member, ButtonInteractionEvent event) {
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

    default boolean memberCanPerform(Member member, ButtonInteractionEvent event) {
        Guild guild = event.getGuild();
        if (guild == null) return false;
        if (member == null) return false;

        return event.getGuild().getIdLong() == member.getGuild().getIdLong();
    }

    default boolean isAdmin(Member member, ButtonInteractionEvent event) {
        boolean canPerform = memberCanPerform(member, event);
        Guild guild = event.getGuild();
        if (guild != null) {
            Role role = guild.getRoleById(Config.getInstance().getAdminRoleId());
            return member.getRoles().contains(role) && canPerform;
        }
        return false;
    }
}
