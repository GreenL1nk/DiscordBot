package global.commands;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author t.me/GreenL1nk
 * 15.01.2024
 */
public class SlashCommandsManager {
    private static SlashCommandsManager instance;
    private final List<ICommand> commandMap = new ArrayList<>();
    private List<Command> guildCommandsMap = new ArrayList<>();

    private SlashCommandsManager() {
    }

    public void updateCommands(JDA jda) {
        List<CommandData> commands = new ArrayList<>();
        getCommands().forEach((command) -> {
            if (command instanceof SlashCommand slashCommand) {
                slashCommand.updateOptions();
                commands.add(Commands.slash(command.getName(), slashCommand.getDescription()).addOptions(slashCommand.getOptions()));
            }
            command.updateChannels(jda);
            command.updateRoles(jda);
        });
        jda.updateCommands().addCommands(commands).queue(c -> {
            guildCommandsMap = c;
        });
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

    @Nullable
    public Long getCommandIdByName(String name) {
        Command command = this.guildCommandsMap.stream().filter(c -> c.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
        if (command == null) return null;
        return command.getIdLong();
    }

    @Nullable
    public ICommand getCommandByName(String name) {
        return commandMap.stream().filter(command -> command.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }
}