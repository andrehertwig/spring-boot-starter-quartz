package de.chandre.quartz.spring;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.spi.JobFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import de.chandre.quartz.spring.QuartzSchedulerProperties.Persistence;
import de.chandre.quartz.spring.QuartzSchedulerProperties.SchedulerFactory;

/**
 * Spring-Boot auto-configuration for Quartz-Scheduler
 * @author André Hertwig
 * @since 1.0.0
 */
@Configuration
@EnableConfigurationProperties(QuartzSchedulerProperties.class)
@ConditionalOnClass(Scheduler.class)
@ConditionalOnProperty(prefix = QuartzSchedulerProperties.PREFIX, name = "enabled", matchIfMissing = true)
public class QuartzSchedulerAutoConfiguration {
	
	private static final Log LOGGER = LogFactory.getLog(QuartzSchedulerAutoConfiguration.class);
	
	public static final String QUARTZ_PROPERTIES_BEAN_NAME = "quartzProperties";
	public static final String QUARTZ_SCHEDULER_FACTORY_BEAN_NAME = "autoSchedulerFactory";
	public static final String QUARTZ_JOB_FACTORY_BEAN_NAME = "autoJobFactory";
	
	@Bean(name = QUARTZ_JOB_FACTORY_BEAN_NAME)
	@ConditionalOnMissingBean(name = QUARTZ_JOB_FACTORY_BEAN_NAME)
    public JobFactory autoJobFactory(ApplicationContext applicationContext) {
        AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        return jobFactory;
    }

	@Bean(name = QUARTZ_SCHEDULER_FACTORY_BEAN_NAME)
	@ConditionalOnMissingBean(name = QUARTZ_SCHEDULER_FACTORY_BEAN_NAME)
	public SchedulerFactoryBean autoSchedulerFactory(ApplicationContext applicationContext, JobFactory jobFactory,
			QuartzSchedulerProperties properties, @Qualifier(QUARTZ_PROPERTIES_BEAN_NAME) Properties quartzProperties) {
		
		LOGGER.debug("creating SchedulerFactory");
		
		SchedulerFactoryBean factory = BeanUtils.instantiateClass(SchedulerFactoryBean.class);
		
        factory.setApplicationContext(applicationContext);
        factory.setJobFactory(jobFactory);
        
        Persistence persistenceSettings = properties.getPersistence();
        if (persistenceSettings.isPersisted()) {
        	factory.setDataSource(getDataSource(applicationContext, persistenceSettings));
        	if (persistenceSettings.isUsePlatformTxManager()) {
        		PlatformTransactionManager txManager = getTransactionManager(applicationContext);
            	if (null != txManager) {
                	factory.setTransactionManager(txManager);
                }
        	}
        }
        
        SchedulerFactory factorySettings = properties.getSchedulerFactory();
        factory.setSchedulerName(factorySettings.getSchedulerName());
        factory.setPhase(factorySettings.getPhase());
        factory.setStartupDelay(factorySettings.getStartupDelay());
        factory.setAutoStartup(factorySettings.isAutoStartup());
        factory.setWaitForJobsToCompleteOnShutdown(factorySettings.isWaitForJobsToCompleteOnShutdown());
        factory.setOverwriteExistingJobs(factorySettings.isOverwriteExistingJobs());
        factory.setExposeSchedulerInRepository(factorySettings.isExposeSchedulerInRepository());
        
        factory.setQuartzProperties(quartzProperties);
        
        Collection<Trigger> triggers = getTriggers(applicationContext);
        if (null != triggers && !triggers.isEmpty()) {
        	factory.setTriggers(triggers.toArray(new Trigger[triggers.size()]));
        	LOGGER.info("staring scheduler factory with " + triggers.size() + " job triggers");
        } else {
        	LOGGER.info("staring scheduler factory with 0 job triggers");
        }
        
        QuartzSchedulerFactoryOverrideHook hook = getQuartzSchedulerFactoryOverrideHook(applicationContext);
        if (null != hook) {
        	factory = hook.override(factory, properties, quartzProperties);
        }
        
		return factory;
	}
	
	private Collection<Trigger> getTriggers(ApplicationContext applicationContext) {
		Map<String, Trigger> triggers = applicationContext.getBeansOfType(Trigger.class);
		if (null != triggers && !triggers.isEmpty()) {
			return triggers.values();
		}
		return null;
	}
	
