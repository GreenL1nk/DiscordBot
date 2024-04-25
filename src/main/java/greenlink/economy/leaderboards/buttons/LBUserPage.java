package greenlink.economy.leaderboards.buttons;

import global.buttons.ArgButton;
import greenlink.economy.EconomyManager;
import greenlink.economy.EconomyUser;
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
 * 02.02.2024
 */
public class LBUserPage extends ArgButton {
    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        Member member = event.getMember();
        if (member == null) return;
        if (event.getGuild() == null) return;
        if (!memberCanPerform(member, event)) return;

        String[] args = getArgs(event);

        LeaderBoardType leaderBoardType = Arrays.stream(LeaderBoardType.values())
                .filter(board -> board.name().equalsIgnoreCase(args[0]))
                .findFirst()
                .orElse(LeaderBoardType.BALANCE);

        event.deferEdit().queue(reply -> {
            EconomyUser economyUser = EconomyManager.getInstance().getEconomyUser(member.getUser());
            if (economyUser == null) return;
            int page = economyUser.calculatePage(leaderBoardType);
            MessageEmbed embedBuilder = getEmbedBuilder(member, leaderBoardType, page);
            Collection<ActionRow> actionRows = new ArrayList<>();
            actionRows.add(getMenu(leaderBoardType));
            Collection<ActionRow> buttons = getButtons(leaderBoardType, page);
            actionRows.addAll(buttons);
            reply.editOriginalEmbeds(embedBuilder)
                    .setComponents(actionRows).queue();
        });

    }

    @Override
    public String getButtonID() {
        return "lbuserpage";
    }
}
