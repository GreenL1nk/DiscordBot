package global.pastebin;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.components.ActionRow;

import java.util.ArrayList;

public record Pastebin(String content, ArrayList<MessageEmbed> embeds, ArrayList<ActionRow> components) {

}
