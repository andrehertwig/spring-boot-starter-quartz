package de.chandre.quartz.spring.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import de.chandre.quartz.spring.QuartzSchedulerAutoConfiguration;
import de.chandre.quartz.spring.app.TestApplication;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=TestApplication.class)
@TestPropertySource(properties = { 
		"quartz.enabled=true", 
		"quartz.properties.org.quartz.scheduler.instanceId=MyTestInstanceId",
		"flyway.enabled=false",
		"spring.datasource.url=jdbc:h2:mem:datajpa;MODE=Oracle",
		"spring.datasource.username=sa",
		"spring.datasource.password=",
		"spring.datasource.driver-class-name=org.h2.Driver",
		"spring.jpa.hibernate.ddl-auto=validate",
		"spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
		"spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
		})
//@DirtiesContext
public class QuartzSchedulerAutoConfig1Test {
	
	@Autowired
	private Scheduler scheduler;
	
	@Autowired
	private SchedulerFactoryBean schedulerFactory;
	
	@Test
	public void startEnvironment_test() throws SchedulerException {
		assertNotNull(scheduler);
		assertNotNull(schedulerFactory);
		
		assertThat(	scheduler.getSchedulerName()).isEqualTo(QuartzSchedulerAutoConfiguration.QUARTZ_SCHEDULER_FACTORY_BEAN_NAME);
		
		assertThat(scheduler.getSchedulerInstanceId()).isEqualTo("MyTestInstanceId");
	}

}
