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

import de.chandre.quartz.context.TestContextConfiguration4;
import de.chandre.quartz.spring.QuartzSchedulerProperties;
import de.chandre.quartz.spring.app.TestApplication;

/**
 * 
 * @author André Hertwig
 * @since 1.0.1
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes=TestApplication.class)
@ContextConfiguration(classes= TestContextConfiguration4.class)
@TestPropertySource(properties = {
		"quartz.enabled=true", 
		"quartz.persistence.persisted=false",
		"quartz.override-config-location-properties=false",
		"quartz.properties-config-location=classpath:differentQuartzScheduler.properties",
		"quartz.properties.org.quartz.scheduler.instanceId=OnlyAppPropertyTestId",
		"quartz.scheduler-factory.scheduler-name=MySpecialScheduler",
		"quartz.scheduler-factory.auto-startup=false",
		"quartz.scheduler-factory.wait-for-jobs-to-complete-on-shutdown=true",
		"quartz.scheduler-factory.overwrite-existing-jobs=true",
		"quartz.scheduler-factory.expose-scheduler-in-repository=true",
		"quartz.scheduler-factory.phase=12345",
		"quartz.scheduler-factory.startup-delay=3000",
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
public class QuartzSchedulerAutoConfig6Test {
	
	@Autowired
	private Scheduler scheduler;
	
	@Autowired
	private SchedulerFactoryBean schedulerFactory;
	
	@Autowired
	private QuartzSchedulerProperties props;
	
	@Test
	public void startEnvironment_test6() throws SchedulerException {
		assertNotNull(scheduler);
		assertNotNull(schedulerFactory);
		
		assertThat(scheduler.getSchedulerInstanceId()).isEqualTo("OnlyAppPropertyTestId");
		
		assertThat(scheduler.getJobGroupNames()).containsExactlyInAnyOrder(
				TestContextConfiguration4.SIMPLE_JOB_GROUP, TestContextConfiguration4.CRON_JOB_GROUP);
		
		assertThat(props.toString()).contains("schedulerName=MySpecialScheduler", "autoStartup=false",
				"waitForJobsToCompleteOnShutdown=true", "overwriteExistingJobs=true",
				"exposeSchedulerInRepository=true", "phase=12345", "startupDelay=3000");

	}

}
