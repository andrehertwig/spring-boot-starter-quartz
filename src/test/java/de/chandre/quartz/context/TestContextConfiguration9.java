package de.chandre.quartz.context;

import java.util.Properties;
import java.util.concurrent.Executors;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import de.chandre.quartz.spring.QuartzSchedulerAutoConfiguration;
import de.chandre.quartz.spring.QuartzSchedulerFactoryOverrideHook;
import de.chandre.quartz.spring.QuartzSchedulerProperties;

@Configuration
@AutoConfigureBefore(QuartzSchedulerAutoConfiguration.class)
public class TestContextConfiguration9 {
	
	public static final String CAPTURE1 = "captured: overriding quartz factory"; 
	
	@Bean
	public QuartzSchedulerFactoryOverrideHook quartzSchedulerFactoryOverrideHook() {
		return new QuartzSchedulerFactoryOverrideHook() {
			
			@Override
			public SchedulerFactoryBean override(SchedulerFactoryBean factory, QuartzSchedulerProperties properties,
					Properties quartzProperties) {
				factory.setTaskExecutor(Executors.newFixedThreadPool(20));
				StaticLog.getInstance().addMessasge(CAPTURE1);
				return factory;
			}
		};
	}
}
