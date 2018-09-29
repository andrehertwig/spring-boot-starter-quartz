package de.chandre.quartz.spring.queue;

/**
 * The service queuing the job  object reference to be executed one after another.<br>
 * This service points to depended Jobs which may use/modify same resources or should not run together at all
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

}
