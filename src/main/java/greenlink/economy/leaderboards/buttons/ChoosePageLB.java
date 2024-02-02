package greenlink.economy.leaderboards.buttons;

import global.buttons.ArgButton;
import global.buttons.IButton;
import global.selectmenus.ArgSelectMenu;
import greenlink.economy.leaderboards.LeaderBoardType;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

import java.util.Arrays;

/**
 * @author t.me/GreenL1nk
 * 02.02.2024
 */
public class ChoosePageLB extends ArgButton {
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
        TextInput name = TextInput.create("lbpage", "Введите номер страницы", TextInputStyle.SHORT).setRequired(true).build();

        Modal modal = Modal.create("numpage-" + leaderBoardType.name(), "Перейти к странице")
                .addComponents(ActionRow.of(name))
                .build();

        event.replyModal(modal).queue();
    }

    @Override
    public String getButtonID() {
        return "lbchoosepage";
    }
}
