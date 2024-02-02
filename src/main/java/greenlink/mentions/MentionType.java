package greenlink.mentions;

/**
 * @author t.me/GreenL1nk
 * 28.01.2024
 */
public enum MentionType {

    GUILD_CHANNEL("Прямо здесь"),
    PRIVATE_CHANNEL("Личные сообщения");

    final String message;
    MentionType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
