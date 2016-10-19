package jobrunner;

import jobrunner.exceptions.JobRunnerException;
import jobrunner.interfaces.IJob;
import jobrunner.util.JobRunnerUtils;

import java.time.LocalTime;
import java.util.Collections;
import java.util.Formatter;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/*
 * JobRunner for jobs implementing the {@link IJob} interface.
 * Jobs are ran in parallel and they should be made thread-safe.
 *
 * @author jaimefreire, @date 10/19/16 11:08 AM
 */
public class JobRunner {

    private int max_parallel_jobs = 10;

    private long addedJobs = 0;
    private long runningJobs = 0;
    private long cancelledJobs = 0;
    private long finishedJobs = 0;

    private List<IJob> managedJobsList = null;
    private ExecutorService executor = null;
    private LinkedList<Future> startedJobsList = null;
    //Runner Default init state,
    private states runnerState = states.INIT;

    /**
     * Constructor using a provided number of parallel instances or the default value of 10.
     *
     * @param parallel_jobs Is the number of maximum parallel jobs ran by this jobrunner.JobRunner.
     */
    public JobRunner(final List<IJob> jobs, final int parallel_jobs) {
        if (parallel_jobs <= 0) {
            System.out.printf("Invalid number of parallel jobs received, using default value: %d %n",
                    max_parallel_jobs);
        } else {
            max_parallel_jobs = parallel_jobs;
            System.out.printf("JobRunner initialized with provided value of %d max. parallel jobs. %n",
                    max_parallel_jobs);
        }
        if (jobs != null && jobs.size() > 0) {
            setJobsList(jobs);
        }
    }


    /**
     * Alternative constructor
     */
    public JobRunner(int parallel_jobs) {
        this(null, parallel_jobs);
    }

    /**
     * Set the job list to be managed by the runner.
     *
     * @param jobs List of runnable jobs implementing {@link IJob} interface
     */
    public void setJobsList(List<IJob> jobs) {
        if (managedJobsList != null || runnerState != states.INIT) {
            //    throw new Exception("Tried to set job lists");
        } else if (jobs != null && jobs.size() > 0) {
            managedJobsList = Collections.unmodifiableList(jobs);
            System.out.printf("Received %d job instances. %n", jobs.size());
            startedJobsList = new LinkedList<>();
            runnerState = states.READY;
        }
    }

    /**
     * Method to start running the
     *
     * @throws JobRunnerException exception wrapping errors from the jobs being executed by the job runner.
     */
    public void start() throws JobRunnerException {

        //Runner must be started only once.
        if (!runnerState.equals(states.READY)) {
            System.err.printf("JobRunner can't be started when it's in %s state. %n", runnerState.name());
            throw new JobRunnerException(String.format("Invalid state: %s. Can only start runner on READY state",
                    runnerState.name()));
        }

        //Fixed thread pool is recommended as no assumptions about the job types (number, type: short or long running
        // processes, or running environment are given).
        executor = Executors.newFixedThreadPool(max_parallel_jobs);

        runnerState = states.STARTED;
        managedJobsList.forEach(job ->
        {
            startedJobsList.add(executor.submit(() -> {
                System.out.printf("Job %s started at %s %n", job.getClass().getName(), LocalTime.now().getNano());
                try {
                    job.execute();
                } catch (Throwable err) {
                    System.err.printf("Error running job: %s %n", err.getClass().getName());
                    stop();
                }
            }));
            this.addedJobs++;
        });
        //Catch all error scenarios and handle according to spec.

        //Check until jobs are finished and while execution hasn't been cancelled by failing job.
        while (!allJobsDone() && !executor.isShutdown()) {
            JobRunnerUtils.sleepForSeconds(1);
        }
        updateStats();
        printRunnerStats();

        if (isJobError()) {
            System.out.println("There was a problem running a job, please verify and run again once fixed.");
            runnerState = states.ERROR;
            throw new JobRunnerException("Exception running job. ");

        } else {
            System.out.println("All jobs in this runner have finished correctly.");
            runnerState = states.FINISHED;
        }
    }

    /**
     * Should only be called in case of exception.
     * Managed internally by the runner.
     */
    private void stop() {
        executor.shutdown(); //Finish started jobs; pending ones will not be started
        startedJobsList.forEach(job -> job.cancel(false));
        System.err.println("Stopping Job Runner due to error in one of the jobs; please verify logs before running " +
                "again");
    }

    /**
     * Managed internally by the runner.
     */
    private boolean allJobsDone() {
        return this.startedJobsList.stream().filter(job -> !job.isDone()).count() <= 0;
    }

    /**
     * Check for errors in the submitted jobs.
     */
    private boolean isJobError() {
        return this.startedJobsList.stream().filter(Future::isCancelled).count() > 0;
    }

    /**
     * Public method exposing current runner state
     *
     * @return {@link states}
     */
    public states getRunnerState() {
        return runnerState;
    }

    /**
     * Private method to update the job runner stats; running Vs. finished jobs.
     */
    private void updateStats() {
        this.runningJobs = this.startedJobsList.stream().filter(job -> !job.isDone()).count();
        this.cancelledJobs = this.startedJobsList.stream().filter(Future::isCancelled).count();
        this.finishedJobs = this.addedJobs - this.runningJobs - this.cancelledJobs;
    }

    /**
     * Prints current job runner status
     */
    public void printRunnerStats() {
        StringBuffer sb = new StringBuffer();
        Formatter formatter = new Formatter(sb);
        //
        formatter.format("JobRunner status %s. %n", this.runnerState);
        formatter.format("%d total added jobs. %n", this.addedJobs);
        formatter.format("%d running jobs. %n", this.runningJobs);
        formatter.format("%d cancelled jobs. %n", this.cancelledJobs);
        formatter.format("%d finished jobs. %n", this.finishedJobs);
        //
        System.out.println(sb.toString());
    }

    /**
     * Expose total finished jobs, useful for testing error scenarios.
     *
     * @return finishedJobs
     */
    public long getFinishedJobs() {
        return this.finishedJobs;
    }

    public enum states {INIT, READY, STARTED, FINISHED, ERROR}
}