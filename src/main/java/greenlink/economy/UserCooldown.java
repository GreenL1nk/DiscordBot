package greenlink.economy;

import global.config.Config;
import greenlink.observer.Observer;

/**
 * @author t.me/GreenL1nk
 * 22.01.2024
 */
public class UserCooldown implements Observer {
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

    public UserCooldown(long uuid, long workLastTime, long timelyLastTime, long dailyLastTime, long weeklyLastTime, long monthlyLastTime, long robLastTime) {
        this.uuid = uuid;
        this.workLastTime = workLastTime;
        this.timelyLastTime = timelyLastTime;
        this.dailyLastTime = dailyLastTime;
        this.weeklyLastTime = weeklyLastTime;
        this.monthlyLastTime = monthlyLastTime;
        this.robLastTime = robLastTime;
    }

    public long getWorkLastTime() {
        return workLastTime;
    }

    public void setWorkLastTime(long workLastTime) {
        this.workLastTime = workLastTime;
        onTimeUpdate(this);
    }

    public boolean canWork() {
        return (config.getWork().getCooldown() + workLastTime) <= System.currentTimeMillis();
    }

    public long getTimelyLastTime() {
        return timelyLastTime;
    }

    public void setTimelyLastTime(long timelyLastTime) {
        this.timelyLastTime = timelyLastTime;
        onTimeUpdate(this);
    }

    public boolean canTimely() {
        return (config.getTimely().getCooldown() + timelyLastTime) <= System.currentTimeMillis();
    }

    public long getDailyLastTime() {
        return dailyLastTime;
    }

    public void setDailyLastTime(long dailyLastTime) {
        this.dailyLastTime = dailyLastTime;
        onTimeUpdate(this);
    }

    public boolean canDaily() {
        return (config.getDaily().getCooldown() + dailyLastTime) <= System.currentTimeMillis();
    }

    public long getWeeklyLastTime() {
        return weeklyLastTime;
    }

    public void setWeeklyLastTime(long weeklyLastTime) {
        this.weeklyLastTime = weeklyLastTime;
        onTimeUpdate(this);
    }

    public boolean canWeekly() {
        return (config.getWeekly().getCooldown() + weeklyLastTime) <= System.currentTimeMillis();
    }

    public long getMonthlyLastTime() {
        return monthlyLastTime;
    }

    public void setMonthlyLastTime(long monthlyLastTime) {
        this.monthlyLastTime = monthlyLastTime;
        onTimeUpdate(this);
    }

    public boolean canMonthly() {
        return (config.getMonthly().getCooldown() + monthlyLastTime) <= System.currentTimeMillis();
    }

    public long getRobLastTime() {
        return robLastTime;
    }

    public void setRobLastTime(long robLastTime) {
        this.robLastTime = robLastTime;
        onTimeUpdate(this);
    }

    public boolean canRob() {
        return (config.getRob().getCooldown() + robLastTime) <= System.currentTimeMillis();
    }

    public long getWorkEpochTimeCD() {
        return (config.getWork().getCooldown() + workLastTime) / 1000;
    }

    public long getTimelyEpochTimeCD() {
        return (config.getTimely().getCooldown() + timelyLastTime) / 1000;
    }

    public long getDailyEpochTimeCD() {
        return (config.getDaily().getCooldown() + dailyLastTime) / 1000;
    }

    public long getWeeklyEpochTimeCD() {
        return (config.getWeekly().getCooldown() + weeklyLastTime) / 1000;
    }

    public long getMonthlyEpochTimeCD() {
        return (config.getMonthly().getCooldown() + monthlyLastTime) / 1000;
    }

    public long getRobEpochTimeCD() {
        return (config.getRob().getCooldown() + robLastTime) / 1000;
    }

    public long getUuid() {
        return uuid;
    }
}
