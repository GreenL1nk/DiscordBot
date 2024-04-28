package greenlink.economy;

import global.BotMain;
import global.config.Config;
import global.utils.Utils;
import greenlink.User;
import greenlink.economy.leaderboards.LeaderBoardType;
import net.dv8tion.jda.api.entities.Role;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.IntStream;

/**
 * @author t.me/GreenL1nk
 * 19.01.2024
 */
public class EconomyUser extends User {
    private int cashBalance;
    private int bankBalance;
    private int currentXP;
    private int currentLevel;
    private long firstReceivedCoin;
    private int earnedFromRobs;
    private int lostFromRobs;
    private int successRobs;
    private int failedRobs;
    private long voiceTime;
    private int totalMessages;

    public EconomyUser(long uuid, int cashBalance, int bankBalance, int currentXP, int currentLevel, long firstReceivedCoin, int earnedFromRobs, int lostFromRobs, int successRobs, int failedRobs, long voiceTime, int totalMessages) {
        super(uuid);
        this.cashBalance = cashBalance;
        this.bankBalance = bankBalance;
        this.currentXP = currentXP;
        this.currentLevel = currentLevel;
        this.firstReceivedCoin = firstReceivedCoin;
        this.earnedFromRobs = earnedFromRobs;
        this.lostFromRobs = lostFromRobs;
        this.successRobs = successRobs;
        this.failedRobs = failedRobs;
        this.voiceTime = voiceTime;
        this.totalMessages = totalMessages;
    }

    public int getTotalBalance() {
        return cashBalance + bankBalance;
    }

    public void addCoins(int count) {
        if (firstReceivedCoin == 0) firstReceivedCoin = System.currentTimeMillis();
        this.cashBalance += count;
        onEconomyUpdate();
    }
    public void removeCoins(int count) {
        this.cashBalance -= count;
        onEconomyUpdate();
    }

    public void bankWithdraw(int count) {
        this.bankBalance -= count;
        int fee = (int) ((count * Config.getInstance().getBankPercent()) / 100);
        addCoins(count - fee);
    }

    public void bankDeposit(int count) {
        this.bankBalance += count;
        removeCoins(count);
    }

    public void processBankFee() {
        this.bankBalance -= (int) ((bankBalance * Config.getInstance().getBankPercent()) / 100);
    }

    public int getCashBalance() {
        return cashBalance;
    }

    public void setCashBalance(int cashBalance) {
        this.cashBalance = cashBalance;
    }

    public int getBankBalance() {
        return bankBalance;
    }

    public void setBankBalance(int bankBalance) {
        this.bankBalance = bankBalance;
    }

    public int getCurrentXP() {
        return currentXP;
    }

    public void setCurrentXP(int currentXP) {
        this.currentXP = currentXP;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(int currentLevel) {
        this.currentLevel = currentLevel;
    }

    public long getFirstReceivedCoin() {
        return firstReceivedCoin;
    }

    public void setFirstReceivedCoin(long firstReceivedCoin) {
        this.firstReceivedCoin = firstReceivedCoin;
    }

    public void addXp(int count) {
        this.currentXP += count;
        checkLevelUP();
        onEconomyUpdate();
    }

    public void removeXp(int count) {
        this.currentXP -= count;
        onEconomyUpdate();
    }

    public void addLevel(int count) {
        this.currentLevel += count;
        onEconomyUpdate();
        if (Config.getInstance().getLevelRoles().containsKey(currentLevel)) {
            List<Long> roleIds = Config.getInstance().getLevelRoles().get(currentLevel);
            BotMain.getInstance().getJda().retrieveUserById(getUuid()).useCache(false).queue(user -> {
                roleIds.forEach(roleId -> {
                    user.getMutualGuilds().stream().filter(guild -> guild.getRoleById(roleId) != null).findFirst().ifPresent(guild -> {
                        Role roleById = guild.getRoleById(roleId);
                        if (roleById != null) {
                            guild.addRoleToMember(user, roleById).queue();
                        }
                    });
                });
            });
        }
    }

    public void removeLevel(int count) {
        this.currentLevel -= count;
        onEconomyUpdate();
    }

    public int calculateExpToNextLevel() {
        return (int) (Config.getInstance().getFirstLevelXP() * Math.pow(Config.getInstance().getXpFormula(), currentLevel));
    }

    public void checkLevelUP() {
        int expToNextLevel = calculateExpToNextLevel();
        if (currentXP >= expToNextLevel) {
            addLevel(1);
            currentXP -= expToNextLevel;
        }
    }

    public int getTotalEarnedExp() {
        return IntStream.range(1, currentLevel)
                .map(level -> (int) (Config.getInstance().getFirstLevelXP() * Math.pow(Config.getInstance().getXpFormula(), level)))
                .sum() + currentXP;
    }

    public int getEarnedFromRobs() {
        return earnedFromRobs;
    }

    public int getLostFromRobs() {
        return lostFromRobs;
    }

    public int getSuccessRobs() {
        return successRobs;
    }

    public int getFailedRobs() {
        return failedRobs;
    }

    public long getVoiceTime() {
        return voiceTime;
    }

    public int getTotalMessages() {
        return totalMessages;
    }

    public void robTry(int count, boolean isRob) {
        if (isRob) {
            earnedFromRobs += count;
            successRobs++;
        }
        else {
            lostFromRobs += count;
            failedRobs++;
        }
    }

    public int getTotalRobs() {
        return successRobs + failedRobs;
    }

    public void addMessage() {
        totalMessages += 1;
        onEconomyUpdate();
    }

    public void addVoiceTime(long count) {
        voiceTime += count;
        onEconomyUpdate();
    }

    public String getFormatVoiceTime() {
        return Utils.formatTime(voiceTime);
    }

    public int getCurrentTop(LeaderBoardType leaderBoardType) {
        try {
            return EconomyManager.getInstance().getCurrentUserRank(leaderBoardType, getUuid());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int calculatePage(LeaderBoardType leaderBoardType) {
        int usersPerPage = 5;
        return (getCurrentTop(leaderBoardType) + usersPerPage - 1) / usersPerPage;
    }
}
