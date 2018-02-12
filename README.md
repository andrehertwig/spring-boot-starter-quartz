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
* quatz-scheduler

Tested with Spring Boot 
* (1.0.2) 1.3.8, 1.4.6, 1.5.3, 1.5.6, 
* (1.0.3) 1.5.10

## Usage

```xml

<dependency>
	<groupId>de.chandre.quartz</groupId>
	<artifactId>spring-boot-starter-quartz</artifactId>
	<version>1.0.3</version>
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
				factory.setTaskExecutor(Executors.newFixedThreadPool(10));
				return factory;
			}
		};
	}
}
```

## Recommended Maven Dependency Management

Because Quartz still will have some transitive dependencies you don't want to have in you application, you should consider the following dependency settings.

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