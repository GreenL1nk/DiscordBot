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
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.time.Instant;
import java.util.List;
import java.util.*;

/**
 * @author t.me/GreenL1nk
 * 23.01.2024
 */
public class LeaderBoardCommand extends SlashCommand {

    public static final HashMap<LeaderBoardType, Integer> countPage = new HashMap<>();

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

    @NotNull
    public static MessageEmbed getEmbedBuilder(Member member, LeaderBoardType leaderBoardType, int page) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.decode("#a48ea2"));
        embedBuilder.setThumbnail(member.getGuild().getIconUrl());

        ArrayList<String> strings = getSortedBoard(leaderBoardType, member).get(page);
        embedBuilder.setTitle("Список лидеров по " + leaderBoardType.getName() + leaderBoardType.getIcon());
        embedBuilder.addField("", String.join("\n", strings), false);

        embedBuilder.setFooter("Страница " + page + " из " + countPage.get(leaderBoardType));
        embedBuilder.setTimestamp(Instant.now());
        return embedBuilder.build();
    }

    public static HashMap<Integer, ArrayList<String>> getSortedBoard(LeaderBoardType leaderBoardType, Member member) {
        int page = 1;
        int topNumber = 1;
        HashMap<Integer, ArrayList<String>> pages = new HashMap<>();
        ArrayList<String> users = new ArrayList<>();
        try {
            ArrayList<EconomyUser> sortedUserByBoardType = EconomyManager.getInstance().getUserTop(leaderBoardType);
            EconomyUser currentMember = sortedUserByBoardType.stream().filter(economyUser -> economyUser.getUuid() == member.getIdLong()).findFirst().orElse(null);
            for (EconomyUser economyUser : sortedUserByBoardType) {
                if (leaderBoardType == LeaderBoardType.ROB) {
                    users.add(String.format("""
                            #**%d.** <@%d>\s
                            Попыток ограблений: **%d**\s
                            Успешных: **%d** | Провальных: **%d**\s
                            Заработано: **%d** | Потеряно **%d**""",
                            topNumber,
                            economyUser.getUuid() , economyUser.getTotalRobs(), economyUser.getSuccessRobs(), economyUser.getFailedRobs(),
                            economyUser.getEarnedFromRobs(), economyUser.getLostFromRobs()));
                }
                if (leaderBoardType == LeaderBoardType.LEVEL) {
                    users.add(String.format("""
                            #**%d.** <@%d>\s
                            Уровень: **%d**\s
                            Всего XP: **%d**""",
                            topNumber,
                            economyUser.getUuid() , economyUser.getCurrentLevel(), economyUser.getTotalEarnedExp()));
                }
                if (leaderBoardType == LeaderBoardType.MESSAGES) {
                    users.add(String.format("""
                            #**%d.** <@%d>\s
                            Всего сообщений: **%d**""",
                            topNumber,
                            economyUser.getUuid() , economyUser.getTotalMessages()));
                }
                if (leaderBoardType == LeaderBoardType.BALANCE) {
                    users.add(String.format("""
                            #**%d.** <@%d>\s
                            Всего: **%d**%s\s
                            Наличные: **%d**%s | Банк: **%d**%s""",
                            topNumber,
                            economyUser.getUuid() , economyUser.getTotalBalance(), Config.getInstance().getIcon().getCoinIcon(),
                            economyUser.getCashBalance(), Config.getInstance().getIcon().getCoinIcon(),
                            economyUser.getBankBalance(), Config.getInstance().getIcon().getCoinIcon()));

                }
                if (leaderBoardType == LeaderBoardType.VOICE) {
                    users.add(String.format("""
                            #**%d.** <@%d>\s
                            Проведено в голосовом чате: **%s**""",
                            topNumber,
                            economyUser.getUuid() , economyUser.getFormatVoiceTime()));
                }
                if (users.size() == 5 || users.size() == sortedUserByBoardType.size()) {
                    users.add(0, String.format("<@%s>, ваша позиция в этом топе: #**%d**\n", member.getId(), sortedUserByBoardType.indexOf(currentMember) + 1));
                    pages.put(page, users);
                    users = new ArrayList<>();
                    page++;
                }
                topNumber++;
            }
        }
        catch (Exception e) {
            BotMain.logger.error("", e);
        }
        countPage.put(leaderBoardType, pages.size());
        return pages;
    }

    public static Collection<ActionRow> getButtons(LeaderBoardType leaderBoardType, int currentPage) {
        Integer pages = countPage.get(leaderBoardType);
        List<Button> buttons = new ArrayList<>();

        buttons.add(Button.of(ButtonStyle.SECONDARY, "lbback-" + (currentPage - 1) + "-" + leaderBoardType.name(), "", Emoji.fromUnicode("⬅️")).withDisabled(currentPage < 2));

        buttons.add(Button.of(ButtonStyle.SECONDARY, "lbchoosepage-" + leaderBoardType.name(), "Перейти к странице").withDisabled(pages < 2));

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
