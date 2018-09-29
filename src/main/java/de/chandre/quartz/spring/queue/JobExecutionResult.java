package de.chandre.quartz.spring.queue;

import java.io.Serializable;

/**
 * Job execution result for callable queue service
 * @author André
 * @since 1.0.5
 *
 */
public class JobExecutionResult implements Serializable {
	private static final long serialVersionUID = -7109542826143909L;
	
	private boolean success;
	private Throwable exception;
	
	public JobExecutionResult(boolean success) {
		this(success, null);
	}
	
	public JobExecutionResult(boolean success, Throwable exception) {
		super();
		this.success = success;
		this.exception = exception;
	}

	/**
	 * if job returned true for {@link QueuedInstance#run()}
	 * @return
	 */
	public boolean isSuccess() {
		return success;
	}
	
	public void setSuccess(boolean success) {
		this.success = success;
	}
	
	/**
	 * when job throws an exception
	 * @return null or exception
	 */
	public Throwable getException() {
		return exception;
	}
	
	public void setException(Throwable exception) {
		this.exception = exception;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("JobExecutionResult [success=").append(success).append(", exception=").append(exception)
				.append("]");
		return builder.toString();
	}
}
