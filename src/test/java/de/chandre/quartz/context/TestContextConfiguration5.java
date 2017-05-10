package de.chandre.quartz.context;

import java.util.Properties;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import de.chandre.quartz.spring.QuartzPropertiesOverrideHook;
import de.chandre.quartz.spring.QuartzSchedulerAutoConfiguration;
import de.chandre.quartz.spring.QuartzSchedulerFactoryOverrideHook;
import de.chandre.quartz.spring.QuartzSchedulerProperties;

@Configuration
@AutoConfigureBefore(QuartzSchedulerAutoConfiguration.class)
public class TestContextConfiguration5 {
	
	public static final String CAPTURE1 = "captured: overriding quartz props";
	public static final String CAPTURE2 = "captured: overriding quartz factory"; 
	
	@Bean
	public QuartzPropertiesOverrideHook quartzPropertiesOverrideHook() {
		return new QuartzPropertiesOverrideHook() {

			@Override
			public Properties override(Properties quartzProperties) {
				StaticLog.getInstance().addMessasge(CAPTURE1);
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
				StaticLog.getInstance().addMessasge(CAPTURE2);
				return factory;
			}
		};
	}
}
