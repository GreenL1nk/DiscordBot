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
 * 29.01.2024
 */
public class PayCommand extends SlashCommand {
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        if (member == null) return;
        if (event.getGuild() == null) return;
        if (!memberCanPerform(member, event)) return;
        if (event.getOptions().isEmpty()) return;
        Member toPay = event.getOptions().get(0).getAsMember();
        if (event.getOptions().get(1).getAsLong() > Integer.MAX_VALUE) {
            event.deferReply(true).setContent("Значение не должно превышать " + Integer.MAX_VALUE).queue();
            return;
        }
        int count = event.getOptions().get(1).getAsInt();
        if (toPay == null) return;
        if (count == 0) return;
        EconomyUser receiver = EconomyManager.getInstance().getEconomyUser(toPay.getUser());
        EconomyUser payer = EconomyManager.getInstance().getEconomyUser(member.getUser());
        if (receiver == null || payer == null) {
            event.deferReply(true).setContent("бот не может использоваться для этих целей").queue();
            return;
        }
        if (payer.getCashBalance() < count) {
            event.deferReply(true).setContent("Ваш баланс наличных меньше " + count + Config.getInstance().getIcon().getCoinIcon()).queue();
            return;
        }
        payer.removeCoins(count);
        receiver.addCoins(count);
        event.deferReply().setContent(String.format("<@%s> успешно перевёл %d%s наличных средств <@%s>",
                payer.getUuid(), count,
                Config.getInstance().getIcon().getCoinIcon(), receiver.getUuid())).queue();
    }

    @Override
    public void updateOptions() {
        options.add(new OptionData(OptionType.USER, "пользователь", "Кому переводим?", true));
        options.add(new OptionData(OptionType.INTEGER, "сумма", "кол-во валюты", true));
    }

    @Override
    public String getName() {
        return "pay";
    }

    @Override
    public String getDescription() {
        return "Перевод валюты пользователю";
    }
}
