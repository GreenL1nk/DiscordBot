package greenlink.economy.commands;

import global.commands.SlashCommand;
import global.config.Config;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * @author t.me/GreenL1nk
 * 23.01.2024
 */
public class TimelyCommand extends SlashCommand {
    @Override
    public void execute(SlashCommandInteractionEvent event) {

    }

    @Override
    public String getName() {
        return "timely";
    }

    @Override
    public String getDescription() {
        return "Награда раз в " + Config.getInstance().getRobCooldown() + "м";
    }
}
