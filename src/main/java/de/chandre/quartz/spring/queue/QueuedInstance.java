package de.chandre.quartz.spring.queue;

/**
 * Interface for instances to queue in {@link QueueService}.<br>
 * A job which should be queued has to implement this interface.<br>
 * Furthermore a {@link QueueService} must be started and within the job you must call {@link QueueService#queueMe(QueuedInstance)}<br>
 * Depending on the implementation you may have other things to do...
 * 
 * @author André
 * @since 1.0.5
 */
public interface QueuedInstance {
	
	String DEFAULT_GROUP = "default";
	
	/**
	 * identification name of queue group.
	 * @return 
	 */
	default String getGroup() {
		return DEFAULT_GROUP;
	};
	
	/**
	 * identification name of object. Only one instance with same name will be queued per group.
	 * @return
	 */
	default String getName() {
		return getClass().getSimpleName();
	};
	
	/**
	 * implement your code here to be executed
	 * @return should return true if job ended successfully
	 */
	boolean run();

}
