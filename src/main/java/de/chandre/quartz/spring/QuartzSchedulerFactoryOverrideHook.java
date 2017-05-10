package de.chandre.quartz.spring;

import java.util.Properties;
import java.util.concurrent.Executor;

import org.springframework.scheduling.quartz.SchedulerFactoryBean;

/**
 * A hook to override some properties, maybe with instance-specific values after application start-up
 * @author André
 * @since 1.0.0
 */
public interface QuartzSchedulerFactoryOverrideHook {
	
	/**
	 * This method will be called after all SchedulerFactoryBean has been prepared<br>
	 * You are able to customize it, maybe for setting a own {@link Executor}
	 * 
	 * @param factory
	 * @return overridden properties
	 */
	SchedulerFactoryBean override(SchedulerFactoryBean factory, QuartzSchedulerProperties properties, Properties quartzProperties);
}
