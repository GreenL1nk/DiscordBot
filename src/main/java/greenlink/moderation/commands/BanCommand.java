package greenlink.moderation.commands;

import global.logger.EventLogger;
import global.commands.SlashCommand;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.concurrent.TimeUnit;

/**
 * @author t.me/GreenL1nk
 * 22.03.2024
 */
public class BanCommand extends SlashCommand implements EventLogger {
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        if (!memberCanPerform(member, event)) return;
        Guild guild = event.getGuild();
        if (guild == null) return;
        if (!isAdmin(member, event)) return;

        Member toBan = event.getOptionsByType(OptionType.USER).get(0).getAsMember();
        int time = event.getOptionsByType(OptionType.USER).get(1).getAsInt();
        String reason = event.getOptionsByType(OptionType.USER).get(2).getAsString();
        if (toBan == null) return;

//        sendLog();

        guild.ban(toBan, 1, TimeUnit.DAYS).queue();
    }

    @Override
    public void updateOptions() {
        options.add(new OptionData(OptionType.USER, "member", "Пользователь", true));
        options.add(new OptionData(OptionType.INTEGER, "time", "Время в минутах", false));
        options.add(new OptionData(OptionType.STRING, "reason", "Причина", false));
    }

    @Override
    public String getName() {
        return "ban";
    }

    @Override
    public String getDescription() {
        return "Банит пользователя на указанное время";
    }

    @Override
    public void EmbedData(String... info) {

    }
}
