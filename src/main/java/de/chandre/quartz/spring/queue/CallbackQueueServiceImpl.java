package de.chandre.quartz.spring.queue;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.annotation.PreDestroy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This service creates a {@link JobCallable} and submits it to a SingleThreadExecutor.<br>
 * as a result you will get the a {@link Future} which returns a {@link JobExecutionResult} or <i>null</i> if no multiple instance are allowed.<br>
 * <br>
 * For example, if your Job implements {@link QueuedInstance} you can do the following in your execution method: <br>
 * <code>
 * Future<JobExecutionResult> future= queueService.queueMe(this);<br>
 * JobExecutionResult jer = future.get(10000L, TimeUnit.MILLISECONDS);<br>
 * </code>
 * 
 * @author André
 * @since 1.0.5
 */
public class CallbackQueueServiceImpl extends AbstractQueueService<Future<JobExecutionResult>> {
	
	private static final Log LOG = LogFactory.getLog(CallbackQueueServiceImpl.class);
	
	private ExecutorService defaultExecutorService = Executors.newSingleThreadExecutor();
	
	private Map<String, ExecutorService> jobQueueMap = new ConcurrentHashMap<>();
	private Set<String> offeredInstances = Collections.newSetFromMap(new ConcurrentHashMap<>());
	
	private boolean multipleInstancesAllowed;
	
	public CallbackQueueServiceImpl() {
		this(false);
	}
	
	/**
	 * 
	 * @param allowMultipleInstances to configure if more than one {@link QueuedInstance} 
	 * 	with same {@link QueuedInstance#getKey()} is allowed (true) ore not (false). 
	 */
	public CallbackQueueServiceImpl(boolean allowMultipleInstances) {
		super();
		this.multipleInstancesAllowed = allowMultipleInstances;
	}


	@PreDestroy
	public void destroy() {
		shutdown();
	}
	
	private void shutdown() {
		super.shutdownExecutor(defaultExecutorService, LOG);
		this.defaultExecutorService = null;
		this.jobQueueMap.values().stream().parallel().forEach(executor -> shutdownExecutor(executor, LOG));
		this.jobQueueMap.clear();
		this.offeredInstances.clear();
	}
	
	@Override
	public Future<JobExecutionResult> queueMe(QueuedInstance instance) {
		String instanceKey = instance.getKey();
		LOG.debug("try queuing job "+ instanceKey + " with hash: "+ instance.hashCode());
		
		if (!multipleInstancesAllowed && offeredInstances.contains(instanceKey)) {
			return null;
		}
		offeredInstances.add(instanceKey);
		JobCallable callable = new JobCallable(instance, offeredInstances);
		
		Future<JobExecutionResult> callResult = null;
		if (QueuedInstance.DEFAULT_GROUP.equals(instance.getGroup())) {
			callResult = this.defaultExecutorService.submit(callable);
		} else {
			 ExecutorService executorService = this.jobQueueMap.get(instance.getGroup());
			 if (null == executorService) {
				 executorService = Executors.newSingleThreadExecutor();
				 ExecutorService otherExecutorService = this.jobQueueMap.putIfAbsent(instance.getGroup(), executorService);
				 if (null !=otherExecutorService) {
					 executorService = otherExecutorService;
				 }
			 }
			 callResult = executorService.submit(callable);
		}
		return callResult;
	}
	
	@Override
	protected Collection<String> getGroupKeys() {
		return jobQueueMap.keySet();
	}
	
	public void reset() {
		shutdown();
		this.defaultExecutorService = Executors.newSingleThreadExecutor();
	}
}
