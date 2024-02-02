package greenlink.economy.commands;

import global.commands.SlashCommand;
import global.commands.SlashCommandsManager;
import global.config.Config;
import greenlink.economy.EconomyManager;
import greenlink.economy.EconomyUser;
import greenlink.economy.leaderboards.LeaderBoardType;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.utils.FileUpload;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.util.ArrayList;

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
            messageEmbed = getEmbedBuilder(member, economyUser, false);
            event.deferReply().addEmbeds(messageEmbed).queue();
        }
        else {
            Member argMember = event.getOptionsByType(OptionType.USER).get(0).getAsMember();
            if (argMember == null) {
                event.deferReply(true).addContent("Нет такого пользователя").queue();
                return;
            }

            event.deferReply().queue();
            InteractionHook hook = event.getHook();

            economyUser = EconomyManager.getInstance().getEconomyUser(argMember.getIdLong());
            messageEmbed = getEmbedBuilder(argMember, economyUser,true);

            byte[] imageData;
            try {
                BufferedImage bufferedImage = generateRankCard(
                        argMember.getEffectiveName(), argMember.getEffectiveAvatarUrl(),
                        argMember.getOnlineStatus(), economyUser.getCurrentLevel(),
                        economyUser.getCurrentXP(), economyUser.calculateExpToNextLevel(), economyUser.getCurrentTop(LeaderBoardType.LEVEL));
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(bufferedImage, "png", baos);
                imageData = baos.toByteArray();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            hook.sendFiles(FileUpload.fromData(imageData, "profile.png")).addEmbeds(messageEmbed).queue();
        }
        economyUser.addCoins(1);
        economyUser.addXp(1);


    }

    @NotNull
    private MessageEmbed getEmbedBuilder(Member member, EconomyUser economyUser, boolean anotherMember) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.decode("#a48ea2"));
        embedBuilder.setThumbnail(member.getUser().getAvatarUrl());

        embedBuilder.addField(
                "",
                String.format("**" + Config.getInstance().getIcon().getInfoIcon() + "Баланс пользователя **" + "<@%d>", economyUser.getUuid()),
                false
                );

        embedBuilder.addField(
                "",
                String.format(
                        """
                                %s**Наличные**: %d %s
                                %s**В банке**: %d %s
                                %s**Общий баланс**: %d %s""",
                        Config.getInstance().getIcon().getCashIcon(), economyUser.getCashBalance(), Config.getInstance().getIcon().getCoinIcon(),
                        Config.getInstance().getIcon().getBankIcon(), economyUser.getBankBalance(), Config.getInstance().getIcon().getCoinIcon(),
                        Config.getInstance().getIcon().getTotalCoinsIcon(), economyUser.getTotalBalance(), Config.getInstance().getIcon().getCoinIcon()
                ),
                false
        );

        embedBuilder.addField(
                "",
                String.format(
                        "**%sУровень:** %d `[%d/%d]`",
                        Config.getInstance().getIcon().getLevelIcon(), economyUser.getCurrentLevel(),
                        economyUser.getCurrentXP(), economyUser.calculateExpToNextLevel()
                ),
                false
        );

        if (!anotherMember) {
            Long workId = SlashCommandsManager.getInstance().getCommandIdByName("work");
            Long timelyId = SlashCommandsManager.getInstance().getCommandIdByName("timely");
            Long dailyId = SlashCommandsManager.getInstance().getCommandIdByName("daily");
            Long weeklyId = SlashCommandsManager.getInstance().getCommandIdByName("weekly");
            Long monthlyId = SlashCommandsManager.getInstance().getCommandIdByName("monthly");
            Long robId = SlashCommandsManager.getInstance().getCommandIdByName("rob");

            ArrayList<String> availableCommands = new ArrayList<>();

            String work = economyUser.getUserCooldown().canWork() ?
                    String.format("* </work:%d> - доступна", workId) : null;
            if (work != null) availableCommands.add(work);

            String timely = economyUser.getUserCooldown().canTimely() ?
                    String.format("* </timely:%d> - доступна", timelyId) : null;
            if (timely != null) availableCommands.add(timely);

            String daily = economyUser.getUserCooldown().canDaily() ?
                    String.format("* </daily:%d> - доступна", dailyId) : null;
            if (daily != null) availableCommands.add(daily);

            String weekly = economyUser.getUserCooldown().canWeekly() ?
                    String.format("* </weekly:%d> - доступна", weeklyId) : null;
            if (weekly != null) availableCommands.add(weekly);

            String monthly = economyUser.getUserCooldown().canMonthly() ?
                    String.format("* </monthly:%d> - доступна", monthlyId) : null;
            if (monthly != null) availableCommands.add(monthly);

            String rob = economyUser.getUserCooldown().canRob() ?
                    String.format("* </rob:%d> - доступна", robId) : null;
            if (rob != null) availableCommands.add(rob);


            StringBuilder commandsBuilder = new StringBuilder();
            for (String command : availableCommands) {
                commandsBuilder.append(command).append("\n");
            }

            if (!availableCommands.isEmpty()) {
                embedBuilder.addField(
                        "Команды",
                        commandsBuilder.toString(),
                        false
                );
            }
        }
        else embedBuilder.setImage("attachment://profile.png");

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

    public BufferedImage generateRankCard(String username, String avatarUrl, OnlineStatus onlineStatus, int level, int currentExp, int maxExp, int top) throws IOException {
        BufferedImage image = new BufferedImage(934, 282, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();

        // Включаем антиалиасинг
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.setColor(new Color(0, 0, 0, 0));
        g.fillRect(0, 0, image.getWidth(), image.getHeight());

        BufferedImage avatar = ImageIO.read(new URL(avatarUrl));

        BufferedImage avatarMask = new BufferedImage(avatar.getWidth(), avatar.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D maskGraphics = avatarMask.createGraphics();
        maskGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        maskGraphics.setColor(Color.WHITE);
        maskGraphics.fillOval(0, 0, avatar.getWidth(), avatar.getHeight());
        maskGraphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_IN));
        maskGraphics.drawImage(avatar, 0, 0, null);
        maskGraphics.dispose();

        int avatarSize = 100;
        int avatarX = 50;
        int avatarY = (image.getHeight() - avatarSize) / 2;

        // Отображаем статус пользователя вокруг аватарки
        Color statusColor = switch (onlineStatus) {
            case ONLINE -> Color.decode("#43b581");
            case IDLE -> Color.decode("#faa61a");
            case DO_NOT_DISTURB -> Color.decode("#f04747");
            default -> Color.decode("#747f8d");
        };
        g.setColor(statusColor);

        int statusSize = avatarSize + 6; // Дополнительный размер для отступа
        int statusX = avatarX - (statusSize - avatarSize) / 2; // Рассчитываем новое положение круга статуса по X
        int statusY = avatarY - (statusSize - avatarSize) / 2; // Рассчитываем новое положение круга статуса по Y
        g.fillOval(statusX, statusY, statusSize, statusSize);

        // Задаем цвет текста и шрифт
        g.setColor(Color.WHITE);
        g.setFont(new Font("Calibri", Font.BOLD, 30)); // Используем шрифт Calibri

        // Рисуем ник пользователя
        g.drawString(username, avatarX + avatarSize + 20, avatarY + 35);


        FontMetrics fm = g.getFontMetrics();
        // Рисуем текст прогресса уровня

        // Рисуем прогресс в заполнителе
        int levelBarX = avatarX + avatarSize + 20;
        int levelBarY = avatarY + 100;
        int levelBarWidth = 640;
        int levelBarHeight = 25; // Уменьшили высоту шкалы
        int progressWidthValue = (int) ((double) currentExp / maxExp * levelBarWidth);
        RoundRectangle2D levelBar = new RoundRectangle2D.Float(levelBarX, levelBarY, levelBarWidth, levelBarHeight, 20, 20);
        g.setColor(Color.decode("#474b4e")); // Задаем цвет шкалы
        g.fill(levelBar);

        // Рисуем прогресс
        g.setColor(Color.WHITE); // Задаем цвет прогресса
        g.fillRoundRect(levelBarX, levelBarY, progressWidthValue, levelBarHeight, 20, 20); // Заполняем прогресс

        // Рисуем "Level 3" над концом полоски прогресса
        String levelMessage = "Ур. " + level + " Ранг #" + top;
        int levelMessageY = levelBarY - 15; // Сдвигаем текст чуть выше конца полоски прогресса

        // Аватарка
        g.drawImage(avatarMask, avatarX, avatarY, avatarSize, avatarSize, null);

        g.drawString(levelMessage, levelBarX, levelMessageY);

        String progressText = currentExp + "/" + maxExp;
        // Перемещаем progressText над концом полоски прогресса
        int progressWidth = fm.stringWidth(progressText);
        int progressTextX = levelBarX + levelBarWidth - progressWidth;
        int progressTextY = levelBarY - 10;
        g.drawString(progressText, progressTextX, progressTextY);

        g.dispose();

        return image;
    }
}
