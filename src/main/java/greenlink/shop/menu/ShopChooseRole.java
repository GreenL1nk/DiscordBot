package greenlink.shop.menu;

import global.selectmenus.ArgSelectMenu;
import greenlink.shop.RoleShop;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.ArrayList;
import java.util.List;

import static greenlink.shop.commands.ShopCommand.*;

public class ShopChooseRole extends ArgSelectMenu {
    @Override
    public void onSelectMenuInteraction(StringSelectInteractionEvent event) {
        Member member = event.getMember();
        if (member == null) return;
        if (event.getGuild() == null) return;
        if (!memberCanPerform(member, event)) return;

        String arg = getValues(event)[0];
        if (!event.getValues().get(0).contains("page-")) {
            int numRole = Integer.parseInt(getValues(event)[1]);
            RoleShop roleShop = rolesShop.stream().filter(rs -> rs.getRole().getId().equalsIgnoreCase(arg)).findFirst().orElse(null);
            List<Button> buttons = roleShop == null ? new ArrayList<>() : getButtons(numRole, roleShop);

            event.editMessageEmbeds(getEmbedBuilder(numRole, roleShop))
                    .setComponents(ActionRow.of(buttons), ActionRow.of(getRoleSelectMenu(rolesShop, 1)))
                    .queue();
        }
        else {
            int num = Integer.parseInt(arg);
            event.editSelectMenu(getRoleSelectMenu(rolesShop, num)).queue();
        }
    }

    @Override
    public String getMenuID() {
        return "shopchoose";
    }
}
