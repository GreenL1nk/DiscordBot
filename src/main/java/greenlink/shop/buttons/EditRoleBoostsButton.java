package greenlink.shop.buttons;

import global.buttons.ArgButton;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

/**
 * @author t.me/GreenL1nk
 * 07.03.2024
 */
public class EditRoleBoostsButton extends ArgButton {
    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (!isAdmin(event.getMember(), event)) return;

        long id = Long.parseLong(getArgs(event)[0]);

        TextInput workExp = TextInput.create("roleshop-work", "Буст к /work", TextInputStyle.SHORT).setRequired(false).build();
        TextInput timelyExp = TextInput.create("roleshop-timely", "Буст к /timely", TextInputStyle.SHORT).setRequired(false).build();
        TextInput dailyExp = TextInput.create("roleshop-daily", "Буст к /daily", TextInputStyle.SHORT).setRequired(false).build();
        TextInput weeklyExp = TextInput.create("roleshop-weekly", "Буст к /weekly", TextInputStyle.SHORT).setRequired(false).build();
        TextInput monthlyExp = TextInput.create("roleshop-monthly", "Буст к /monthly", TextInputStyle.SHORT).setRequired(false).build();

        Modal modal = Modal.create("roleshop-" + id, "Перейти дальше, к редактированию")
                .addComponents(ActionRow.of(workExp), ActionRow.of(timelyExp), ActionRow.of(dailyExp), ActionRow.of(weeklyExp), ActionRow.of(monthlyExp))
                .build();

        event.replyModal(modal).queue();
    }

    @Override
    public String getButtonID() {
        return "roleshopboosts";
    }
}
