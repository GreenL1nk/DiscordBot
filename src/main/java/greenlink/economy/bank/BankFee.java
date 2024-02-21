package greenlink.economy.bank;

/**
 * @author t.me/GreenL1nk
 * 05.02.2024
 */
public class BankFee {

    long uuid;
    long nextTimeFee;

    public BankFee(long uuid, long nextTimeFee) {
        this.uuid = uuid;
        this.nextTimeFee = nextTimeFee;
    }

    public long getUuid() {
        return uuid;
    }

    public long getNextTimeFee() {
        return nextTimeFee;
    }
}
