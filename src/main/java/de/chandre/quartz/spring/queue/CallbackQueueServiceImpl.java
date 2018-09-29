package de.chandre.quartz.spring.queue;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.annotation.PreDestroy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This service creates a {@link JobCallable} and submits it to a task executor.  
 * as a result you will get the a {@link Future} which returns a {@link JobExecutionResult}.
 * 
 * @author André
 * @since 1.0.5
 */
public class CallbackQueueServiceImpl implements QueueService<Future<JobExecutionResult>> {
	
	private static final Log LOG = LogFactory.getLog(CallbackQueueServiceImpl.class);
	
	private ExecutorService scheduledExecutorService = Executors.newSingleThreadExecutor();
	
	public CallbackQueueServiceImpl() {
		super();
	}
	
	@PreDestroy
	public void destroy() {
		scheduledExecutorService.shutdown();
	}
	
	@Override
	public Future<JobExecutionResult> queueMe(QueuedInstance instance) {
		LOG.debug("queuing job " + instance.getName() + " with hash: "+ instance.hashCode());
		JobCallable callable = new JobCallable(instance);
		Future<JobExecutionResult> callResult = scheduledExecutorService.submit(callable);
		return callResult;
	}
}
