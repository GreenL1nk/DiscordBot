package greenlink.economy.leaderboards.selectmenus;

import global.selectmenus.ISelectMenu;
import greenlink.economy.leaderboards.LeaderBoardType;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static greenlink.economy.leaderboards.LeaderBoardCommand.*;

/**
 * @author t.me/GreenL1nk
 * 01.02.2024
 */
public class ChooseBoardTypeMenu implements ISelectMenu {
    @Override
    public void onSelectMenuInteraction(StringSelectInteractionEvent event) {
        Member member = event.getMember();
        if (member == null) return;
        if (event.getGuild() == null) return;
        if (!memberCanPerform(member, event)) return;

        LeaderBoardType leaderBoardType = Arrays.stream(LeaderBoardType.values())
                .filter(boardType -> boardType.name().equalsIgnoreCase(event.getValues().get(0)))
                .findFirst().orElse(LeaderBoardType.BALANCE);


        event.deferEdit().queue(reply -> {
            MessageEmbed embedBuilder = getEmbedBuilder(member, leaderBoardType, 1);
            Collection<ActionRow> actionRows = new ArrayList<>();
            actionRows.add(getMenu(leaderBoardType));
            Collection<ActionRow> buttons = getButtons(leaderBoardType, 1);
            actionRows.addAll(buttons);
            reply.editOriginalEmbeds(embedBuilder)
                    .setComponents(actionRows).queue();
        });
    }

    @Override
    public String getMenuID() {
        return "chooseboard";
    }
}
