package greenlink.shop;

import net.dv8tion.jda.api.entities.Role;

/**
 * @author t.me/GreenL1nk
 * 25.02.2024
 */
public class RoleShop {

    String workExp;
    String timelyExp;
    String dailyExp;
    String weeklyExp;
    String monthlyExp;
    Role role;
    int leftCount;
    double coinMultiplier;
    int price;

    public RoleShop(String workExp, String timelyExp, String dailyExp, String weeklyExp, String monthlyExp, int leftCount, double coinMultiplier, int price, Role role) {
        this.workExp = workExp;
        this.timelyExp = timelyExp;
        this.dailyExp = dailyExp;
        this.weeklyExp = weeklyExp;
        this.monthlyExp = monthlyExp;
        this.leftCount = leftCount;
        this.coinMultiplier = coinMultiplier;
        this.price = price;
        this.role = role;
    }

    public String getWorkExp() {
        return workExp;
    }

    public String getTimelyExp() {
        return timelyExp;
    }

    public String getDailyExp() {
        return dailyExp;
    }

    public String getWeeklyExp() {
        return weeklyExp;
    }

    public String getMonthlyExp() {
        return monthlyExp;
    }

    public Role getRole() {
        return role;
    }

    public int getLeftCount() {
        return leftCount;
    }

    public double getCoinMultiplier() {
        return coinMultiplier;
    }

    public int getPrice() {
        return price;
    }

    public void setWorkExp(String workExp) {
        this.workExp = workExp;
    }

    public void setTimelyExp(String timelyExp) {
        this.timelyExp = timelyExp;
    }

    public void setDailyExp(String dailyExp) {
        this.dailyExp = dailyExp;
    }

    public void setWeeklyExp(String weeklyExp) {
        this.weeklyExp = weeklyExp;
    }

    public void setMonthlyExp(String monthlyExp) {
        this.monthlyExp = monthlyExp;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setLeftCount(int leftCount) {
        this.leftCount = leftCount;
    }

    public void setCoinMultiplier(double coinMultiplier) {
        this.coinMultiplier = coinMultiplier;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
