package de.chandre.quartz.context;

import java.util.Properties;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.chandre.quartz.spring.QuartzPropertiesOverrideHook;
import de.chandre.quartz.spring.QuartzSchedulerAutoConfiguration;

@Configuration
@AutoConfigureBefore(QuartzSchedulerAutoConfiguration.class)
public class TestContextConfiguration5 {
	
	public static final String CAPTURE = "captured: overriding quartz props"; 
	
	@Bean
	public QuartzPropertiesOverrideHook quartzPropertiesOverrideHook() {
		return new QuartzPropertiesOverrideHook() {

			@Override
			public Properties override(Properties quartzProperties) {
				StaticLog.getInstance().setMessasge(CAPTURE);
				return quartzProperties;
			}
		};
	}
}
