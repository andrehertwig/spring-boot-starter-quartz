package de.chandre.quartz.jobs;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import de.chandre.quartz.spring.queue.JobExecutionResult;
import de.chandre.quartz.spring.queue.QueueService;
import de.chandre.quartz.spring.queue.QueuedInstance;

/**
 * simple example of Quartz job
 * @author Andre
 *
 */
@Scope(scopeName=ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CallbackQueuedJob implements Job, QueuedInstance
{
	private static final Log LOGGER = LogFactory.getLog(CallbackQueuedJob.class);
	
	@Autowired
	private QueueService<Future<JobExecutionResult>> queueService;
	
	private JobExecutionContext context = null;
	
	@Override
	public String getName() {
		if (null != context) {
			return context.getTrigger().getKey().getName();
		}
		return QueuedInstance.super.getName();
	}
	
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException{
    	LOGGER.info("start executing callback job: " + jobExecutionContext.getTrigger().getKey().getName());
    	this.context = jobExecutionContext;
    	Future<JobExecutionResult> future= queueService.queueMe(this);
    	
    	try {
    		JobExecutionResult jer = future.get(10000L, TimeUnit.MILLISECONDS);
    		
    		if (jer.getException() != null) {
    			throw new JobExecutionException(jer.getException());
    		} else {
    			LOGGER.info("finished callback job with: " + jer.isSuccess());
    		}
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			throw new JobExecutionException(e);
		}
	}

	@Override
	public boolean run() {
		LOGGER.info("running the " + getClass().getSimpleName() +  " with trigger " + context.getTrigger().getKey().getName());
		return true;
	}
}
