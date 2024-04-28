package greenlink.shop.menu;

import global.config.Config;
import global.selectmenus.ArgSelectMenu;
import greenlink.databse.DatabaseConnector;
import greenlink.shop.RoleShop;
import greenlink.shop.commands.SettingCommand;
import greenlink.shop.commands.ShopCommand;
import greenlink.shop.modals.EditRoleShop;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author t.me/GreenL1nk
 * 27.02.2024
 */
public class ChooseRole extends ArgSelectMenu {

    @Override
    public void onSelectMenuInteraction(StringSelectInteractionEvent event) {
        Member member = event.getMember();
        if (member == null) return;
        if (!memberCanPerform(member, event)) return;
        if (event.getValues().isEmpty()) return;
        Guild guild = event.getGuild();
        if (guild == null) return;

        if (event.getValues().get(0).contains("page-")) {
            String s = event.getValues().get(0);
            String nextPage = s.substring(s.indexOf("page-") + 5);

            event.deferEdit().queue(hook -> hook.editOriginalComponents(SettingCommand.getRoleSelectMenu(ShopCommand.rolesShop, Integer.parseInt(nextPage), guild.getRoles())).queue());
        }
        else {
            long id = Long.parseLong(event.getValues().get(0));

            RoleShop roleShop = EditRoleShop.cacheRole.stream().filter(rs -> rs.getRole().getIdLong() == id).findFirst().orElse(null);
            if (roleShop == null) {
                roleShop = DatabaseConnector.getInstance().loadShopRole(id);
                EditRoleShop.cacheRole.add(roleShop);
            }

            List<Button> buttons = new ArrayList<>();

            buttons.add(Button.of(ButtonStyle.SECONDARY, "roleshoprequirement-" + id, "Отредактировать цену и кол-во"));
            buttons.add(Button.of(ButtonStyle.SECONDARY, "roleshopboosts-" + id, "Отредактировать бусты"));
            buttons.add(Button.of(ButtonStyle.SUCCESS, "roleshopsave-" + id, "Сохранить и добавить в магазин"));
            buttons.add(Button.of(ButtonStyle.DANGER, "roleshopdelete-" + id, "Удалить из магазина")
                    .withDisabled(ShopCommand.rolesShop.stream().anyMatch(rs -> rs.getRole().getIdLong() == id)));

            RoleShop finalRoleShop = roleShop;
            event.deferReply().queue(reply -> {
                reply.editOriginalComponents(Collections.singleton(ActionRow.of(buttons))).setEmbeds(getEmbedBuilder(finalRoleShop)).queue();
            });
        }
    }

    @Override
    public String getMenuID() {
        return "chooseroleshop";
    }

    @NotNull
    public static MessageEmbed getEmbedBuilder(RoleShop roleShop) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.decode("#a48ea2"));

        embedBuilder.addField("Редактирование роли | ", String.format("""
                * <@&%d>
                * Цена: **%d**
                * Наличие: **%d**
                * Множитель: **x%s**""", roleShop.getRole().getIdLong(), roleShop.getPrice(), roleShop.getLeftCount(), roleShop.getCoinMultiplier()), true);

        embedBuilder.addField("Бусты", String.format("""
                        * /work: **%s**
                        * /timely: **%s**
                        * /daily: **%s**
                        * /weekly: **%s**
                        * /monthly: **%s**"""
                , roleShop.getWorkExp(), roleShop.getTimelyExp(),
                roleShop.getDailyExp(), roleShop.getWeeklyExp(), roleShop.getMonthlyExp()), true);

        embedBuilder.setTimestamp(Instant.now());
        return embedBuilder.build();
    }

    @Override
    public boolean memberCanPerform(Member member, StringSelectInteractionEvent event) {
        boolean canPerform = super.memberCanPerform(member, event);
        Guild guild = event.getGuild();
        if (guild != null) {
            Role role = guild.getRoleById(Config.getInstance().getAdminRoleId());
            return member.getRoles().contains(role) && canPerform;
        }
        return false;
    }
}
