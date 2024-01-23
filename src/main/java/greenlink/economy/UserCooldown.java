package greenlink.economy;

import global.config.Config;

import java.util.concurrent.TimeUnit;

/**
 * @author t.me/GreenL1nk
 * 22.01.2024
 */
public class UserCooldown {
    private long workLastTime;
    private long timelyLastTime;
    private long dailyLastTime;
    private long weeklyLastTime;
    private long monthlyLastTime;
    private long robLastTime;
    private long uuid;
    Config config = Config.getInstance();

    public UserCooldown(long uuid) {
        this.uuid = uuid;
    }

    public long getWorkLastTime() {
        return workLastTime;
    }

    public void setWorkLastTime(long workLastTime) {
        this.workLastTime = workLastTime;
    }

    public boolean canWork() {
        return (config.getWorkCooldown() + workLastTime) <= System.currentTimeMillis();
    }

    public long getTimelyLastTime() {
        return timelyLastTime;
    }

    public void setTimelyLastTime(long timelyLastTime) {
        this.timelyLastTime = timelyLastTime;
    }

    public boolean canTimely() {
        return (config.getTimelyCooldown() + timelyLastTime) <= System.currentTimeMillis();
    }

    public long getDailyLastTime() {
        return dailyLastTime;
    }

    public void setDailyLastTime(long dailyLastTime) {
        this.dailyLastTime = dailyLastTime;
    }

    public boolean canDaily() {
        return (config.getDailyCooldown() + dailyLastTime) <= System.currentTimeMillis();
    }

    public long getWeeklyLastTime() {
        return weeklyLastTime;
    }

    public void setWeeklyLastTime(long weeklyLastTime) {
        this.weeklyLastTime = weeklyLastTime;
    }

    public boolean canWeekly() {
        return (config.getWeeklyCooldown() + weeklyLastTime) <= System.currentTimeMillis();
    }

    public long getMonthlyLastTime() {
        return monthlyLastTime;
    }

    public void setMonthlyLastTime(long monthlyLastTime) {
        this.monthlyLastTime = monthlyLastTime;
    }

    public boolean canMonthly() {
        return (config.getMonthlyCooldown() + monthlyLastTime) <= System.currentTimeMillis();
    }

    public long getRobLastTime() {
        return robLastTime;
    }

    public void setRobLastTime(long robLastTime) {
        this.robLastTime = robLastTime;
    }

    public boolean canRob() {
        return (config.getRobCooldown() + robLastTime) <= System.currentTimeMillis();
    }

    public long getWorkEpochTimeCD() {
        return (TimeUnit.MINUTES.toMillis(Config.getInstance().getWorkCooldown()) + workLastTime) / 1000;
    }

    public long getUuid() {
        return uuid;
    }
}
