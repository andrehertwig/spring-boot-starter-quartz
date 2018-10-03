package de.chandre.quartz.spring.queue;

import java.util.Set;
import java.util.concurrent.Callable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The callable used by {@link CallbackQueueServiceImpl}
 * 
 * @author André
 * @since 1.0.5
 *
 */
public class JobCallable implements Callable<JobExecutionResult> {
	
	private static final Log LOG = LogFactory.getLog(JobCallable.class);

	private QueuedInstance queuedInstance;
	private Set<String> offeredInstances;
	
	public JobCallable(QueuedInstance queuedInstance, Set<String> offeredInstances) {
		this.queuedInstance = queuedInstance;
		this.offeredInstances = offeredInstances;
	}
	
	public String getName() {
		return this.queuedInstance.getName();
	}
	
	@Override
	public JobExecutionResult call() throws Exception {
		LOG.debug("starting queued quartz instance " + queuedInstance.getName());
		try {
			if (!offeredInstances.remove(queuedInstance.getKey())) {
				LOG.warn("queued quartz instance " + queuedInstance.getName() + " hat not been removed from offered jobs.");
			}
			//run the logic.
			boolean result = queuedInstance.run();
			if (!result) {
				LOG.debug("queued quartz instance " + queuedInstance.getName() + " ended with false");
			}
			return new JobExecutionResult(result);
		} catch (Throwable e) {
			LOG.debug("queued quartz instance thowed an exception: " + queuedInstance.getName());
			LOG.debug(e.getMessage(), e);
			return new JobExecutionResult(false, e);
		}
	}
}
