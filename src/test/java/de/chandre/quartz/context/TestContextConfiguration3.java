package de.chandre.quartz.context;

import java.io.IOException;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.chandre.quartz.spring.QuartzSchedulerAutoConfiguration;
import de.chandre.quartz.spring.QuartzSchedulerProperties;

@Configuration
public class TestContextConfiguration3 {
	
	@Bean(name = QuartzSchedulerAutoConfiguration.QUARTZ_PROPERTIES_BEAN_NAME)
	public Properties quartzProperties(
			@Autowired ApplicationContext applicationContext,
			@Autowired QuartzSchedulerProperties properties) throws IOException {
		
		System.out.println("my overridden quartz.properties loading");
		
		PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
		propertiesFactoryBean.setLocation(applicationContext.getResource("classpath:overriddenQuartzScheduler.properties"));
		propertiesFactoryBean.afterPropertiesSet();
		return propertiesFactoryBean.getObject();
	}
}
