package greenlink.shop.buttons;

import global.buttons.ArgButton;
import greenlink.databse.DatabaseConnector;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

/**
 * @author t.me/GreenL1nk
 * 07.03.2024
 */
public class DeleteRoleShopButton extends ArgButton {
    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (!isAdmin(event.getMember(), event)) return;

        long id = Long.parseLong(getArgs(event)[0]);
        DatabaseConnector.getInstance().deleteRoleShopById(id);

        event.deferReply(true).setContent("Роль успешно удалена из магазина и базы").queue();
    }

    @Override
    public String getButtonID() {
        return "roleshopdelete";
    }
}
