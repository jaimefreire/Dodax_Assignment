package jobrunner.exceptions;

/**
 * Created by jaimefreire on 19.10.16.
 * class holding non recoverable errors in the JobRunner domain, using
 * {@link RuntimeException} as a base (unchecked exceptions).
 */
public class JobRunnerException extends RuntimeException {

    /**
     * Constructor with error message
     *
     * @param msg Exception message
     */
    public JobRunnerException(String msg) {
        super(msg);
    }
}
