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
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

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

        event.deferReply().queue(reply -> {
            MessageEmbed embedBuilder = getEmbedBuilder(1);
            Collection<ActionRow> actionRows = new ArrayList<>();
            reply.editOriginalEmbeds(embedBuilder)
                    .setComponents(actionRows).queue();
        });
    }

    @NotNull
    public MessageEmbed getEmbedBuilder(int page) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.decode("#a48ea2"));

        HashMap<Integer, ArrayList<MessageEmbed.Field>> sortedRoles = getSortedRoles();
        ArrayList<MessageEmbed.Field> fields = sortedRoles.get(page);

        embedBuilder.addField("Магазин ролей", "Выберите роль, которую хотите купить", false);
        if (fields != null) embedBuilder.getFields().addAll(fields);

        embedBuilder.setFooter("Страница " + page + " из " + sortedRoles.size());
        embedBuilder.setTimestamp(Instant.now());
        return embedBuilder.build();
    }

    public HashMap<Integer, ArrayList<MessageEmbed.Field>> getSortedRoles() {
        int page = 1;
        HashMap<Integer, ArrayList<MessageEmbed.Field>> pages = new HashMap<>();
        ArrayList<MessageEmbed.Field> roles = new ArrayList<>();
        for (RoleShop role : rolesShop) {
            roles.add(new MessageEmbed.Field(
                    String.format("Роль #%d", 1),
                    String.format("""
                                    * Роль: **<@&%s>**\s
                                    * Цена: **%d**\s
                                    * Доступно к покупке: **%s**""",
                            role.getRole().getIdLong(), role.getPrice(), role.getLeftCount()),
                    false
            ));
            if (roles.size() == 25 || roles.size() == rolesShop.size()) {
                pages.put(page, roles);
                roles = new ArrayList<>();
                page++;
            }
        }
        return pages;
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
