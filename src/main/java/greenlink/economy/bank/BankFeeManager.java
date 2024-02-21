package greenlink.economy.bank;

import global.BotMain;
import global.commands.ICommand;
import greenlink.databse.DatabaseConnector;
import greenlink.economy.EconomyManager;
import greenlink.economy.EconomyUser;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author t.me/GreenL1nk
 * 04.02.2024
 */
public class BankFeeManager {
    private static BankFeeManager instance;

    @Nullable
    ScheduledExecutorService executorService;

    public void runScheduleIfNotExist() {
        if (executorService != null) return;
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        BankFee bankFee = getLowestNextFeeTime();
        if (bankFee == null) return;
        this.executorService = executorService;
        long scheduleTime = bankFee.getNextTimeFee() - System.currentTimeMillis();
        executorService.schedule(() -> {
            this.executorService = null;
            processFee(bankFee);
            DatabaseConnector.getInstance().lastFeeTime(bankFee.getUuid(), true);
            runScheduleIfNotExist();
        }, scheduleTime, TimeUnit.MILLISECONDS);
    }

    @Nullable
    public BankFee getLowestNextFeeTime() {
        return DatabaseConnector.getInstance().getLowestNextFeeTime();
    }

    public void processFee(BankFee bankFee) {
        EconomyManager.getInstance().getEconomyUser(bankFee.getUuid()).queue(EconomyUser::processBankFee);
    }

    private BankFeeManager() {
        runScheduleIfNotExist();
    }

    public static synchronized BankFeeManager getInstance() {
        if (instance == null) {
            instance = new BankFeeManager();
        }
        return instance;
    }
}