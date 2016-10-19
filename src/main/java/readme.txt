= Files =

 * interfaces.IJob.java: interfaces.IJob interface

= Task =

In our backend application, there are multiple classes implementing the interfaces.IJob interface.
Each of this implementations performs certain operations and is scheduled to run at a certain time on our servers.
Because the amount of jobs we have to run increases steadily, we want to create a new job,
which takes a list of interfaces.IJob instances and runs them in parallel. The amount of threads that the new implementation is using to run the jobs
in parallel should be configurable. If one of the jobs throws an exception, all other currently running jobs should be stopped as well and
the execute() method should pass the exception to the caller. To make sure the implementation works as designed UnitTests are required.

= Deliverables =

 * New interfaces.IJob implementation including UnitTests

