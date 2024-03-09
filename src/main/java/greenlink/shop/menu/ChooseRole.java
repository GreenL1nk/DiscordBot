package greenlink.shop.menu;

import global.config.Config;
import global.selectmenus.ArgSelectMenu;
import greenlink.shop.commands.SettingCommand;
import greenlink.shop.commands.ShopCommand;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

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
            String nextPage = s.substring(s.indexOf("page-"));

            event.deferEdit().queue(hook -> hook.editOriginalComponents(SettingCommand.getRoleSelectMenu(ShopCommand.rolesShop, Integer.parseInt(nextPage))).queue());
        }
        else {
            Role role = guild.getRoleById(event.getValues().get(0));
            if (role == null) return;

            TextInput price = TextInput.create("roleshop-price", "Цена", TextInputStyle.SHORT).setRequired(true).build();
            TextInput count = TextInput.create("roleshop-count", "Наличие", TextInputStyle.SHORT).setRequired(true).build();
            TextInput multiplier = TextInput.create("roleshop-multiplier", "Множитель", TextInputStyle.SHORT).setRequired(false).setPlaceholder("1").build();

            Modal modal = Modal.create("roleshop-" + role.getIdLong(), "Перейти дальше, к редактированию")
                    .addComponents(ActionRow.of(price), ActionRow.of(count), ActionRow.of(multiplier))
                    .build();

            event.replyModal(modal).queue();
        }
    }

    @Override
    public String getMenuID() {
        return "chooseroleshop";
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
