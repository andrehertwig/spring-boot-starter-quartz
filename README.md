# Spring-Boot auto configuration for Quartz Scheduler

> Just a Spring-Boot starter for Quartz Scheduler.

Of course there are already several starters for Quartz Scheduler, but none of them fulfill all my needs, therefore I created my own.

[![Maven Central](https://img.shields.io/maven-central/v/de.chandre.quartz/spring-boot-starter-quartz.svg)](https://mvnrepository.com/artifact/de.chandre.quartz)
[![GitHub issues](https://img.shields.io/github/issues/andrehertwig/spring-boot-starter-quartz.svg)](https://github.com/andrehertwig/spring-boot-starter-quartz/issues)
[![license](https://img.shields.io/github/license/andrehertwig/spring-boot-starter-quartz.svg)](https://github.com/andrehertwig/spring-boot-starter-quartz/blob/develop/LICENSE)

This is just a spare-time project. The usage of this tool (especially in production systems) is at your own risk.

## Requirements, Dependencies
* spring-boot
* quatz-scheduler

Tested with Spring Boot 1.3.8, 1.4.6, 1.5.3

## Usage

```xml

	<dependency>
		<groupId>de.chandre.quartz</groupId>
		<artifactId>spring-boot-starter-quartz</artifactId>
		<version>1.0.0</version>
	</dependency>
	
```

## Configurations

For special configuration, please check the [additional-spring-configuration-metadata.json](src/main/resources/META-INF/additional-spring-configuration-metadata.json) 

## Additional Things

Check `de.chandre.quartz.spring.QuartzUtils` for Builders for JobDetail, SimpleTrigger and CronTrigger

If you want to add scheduler properties at runtime while application start-up, you are able to do that by implementing the `de.chandre.quartz.spring.QuartzPropertiesOverrideHook` (Maybe if your Configuration is stored in a database, or you want to change the Quartz table prefix with Hibernate's common table prefix).