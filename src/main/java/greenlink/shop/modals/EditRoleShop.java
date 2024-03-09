package greenlink.shop.modals;

import global.BotMain;
import global.config.Config;
import global.modals.ArgModal;
import greenlink.shop.RoleShop;
import greenlink.shop.commands.ShopCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
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
 * 29.02.2024
 */
public class EditRoleShop extends ArgModal {

    public static ArrayList<RoleShop> cacheRole = new ArrayList<>();

    @Override
    public void onModalInteraction(ModalInteractionEvent event) {
        Member member = event.getMember();
        if (!memberCanPerform(member, event)) return;
        Guild guild = event.getGuild();
        if (guild == null) return;

        long id = Long.parseLong(getArgs(event)[0]);

        Role role = BotMain.getInstance().getJda().getRoleById(id);

        RoleShop roleShop = cacheRole.stream().filter(rs -> rs.getRole().getIdLong() == id).findFirst().orElse(null);
        if (roleShop == null) {
            roleShop = new RoleShop("x1", "x1", "x1", "x1", "x1", 0, 1, 1, role);
            cacheRole.add(roleShop);
        }

        if (event.getValue("roleshop-price") != null) {
            roleShop.setPrice(Integer.parseInt(event.getValue("roleshop-price").getAsString()));
            roleShop.setLeftCount(Integer.parseInt(event.getValue("roleshop-count").getAsString()));
            roleShop.setCoinMultiplier(event.getValue("roleshop-multiplier") != null ? Integer.parseInt(event.getValue("roleshop-multiplier").getAsString()) : 1);
        }
        else {
            roleShop.setWorkExp(event.getValue("roleshop-work").getAsString());
            roleShop.setTimelyExp(event.getValue("roleshop-timely").getAsString());
            roleShop.setDailyExp(event.getValue("roleshop-daily").getAsString());
            roleShop.setWeeklyExp(event.getValue("roleshop-weekly").getAsString());
            roleShop.setMonthlyExp(event.getValue("roleshop-monthly").getAsString());
        }

        //описание роли

        List<Button> buttons = new ArrayList<>();

        buttons.add(Button.of(ButtonStyle.SECONDARY, "roleshoprequirement-" + id, "Отредактировать цену и кол-во"));
        buttons.add(Button.of(ButtonStyle.SECONDARY, "roleshopboosts-" + id, "Отредактировать бусты"));
        buttons.add(Button.of(ButtonStyle.SUCCESS, "roleshopsave-" + id, "Сохранить и добавить в магазин"));
        //save db
        buttons.add(Button.of(ButtonStyle.DANGER, "roleshopdelete-" + id, "Удалить из магазина")
                .withDisabled(ShopCommand.rolesShop.stream().anyMatch(rs -> rs.getRole().getIdLong() == id)));

        RoleShop finalRoleShop = roleShop;
        event.deferReply().queue(reply -> {
            reply.editOriginalComponents(Collections.singleton(ActionRow.of(buttons))).setEmbeds(getEmbedBuilder(finalRoleShop)).queue();
        });
    }

    @NotNull
    public static MessageEmbed getEmbedBuilder(RoleShop roleShop) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.decode("#a48ea2"));

        embedBuilder.addField("Редактирование роли | ", String.format("""
                * <@&%d>
                * Цена: **%d**
                * Наличие: **%d**
                * Множитель: **x%d**""", roleShop.getRole().getIdLong(), roleShop.getPrice(), roleShop.getLeftCount(), roleShop.getCoinMultiplier()), true);

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
    public String getModalID() {
        return "roleshop-";
    }

    @Override
    public boolean memberCanPerform(Member member, ModalInteractionEvent event) {
        boolean canPerform = super.memberCanPerform(member, event);
        Guild guild = event.getGuild();
        if (guild != null) {
            Role role = guild.getRoleById(Config.getInstance().getAdminRoleId());
            return member.getRoles().contains(role) && canPerform;
        }
        return false;
    }
}
