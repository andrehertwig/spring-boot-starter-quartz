package de.chandre.quartz.spring.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import de.chandre.quartz.context.TestContextConfiguration11;
import de.chandre.quartz.jobs.CallbackQueuedJob;
import de.chandre.quartz.spring.app.TestApplication2;
import de.chandre.quartz.spring.queue.QueueService;
import de.chandre.quartz.spring.queue.QueuedInstance;

/**
 * JMX test
 * @author André
 * @since 1.0.5
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes=TestApplication2.class)
@ContextConfiguration(classes= TestContextConfiguration11.class)
@TestPropertySource(properties = { 
		"quartz.enabled=true", 
		"quartz.scheduler-factory.scheduler-name=MyTestScheduler",
		"quartz.properties.org.quartz.scheduler.instanceId=MyTestInstanceId",
		"quartz.properties.org.quartz.scheduler.jmx.export=false",
		"quartz.metrics.enabled=true",
		"flyway.enabled=false",
		"spring.datasource.url=jdbc:h2:mem:datajpa;MODE=Oracle",
		"spring.datasource.username=sa",
		"spring.datasource.password=",
		"spring.datasource.driver-class-name=org.h2.Driver",
		"spring.jpa.hibernate.ddl-auto=validate",
		"spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
		"spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
		"spring.jmx.enabled=true"
		})
@DirtiesContext
public class QuartzSchedulerAutoConfig11Test {
	
	@Autowired
	private Scheduler scheduler;
	
	@Autowired
	private SchedulerFactoryBean schedulerFactory;
	
	@Autowired
	private QueueService queueService;
	
	@Test
	public void startEnvironment_test() throws SchedulerException {
		assertNotNull(scheduler);
		assertNotNull(schedulerFactory);
		
		assertThat(	scheduler.getSchedulerName()).isEqualTo("MyTestScheduler");
		
		assertThat(scheduler.getSchedulerInstanceId()).isEqualTo("MyTestInstanceId");
		
		try {
			ManagementFactory.getPlatformMBeanServer();
			List<MBeanServer> servers = MBeanServerFactory.findMBeanServer(null);
			assertNotNull(servers);
			assertThat(servers.size()).isGreaterThan(0);
			MBeanServer server = servers.get(0);
			List<String> domains = Arrays.asList(server.getDomains());
			assertNotNull(domains);
			assertThat(domains.size()).isGreaterThan(0);
			
			String domain = "quartz";
			assertThat(domains).doesNotContain(domain);
			
			//wait a while until some jobs have been triggered
			Thread.sleep(1000L);
			
			assertThat(queueService.getGroups()).containsOnlyOnce(QueuedInstance.DEFAULT_GROUP, CallbackQueuedJob.GROUP);
			
		} catch (Exception e) {
			assertTrue(e.getMessage(), false);
		}
	}
}
