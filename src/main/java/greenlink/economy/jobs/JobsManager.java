package greenlink.economy.jobs;

import global.config.Config;

import java.util.List;
import java.util.Random;

/**
 * @author t.me/GreenL1nk
 * 23.01.2024
 */
public class JobsManager {
    private static JobsManager instance;
    private final List<Job> jobs = Config.getInstance().getJobs();

    private JobsManager() {
    }

    public Job getRandomJob() {
        Random random = new Random();
        int randomIndex = random.nextInt(jobs.size());
        return jobs.get(randomIndex);
    }

    public static synchronized JobsManager getInstance() {
        if (instance == null) {
            instance = new JobsManager();
        }
        return instance;
    }
}