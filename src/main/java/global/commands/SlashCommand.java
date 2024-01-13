package global.commands;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.List;

/**
 * @author t.me/GreenL1nk
 * 12.01.2024
 */
public abstract class SlashCommand implements ICommand {
    protected final List<OptionData> options = new ArrayList<>();

    abstract public void execute(SlashCommandInteractionEvent event);

    /**
     * Override this function to add command aliases;
     */
    @Override
    public void updateAliases() {

    }

    /**
     * Override this function to add your specific authorized roles ex:
     * roles.add(jda.getGuildById().getRoleById("myRoleID"));
     */
    @Override
    public void updateRoles(JDA jda) {

    }

    /**
     * Override this function to add your specific authorized channels ex:
     * channels.add(jda.getChannelByID("myChannelID"));
     */
    @Override
    public void updateChannels(JDA jda) {

    }

    @Override
    public abstract String getName();

    @Override
    public abstract String getDescription();

    /**
     * Use this function to add options to your commands.
     */
    public void updateOptions() {

    }

    public List<OptionData> getOptions() {
        return options;
    }
}
