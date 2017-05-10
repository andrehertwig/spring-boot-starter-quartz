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

import de.chandre.quartz.context.StaticLog;
import de.chandre.quartz.context.TestContextConfiguration5;
import de.chandre.quartz.spring.app.TestApplication;

/**
 * 
 * @author André Hertwig
 * @since 1.0.1
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes=TestApplication.class)
@ContextConfiguration(classes= TestContextConfiguration5.class)
@TestPropertySource(properties = {
		"quartz.enabled=true", 
		"quartz.persistence.persisted=false",
		"quartz.override-config-location-properties=false",
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
		"spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
		"logging.level.=error",
		"spring.main.banner-mode=off"})
//@DirtiesContext
public class QuartzSchedulerAutoConfig5Test {
	
	@Autowired
	private Scheduler scheduler;
	
	@Autowired
	private SchedulerFactoryBean schedulerFactory;
	
	@Test
	public void startEnvironment_test5() throws SchedulerException {
		assertNotNull(scheduler);
		assertNotNull(schedulerFactory);
		
		assertThat(scheduler.getSchedulerInstanceId()).isEqualTo("QuartzSchedulerTestId");
		
		assertThat(StaticLog.getInstance().getMessasges()).containsExactlyInAnyOrder(
				TestContextConfiguration5.CAPTURE1,TestContextConfiguration5.CAPTURE2 );
	}
}
