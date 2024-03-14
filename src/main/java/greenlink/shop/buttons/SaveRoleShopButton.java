package greenlink.shop.buttons;

import global.buttons.ArgButton;
import greenlink.databse.DatabaseConnector;
import greenlink.shop.RoleShop;
import greenlink.shop.modals.EditRoleShop;
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

        RoleShop roleShop = EditRoleShop.cacheRole.stream().filter(rs -> rs.getRole().getIdLong() == id).findFirst().orElse(null);
        if (roleShop == null) return;

        DatabaseConnector.getInstance().saveRoleShop(roleShop);
    }

    @Override
    public String getButtonID() {
        return "roleshopsave";
    }
}
