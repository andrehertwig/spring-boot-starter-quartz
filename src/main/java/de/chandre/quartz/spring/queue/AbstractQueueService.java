package de.chandre.quartz.spring.queue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.apache.commons.logging.Log;

/**
 * 
 * @author André
 * @since 1.0.5
 *
 * @param <T>
 */
public abstract class AbstractQueueService<T> implements QueueService<T> {
	
	private long waitForTerminationTime = 10000L;
	
	private TimeUnit waitForTerminationUnit = TimeUnit.MILLISECONDS;
	
	/**
	 * should return a list of all active groups submitted to the queue service
	 * @return
	 */
	protected abstract Collection<String> getGroupKeys();
	
	@Override
	public Collection<String> getGroups() {
		List<String> list = new ArrayList<>();
		list.add(QueuedInstance.DEFAULT_GROUP);
		Collection<String> keys = getGroupKeys();
		if (null != keys) {
			list.addAll(getGroupKeys());
		}
		return Collections.unmodifiableList(list);
	}

	/**
	 * This wait time is only for shutdown of the used executor services
	 * @return 10000L per default if not set manually
	 */
	public long getWaitForTerminationTime() {
		return waitForTerminationTime;
	}

	/**
	 * This wait time is only for shutdown of the used executor services
	 * 
	 * @param waitForTerminationTime a long value corresponding to {@link #setWaitForTerminationUnit(TimeUnit)}
	 */
	public void setWaitForTerminationTime(long waitForTerminationTime) {
		this.waitForTerminationTime = waitForTerminationTime;
	}

	/**
	 * time unit for {@link #getWaitForTerminationTime()}
	 * 
	 * @return {@link TimeUnit#MILLISECONDS} per default if not set manually
	 */
	public TimeUnit getWaitForTerminationUnit() {
		return waitForTerminationUnit;
	}

	/**
	 * time unit for {@link #setWaitForTerminationTime(long)}
	 * @param waitForTerminationUnit
	 */
	public void setWaitForTerminationUnit(TimeUnit waitForTerminationUnit) {
		this.waitForTerminationUnit = waitForTerminationUnit;
	}
	
	/**
	 * shuts down the executor service waiting the configured time and catches possible exceptions
	 * 
	 * @param executorService
	 * @param LOG the logger to log exceptions
	 */
	protected void shutdownExecutorLogging(ExecutorService executorService, Log LOG) {
		executorService.shutdown();
		try {
			executorService.awaitTermination(getWaitForTerminationTime(), getWaitForTerminationUnit());
		} catch (InterruptedException e) {
			if (null != LOG) {
				LOG.warn("ExecutorService didn't shut down within " + getWaitForTerminationTime() + " " + getWaitForTerminationUnit());
				LOG.debug(e.getMessage(), e);
			}
			executorService.shutdownNow();
		}
	}
	
	/**
	 * shuts down the executor service waiting the configured time and catches possible exceptions. applys the function in case of exception
	 * 
	 * @param executorService
	 * @param logException function with a Void return 
	 */
	protected void shutdownExecutor(ExecutorService executorService, Function<Exception, Void> logException) {
		executorService.shutdown();
		try {
			executorService.awaitTermination(getWaitForTerminationTime(), getWaitForTerminationUnit());
		} catch (InterruptedException e) {
			if (null != logException) {
				logException.apply(e);
			}
			executorService.shutdownNow();
		}
	}
	
}
