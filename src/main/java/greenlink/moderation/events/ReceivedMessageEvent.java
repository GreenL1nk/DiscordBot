package greenlink.moderation.events;

import greenlink.databse.DatabaseConnector;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class ReceivedMessageEvent extends ListenerAdapter {

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        DatabaseConnector.getInstance().saveMessage(event);
    }
}
