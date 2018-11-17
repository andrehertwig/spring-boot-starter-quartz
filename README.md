# Spring-Boot auto configuration for Quartz Scheduler

> Just a Spring-Boot starter for Quartz Scheduler.

Of course there are already several starters for Quartz Scheduler, but none of them fulfill all my needs, therefore I created my own.

[![Maven Central](https://img.shields.io/maven-central/v/de.chandre.quartz/spring-boot-starter-quartz.svg)](https://mvnrepository.com/artifact/de.chandre.quartz)
[![GitHub issues](https://img.shields.io/github/issues/andrehertwig/spring-boot-starter-quartz.svg)](https://github.com/andrehertwig/spring-boot-starter-quartz/issues)
[![license](https://img.shields.io/github/license/andrehertwig/spring-boot-starter-quartz.svg)](https://github.com/andrehertwig/spring-boot-starter-quartz/blob/develop/LICENSE)

This is just a spare-time project. The usage of this tool (especially in production systems) is at your own risk.

# Content

1. [Requirements, Dependencies](#requirements-dependencies)
2. [Usage](#usage)
3. [Configuration Properties](#configuration-properties)
4. [Additional Things](#additional-things)
   1. [Utils](#utils)
   2. [Hooks](#hooks)
   3. [Job interdependencies](#job-interdependencies-with-105)
5. [Recommended Maven Dependency Management](#recommended-maven-dependency-management)

## Requirements, Dependencies
* spring-boot
* quartz-scheduler

Tested with Spring Boot 
* (until 1.0.2) 1.3.8, 1.4.6, 1.5.3, 1.5.6, 
* (since 1.0.3) 1.4.6, 1.5.10, 1.5.16, 1.5.17

## Usage

```xml

<dependency>
	<groupId>de.chandre.quartz</groupId>
	<artifactId>spring-boot-starter-quartz</artifactId>
	<version>1.0.5</version>
</dependency>
	
```

Maybe you have to explicitly enable the component scan for the package:
```java

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan(basePackages={"your.packages", "de.chandre.quartz.spring"})
public class MyBootApplication {
 
}
```

## Configuration Properties

For special configuration, please check the [additional-spring-configuration-metadata.json](src/main/resources/META-INF/additional-spring-configuration-metadata.json) 

[Quartz 2.x Configuration](http://www.quartz-scheduler.org/documentation/quartz-2.x/configuration/ConfigMain.html)

```ini
  # if auto configuration is enabled
  quartz.enabled=true
  
  ################################
  #      Quartz Persistence      #
  ################################
  
  # should be set to true if quartz is configured to persist its data to a database
  quartz.persistence.persisted=false
  
  # Only if quartz.persisted=true. if the PlatformTransactionManager should be used. Must be configured as Bean.
  quartz.persistence.use-platform-tx-manager=true
  
  #since 1.0.4
  #String
  #Only if quartz.persisted=true. if there are more than one PlatformTransactionManagers 
  # within the context you can specify the bean name, which txManager to use.
  quartz.persistence.platform-tx-manager-bean-name=
  
  #String
  # Only if quartz.persisted=true. If more than one database connection is configured the 
  # name (case-sensitive) of the used DataSource must be configured.
  quartz.persistence.data-source-name=
  
  ################################
  #   Quartz SchedulerFactory    #
  ################################
  
  #String
  # Optional: a name for the scheduler
  quartz.scheduler-factory.schedulerName=
  
  # Set whether to automatically start the scheduler after initialization.
  quartz.scheduler-factory.auto-startup=true
  
  # Set whether to wait for running jobs to complete on shutdown.
  quartz.scheduler-factory.wait-for-jobs-to-complete-on-shutdown=false
  
  # Set whether any jobs defined on this scheduler-factoryBean should overwrite existing job definitions.
  quartz.scheduler-factory.overwrite-existing-jobs=false
  
  # Set whether to expose the Spring-managed Scheduler instance in the Quartz SchedulerRepository.
  quartz.scheduler-factory.expose-scheduler-in-repository=false
  
  # Specify the phase in which this scheduler should be started and stopped. 
  # The startup order proceeds from lowest to highest, and the shutdown order is the reverse of that.
  quartz.scheduler-factory.phase=java.lang.Integer.MAX_VALUE
  
  # Set the number of seconds to wait after initialization before starting the scheduler asynchronously.
  # Default is 0, meaning immediate synchronous startup on initialization of this bean.
  quartz.scheduler-factory.startup-delay=0
  
  ################################
  #      Quartz Properties       #
  ################################
  
  # Optional: a different resource location for quartz internal properties. 
  # (http://www.quartz-scheduler.org/documentation/quartz-2.x/configuration/ConfigMain.html)
  quartz.properties-config-location=classpath:/org/quartz/quartz.properties
  
  # Optional: option to manage quartz internal properties via spring application properties. 
  # (http://www.quartz-scheduler.org/documentation/quartz-2.x/configuration/ConfigMain.html)
  # you can also check org.quartz.impl.StdSchedulerFactory for static variables
  quartz.properties.*
  #example:
  #quartz.properties.org.quartz.scheduler.rmi.export=true
  
  # If true, the properties from spring application will override the exsisting 
  # quartz properties from quartz.properties-config-location.
  # If false only Springs quartz.properties.* will be used with fallback to file if empty.
  quartz.override-config-location-properties=true
  
  ################################
  #      Spring Boot Actuator    #
  ################################
  
  # With 1.0.5
  
  # if set to true the TriggerMetricsListener will be added to Quartz listeners 
  # which requires a configured CounterService and GaugeService
  quartz.metrics.enabled=false
  
  # listener name for Quartz. Default: class name
  quartz.metrics.listener-name=
  
  # if metrics for counting fired job groups is enabled
  quartz.metrics.enable-job-group-counter=false
  
  # if metrics for counting fired jobs should be enabled
  quartz.metrics.enable-job-counter=true
  
  # if metrics for counting fired triggers should be enabled
  quartz.metrics.enable-trigger-counter=true
  
  # if metrics for final instructions per job/trigger should be enabled
  quartz.metrics.enable-execution-instruction-counter=false
  
  # if metrics for gauge of fired jobs should be enabled
  quartz.metrics.enable-job-gauges=true
  
  # if metrics for gauge of fired triggers should be enabled
  quartz.metrics.enable-trigger-gauges=true

```

The Property `quartz.properties.org.quartz.scheduler.instanceName` is overridden by Spring by default with the SchedulerFactory bean name. To rename it, use `quartz.scheduler-factory.schedulerName`

## Additional Things

### Utils
Check `de.chandre.quartz.spring.QuartzUtils` for Builders for JobDetail, SimpleTrigger and CronTrigger

### Hooks
If you want to add scheduler properties at runtime while application start-up, you are able to do that by implementing the `de.chandre.quartz.spring.QuartzPropertiesOverrideHook` (Maybe if your Configuration is stored in a database, or you want to change the Quartz table prefix with Hibernate's common table prefix).

If you want to customize the SchedulerFactory, e.g. to set own task executor, you are able to do that by implementing the `de.chandre.quartz.spring.QuartzSchedulerFactoryOverrideHook`

Example:

```java
@Configuration
@AutoConfigureBefore(QuartzSchedulerAutoConfiguration.class)
public class SchedulerConfig
{
	private static final Logger LOGGER = LogManager.getFormatterLogger(SchedulerConfig.class);
	
	private static final String QRTZ_TABLE_PREFIX_KEY = "org.quartz.jobStore.tablePrefix";
	
	private static final String QRTZ_JOB_CLASS = "org.quartz.jobStore.class";

	@Bean
	public QuartzPropertiesOverrideHook quartzPropertiesOverrideHook() {
		return new QuartzPropertiesOverrideHook() {

			@Override
			public Properties override(Properties quartzProperties) {
				String jobclazz = (String) quartzProperties.get(QRTZ_JOB_CLASS);
				if (!jobclazz.contains("RAMJobStore")) {
					String qrtzPrefix = (String) quartzProperties.get(QRTZ_TABLE_PREFIX_KEY);
					LOGGER.info("setting %s to %s", QRTZ_TABLE_PREFIX_KEY, MyCustomNamingStrategy.getPrefix() + qrtzPrefix);
					quartzProperties.put(QRTZ_TABLE_PREFIX_KEY, MyCustomNamingStrategy.getPrefix() + qrtzPrefix);
				}
				return quartzProperties;
			}
		};
	}
	
	@Bean
	public QuartzSchedulerFactoryOverrideHook quartzSchedulerFactoryOverrideHook() {
		return new QuartzSchedulerFactoryOverrideHook() {
		
			@Override
			public SchedulerFactoryBean override(SchedulerFactoryBean factory, QuartzSchedulerProperties properties,
					Properties quartzProperties) {
					
				// when doing this you may should not set "quartz.properties.org.quartz.threadPool.class" in properties
				factory.setTaskExecutor(Executors.newFixedThreadPool(10));
				return factory;
			}
		};
	}
}
```

### Job interdependencies (With 1.0.5)
Since 1.0.5 there are also predefined classes to queue dependent jobs. This implementation aims to jobs modifying same resources or should not run together at all.

First defining the queue service

```java
	
	@Bean(name="queueService")
	public QueueService<Future<JobExecutionResult>> callbackQueueServiceImpl() {
		//AsyncQueueServiceImpl or any own implemented service
		return new CallbackQueueServiceImpl();
	}

```

Afterwards within the job

```java

@Scope(scopeName=ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CallbackQueuedJob implements Job, QueuedInstance
{
	private final Log LOGGER = LogFactory.getLog(CallbackQueuedJob.class);
	
	public static String GROUP = "myGroup";
	
	@Autowired
	private QueueService<Future<JobExecutionResult>> queueService;
	
	private JobExecutionContext context = null;
	
	@Override
	public String getGroup() {
		return GROUP;
	}
	
	@Override
	public String getName() {
		if (null != context) {
			return context.getTrigger().getKey().getName();
		}
		return QueuedInstance.super.getName();
	}
	
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException{
    	this.context = jobExecutionContext;
    	Future<JobExecutionResult> future= queueService.queueMe(this);
    	try {
    		if (null != future) {
    			JobExecutionResult jer = future.get(10000L, TimeUnit.MILLISECONDS);
        		
        		if (jer.getException() != null) {
        			throw new JobExecutionException(jer.getException());
        		}
    		} else {
    			LOGGER.info("job not added " + jobExecutionContext.getTrigger().getKey().getName());
    		}
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			throw new JobExecutionException(e);
		}
	}

	@Override
	public boolean run() {
		
		//doing someing ... 
		
		/*
		 * can use this.context because my job bean should be a prototype
		 */
		
		return true;
	}
}

```

## Recommended Maven Dependency Management

Because Quartz still will have some transitive dependencies you may don't want to have in your application, you should consider about the following dependency settings.

```xml
<dependency>
	<groupId>org.quartz-scheduler</groupId>
	<artifactId>quartz</artifactId>
	<version>${quartz-version}</version>
	<exclusions>
		<exclusion>
			<groupId>com.mchange</groupId>
			<artifactId>c3p0</artifactId>
		</exclusion>
		<exclusion>
			<groupId>com.mchange</groupId>
			<artifactId>mchange-commons-java</artifactId>
		</exclusion>
		<exclusion>
			<groupId>com.zaxxer</groupId>
			<artifactId>HikariCP-java6</artifactId>
		</exclusion>
	</exclusions>
</dependency>
```