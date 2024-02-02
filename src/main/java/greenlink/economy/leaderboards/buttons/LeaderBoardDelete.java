package greenlink.economy.leaderboards.buttons;

import global.buttons.IButton;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

/**
 * @author t.me/GreenL1nk
 * 02.02.2024
 */
public class LeaderBoardDelete implements IButton {
    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        Member member = event.getMember();
        if (member == null) return;
        if (event.getGuild() == null) return;
        if (!memberCanPerform(member, event)) return;

        event.deferEdit().queue(message -> message.deleteOriginal().queue());
    }

    @Override
    public String getButtonID() {
        return "lbdelete";
    }
}
