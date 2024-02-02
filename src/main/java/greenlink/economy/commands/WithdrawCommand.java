package greenlink.economy.commands;

import global.commands.SlashCommand;
import global.config.Config;
import greenlink.economy.EconomyManager;
import greenlink.economy.EconomyUser;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

/**
 * @author t.me/GreenL1nk
 * 23.01.2024
 */
public class WithdrawCommand extends SlashCommand {
    public void execute(SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        if (member == null) return;
        if (event.getGuild() == null) return;
        if (!memberCanPerform(member, event)) return;
        if (event.getOptions().isEmpty()) return;
        if (Integer.MAX_VALUE < event.getOptions().get(0).getAsLong()) {
            event.deferReply(true).setContent("Значение не должно превышать " + Integer.MAX_VALUE).queue();
            return;
        }
        int count = event.getOptions().get(0).getAsInt();
        if (count == 0) return;

        EconomyUser economyUser = EconomyManager.getInstance().getEconomyUser(member.getIdLong());
        if (economyUser.getBankBalance() < count) {
            event.deferReply(true).setContent("Ваш банковский баланс меньше " + count + Config.getInstance().getIcon().getCoinIcon()).queue();
            return;
        }
        economyUser.bankWithdraw(count);

        event.deferReply(true).setContent(String.format("<@%s> вы успешно сняли %d%s наличных средств с банковского счёта",
                economyUser.getUuid(), count,
                Config.getInstance().getIcon().getCoinIcon())).queue();
    }

    @Override
    public void updateOptions() {
        options.add(new OptionData(OptionType.INTEGER, "сумма", "кол-во валюты", true).setMinValue(1));
    }

    @Override
    public String getName() {
        return "withdraw";
    }

    @Override
    public String getDescription() {
        return "вывод денег с банка";
    }
}
