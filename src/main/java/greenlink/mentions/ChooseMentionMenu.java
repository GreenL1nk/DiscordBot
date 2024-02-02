package greenlink.mentions;

import global.commands.ICommand;
import global.commands.SlashCommandsManager;
import global.selectmenus.ArgSelectMenu;
import greenlink.databse.DatabaseConnector;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;

/**
 * @author t.me/GreenL1nk
 * 29.01.2024
 */
public class ChooseMentionMenu extends ArgSelectMenu {

    @Override
    public void onSelectMenuInteraction(StringSelectInteractionEvent event) {
        Member member = event.getMember();
        if (member == null) return;
        if (event.getGuild() == null) return;
        if (!memberCanPerform(member, event)) return;
        event.deferEdit().queue();
        if (event.getValues().isEmpty()) return;

        MentionType mentionType = MentionType.valueOf(event.getValues().get(0).toUpperCase());
        saveMention(SlashCommandsManager.getInstance().getCommandByName(getArgs(event)[0]), member.getUser(), event.getGuild(), event.getChannel(), mentionType);
    }

    void saveMention(ICommand command, User user, Guild guild, Channel channel, MentionType mentionType) {
        DatabaseConnector.getInstance().saveMentionUser(command, user, guild, channel, mentionType);
        MentionManager.getInstance().runScheduleIfNotExist(command);
    }

    @Override
    public String getMenuID() {
        return "mention-";
    }
}
