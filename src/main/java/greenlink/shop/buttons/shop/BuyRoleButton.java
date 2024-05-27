package greenlink.shop.buttons.shop;

import global.buttons.ArgButton;
import greenlink.shop.RoleShop;
import greenlink.shop.commands.ShopCommand;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public class BuyRoleButton extends ArgButton {
    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        Member member = event.getMember();
        if (member == null) return;
        if (event.getGuild() == null) return;
        if (!memberCanPerform(member, event)) return;

        String[] args = getArgs(event);
        String roleId = args[0];

        RoleShop roleShop = ShopCommand.rolesShop.stream().filter(rs -> rs.getRole().getId().equalsIgnoreCase(roleId)).findFirst().orElse(null);
        if (roleShop == null) return;

        String response = roleShop.buyRole(member);
        event.getInteraction().deferReply(true).setContent(response).queue();
    }

    @Override
    public String getButtonID() {
        return "rolebuy-";
    }
}
