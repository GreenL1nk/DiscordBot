package greenlink.shop.commands;

import global.BotMain;
import global.commands.SlashCommand;
import greenlink.databse.DatabaseConnector;
import greenlink.shop.RoleShop;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Instant;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author t.me/GreenL1nk
 * 28.02.2024
 */
public class ShopCommand extends SlashCommand {

    public static ArrayList<RoleShop> rolesShop = new ArrayList<>();

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        if (member == null) return;
        if (event.getGuild() == null) return;
        if (!memberCanPerform(member, event)) return;

        RoleShop roleShop = !rolesShop.isEmpty() ? rolesShop.get(0) : null;
        List<Button> buttons = roleShop == null ? new ArrayList<>() : getButtons(1, roleShop);


        event.deferReply().queue(reply -> {
            MessageEmbed embedBuilder = getEmbedBuilder(1, roleShop);
            reply.editOriginalEmbeds(embedBuilder)
                    .setComponents(ActionRow.of(buttons), ActionRow.of((getRoleSelectMenu(rolesShop, 1))))
                    .queue();
        });
    }

    @NotNull
    public static MessageEmbed getEmbedBuilder(int roleNum, @Nullable RoleShop role) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.decode("#a48ea2"));

        if (role == null) {
            embedBuilder.addField("Магазин пока что пуст.", "", false);
        }
        else {
            embedBuilder.addField("Магазин ролей", "", false);
            embedBuilder.addField("",
                    String.format("""
                                    * Роль: **<@&%s>**
                                    * Цена: **%d**\s
                                    * Доступно к покупке: **%s**""",
                            role.getRole().getIdLong(), role.getPrice(), role.getLeftCount()),
                    true);
            embedBuilder.addField("Бусты", String.format("""
                        * /work: **%s**
                        * /timely: **%s**
                        * /daily: **%s**
                        * /weekly: **%s**
                        * /monthly: **%s**"""
                    , role.getWorkExp(), role.getTimelyExp(),
                    role.getDailyExp(), role.getWeeklyExp(), role.getMonthlyExp()), true);
        }

        embedBuilder.setFooter("Роль " + roleNum + " из " + rolesShop.size());
        embedBuilder.setTimestamp(Instant.now());
        return embedBuilder.build();
    }

    public static List<Button> getButtons(int roleNum, RoleShop role) {
        List<Button> buttons = new ArrayList<>();

        buttons.add(Button.of(ButtonStyle.SUCCESS, "rolebuy-" + role.getRole().getId(), "Купить"));
        buttons.add(Button.of(ButtonStyle.SECONDARY, "role-" + (roleNum - 1), "Предыдущая роль").withDisabled(roleNum <= 1));
        buttons.add(Button.of(ButtonStyle.SECONDARY, "role-" + (roleNum + 1), "Следующая роль").withDisabled(rolesShop.size() < roleNum));

        return buttons;
    }

    private void addFromDBRolesShop() {
        try (Connection conn = DatabaseConnector.getInstance().getConnection();
             PreparedStatement selectStatement = conn.prepareStatement("SELECT * FROM shop_roles")) {
            try (ResultSet result = selectStatement.executeQuery()) {
                while (result.next()) {
                    long id = result.getLong("id");
                    if (rolesShop.stream().noneMatch(roleShop -> roleShop.getRole().getIdLong() == id)) {

                        Role roleById = BotMain.getInstance().getJda().getRoleById(id);
                        if (roleById == null) continue;

                        String workExp = result.getString("work_exp");
                        String timelyExp = result.getString("timely_exp");
                        String dailyExp = result.getString("daily_exp");
                        String weeklyExp = result.getString("weekly_exp");
                        String monthlyExp = result.getString("monthly_exp");
                        int leftCount = result.getInt("left_count");
                        int coinMultiplier = result.getInt("coin_multiplier");
                        int price = result.getInt("price");

                        RoleShop roleShop = new RoleShop(workExp, timelyExp, dailyExp, weeklyExp, monthlyExp, leftCount, coinMultiplier, price, roleById);
                        rolesShop.add(roleShop);
                    }
                }
            }
        }
        catch (Exception e) {
            BotMain.logger.error("", e);
        }
    }

    public static SelectMenu getRoleSelectMenu(List<RoleShop> roles, int numRole) {
        List<RoleShop> copyRoles = new ArrayList<>(roles.stream().filter(roleShop -> roleShop.getLeftCount() > 0).toList());
        int nextRole = numRole + 1;
        numRole -= 1;
        int startIndex = numRole * 23;
        int endIndex = Math.min(startIndex + 23, copyRoles.size());
        List<RoleShop> rolesOnPage = copyRoles.subList(startIndex, endIndex);

        int finalNumRole = numRole;
        List<SelectOption> options = rolesOnPage.stream()
                .filter(role -> !role.getRole().isPublicRole())
                .map(role -> SelectOption.of(role.getRole().getName(), "-" + role.getRole().getId() + "-" + finalNumRole))
                .collect(Collectors.toList());

        if (endIndex < copyRoles.size()) {
            options.add(SelectOption.of("Перейти на следующую страницу", "rolepage-" + nextRole));
        }
        if (numRole > 0) {
            options.add(SelectOption.of("Перейти на предыдущую страницу", "rolepage-" + numRole));
        }

        StringSelectMenu.Builder dropdown = StringSelectMenu.create("shopchoose")
                .addOptions(options)
                .setPlaceholder("Выберите роль для просмотра");

        return dropdown.build();
    }

    public ShopCommand() {
        addFromDBRolesShop();
    }

    @Override
    public String getName() {
        return "shop";
    }

    @Override
    public String getDescription() {
        return "Магазин ролей";
    }
}
