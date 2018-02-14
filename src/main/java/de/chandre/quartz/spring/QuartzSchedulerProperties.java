package de.chandre.quartz.spring;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties for Spring-Boot auto-configuration for Quartz-Scheduler 
 * @author André Hertwig
 * @since 1.0.0
 */
@ConfigurationProperties(prefix = QuartzSchedulerProperties.PREFIX, ignoreUnknownFields = true)
public class QuartzSchedulerProperties {
	
	public static final String PREFIX = "quartz";
	
	public static final String DEFAULT_CONFIG_LOCATION = "classpath:/org/quartz/quartz.properties";
	
	/*
	 *  if auto-config is enabled
	 */
	private boolean enabled = true;
	/*
	 * persistence settings
	 */
	private Persistence persistence = new Persistence();
	/*
	 * scheduler factory settings 
	 */
	private SchedulerFactory schedulerFactory = new SchedulerFactory();
	/*
	 * properties settings
	 */
	private String propertiesConfigLocation;
	private Map<String, String> properties = new HashMap<String, String>();
	private boolean overrideConfigLocationProperties = true;

	/**
	 * if auto configuration is enabled
	 * @return
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * if auto configuration is enabled
	 * @param enabled
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public Persistence getPersistence() {
		return persistence;
	}

	public void setPersistence(Persistence persistence) {
		this.persistence = persistence;
	}

	public SchedulerFactory getSchedulerFactory() {
		return schedulerFactory;
	}

	public void setSchedulerFactory(SchedulerFactory schedulerFactory) {
		this.schedulerFactory = schedulerFactory;
	}
	
	public static class Persistence {
		
		private boolean persisted = false;
		private boolean usePlatformTxManager = true;
		private String platformTxManagerBeanName; 
		private String dataSourceName;
		
		public boolean isPersisted() {
			return persisted;
		}

		public void setPersisted(boolean persisted) {
			this.persisted = persisted;
		}

		public boolean isUsePlatformTxManager() {
			return usePlatformTxManager;
		}

		public void setUsePlatformTxManager(boolean usePlatformTxManager) {
			this.usePlatformTxManager = usePlatformTxManager;
		}

		public String getPlatformTxManagerBeanName() {
			return platformTxManagerBeanName;
		}

		public void setPlatformTxManagerBeanName(String platformTxManagerBeanName) {
			this.platformTxManagerBeanName = platformTxManagerBeanName;
		}

		public String getDataSourceName() {
			return dataSourceName;
		}

		public void setDataSourceName(String dataSourceName) {
			this.dataSourceName = dataSourceName;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("Persistence [persisted=").append(persisted).append(", usePlatformTxManager=")
					.append(usePlatformTxManager).append(", dataSourceName=").append(dataSourceName).append("]");
			return builder.toString();
		}
	}

	public static class SchedulerFactory {
		
		private String schedulerName;
		private boolean autoStartup = true;
		private boolean waitForJobsToCompleteOnShutdown = false;
		private boolean overwriteExistingJobs = false;
		private boolean exposeSchedulerInRepository = false;
		private int phase = Integer.MAX_VALUE;
		private int startupDelay = 0;
		
		public String getSchedulerName() {
			return schedulerName;
		}
		
		public void setSchedulerName(String schedulerName) {
			this.schedulerName = schedulerName;
		}

		public boolean isAutoStartup() {
			return autoStartup;
		}

		public void setAutoStartup(boolean autoStartup) {
			this.autoStartup = autoStartup;
		}

		public boolean isWaitForJobsToCompleteOnShutdown() {
			return waitForJobsToCompleteOnShutdown;
		}

		public void setWaitForJobsToCompleteOnShutdown(boolean waitForJobsToCompleteOnShutdown) {
			this.waitForJobsToCompleteOnShutdown = waitForJobsToCompleteOnShutdown;
		}

		public boolean isOverwriteExistingJobs() {
			return overwriteExistingJobs;
		}

		public void setOverwriteExistingJobs(boolean overwriteExistingJobs) {
			this.overwriteExistingJobs = overwriteExistingJobs;
		}

		public boolean isExposeSchedulerInRepository() {
			return exposeSchedulerInRepository;
		}

		public void setExposeSchedulerInRepository(boolean exposeSchedulerInRepository) {
			this.exposeSchedulerInRepository = exposeSchedulerInRepository;
		}
		
		public int getPhase() {
			return phase;
		}

		public void setPhase(int phase) {
			this.phase = phase;
		}

		public int getStartupDelay() {
			return startupDelay;
		}

		public void setStartupDelay(int startupDelay) {
			this.startupDelay = startupDelay;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("SchedulerFactory [schedulerName=").append(schedulerName).append(", autoStartup=")
					.append(autoStartup).append(", waitForJobsToCompleteOnShutdown=")
					.append(waitForJobsToCompleteOnShutdown).append(", overwriteExistingJobs=")
					.append(overwriteExistingJobs).append(", exposeSchedulerInRepository=")
					.append(exposeSchedulerInRepository).append(", phase=").append(phase).append(", startupDelay=")
					.append(startupDelay).append("]");
			return builder.toString();
		}
	}

	public String getPropertiesConfigLocation() {
		return propertiesConfigLocation;
	}

	public void setPropertiesConfigLocation(String propertiesConfigLocation) {
		this.propertiesConfigLocation = propertiesConfigLocation;
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}

	public boolean isOverrideConfigLocationProperties() {
		return overrideConfigLocationProperties;
	}

	public void setOverrideConfigLocationProperties(boolean overrideConfigLocationProperties) {
		this.overrideConfigLocationProperties = overrideConfigLocationProperties;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("QuartzSchedulerProperties [enabled=").append(enabled).append(", persistence=")
				.append(persistence).append(", schedulerFactory=").append(schedulerFactory)
				.append(", propertiesConfigLocation=").append(propertiesConfigLocation).append(", properties=")
				.append(properties).append(", overrideConfigLocationProperties=")
				.append(overrideConfigLocationProperties).append("]");
		return builder.toString();
	}
	
}
