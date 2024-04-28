package greenlink.economy.leaderboards.modals;

import global.modals.ArgModal;
import greenlink.economy.leaderboards.LeaderBoardCommand;
import greenlink.economy.leaderboards.LeaderBoardType;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static greenlink.economy.leaderboards.LeaderBoardCommand.*;

/**
 * @author t.me/GreenL1nk
 * 02.02.2024
 */
public class ChoosePageLBModal extends ArgModal {
    @Override
    public void onModalInteraction(ModalInteractionEvent event) {
        Member member = event.getMember();
        if (member == null) return;
        if (event.getGuild() == null) return;
        if (!memberCanPerform(member, event)) return;

        String[] args = getArgs(event);

        LeaderBoardType leaderBoardType = Arrays.stream(LeaderBoardType.values())
                .filter(board -> board.name().equalsIgnoreCase(args[0]))
                .findFirst()
                .orElse(LeaderBoardType.BALANCE);

        ModalMapping lbpage = event.getValue("lbpage");
        if (lbpage != null) {
            try {
                int page = Integer.parseInt(lbpage.getAsString());
                if (page <= getPageCount(leaderBoardType) || page == 0) {
                    event.deferEdit().queue(reply -> {
                        MessageEmbed embedBuilder = getEmbedBuilder(member, leaderBoardType, page);
                        Collection<ActionRow> actionRows = new ArrayList<>();
                        actionRows.add(getMenu(leaderBoardType));
                        Collection<ActionRow> buttons = getButtons(leaderBoardType, page);
                        actionRows.addAll(buttons);
                        reply.editOriginalEmbeds(embedBuilder)
                                .setComponents(actionRows).queue();
                    });
                    return;
                }
            } catch (NumberFormatException ignored) {
            }
        }
        event.deferReply(true).setContent("Число должно быть в диапазоне от 1 до " + getPageCount(leaderBoardType)).queue();
    }

    @Override
    public String getModalID() {
        return "numpage";
    }
}
