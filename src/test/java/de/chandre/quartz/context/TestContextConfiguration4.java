package de.chandre.quartz.context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.SimpleTrigger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

import de.chandre.quartz.jobs.SimpleCronJob;
import de.chandre.quartz.jobs.SimpleJob;
import de.chandre.quartz.spring.QuartzUtils;

@Configuration
public class TestContextConfiguration4 {
	
	public static final String SIMPLE_JOB_NAME = "SimpleJobName";
	public static final String SIMPLE_JOB_GROUP = "SimpleJobGroup";
	public static final String CRON_JOB_NAME = "CronJobName";
	public static final String CRON_JOB_GROUP = "CronJobGroup";
	
	@Bean(name="simpleJobDetail")
	public JobDetailFactoryBean simpleJobDetail() {
		return QuartzUtils.createJobDetail(SimpleJob.class, SIMPLE_JOB_NAME, SIMPLE_JOB_GROUP, "Just a Simple Job", null);
	}
	
	@Bean(name="simpleJobTrigger")
	public SimpleTriggerFactoryBean createSimpleTrigger(@Qualifier("simpleJobDetail") JobDetail jobDetail) {
		return QuartzUtils.createSimpleTrigger(jobDetail, null, null, "Simple trigger 1", 5000L, 60000L, null);
	}
	
	@Bean(name="simpleJobTrigger2")
	public SimpleTriggerFactoryBean createSimpleTrigger2(@Qualifier("simpleJobDetail") JobDetail jobDetail) {
		Map<String, Object> map = new HashMap<>(1);
		map.put("myKey", "myValue");
		return QuartzUtils.createSimpleTrigger(jobDetail, "STName2", "STGroup2", "STDesc2", 10000L, 30000L,
				SimpleTrigger.REPEAT_INDEFINITELY,
				SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_EXISTING_COUNT, map, 1000);
	}
	
	
	@Bean(name="cronJobDetail")
	public JobDetailFactoryBean cronJobDetail() {
		return QuartzUtils.createJobDetail(SimpleCronJob.class, CRON_JOB_NAME, CRON_JOB_GROUP, "Just a Cron Job", null);
	}
	
	@Bean(name="cronTrigger")
	public CronTriggerFactoryBean createSimpleCronTrigger(@Qualifier("cronJobDetail") JobDetail jobDetail) {
	    return QuartzUtils.createCronTrigger(jobDetail, null, "Cron", null, "0 0 0/1 1/1 * ? *", 5000L, null);
	}
	
	@Bean(name="cronTrigger2")
	public CronTriggerFactoryBean createSimpleCronTrigger2(@Qualifier("cronJobDetail") JobDetail jobDetail) {
		Map<String, Object> map = new HashMap<>(1);
		map.put("myKey", "myValue");
	    return QuartzUtils.createCronTrigger(jobDetail, "CTName2", "Cron", "CTDesc2", "0 0 0/1 1/1 * ? *", 
	    		CronTrigger.MISFIRE_INSTRUCTION_SMART_POLICY, map, 10000L, new Date(), "Europe/Berlin", 1234);
	}
}
