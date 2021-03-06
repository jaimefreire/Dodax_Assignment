# Dodax_Assignment
Dodax JobRunner assignment
- Solution to Dodax JobRunner Assignment.

Assumptions:
- Each job should be made thread-safe
- An exception in a running job triggers the immediate cancellation of the failing job and any other runnning jobs. 
  Also no more jobs are started and the runner ends in ERROR state; information of the number of succesful / cancelled jobs is also available
  from the Jobrunner.
- Each instance of the job runner executes each of the jobs in the provided job list in parallel according to the
provided value (max. number of parallel jobs) which defaults to 10; this is done once.
  If errors occurr, a new runner instance should be created with the updated job list.

- The job runner exposes some information to the caller:
    - Current state (Init, Ready, Started, Finished, Error).
    - Number of jobs and it's state (added, running, finished and cancelled).
    - Job cancellation is triggered on all running tasks after an exception is detected in a running job.
    
Requirements.
- At least Java Runtime Environment 8 is needed; the runner uses some lambdas, streams and the new Java DateTime API.

Instructions.
- Clone the git repository and run the tests with : gradle test (Running time around 70 seconds for this version).

Jaime Freire
jaime.freire@gmail.com
