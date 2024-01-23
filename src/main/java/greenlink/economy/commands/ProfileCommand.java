package greenlink.economy.commands;

import global.commands.SlashCommand;
import global.commands.SlashCommandsManager;
import global.config.Config;
import global.utils.Utils;
import greenlink.economy.EconomyManager;
import greenlink.economy.EconomyUser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
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
 * 19.01.2024
 */
public class ProfileCommand extends SlashCommand {

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        Guild guild = event.getGuild();
        if (member == null) return;
        if (guild == null) return;

        EconomyUser economyUser;
        MessageEmbed messageEmbed;
        if (event.getOptions().isEmpty()) {
            economyUser = EconomyManager.getInstance().getEconomyUser(member.getIdLong());
            messageEmbed = getEmbedBuilder(member, economyUser);
        }
        else {
            Member argMember = event.getOptionsByType(OptionType.USER).get(0).getAsMember();
            if (argMember == null) {
                event.deferReply(true).addContent("Нет такого пользователя").queue();
                return;
            }
            economyUser = EconomyManager.getInstance().getEconomyUser(argMember.getIdLong());
            messageEmbed = getEmbedBuilder(argMember, economyUser);
        }
        economyUser.addCoins(1);
        economyUser.addXp(1);
        event.deferReply().addEmbeds(messageEmbed).queue();
    }

    @NotNull
    private MessageEmbed getEmbedBuilder(Member member, EconomyUser economyUser) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.decode("#a48ea2"));
        embedBuilder.setThumbnail(member.getUser().getAvatarUrl());

        embedBuilder.addField(
                "",
                String.format("**" + Config.getInstance().getInfoIcon() + "Баланс пользователя **" + "<@%d>", economyUser.getUuid()),
                false
                );

        embedBuilder.addField(
                "",
                String.format(
                        """
                                %s**Наличные**: %d %s
                                %s**В банке**: %d %s
                                %s**Общий баланс**: %d %s""",
                        Config.getInstance().getCashIcon(), economyUser.getCashBalance(), Config.getInstance().getCoinIcon(),
                        Config.getInstance().getBankIcon(), economyUser.getBankBalance(), Config.getInstance().getCoinIcon(),
                        Config.getInstance().getTotalCoinsIcon(), economyUser.getTotalBalance(), Config.getInstance().getCoinIcon()
                ),
                false
        );

        embedBuilder.addField(
                "",
                String.format(
                        "**%sУровень:** %d `[%d/%d]`",
                        Config.getInstance().getLevelIcon(), economyUser.getCurrentLevel(),
                        economyUser.getCurrentXP(), economyUser.calculateExpToNextLevel()
                ),
                false
        );

        Long workId = SlashCommandsManager.getInstance().getCommandByName("work");
        Long timelyId = SlashCommandsManager.getInstance().getCommandByName("timely");
        Long dailyId = SlashCommandsManager.getInstance().getCommandByName("daily");
        Long weeklyId = SlashCommandsManager.getInstance().getCommandByName("weekly");
        Long monthlyId = SlashCommandsManager.getInstance().getCommandByName("monthly");
        Long robId = SlashCommandsManager.getInstance().getCommandByName("rob");

        String work = economyUser.getUserCooldown().canWork() ?
                String.format("* </work:%d> - доступна", workId) :
                String.format("* </work:%d> - будет доступна через `%s`",
                        workId, Utils.formatTime(System.currentTimeMillis() - economyUser.getUserCooldown().getWorkLastTime()));

        String timely = economyUser.getUserCooldown().canTimely() ?
                String.format("* </timely:%d> - доступна", timelyId) :
                String.format("* </timely:%d> - будет доступна через `%s`",
                        timelyId, Utils.formatTime(System.currentTimeMillis() - economyUser.getUserCooldown().getTimelyLastTime()));

        String daily = economyUser.getUserCooldown().canDaily() ?
                String.format("* </daily:%d> - доступна", dailyId) :
                String.format("* </daily:%d> - будет доступна через `%s`",
                        dailyId, Utils.formatTime(System.currentTimeMillis() - economyUser.getUserCooldown().getDailyLastTime()));

        String weekly = economyUser.getUserCooldown().canWeekly() ?
                String.format("* </weekly:%d> - доступна", weeklyId) :
                String.format("* </weekly:%d> - будет доступна через `%s`",
                        weeklyId, Utils.formatTime(System.currentTimeMillis() - economyUser.getUserCooldown().getWeeklyLastTime()));

        String monthly = economyUser.getUserCooldown().canMonthly() ?
                String.format("* </monthly:%d> - доступна", monthlyId) :
                String.format("* </monthly:%d> - будет доступна через `%s`",
                        monthlyId, Utils.formatTime(System.currentTimeMillis() - economyUser.getUserCooldown().getMonthlyLastTime()));

        String rob = economyUser.getUserCooldown().canRob() ?
                String.format("* </rob:%d> - доступна", robId) :
                String.format("* </rob:%d> - будет доступна через `%s`",
                        robId, Utils.formatTime(System.currentTimeMillis() - economyUser.getUserCooldown().getRobLastTime()));

        embedBuilder.addField(
          "Команды",
          work + "\n" +
                  timely + "\n" +
                  daily + "\n" +
                  weekly + "\n" +
                  monthly + "\n" +
                  rob + "\n",
          false
        );

        embedBuilder.setTimestamp(Instant.now());
        return embedBuilder.build();
    }

    @Override
    public void updateOptions() {
        options.add(new OptionData(OptionType.USER, "member", "Пользователь", false));
    }

    @Override
    public String getName() {
        return "profile";
    }

    @Override
    public String getDescription() {
        return "Показывает профиль пользователя";
    }

}
