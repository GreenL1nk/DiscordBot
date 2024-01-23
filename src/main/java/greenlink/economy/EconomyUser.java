package greenlink.economy;

import global.config.Config;
import greenlink.User;
import greenlink.observer.Observer;
import org.jetbrains.annotations.Nullable;

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

    public EconomyUser(long uuid, int cashBalance, int bankBalance, int currentXP, int currentLevel, long firstReceivedCoin) {
        super(uuid);
        this.cashBalance = cashBalance;
        this.bankBalance = bankBalance;
        this.currentXP = currentXP;
        this.currentLevel = currentLevel;
        this.firstReceivedCoin = firstReceivedCoin;
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
    }

    public void removeLevel(int count) {
        this.currentLevel -= count;
        onEconomyUpdate();
    }

    public int calculateExpToNextLevel() {
        return (int) (Config.getInstance().getFirstLevelXP() * Math.pow(Config.getInstance().getXpFormula(), currentLevel));
    }

    public void checkLevelUP() {
        if (currentXP >= calculateExpToNextLevel()) {
            addLevel(1);
        }
    }
}
