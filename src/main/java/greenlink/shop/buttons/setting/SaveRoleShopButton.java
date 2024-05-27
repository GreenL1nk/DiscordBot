package greenlink.shop.buttons.setting;

import global.BotMain;
import global.buttons.ArgButton;
import greenlink.databse.DatabaseConnector;
import greenlink.shop.RoleShop;
import greenlink.shop.commands.ShopCommand;
import greenlink.shop.modals.EditRoleShop;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

/**
 * @author t.me/GreenL1nk
 * 07.03.2024
 */
public class SaveRoleShopButton extends ArgButton {
    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (!isAdmin(event.getMember(), event)) return;

        long id = Long.parseLong(getArgs(event)[0]);

        RoleShop toSave = EditRoleShop.cacheRole.stream().filter(rs -> rs.getRole().getIdLong() == id).findFirst().orElse(null);
        if (toSave == null) return;

        if (!event.getGuild().getMemberById(BotMain.getInstance().getJda().getSelfUser().getId()).canInteract(toSave.getRole())) {
            event.deferReply(true).setContent(String.format("бот не сможет присвоить роль - <@&%s>, так как она выше по иерархии", toSave.getRole().getId())).queue();
        }

        DatabaseConnector.getInstance().saveRoleShop(toSave);
        RoleShop oldRole = ShopCommand.rolesShop.stream().filter(rs -> rs.getRole().getIdLong() == toSave.getRole().getIdLong()).findFirst().orElse(null);
        ShopCommand.rolesShop.remove(oldRole);
        ShopCommand.rolesShop.add(toSave);
        event.deferReply(true).setContent("Роль успешно сохранена в магазин и базу").queue();
    }

    @Override
    public String getButtonID() {
        return "roleshopsave";
    }
}
