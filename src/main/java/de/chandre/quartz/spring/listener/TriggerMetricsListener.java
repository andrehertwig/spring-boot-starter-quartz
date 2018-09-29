package de.chandre.quartz.spring.listener;

import javax.annotation.PostConstruct;

import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.Trigger.CompletedExecutionInstruction;
import org.quartz.listeners.TriggerListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.boot.actuate.metrics.GaugeService;
import org.springframework.util.StringUtils;

/**
 * Quartz metrics listener for Spring Boot actuator.<br>
 * requires a CounterService and GaugeService to be configured.
 * 
 * @author André
 * @since 1.0.5
 *
 */
public class TriggerMetricsListener extends TriggerListenerSupport {
	
	public static final String SEPARATOR = ".";
	
	public static final String METRIC_PREFIX = "quartz" + SEPARATOR;
	
	public static final String METRIC_INFIX_TYPE_JOB = "job" + SEPARATOR;
	public static final String METRIC_INFIX_TYPE_TRIGGER = "trigger" + SEPARATOR;
	
	
	public static final String METRIC_SUFFIX_START = SEPARATOR +"fired";
	public static final String METRIC_SUFFIX_COMPLETE = SEPARATOR + "completed";
	public static final String METRIC_SUFFIX_MISFIRE = SEPARATOR + "misfired" ;
	
	private boolean enabled;
	private String name;
	
	@Autowired(required=false)
	private CounterService counterService;
	
	@Autowired(required=false)
	private GaugeService gaugeService;
	
	public TriggerMetricsListener(boolean enabled, String name) {
		this.enabled = enabled;
		this.name = StringUtils.isEmpty(name) ? getClass().getSimpleName() : name;
	}
	
	public boolean isActive() {
		return this.enabled && (this.counterService != null || this.gaugeService != null);
	}
	
	@PostConstruct
	public void init() {
		getLog().info(this.getClass().getName() + " is " + (isActive() ? "active" :"deactivated"));
	}

	@Override
	public String getName() {
		return this.name;
	}
	
	protected void mesure(String suffix, Trigger trigger, JobExecutionContext context,
			CompletedExecutionInstruction triggerInstructionCode) {
		if (null == this.counterService && null == this.gaugeService) {
			return;
		}
		getLog().trace("exposing metrics");
		
		String jobKey = METRIC_PREFIX + METRIC_INFIX_TYPE_JOB + trigger.getJobKey().getGroup() 
				+ SEPARATOR + trigger.getJobKey().getName() + suffix;
		String triggerKey = METRIC_PREFIX + METRIC_INFIX_TYPE_TRIGGER +trigger.getKey().getGroup() 
				+ SEPARATOR + trigger.getKey().getName() + suffix;
		
		if (null != this.counterService) {
			//count job group
			this.counterService.increment(METRIC_PREFIX + METRIC_INFIX_TYPE_JOB +trigger.getJobKey().getGroup() + suffix);
			//count job group and job name
			this.counterService.increment(jobKey);
			//count trigger group and trigger name
			this.counterService.increment(triggerKey);
			// count finish code
			if (null != triggerInstructionCode) {
				this.counterService.increment(triggerKey + SEPARATOR + triggerInstructionCode.name());
				//if a job has more than one trigger .
				this.counterService.increment(jobKey + SEPARATOR + triggerInstructionCode.name());
			}
		}
		
		if (null != context && null != this.gaugeService) {
			if (context.getJobRunTime() != -1) {
				gaugeService.submit(triggerKey, Long.valueOf(context.getJobRunTime()).doubleValue());
				//if a job has more than one trigger .
				gaugeService.submit(jobKey, Long.valueOf(context.getJobRunTime()).doubleValue());
			}
		}
		
	}
	
	@Override
	public void triggerFired(Trigger trigger, JobExecutionContext context) {
		mesure(METRIC_SUFFIX_START, trigger, context, null);
	}
	
	@Override
	public void triggerMisfired(Trigger trigger) {
		mesure(METRIC_SUFFIX_MISFIRE, trigger, null, null);
	}
	
	@Override
	public void triggerComplete(Trigger trigger, JobExecutionContext context,
			CompletedExecutionInstruction triggerInstructionCode) {
		mesure(METRIC_SUFFIX_COMPLETE, trigger, context, triggerInstructionCode);
	}

}
