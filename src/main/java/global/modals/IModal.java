package global.modals;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;

/**
 * @author t.me/GreenL1nk
 * 02.02.2024
 */
public interface IModal {

    void onModalInteraction(ModalInteractionEvent event);
    String getModalID();

    default boolean memberCanPerform(Member member, ModalInteractionEvent event) {
        Guild guild = event.getGuild();
        if (guild == null) return false;
        if (member == null) return false;

        return event.getGuild().getIdLong() == member.getGuild().getIdLong();
    }

}
