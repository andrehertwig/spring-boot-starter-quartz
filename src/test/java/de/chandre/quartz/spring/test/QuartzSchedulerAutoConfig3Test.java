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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import de.chandre.quartz.context.TestContextConfiguration3;
import de.chandre.quartz.spring.app.TestApplication;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=TestApplication.class)
@ContextConfiguration(classes= TestContextConfiguration3.class)
@TestPropertySource(properties = {
		"quartz.enabled=true", 
		"quartz.persistence.persisted=true",
		"quartz.propertiesConfigLocation=classpath:differentQuartzScheduler.properties",
		"flyway.enabled=true",
		"flyway.locations=classpath:db/migration/h2",
		"spring.datasource.initialize=true",
		"spring.datasource.url=jdbc:h2:mem:datajpa;MODE=Oracle",
		"spring.datasource.username=sa",
		"spring.datasource.password=",
		"spring.datasource.driver-class-name=org.h2.Driver",
		"spring.jpa.hibernate.ddl-auto=validate",
		"spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
		"spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"})
//@DirtiesContext
public class QuartzSchedulerAutoConfig3Test {
	
	@Autowired
	private Scheduler scheduler;
	
	@Autowired
	private SchedulerFactoryBean schedulerFactory;
	
	@Test
	public void startEnvironment_test3() throws SchedulerException {
		assertNotNull(scheduler);
		assertNotNull(schedulerFactory);
		
		assertThat(scheduler.getSchedulerInstanceId()).isEqualTo("OverriddenQuartzSchedulerTestId");
		
	}

}
