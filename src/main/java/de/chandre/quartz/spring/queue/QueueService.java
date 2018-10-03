package de.chandre.quartz.spring.queue;

import java.util.Collection;

import org.quartz.listeners.JobChainingJobListener;

/**
 * The service queuing the job object reference to be executed one after another.<br>
 * This service points to depended Jobs which may use/modify same resources or should not run together at all.<br>
 * If you want to be sure that Job A will run first and Job B after them, consider using the {@link JobChainingJobListener} instead.
 * 
 * @author André
 * @since 1.0.5
 */
public interface QueueService<T> {

	/**
	 * adds the instance to queue <br>
	 * @param instance the job object reference
	 * @return the value regarding the implementations ({@link CallbackQueueServiceImpl}, {@link AsyncQueueServiceImpl})
	 */
	T queueMe(QueuedInstance instance);
	
	/**
	 * returns all active groups
	 * 
	 * @return
	 */
	Collection<String> getGroups();

}
