package global.commands;

import global.config.Config;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * @author t.me/GreenL1nk
 * 12.01.2024
 */
public interface ICommand {

    List<Role> roles = new ArrayList<>();
    List<Channel> channels = new ArrayList<>();


    /**
    Override this function the set the command name ex: return "play"; This command will be executed as "/play" if using SlashExecutor for example.
     */
    String getName();

    /**
     * Override this function to add your specific authorized roles ex:
     * roles.add(jda.getGuildById().getRoleById("myRoleID"));
     */
    void updateRoles(JDA jda);
    /**
     * Override this function to add your specific authorized channels ex:
     * channels.add(jda.getChannelByID("myChannelID"));
     */
    void updateChannels(JDA jda);

    String getDescription();


    default List<Channel> getChannels() {
        return channels;
    }

    default List<Role> getRoles() {
        return roles;
    }

    default boolean memberHasAnyRole(Member member) {
        return member.getRoles().stream().anyMatch(roles::contains);
    }

    default boolean hasChannel(Channel channel) {
        if (channels.isEmpty()) return true;
        return channels.contains(channel);
    }

    default boolean memberCanPerform(Member member, SlashCommandInteractionEvent event) {
        if (!isEnabled()) {
            event.deferReply(true).setContent("Эта команда временно отключена").queue();
            return false;
        }
        Guild guild = event.getGuild();
        if (guild == null) return false;
        if (member == null) return false;

        return event.getGuild().getIdLong() == member.getGuild().getIdLong();
    }

    default boolean isEnabled() {
        Boolean valueFromJson = (Boolean) Config.getInstance().getValueFromJson(getName());
        if (valueFromJson == null) return true;
        return valueFromJson;
    }

    default boolean isAdmin(Member member, SlashCommandInteractionEvent event) {
        boolean canPerform = memberCanPerform(member, event);
        Guild guild = event.getGuild();
        if (guild != null) {
            Role role = guild.getRoleById(Config.getInstance().getAdminRoleId());
            return member.getRoles().contains(role) && canPerform;
        }
        return false;
    }
}
