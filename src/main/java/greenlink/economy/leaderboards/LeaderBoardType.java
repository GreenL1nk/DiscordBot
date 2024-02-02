package greenlink.economy.leaderboards;

import global.config.Config;

/**
 * @author t.me/GreenL1nk
 * 31.01.2024
 */
public enum LeaderBoardType {
    LEVEL("уровню", Config.getInstance().getIcon().getLevelBoardIcon()),
    VOICE("голосовой активности", Config.getInstance().getIcon().getVoiceBoardIcon()),
    MESSAGES("отправленным сообщениям", Config.getInstance().getIcon().getMessageBoardIcon()),
    ROB("ограблениям", Config.getInstance().getIcon().getRobBoardIcon()),
    BALANCE("балансу", Config.getInstance().getIcon().getBalanceBoardIcon());

    private final String name;
    private final String icon;

    LeaderBoardType(String name, String icon) {
        this.name = name;
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public String getIcon() {
        return icon;
    }
}