	private PlatformTransactionManager getTransactionManager(ApplicationContext applicationContext) {
		return applicationContext.getBean(PlatformTransactionManager.class);
	}
	
	private DataSource getDataSource(ApplicationContext applicationContext, Persistence persistenceSettings) {
		DataSource dataSource = null;
		Map<String, DataSource> datasources = applicationContext.getBeansOfType(DataSource.class);
		int dsSize = null != datasources ? datasources.size() : 0;
		if (null != datasources && null != persistenceSettings.getDataSourceName()) {
			dataSource = datasources.get(persistenceSettings.getDataSourceName());
		} else if (null != datasources && dsSize == 1 && null == persistenceSettings.getDataSourceName()){
			dataSource = datasources.values().iterator().next();
		}
		
	    if (dataSource == null) {
	    	throw new BeanInitializationException(
	    			"A datasource is required when starting Quartz-Scheduler in persisted mode. " +
	    			"No DS found in map with size: " + dsSize + ", and configured DSName: " + persistenceSettings.getDataSourceName());
	    }
	    return dataSource;
	}
	
	private QuartzSchedulerFactoryOverrideHook getQuartzSchedulerFactoryOverrideHook(ApplicationContext applicationContext) {
		try {
			return applicationContext.getBean(QuartzSchedulerFactoryOverrideHook.class);
		} catch (Exception e) {
			LOGGER.info("no QuartzSchedulerFactoryOverrideHook configured");
			LOGGER.debug(e.getMessage(), e);
		}
		return null;
	}
	
	@Bean(name = QUARTZ_PROPERTIES_BEAN_NAME)
	@ConditionalOnMissingBean(name = QUARTZ_PROPERTIES_BEAN_NAME)
	public Properties quartzProperties(ApplicationContext applicationContext, QuartzSchedulerProperties properties)
			throws IOException {

		Properties quartzProperties = null;
		
		if (properties.isOverrideConfigLocationProperties()) {
			//merge properties from file with springs application properties
			quartzProperties = loadConfigLocationProperties(applicationContext, properties);
			quartzProperties.putAll(properties.getProperties());
		} else if (null != properties.getProperties() && !properties.getProperties().isEmpty()) {
			// only use the spring application properties
			quartzProperties = getConfiguredProperties(properties);
		}  else {
			// only use the properties from file
			quartzProperties = loadConfigLocationProperties(applicationContext, properties);
		}
		
		//Call the override hook to possibly change runtime data
		QuartzPropertiesOverrideHook hook = getQuartzPropOverrideHook(applicationContext);
		if (null != hook) {
			quartzProperties = hook.override(quartzProperties);
		}

		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Quartz-Properties");
			quartzProperties.entrySet().forEach(entry -> {
				LOGGER.trace("    " + entry.getKey() + " = " + entry.getValue());
			});
		}

		return quartzProperties;
	}

	private Properties getConfiguredProperties(QuartzSchedulerProperties properties) {
		Properties quartzProperties = new Properties();
		quartzProperties.putAll(properties.getProperties());
		return quartzProperties;
	}
	
	private Properties loadConfigLocationProperties(ApplicationContext applicationContext, 
			QuartzSchedulerProperties properties) throws IOException {
		
		String location = properties.getPropertiesConfigLocation();
		if(null == location || location.trim().length() == 0) {
			location = QuartzSchedulerProperties.DEFAULT_CONFIG_LOCATION;
			LOGGER.debug("using default 'quartz.properties' from classpath: " + location);
		} else {
			LOGGER.debug("using 'quartz.properties' from location: " + location);
		}
		PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
		propertiesFactoryBean.setLocation(applicationContext.getResource(location));
		propertiesFactoryBean.afterPropertiesSet();
		return propertiesFactoryBean.getObject();
	}
	
	private QuartzPropertiesOverrideHook getQuartzPropOverrideHook(ApplicationContext applicationContext) {
		try {
			return applicationContext.getBean(QuartzPropertiesOverrideHook.class);
		} catch (Exception e) {
			LOGGER.info("no QuartzPropertiesOverrideHook configured");
			LOGGER.debug(e.getMessage(), e);
		}
		return null;
	}
	
}
