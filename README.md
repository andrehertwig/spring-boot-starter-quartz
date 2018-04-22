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
5. [Recommended Maven Dependency Management](#recommended-maven-dependency-management)

## Requirements, Dependencies
* spring-boot
* quartz-scheduler

Tested with Spring Boot 
* (until 1.0.2) 1.3.8, 1.4.6, 1.5.3, 1.5.6, 
* (since 1.0.3) 1.4.6, 1.5.10

## Usage

```xml

<dependency>
	<groupId>de.chandre.quartz</groupId>
	<artifactId>spring-boot-starter-quartz</artifactId>
	<version>1.0.4</version>
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
  quartz.properties.*
  
  # If true, the properties from spring application will override the exsisting 
  # quartz properties from quartz.properties-config-location.
  # If false only Springs quartz.properties.* will be used with fallback to file if empty.
  quartz.override-config-location-properties=true

```

## Additional Things

Check `de.chandre.quartz.spring.QuartzUtils` for Builders for JobDetail, SimpleTrigger and CronTrigger

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