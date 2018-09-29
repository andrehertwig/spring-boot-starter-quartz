package de.chandre.quartz.spring;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobListener;
import org.quartz.Scheduler;
import org.quartz.SchedulerListener;
import org.quartz.Trigger;
import org.quartz.TriggerListener;
import org.quartz.impl.SchedulerRepository;
import org.quartz.spi.JobFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import de.chandre.quartz.spring.QuartzSchedulerProperties.Persistence;
import de.chandre.quartz.spring.QuartzSchedulerProperties.SchedulerFactory;
import de.chandre.quartz.spring.listener.TriggerMetricsListener;

/**
 * Spring-Boot auto-configuration for Quartz-Scheduler
 * @author André Hertwig
 * @since 1.0.0
 */
@Configuration
@EnableConfigurationProperties(QuartzSchedulerProperties.class)
@ConditionalOnClass(Scheduler.class)
public class QuartzSchedulerAutoConfiguration {
	
	private static final Log LOGGER = LogFactory.getLog(QuartzSchedulerAutoConfiguration.class);
	
	public static final String QUARTZ_PROPERTIES_BEAN_NAME = "quartzProperties";
	public static final String QUARTZ_SCHEDULER_FACTORY_BEAN_NAME = "autoSchedulerFactory";
	public static final String QUARTZ_JOB_FACTORY_BEAN_NAME = "autoJobFactory";
	public static final String QUARTZ_SCHEDULER_METRICS_LISTENER_BEAN_NAME = "quartzMetricsListener";
	
	@Configuration
	@ConditionalOnProperty(prefix = QuartzSchedulerProperties.PREFIX, name = "enabled", havingValue="true", matchIfMissing = true)
	@ConditionalOnMissingBean(name = QUARTZ_SCHEDULER_FACTORY_BEAN_NAME)
	protected static class SchedulerFactoryConfiguration {
		
		private static Collection<Trigger> getTriggers(ApplicationContext applicationContext) {
			Map<String, Trigger> triggers = applicationContext.getBeansOfType(Trigger.class);
			if (null != triggers && !triggers.isEmpty()) {
				return triggers.values();
			}
			return null;
		}
		
		private static PlatformTransactionManager getTransactionManager(ApplicationContext applicationContext, String txManagerBeanName) {
			Map<String, PlatformTransactionManager> txManagers = applicationContext.getBeansOfType(PlatformTransactionManager.class);
			if (null != txManagers && txManagers.size() > 0) {
				if (txManagers.size() == 1) {
					LOGGER.debug("only one txManager found, returning: " + txManagers.keySet().iterator().next());
					return txManagers.values().iterator().next();
				} else if (!StringUtils.isEmpty(txManagerBeanName)) {
					LOGGER.debug("more than one txManager found, try using: " + txManagerBeanName);
					PlatformTransactionManager txManager = txManagers.get(txManagerBeanName);
					if (null == txManager) {
						LOGGER.warn("QuartzSchedulerAutoConfiguration is configured to use " + txManagerBeanName 
								+ " as PlatformTransactionManager, but no bean for this name has been found in context!");
					}
					return txManager;
				} else {
					LOGGER.warn("QuartzSchedulerAutoConfiguration is configured to use PlatformTransactionManager, "
							+ "but more than one has been found in context! "
							+ "Consider using quartz.persistence.platform-tx-manager-bean-name in pallication configuration.");
				}
			} else {
				LOGGER.warn("QuartzSchedulerAutoConfiguration is configured to use PlatformTransactionManager, "
						+ "but no bean of this type has been found in context!");
			}
			return null;
		}
		
		private static DataSource getDataSource(ApplicationContext applicationContext, Persistence persistenceSettings) {
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
		
		private static QuartzSchedulerFactoryOverrideHook getQuartzSchedulerFactoryOverrideHook(ApplicationContext applicationContext) {
			try {
				return applicationContext.getBean(QuartzSchedulerFactoryOverrideHook.class);
			} catch (Exception e) {
				LOGGER.info("no QuartzSchedulerFactoryOverrideHook configured");
				LOGGER.trace(e.getMessage(), e);
			}
			return null;
		}
		
		@Bean(name = QUARTZ_JOB_FACTORY_BEAN_NAME)
		@ConditionalOnMissingBean(name = QUARTZ_JOB_FACTORY_BEAN_NAME)
	    public JobFactory autoJobFactory(ApplicationContext applicationContext,
	    		@Autowired(required=false) QuartzSchedulerProperties properties) {
			if (null == properties) {
				LOGGER.warn("no QuartzSchedulerProperties found, consider to set quartz.enabled=true in properties");
				return null;
			}
	        AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
	        jobFactory.setApplicationContext(applicationContext);
	        return jobFactory;
	    }
		
		@Bean(name = QUARTZ_PROPERTIES_BEAN_NAME)
		@ConditionalOnMissingBean(name = QUARTZ_PROPERTIES_BEAN_NAME)
		public Properties quartzProperties(ApplicationContext applicationContext, 
				@Autowired(required=false) QuartzSchedulerProperties properties)
				throws IOException {
			
			if (null == properties) {
				LOGGER.warn("no QuartzSchedulerProperties found, consider to set quartz.enabled=true in properties");
				return null;
			}

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

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Quartz-Properties");
				quartzProperties.entrySet().forEach(entry -> {
					LOGGER.debug("    " + entry.getKey() + " = " + entry.getValue());
				});
			}

			return quartzProperties;
		}

		private static Properties getConfiguredProperties(QuartzSchedulerProperties properties) {
			Properties quartzProperties = new Properties();
			quartzProperties.putAll(properties.getProperties());
			return quartzProperties;
		}
		
