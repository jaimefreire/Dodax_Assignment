package jobrunner.util;

import java.util.concurrent.TimeUnit;

/**
 * Created by jaimefreire on 19.10.16.
 */
public class JobRunnerUtils {

    /**
     * Utility method to pause the current thread for a number of seconds.
     *
     * @param seconds Number of seconds to pause the thread
     */
    public static void sleepForSeconds(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            System.err.printf("Error pausing the thread: %s %n", e.getMessage());
        }
    }
}
