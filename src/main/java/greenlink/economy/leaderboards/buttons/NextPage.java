package greenlink.economy.leaderboards.buttons;

import global.buttons.ArgButton;
import greenlink.economy.leaderboards.LeaderBoardType;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static greenlink.economy.leaderboards.LeaderBoardCommand.*;

/**
 * @author t.me/GreenL1nk
 * 01.02.2024
 */
public class NextPage extends ArgButton {
    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        Member member = event.getMember();
        if (member == null) return;
        if (event.getGuild() == null) return;
        if (!memberCanPerform(member, event)) return;

        String[] args = getArgs(event);
        int nextPage = Integer.parseInt(args[0]);
        LeaderBoardType leaderBoardType = Arrays.stream(LeaderBoardType.values())
                .filter(board -> board.name().equalsIgnoreCase(args[1]))
                .findFirst()
                .orElse(LeaderBoardType.BALANCE);

        event.deferEdit().queue(reply -> {
            MessageEmbed embedBuilder = getEmbedBuilder(member, leaderBoardType, nextPage);
            Collection<ActionRow> actionRows = new ArrayList<>();
            actionRows.add(getMenu(leaderBoardType));
            Collection<ActionRow> buttons = getButtons(leaderBoardType, nextPage);
            actionRows.addAll(buttons);
            reply.editOriginalEmbeds(embedBuilder)
                    .setComponents(actionRows).queue();
        });

    }

    @Override
    public String getButtonID() {
        return "lbnext";
    }
}
