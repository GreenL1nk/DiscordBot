package global.modals;

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;

/**
 * @author t.me/GreenL1nk
 * 02.02.2024
 */
public abstract class ArgModal implements IModal {

    public String[] getArgs(ModalInteractionEvent event) {
        String substring = event.getModalId().substring(event.getModalId().indexOf("-") + 1);
        return substring.split("-");
    }

}