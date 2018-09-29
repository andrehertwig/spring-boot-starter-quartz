package de.chandre.quartz.spring.queue;

import java.util.concurrent.Callable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author André
 * @since 1.0.5
 *
 */
public class JobCallable implements Callable<JobExecutionResult> {
	
	private static final Log LOG = LogFactory.getLog(JobCallable.class);

	private QueuedInstance queuedInstance;
	
	public JobCallable(QueuedInstance queuedInstance) {
		this.queuedInstance = queuedInstance;
	}
	
	public String getName() {
		return this.queuedInstance.getName();
	}
	
	@Override
	public JobExecutionResult call() throws Exception {
		LOG.info("starting queued quartz instance " + queuedInstance.getName());
		try {
			boolean result = queuedInstance.run();
			if (!result) {
				LOG.info("queued quartz instance " + queuedInstance.getName() + " ended with false");
			}
			return new JobExecutionResult(result);
		} catch (Throwable e) {
			LOG.error("queued quartz instance thowed an exception: " + queuedInstance.getName());
			LOG.error(e.getMessage(), e);
			return new JobExecutionResult(false, e);
		}
	}
}
