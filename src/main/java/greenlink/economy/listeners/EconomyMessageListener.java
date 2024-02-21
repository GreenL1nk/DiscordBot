package greenlink.economy.listeners;

import greenlink.economy.EconomyManager;
import greenlink.economy.EconomyUser;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

/**
 * @author t.me/GreenL1nk
 * 01.02.2024
 */
public class EconomyMessageListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        Member member = event.getMember();
        if (event.getChannelType() != ChannelType.PRIVATE && member != null) {
            EconomyUser economyUser = EconomyManager.getInstance().getEconomyUser(member.getUser());
            if (economyUser == null) {
                return;
            }
            economyUser.addMessage();
        }
    }
}
