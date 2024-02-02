package global.buttons;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

/**
 * @author t.me/GreenL1nk
 * 15.01.2024
 */
public class ButtonListener extends ListenerAdapter {

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        ButtonManager.getInstance().getButtons().stream()
                .filter(button -> {
                    if (button instanceof ArgButton) {
                        return event.getComponentId().startsWith(button.getButtonID());
                    }
                    return button.getButtonID().equals(event.getComponentId());
                })
                .findFirst()
                .ifPresent((button) -> button.onButtonInteraction(event));
    }

}
