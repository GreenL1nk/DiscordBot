package greenlink.economy.commands;

import global.commands.SlashCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

/**
 * @author t.me/GreenL1nk
 * 23.01.2024
 */
public class RobCommand extends SlashCommand {
    @Override
    public void execute(SlashCommandInteractionEvent event) {

    }

    @Override
    public void updateOptions() {
        options.add(new OptionData(OptionType.USER, "пользователь", "У кого будем красть?", true));
    }

    @Override
    public String getName() {
        return "rob";
    }

    @Override
    public String getDescription() {
        return "Попытка ограбить пользователя";
    }
}
