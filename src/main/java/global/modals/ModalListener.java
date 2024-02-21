package global.modals;

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

/**
 * @author t.me/GreenL1nk
 * 02.02.2024
 */
public class ModalListener extends ListenerAdapter {

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        ModalManager.getInstance().getModals().stream()
                .filter(modal -> {
                    if (modal instanceof ArgModal) {
                        return event.getModalId().startsWith(modal.getModalID());
                    }
                    return modal.getModalID().equals(event.getModalId());
                })
                .findFirst()
                .ifPresent((modal) -> modal.onModalInteraction(event));
    }

}
