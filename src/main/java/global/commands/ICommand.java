package global.commands;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.events.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * @author t.me/GreenL1nk
 * 12.01.2024
 */
public interface ICommand {

    List<String> aliases = new ArrayList<>();
    List<Role> roles = new ArrayList<>();
    List<Channel> channels = new ArrayList<>();


    /**
    Override this function the set the command name ex: return "play"; This command will be executed as "/play" if using SlashExecutor for example.
     */
    String getName();

    /**
     * Override this function to add command aliases;
     */
    void updateAliases();
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

    default List<String> getAliases() {
        return aliases;
    }

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
}
