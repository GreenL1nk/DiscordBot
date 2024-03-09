package greenlink.shop.commands;

import global.commands.SlashCommand;
import global.config.Config;
import greenlink.shop.RoleShop;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author t.me/GreenL1nk
 * 27.02.2024
 */
public class SettingCommand extends SlashCommand {
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        if (!memberCanPerform(member, event)) return;
        Guild guild = event.getGuild();
        if (guild == null) return;
        event.deferReply().queue(reply -> {
            reply.editOriginalComponents(getRoleSelectMenu(ShopCommand.rolesShop, 1)).queue();
        });
    }

    @Override
    public String getName() {
        return "settings";
    }

    @Override
    public String getDescription() {
        return "Настройки";
    }

    @Override
    public boolean memberCanPerform(Member member, SlashCommandInteractionEvent event) {
        boolean canPerform = super.memberCanPerform(member, event);
        Guild guild = event.getGuild();
        if (guild != null) {
            Role role = guild.getRoleById(Config.getInstance().getAdminRoleId());
            return member.getRoles().contains(role) && canPerform;
        }
        return false;
    }

    public static ActionRow getRoleSelectMenu(List<RoleShop> roles, int page) {
        int nextPage = page + 1;
        page -= 1;
        int startIndex = page * 23;
        int endIndex = Math.min(startIndex + 23, roles.size());
        List<RoleShop> rolesOnPage = roles.subList(startIndex, endIndex);

        List<SelectOption> options = rolesOnPage.stream()
                .filter(role -> !role.getRole().isPublicRole())
                .map(role -> SelectOption.of(role.getRole().getName(), role.getRole().getId()))
                .collect(Collectors.toList());

        if (endIndex < roles.size()) {
            options.add(SelectOption.of("Перейти на следующую страницу", "page-" + nextPage));
        }
        if (page > 0) {
            options.add(SelectOption.of("Перейти на предыдущую страницу", "page-" + page));
        }

        StringSelectMenu.Builder dropdown = StringSelectMenu.create("chooseroleshop")
                .addOptions(options)
                .setPlaceholder("Выберите роль для добавления в магазин");

        return ActionRow.of(dropdown.build());
    }
}
