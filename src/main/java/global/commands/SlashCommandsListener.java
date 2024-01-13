package global.commands;

import global.BotMain;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class SlashCommandsListener extends ListenerAdapter {

    private final BotMain botMain;

    public SlashCommandsListener(BotMain botMain) {
        this.botMain = botMain;
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        botMain.getCommands().stream()
                .filter(command -> command.getName().equalsIgnoreCase(event.getName()))
                .findFirst()
                .ifPresent((c) -> {
                    if (c instanceof SlashCommand command) {
                        if (!command.hasChannel(event.getChannel())) {
                            event.reply("Эта команда не может быть использована в этом канале.").setEphemeral(true).queue();
                            return;
                        }

                        if (command.getRoles() != null && !command.getRoles().isEmpty()) {
                            if (event.getMember() == null) return;
                            if (command.memberHasAnyRole(event.getMember())) command.execute(event);
                            return;
                        }
                        command.execute(event);
                    }
                });
    }

}