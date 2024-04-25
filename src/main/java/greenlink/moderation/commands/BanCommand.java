package greenlink.moderation.commands;

import global.commands.SlashCommand;
import greenlink.moderation.EventLogger;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
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
        String reason = event.getOptionsByType(OptionType.USER).get(1).getAsString();
        if (toBan == null) return;

        sendLog(embedData(toBan.getId(), reason));

        guild.ban(toBan, 0, TimeUnit.SECONDS).queue();
    }

    @Override
    public void updateOptions() {
        options.add(new OptionData(OptionType.USER, "member", "Пользователь", true));
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
    public MessageEmbed embedData(String... payload) {

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.addField("",
                String.format("""
                        <@%s> был забанен\s
                        Причина: `%s`
                        """, payload[0], payload[1])
                , false);

        return embedBuilder.build();
    }
}
