package greenlink.economy.leaderboards;

import global.BotMain;
import global.commands.SlashCommand;
import global.config.Config;
import greenlink.economy.EconomyManager;
import greenlink.economy.EconomyUser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;

import java.awt.*;
import java.time.Instant;
import java.util.List;
import java.util.*;

/**
 * @author t.me/GreenL1nk
 * 23.01.2024
 */
public class LeaderBoardCommand extends SlashCommand {

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        if (member == null) return;
        if (event.getGuild() == null) return;
        if (!memberCanPerform(member, event)) return;

        LeaderBoardType leaderBoardType = LeaderBoardType.BALANCE;
        event.deferReply().queue(reply -> {
            MessageEmbed embedBuilder = getEmbedBuilder(member, leaderBoardType, 1);
            Collection<ActionRow> actionRows = new ArrayList<>();
            actionRows.add(getMenu(leaderBoardType));
            Collection<ActionRow> buttons = getButtons(leaderBoardType, 1);
            actionRows.addAll(buttons);
            reply.editOriginalEmbeds(embedBuilder)
                    .setComponents(actionRows).queue();
        });

    }

    public static MessageEmbed getEmbedBuilder(Member member, LeaderBoardType leaderBoardType, int page) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.decode("#a48ea2"));
        embedBuilder.setThumbnail(member.getGuild().getIconUrl());

        List<String> strings = getSortedBoard(leaderBoardType, member, page);
        embedBuilder.setTitle("Список лидеров по " + leaderBoardType.getName() + leaderBoardType.getIcon());
        embedBuilder.addField("", String.join("\n", strings), false);

        embedBuilder.setFooter("Страница " + page + " из " + getPageCount(leaderBoardType));
        embedBuilder.setTimestamp(Instant.now());
        return embedBuilder.build();
    }

    public static List<String> getSortedBoard(LeaderBoardType leaderBoardType, Member member, int page) {
        List<String> users = new ArrayList<>();

        try {
            List<EconomyUser> sortedUserByBoardType = EconomyManager.getInstance().getUserTopByPage(leaderBoardType, page);
            EconomyUser currentMember = EconomyManager.getInstance().getEconomyUser(member.getUser());

            if (currentMember != null) {
                users.add(0, String.format("<@%s>, ваша позиция в этом топе: #**%d**\n", member.getIdLong(), currentMember.getCurrentTop(leaderBoardType)));
            }

            for (int i = 0; i < 5; i++) {
                EconomyUser economyUser = sortedUserByBoardType.get(i);
                users.add(formatUserData((page - 1) * 5 + i + 1, economyUser, leaderBoardType));
            }
        } catch (Exception e) {
            BotMain.logger.error("", e);
        }

        return users;
    }

    public static int getTotalUsersCount(LeaderBoardType leaderBoardType) {
        try {
            return EconomyManager.getInstance().getUserCount(leaderBoardType);
        } catch (Exception e) {
            BotMain.logger.error("", e);
            return 0;
        }
    }

    public static int getPageCount(LeaderBoardType leaderBoardType) {
        int totalUsers = getTotalUsersCount(leaderBoardType);
        int pageSize = 5;
        return (int) Math.ceil((double) totalUsers / pageSize);
    }

    private static String formatUserData(int topNumber, EconomyUser economyUser, LeaderBoardType leaderBoardType) {
        return switch (leaderBoardType) {
            case ROB -> String.format("""
                            #**%d.** <@%d>\s
                            Попыток ограблений: **%d**\s
                            Успешных: **%d** | Провальных: **%d**\s
                            Заработано: **%d** | Потеряно **%d**""",
                    topNumber,
                    economyUser.getUuid(), economyUser.getTotalRobs(), economyUser.getSuccessRobs(), economyUser.getFailedRobs(),
                    economyUser.getEarnedFromRobs(), economyUser.getLostFromRobs());
            case LEVEL -> String.format("""
                            #**%d.** <@%d>\s
                            Уровень: **%d**\s
                            Всего XP: **%d**""",
                    topNumber,
                    economyUser.getUuid(), economyUser.getCurrentLevel(), economyUser.getTotalEarnedExp());
            case MESSAGES -> String.format("""
                            #**%d.** <@%d>\s
                            Всего сообщений: **%d**""",
                    topNumber,
                    economyUser.getUuid(), economyUser.getTotalMessages());
            case BALANCE -> String.format("""
                            #**%d.** <@%d>\s
                            Всего: **%d**%s\s
                            Наличные: **%d**%s | Банк: **%d**%s""",
                    topNumber,
                    economyUser.getUuid(), economyUser.getTotalBalance(), Config.getInstance().getIcon().getCoinIcon(),
                    economyUser.getCashBalance(), Config.getInstance().getIcon().getCoinIcon(),
                    economyUser.getBankBalance(), Config.getInstance().getIcon().getCoinIcon());
            case VOICE -> String.format("""
                            #**%d.** <@%d>\s
                            Проведено в голосовом чате: **%s**""",
                    topNumber,
                    economyUser.getUuid(), economyUser.getFormatVoiceTime());
        };
    }

    public static Collection<ActionRow> getButtons(LeaderBoardType leaderBoardType, int currentPage) {
        int pages = getPageCount(leaderBoardType);
        List<Button> buttons = new ArrayList<>();

        buttons.add(Button.of(ButtonStyle.SECONDARY, "lbback-" + (currentPage - 1) + "-" + leaderBoardType.name(), "", Emoji.fromUnicode("⬅️")).withDisabled(currentPage < 2));

        buttons.add(Button.of(ButtonStyle.SECONDARY, "lbchoosepage-" + leaderBoardType.name(), "Перейти к странице").withDisabled(pages < 2));

        buttons.add(Button.of(ButtonStyle.SECONDARY, "lbuserpage-" + leaderBoardType.name(), "Перейти к своей").withDisabled(pages < 2));

        buttons.add(Button.of(ButtonStyle.SECONDARY, "lbnext-" + (currentPage + 1) + "-" + leaderBoardType.name(), "", Emoji.fromUnicode("➡️")).withDisabled(pages < 2 || pages == currentPage));

        buttons.add(Button.of(ButtonStyle.SECONDARY, "lbdelete", "", Emoji.fromUnicode("❌")).asEnabled());
        return Collections.singleton(ActionRow.of(buttons));
    }

    public static ActionRow getMenu(LeaderBoardType leaderBoardType) {
        SelectOption balance = SelectOption.of("По " + LeaderBoardType.BALANCE.getName(), LeaderBoardType.BALANCE.name())
                .withEmoji(Emoji.fromUnicode(LeaderBoardType.BALANCE.getIcon()));
        SelectOption voice = SelectOption.of("По " + LeaderBoardType.VOICE.getName(), LeaderBoardType.VOICE.name())
                .withEmoji(Emoji.fromUnicode(LeaderBoardType.VOICE.getIcon()));
        SelectOption rob = SelectOption.of("По " + LeaderBoardType.ROB.getName(), LeaderBoardType.ROB.name())
                .withEmoji(Emoji.fromUnicode(LeaderBoardType.ROB.getIcon()));
        SelectOption messages = SelectOption.of("По " + LeaderBoardType.MESSAGES.getName(), LeaderBoardType.MESSAGES.name())
                .withEmoji(Emoji.fromUnicode(LeaderBoardType.MESSAGES.getIcon()));
        SelectOption level = SelectOption.of("По " + LeaderBoardType.LEVEL.getName(), LeaderBoardType.LEVEL.name())
                .withEmoji(Emoji.fromUnicode(LeaderBoardType.LEVEL.getIcon()));

        StringSelectMenu.Builder dropdown = StringSelectMenu.create("chooseboard").addOptions(level, rob, messages, voice, balance).setDefaultOptions(balance);
        if (leaderBoardType == LeaderBoardType.VOICE) dropdown = StringSelectMenu.create("chooseboard").addOptions(level, rob, messages, voice, balance).setDefaultOptions(voice);
        if (leaderBoardType == LeaderBoardType.ROB) dropdown = StringSelectMenu.create("chooseboard").addOptions(level, rob, messages, voice, balance).setDefaultOptions(rob);
        if (leaderBoardType == LeaderBoardType.MESSAGES) dropdown = StringSelectMenu.create("chooseboard").addOptions(level, rob, messages, voice, balance).setDefaultOptions(messages);
        if (leaderBoardType == LeaderBoardType.LEVEL) dropdown = StringSelectMenu.create("chooseboard").addOptions(level, rob, messages, voice, balance).setDefaultOptions(level);

        return ActionRow.of(dropdown.build());
    }

    @Override
    public String getName() {
        return "leaderboard";
    }

    @Override
    public String getDescription() {
        return "Топы пользователей";
    }
}
