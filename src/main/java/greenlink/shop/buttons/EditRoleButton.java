package greenlink.shop.buttons;

import global.buttons.ArgButton;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

/**
 * @author t.me/GreenL1nk
 * 06.03.2024
 */
public class EditRoleButton extends ArgButton {
    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (!isAdmin(event.getMember(), event)) return;

        long id = Long.parseLong(getArgs(event)[0]);

        TextInput price = TextInput.create("roleshop-price", "Цена", TextInputStyle.SHORT).setRequired(true).build();
        TextInput count = TextInput.create("roleshop-count", "Наличие", TextInputStyle.SHORT).setRequired(true).build();
        TextInput multiplier = TextInput.create("roleshop-multiplier", "Множитель", TextInputStyle.SHORT).setRequired(false).build();

        Modal modal = Modal.create("roleshop-" + id, "Перейти дальше, к редактированию")
                .addComponents(ActionRow.of(price), ActionRow.of(count), ActionRow.of(multiplier))
                .build();

        event.replyModal(modal).queue();
    }

    @Override
    public String getButtonID() {
        return "roleshoprequirement";
    }
}
