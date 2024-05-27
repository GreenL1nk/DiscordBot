package greenlink.shop.buttons.shop;

import global.buttons.ArgButton;
import greenlink.shop.RoleShop;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.ArrayList;
import java.util.List;

import static greenlink.shop.commands.ShopCommand.*;

public class ShiftingRoleButton extends ArgButton {
    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        Member member = event.getMember();
        if (member == null) return;
        if (event.getGuild() == null) return;
        if (!memberCanPerform(member, event)) return;

        int numRole = Integer.parseInt(getArgs(event)[0]);

        RoleShop roleShop = !rolesShop.isEmpty() ? rolesShop.get(numRole - 1) : null;
        List<Button> buttons = roleShop == null ? new ArrayList<>() : getButtons(numRole, roleShop);

        event.editMessageEmbeds(getEmbedBuilder(numRole, roleShop))
                .setComponents(ActionRow.of(buttons), ActionRow.of(getRoleSelectMenu(rolesShop, 1)))
                .queue();
    }

    @Override
    public String getButtonID() {
        return "role-";
    }
}
