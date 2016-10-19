import jobrunner.JobRunner;
import jobrunner.exceptions.JobRunnerException;
import jobrunner.interfaces.IJob;
import jobrunner.util.JobRunnerUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/*
 * UnitTests for the jobrunner.JobRunner class
 *
 */
public class JobRunnerTest {

    //Test initial state
    @Test
    public void testRunnerOneJobAdded() {

        List<IJob> jobList = new ArrayList<>();
        JobRunner runner = new JobRunner(5);
        jobList.add(() -> System.out.println("Mock job 1"));
        assertEquals(JobRunner.states.INIT, runner.getRunnerState());
        runner.setJobsList(jobList);
        assertEquals(JobRunner.states.READY, runner.getRunnerState());
    }


    @Test
    public void testRunnerAlternativeInit() {

        List<IJob> jobList = new ArrayList<>();
        jobList.add(() -> System.out.println("Mock job 2"));
        JobRunner runner = new JobRunner(jobList, 4);
        assertEquals(JobRunner.states.READY, runner.getRunnerState());
    }

    @Test
    public void testRunnerNoJobs() {

        List<IJob> jobList = new ArrayList<>();
        JobRunner runner = new JobRunner(jobList, 6);
        assertEquals(JobRunner.states.INIT, runner.getRunnerState());
    }

    @Test
    public void testRunnerCompleteSingleRun() {

        List<IJob> jobList = new ArrayList<>();
        JobRunner runner = new JobRunner(1);
        jobList.add(() -> System.out.println("Mock job 3"));
        assertEquals(JobRunner.states.INIT, runner.getRunnerState());
        runner.setJobsList(jobList);
        assertEquals(JobRunner.states.READY, runner.getRunnerState());
        try {
            runner.start();
        } catch (JobRunnerException jre) {
            fail(String.format("Unexpected exception: %s", jre.getClass().getName()));
        }

        assertEquals(JobRunner.states.FINISHED, runner.getRunnerState());

    }

    @Test(expected = JobRunnerException.class)
    public void testRunnerOneJobException() {

        List<IJob> jobList = new ArrayList<>();

        jobList.add(() -> {
            JobRunnerUtils.sleepForSeconds(3);
            throw new RuntimeException("Ups!");
        });
        JobRunner runner = new JobRunner(jobList, 1);
        assertEquals(JobRunner.states.READY, runner.getRunnerState());
        runner.start();
        assertEquals(JobRunner.states.ERROR, runner.getRunnerState());
    }

    @Test(expected = JobRunnerException.class)
    public void testRunnerFiftyJobsOneExceptionAtStart() {

        List<IJob> jobList = new ArrayList<>();

        //Generate jobs for runner
        for (int i = 0; i < 50; i++) {
            jobList.add(() -> {
                System.out.println("Mock job E start.");
                JobRunnerUtils.sleepForSeconds(5);
                System.out.println("Mock job E end.");
            });
        }

        jobList.add(0, () -> {
            JobRunnerUtils.sleepForSeconds(3);
            throw new RuntimeException("Ups!");
        });
        JobRunner runner = new JobRunner(jobList, 10);
        assertEquals(JobRunner.states.READY, runner.getRunnerState());
        runner.start();
        assertEquals(JobRunner.states.ERROR, runner.getRunnerState());
    }

    @Test(expected = JobRunnerException.class)
    public void testRunnerFiftyJobsOneExceptionHalfway() {

        List<IJob> jobList = new ArrayList<>();

        //Generate jobs for runner
        for (int i = 0; i < 50; i++) {
            jobList.add(() -> {
                System.out.println("Mock job E start.");
                JobRunnerUtils.sleepForSeconds(2);
                System.out.println("Mock job E end.");
            });
        }

        jobList.add(25, () -> {
            JobRunnerUtils.sleepForSeconds(1);
            throw new RuntimeException("Ups!");
        });
        JobRunner runner = new JobRunner(jobList, 10);
        assertEquals(JobRunner.states.READY, runner.getRunnerState());
        runner.start();
        assertEquals(JobRunner.states.ERROR, runner.getRunnerState());
        assertTrue("Number of finished jobs shouldn't be higher than the error job.", runner.getFinishedJobs() <= 25);
    }

    @Test
    public void testRunnerFiveLightJobs() {

        List<IJob> jobList = new ArrayList<>();

        //Generate jobs for runner
        for (int i = 0; i < 5; i++) {
            jobList.add(() -> System.out.println("Mock job."));
        }

        JobRunner runner = new JobRunner(jobList, 10);

        assertEquals(JobRunner.states.READY, runner.getRunnerState());
        try {
            runner.start();
        } catch (JobRunnerException jre) {
            fail(String.format("Unexpected exception: %s", jre.getClass().getName()));
        }
        assertEquals(JobRunner.states.FINISHED, runner.getRunnerState());
    }

    @Test
    public void testRunnerFiftyLightJobsWait() {

        List<IJob> jobList = new ArrayList<>();

        //Generate jobs for runner
        for (int i = 0; i < 50; i++) {
            jobList.add(() -> System.out.println("Mock job B."));
        }

        JobRunner runner = new JobRunner(jobList, 10);

        assertEquals(JobRunner.states.READY, runner.getRunnerState());
        try {
            runner.start();
        } catch (JobRunnerException jre) {
            fail(String.format("Unexpected exception: %s", jre.getClass().getName()));
        }
        assertEquals(JobRunner.states.FINISHED, runner.getRunnerState());
    }

    @Test
    public void testRunnerTenHeavyJobs() {

        List<IJob> jobList = new ArrayList<>();
        //Generate jobs for runner
        for (int i = 0; i < 10; i++) {
            jobList.add(() -> {
                System.out.println("Mock job H start.");
                JobRunnerUtils.sleepForSeconds(5);
                System.out.println("Mock job H end.");
            });
        }
        JobRunner runner = new JobRunner(jobList, 1);
        assertEquals(JobRunner.states.READY, runner.getRunnerState());
        try {
            runner.start();
        } catch (JobRunnerException jre) {
            fail(String.format("Unexpected exception: %s", jre.getClass().getName()));
        }
        assertEquals(JobRunner.states.FINISHED, runner.getRunnerState());
    }

    @Test
    public void testRunnerFiveJobsWait() {

        List<IJob> jobList = new ArrayList<>();

        //Generate jobs for runner
        for (int i = 0; i < 5; i++) {
            jobList.add(() -> {
                JobRunnerUtils.sleepForSeconds(1);
                System.out.println("Mock job B.");
            });
        }

        JobRunner runner = new JobRunner(jobList, 1);

        assertEquals(JobRunner.states.READY, runner.getRunnerState());
        try {
            runner.start();
        } catch (JobRunnerException jre) {
            fail(String.format("Unexpected exception: %s", jre.getClass().getName()));
        }
        assertEquals(JobRunner.states.FINISHED, runner.getRunnerState());
    }
}