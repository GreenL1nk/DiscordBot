package global.commands;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.util.ArrayList;
import java.util.List;

/**
 * @author t.me/GreenL1nk
 * 15.01.2024
 */
public class SlashCommandsManager {
    private static SlashCommandsManager instance;
    private final List<ICommand> commandMap = new ArrayList<>();

    private SlashCommandsManager() {
    }

    public void updateCommands(JDA jda) {
        List<CommandData> commands = new ArrayList<>();
        getCommands().forEach((command) -> {
            if (command instanceof SlashCommand slashCommand) {
                slashCommand.updateOptions();
                commands.add(Commands.slash(command.getName(), slashCommand.getDescription()).addOptions(slashCommand.getOptions()));
            }
            command.updateAliases();
            command.updateChannels(jda);
            command.updateRoles(jda);
        });
        jda.updateCommands().addCommands(commands).queue();
    }

    public void addCommands(ICommand ... iCommand) {
        getCommands().addAll(List.of(iCommand));
    }

    public List<ICommand> getCommands() { return commandMap; }

    public static synchronized SlashCommandsManager getInstance() {
        if (instance == null) {
            instance = new SlashCommandsManager();
        }
        return instance;
    }
}