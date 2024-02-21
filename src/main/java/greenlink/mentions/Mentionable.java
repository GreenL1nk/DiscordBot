package greenlink.mentions;

import global.commands.ICommand;
import global.commands.SlashCommandsManager;
import global.config.configs.ConfigImpl;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;

/**
 * @author t.me/GreenL1nk
 * 28.01.2024
 */
public interface Mentionable {

    ConfigImpl config();

    default ActionRow getActionRow(String command) {
        schedule(command);
        StringSelectMenu.Builder dropdown = StringSelectMenu.create("mention-" + command).setPlaceholder("Выберите способ отправки уведомления");
        for (MentionType value : MentionType.values()) {
            dropdown.addOptions(SelectOption.of(value.getMessage(), value.name().toLowerCase()));
        }
        return ActionRow.of(dropdown.build());
    }

    default void schedule(String command) {
        ICommand commandByName = SlashCommandsManager.getInstance().getCommandByName(command);
        if (commandByName == null) return;
        MentionManager.getInstance().runScheduleIfNotExist(commandByName);
    }

}
