package jobrunner.interfaces;

import jobrunner.JobRunner;

/**
 * Interface implemented by all Jobs ran by the {@link JobRunner}
 */
public interface IJob {
    void execute();
}