		private static Properties loadConfigLocationProperties(ApplicationContext applicationContext, 
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
		
		private static QuartzPropertiesOverrideHook getQuartzPropOverrideHook(ApplicationContext applicationContext) {
			try {
				return applicationContext.getBean(QuartzPropertiesOverrideHook.class);
			} catch (Exception e) {
				LOGGER.info("no QuartzPropertiesOverrideHook configured");
				LOGGER.trace(e.getMessage(), e);
			}
			return null;
		}
		
		@Bean(name = QUARTZ_SCHEDULER_FACTORY_BEAN_NAME)
		@ConditionalOnMissingBean
		public SchedulerFactoryBean autoSchedulerFactory(ApplicationContext applicationContext, JobFactory jobFactory,
				@Autowired(required=false) QuartzSchedulerProperties properties,
				@Qualifier(QUARTZ_PROPERTIES_BEAN_NAME) Properties quartzProperties,
				@Autowired(required=false) List<TriggerListener> triggerListeners,
				@Autowired(required=false) List<JobListener> jobListeners,
				@Autowired(required=false) List<SchedulerListener> schedulerListeners) {
			
			if (null == properties) {
				LOGGER.warn("no QuartzSchedulerProperties found, consider to set quartz.enabled=true in properties");
				return null;
			}
			
			LOGGER.debug("creating SchedulerFactory");
			 
	        SchedulerFactory factorySettings = properties.getSchedulerFactory();
			SchedulerRepository schedulerRepo = SchedulerRepository.getInstance();
			if (schedulerRepo.remove(QUARTZ_SCHEDULER_FACTORY_BEAN_NAME)) {
				LOGGER.debug("removed scheduler from SchedulerRepository with name: " + QUARTZ_SCHEDULER_FACTORY_BEAN_NAME);
			}
			if (null != factorySettings.getSchedulerName() && schedulerRepo.remove(factorySettings.getSchedulerName())) {
				LOGGER.debug("removed scheduler from SchedulerRepository with name: " + factorySettings.getSchedulerName());
			}
			
			SchedulerFactoryBean factory = BeanUtils.instantiateClass(SchedulerFactoryBean.class);
			
	        factory.setApplicationContext(applicationContext);
	        factory.setJobFactory(jobFactory);
	        
	        Persistence persistenceSettings = properties.getPersistence();
	        if (persistenceSettings.isPersisted()) {
	        	factory.setDataSource(getDataSource(applicationContext, persistenceSettings));
	        	if (persistenceSettings.isUsePlatformTxManager()) {
	        		PlatformTransactionManager txManager = getTransactionManager(applicationContext, persistenceSettings.getPlatformTxManagerBeanName());
	            	if (null != txManager) {
	                	factory.setTransactionManager(txManager);
	                }
	        	}
	        }
	        
	        if (!StringUtils.isEmpty(factorySettings.getSchedulerName())) {
	        	factory.setSchedulerName(factorySettings.getSchedulerName());
	        } else {
	        	LOGGER.debug("no SchedulerName configured, using bean name: " + QUARTZ_SCHEDULER_FACTORY_BEAN_NAME);
	        }
	        factory.setPhase(factorySettings.getPhase());
	        factory.setStartupDelay(factorySettings.getStartupDelay());
	        factory.setAutoStartup(factorySettings.isAutoStartup());
	        factory.setWaitForJobsToCompleteOnShutdown(factorySettings.isWaitForJobsToCompleteOnShutdown());
	        factory.setOverwriteExistingJobs(factorySettings.isOverwriteExistingJobs());
	        factory.setExposeSchedulerInRepository(factorySettings.isExposeSchedulerInRepository());
	        
	        factory.setQuartzProperties(quartzProperties);
	        
	        if (!CollectionUtils.isEmpty(jobListeners)) {
	        	LOGGER.info("configuring " + jobListeners.size() + " job listeners");
	        	factory.setGlobalJobListeners(jobListeners.toArray(new JobListener[]{}));
	        }
	        if (!CollectionUtils.isEmpty(triggerListeners)) {
	        	LOGGER.info("configuring " + triggerListeners.size() + " trigger listeners");
	        	factory.setGlobalTriggerListeners(triggerListeners.toArray(new TriggerListener[]{}));
	        }
	        if (!CollectionUtils.isEmpty(schedulerListeners)) {
	        	LOGGER.info("configuring " + schedulerListeners.size() + " scheduler listeners");
	        	factory.setSchedulerListeners(schedulerListeners.toArray(new SchedulerListener[]{}));
	        }
	        
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
	}
	
	@Configuration
	@ConditionalOnProperty(prefix = QuartzSchedulerProperties.PREFIX+".metrics", name = "enabled", havingValue="true", matchIfMissing = false)
	@ConditionalOnMissingBean(name = QUARTZ_SCHEDULER_METRICS_LISTENER_BEAN_NAME)
	@AutoConfigureBefore(name=QUARTZ_SCHEDULER_FACTORY_BEAN_NAME)
	protected static class SchedulerMetricsListenerConfiguration {
		
		@Bean(name = QUARTZ_SCHEDULER_METRICS_LISTENER_BEAN_NAME)
		@ConditionalOnMissingBean
		public TriggerMetricsListener schedulerMetricsListener(@Autowired(required=false) QuartzSchedulerProperties properties) {
			if (null == properties) {
				LOGGER.warn("no QuartzSchedulerProperties found, consider to set quartz.enabled=true in properties");
				return null;
			}
			TriggerMetricsListener listener = new TriggerMetricsListener(properties.getMetrics().isEnabled(),
					properties.getMetrics().getListenerName());
			return listener;
		}
	}
}
