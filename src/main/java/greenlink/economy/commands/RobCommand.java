package greenlink.economy.commands;

import global.commands.SlashCommand;
import global.config.Config;
import global.config.configs.ConfigImpl;
import global.config.configs.RobConfig;
import greenlink.economy.EconomyManager;
import greenlink.economy.EconomyUser;
import greenlink.mentions.Mentionable;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.time.Instant;

/**
 * @author t.me/GreenL1nk
 * 23.01.2024
 */
public class RobCommand extends SlashCommand implements Mentionable {
    RobConfig config = (RobConfig) config();
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        if (member == null) return;
        if (event.getGuild() == null) return;
        if (!memberCanPerform(member, event)) return;
        if (event.getOptions().isEmpty()) {
            event.reply("Необходимо указать пользователя").setEphemeral(true).queue();
            return;
        }
        EconomyUser economyUser = EconomyManager.getInstance().getEconomyUser(member.getIdLong());
        if (!economyUser.getUserCooldown().canRob()) {
            event.deferReply(true).setContent(String.format("**:x: <@%d>, попытка будет доступна <t:%d:R>.**",
                    member.getIdLong(),
                    economyUser.getUserCooldown().getRobEpochTimeCD()
            )).queue();
            return;
        }
        if (economyUser.getCashBalance() <= 0 || economyUser.getCashBalance() < config.getMinValue()) {
            event.reply(String.format(":x: <@%d> у вас нет достаточного кол-ва наличных средств", member.getIdLong())).setEphemeral(true).queue();
            return;
        }
        Member argMember = event.getOptionsByType(OptionType.USER).get(0).getAsMember();
        if (argMember == null) {
            event.deferReply(true).addContent("Нет такого пользователя").queue();
            return;
        }
        if (argMember == member) {
            event.deferReply(true).addContent("Нельзя красть у себя").queue();
            return;
        }
        EconomyUser toRob = EconomyManager.getInstance().getEconomyUser(argMember.getIdLong());
        if (toRob.getCashBalance() <= 0 || toRob.getCashBalance() < config.getMinValue()) {
            event.reply(String.format(":x: У <@%d> нет достаточного кол-ва наличных средств", argMember.getIdLong())).setEphemeral(true).queue();
            return;
        }

        economyUser.getUserCooldown().setRobLastTime(System.currentTimeMillis());
        boolean isFail = config.checkFail();
        EconomyUser winner = isFail ? toRob : economyUser;
        EconomyUser looser = toRob == winner ? economyUser : toRob;

        int value = config.getRandomValue();
        if (value > looser.getCashBalance()) value = looser.getCashBalance();

        economyUser.robTry(value, !isFail);
        winner.addCoins(value);
        looser.removeCoins(value);

        event.deferReply().addEmbeds(getEmbedBuilder(member, winner, isFail, looser, value, economyUser)).addComponents(getActionRow(getName())).queue();
    }

    @NotNull
    private MessageEmbed getEmbedBuilder(Member member, EconomyUser winner, boolean isFail, EconomyUser looser, int value, EconomyUser economyUser) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.decode("#a48ea2"));
        embedBuilder.setThumbnail(member.getUser().getAvatarUrl());

        if (isFail) {
            embedBuilder.addField(
                    config.getIcon() + " **Ограбление**",
                    String.format("<@%d>, во время ограбления вы были пойманы и заплатили компенсацию пользователю <@%d>" +
                                    "\n в размере %d%s",
                            looser.getUuid(), winner.getUuid(),
                            value, Config.getInstance().getIcon().getCoinIcon()),
                    false
            );
        }
        else {
            embedBuilder.addField(
                    config.getIcon() + " **Ограбление**",
                    String.format("<@%d>, вы успешно ограбили пользователя <@%d>" +
                                    "\n на сумму %d%s",
                            winner.getUuid(), looser.getUuid(),
                            value, Config.getInstance().getIcon().getCoinIcon()),
                    false
            );
        }

        embedBuilder.addField(
                "**Следующую попытку кражи можно будет совершить**",
                String.format("<t:%d:R>", economyUser.getUserCooldown().getRobEpochTimeCD()),
                false
        );

        embedBuilder.setTimestamp(Instant.now());
        return embedBuilder.build();
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

    @Override
    public ConfigImpl config() {
        return Config.getInstance().getRob();
    }
}
