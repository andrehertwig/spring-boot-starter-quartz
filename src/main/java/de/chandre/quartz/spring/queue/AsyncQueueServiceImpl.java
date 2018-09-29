package de.chandre.quartz.spring.queue;

import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The async queue service puts the job instance to a queue and and returns a boolean value.<br>
 * Execution will happen afterwards.<br>
 * For Quartz the job may finished successfully and really fast. 
 * As alternative the {@link CallbackQueueServiceImpl} will return a result.
 * 
 * @author André
 * @since 1.0.5
 */
public class AsyncQueueServiceImpl implements QueueService<Boolean> {
	
	private static final Log LOG = LogFactory.getLog(AsyncQueueServiceImpl.class);
	
	private Map<String, Queue<QueuedInstance>> jobQueueMap = new ConcurrentHashMap<>();
	
	private ExecutorService scheduledExecutorService;
	
	private boolean multipleInstancesAllowed;
	
	/**
	 * When using this constructor only one instance with same name will be queued
	 */
	public AsyncQueueServiceImpl() {
		this(false);
	}
	
	/**
	 * 
	 * @param allowMultipleInstances to allow that more than instance to be queued with same name
	 */
	public AsyncQueueServiceImpl(boolean allowMultipleInstances) {
		super();
		this.multipleInstancesAllowed = allowMultipleInstances;
	}
	
	@PostConstruct
	public void init() {
		runQueue();
	}
	
	@PreDestroy
	public void destroy() {
		scheduledExecutorService.shutdown();
	}
	
	
	@Override
	public Boolean queueMe(QueuedInstance instance) {
		LOG.debug("try queuing job " + instance.getName() + " with hash: "+ instance.hashCode());
		Queue<QueuedInstance> jobQueue = jobQueueMap.get(instance.getGroup());
		if (null == jobQueue) {
			jobQueue = new ConcurrentLinkedQueue<QueuedInstance>();
			jobQueueMap.put(instance.getGroup(), jobQueue);
		}
		if (!multipleInstancesAllowed) {
			Optional<QueuedInstance> queuedInstance = jobQueue.stream().filter(qi -> qi.getName().equals(instance.getName())).findFirst();
			if (queuedInstance.isPresent()) {
				return Boolean.FALSE;
			}
		}
		return Boolean.valueOf(jobQueue.add(instance));
	}
	
	Map<String, Queue<QueuedInstance>> getQueueMap() {
		return jobQueueMap;
	}
	
	private void runQueue() {
		scheduledExecutorService = Executors.newSingleThreadExecutor();
		scheduledExecutorService.execute(new QueueTask(this));
	}
	
	private static class QueueTask implements Runnable {
		
		private AsyncQueueServiceImpl service;

		QueueTask(AsyncQueueServiceImpl serviceInstance) {
			this.service = serviceInstance;
		}
		@Override
		public void run() {
			
			while(true) {
				
				service.getQueueMap().values().parallelStream().forEach(jobQueue -> {
					QueuedInstance queuedInstance = jobQueue.poll();
					if (null != queuedInstance) {
						LOG.info("starting queued quartz instance " + queuedInstance.getName());
						try {
							boolean result = queuedInstance.run();
							if (!result) {
								LOG.info("queued quartz instance " + queuedInstance.getName() + " ended with false");
							}
						} catch (Exception e) {
							LOG.error("queued quartz instance thowed an exception: " + queuedInstance.getName());
							LOG.error(e.getMessage(), e);
						}
					}
				});
			}
		}
	}
}
