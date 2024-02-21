package greenlink.economy.commands;

import global.commands.SlashCommand;
import global.config.Config;
import global.config.configs.ConfigImpl;
import global.config.configs.DailyConfig;
import greenlink.economy.EconomyManager;
import greenlink.economy.EconomyUser;
import greenlink.mentions.Mentionable;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.time.Instant;

/**
 * @author t.me/GreenL1nk
 * 23.01.2024
 */
public class DailyCommand extends SlashCommand implements Mentionable {

    DailyConfig config = (DailyConfig) config();
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        if (member == null) return;
        if (event.getGuild() == null) return;
        if (!memberCanPerform(member, event)) return;

        EconomyUser economyUser = EconomyManager.getInstance().getEconomyUser(member.getUser());
        if (economyUser == null) {
            event.deferReply(true).setContent("Бот не может использоваться для этих целей").queue();
            return;
        }
        if (!economyUser.getUserCooldown().canDaily()) {
            event.deferReply(true).setContent(String.format("**:x: <@%d>, ежедневный бонус будет доступен <t:%d:R>.**",
                    member.getIdLong(),
                    economyUser.getUserCooldown().getDailyEpochTimeCD()
                    )).queue();
            return;
        }

        economyUser.getUserCooldown().setDailyLastTime(System.currentTimeMillis());
        int value = config.getRandomValue();

        economyUser.addCoins(value);

        String message = String.format("<@%d>, вы забрали свои %d%s", member.getIdLong(), value, Config.getInstance().getIcon().getCoinIcon());

        event.deferReply().addEmbeds(getEmbedBuilder(member, economyUser, message)).addComponents(getActionRow(getName())).queue();
    }

    @NotNull
    private MessageEmbed getEmbedBuilder(Member member, EconomyUser economyUser, String message) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.decode("#a48ea2"));
        embedBuilder.setThumbnail(member.getUser().getAvatarUrl());

        embedBuilder.addField(
                config.getIcon() + " **Ежедневный бонус**",
                message,
                false
        );

        embedBuilder.addField(
                "**Следующий бонус можно будет забрать:**",
                String.format("<t:%d:R>", economyUser.getUserCooldown().getDailyEpochTimeCD()),
                false
        );

        embedBuilder.setTimestamp(Instant.now());
        return embedBuilder.build();
    }
    @Override
    public String getName() {
        return "daily";
    }

    @Override
    public String getDescription() {
        return "Ежедневный бонус";
    }

    @Override
    public ConfigImpl config() {
        return Config.getInstance().getDaily();
    }
}
