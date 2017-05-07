package de.chandre.quartz.spring;

import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

/**
 * Convenience methods for creating {@link JobDetail}s and {@link Trigger}s for a Spring managed environment
 * 
 * @author André Hertwig
 * @since 1.0.0
 */
public class QuartzUtils {
	
	private QuartzUtils() {}
	
	/**
	 * 
	 * @return build wrapper for JobDetail
	 */
	public static QuartzJobBuilder jobBuilder() {
	    return new QuartzJobBuilder();
	}
	
	/**
	 * build wrapper for JobDetail
	 * @since 1.0.0
	 */
	public static class QuartzJobBuilder {
		private final JobDetailFactoryBean jobDetailFactoryBean;

		public QuartzJobBuilder() {
			jobDetailFactoryBean = new JobDetailFactoryBean();
		}
		/**
		 * @see JobDetailFactoryBean#setJobClass(Class)
		 * @param clazz
		 * @return
		 */
		public QuartzJobBuilder jobClass(Class<?> clazz) {
			jobDetailFactoryBean.setJobClass(clazz);
			return this;
		}
		/**
		 * @see JobDetailFactoryBean#setBeanName(String)
		 * @param name
		 * @return
		 */
		public QuartzJobBuilder beanName(String beanName) {
			jobDetailFactoryBean.setBeanName(beanName);
			return this;
		}
		/**
		 * @see JobDetailFactoryBean#setName(String)
		 * @param name
		 * @return
		 */
		public QuartzJobBuilder name(String name) {
			jobDetailFactoryBean.setName(name);
			return this;
		}
		/**
		 * @see JobDetailFactoryBean#setGroup(String)
		 * @param group
		 * @return
		 */
		public QuartzJobBuilder group(String group) {
			jobDetailFactoryBean.setGroup(group);
			return this;
		}
		/**
		 * @see JobDetailFactoryBean#setDescription(String)
		 * @param description
		 * @return
		 */
		public QuartzJobBuilder description(String description) {
			jobDetailFactoryBean.setDescription(description);
			return this;
		}
		/**
		 * @see JobDetailFactoryBean#setDurability(boolean)
		 * @param durability
		 * @return
		 */
		public QuartzJobBuilder durability(boolean durability) {
			jobDetailFactoryBean.setDurability(durability);
			return this;
		}
		/**
		 * @see JobDetailFactoryBean#setRequestsRecovery(boolean)
		 * @param requestRecovery
		 * @return
		 */
		public QuartzJobBuilder requestRecovery(boolean requestRecovery) {
			jobDetailFactoryBean.setRequestsRecovery(requestRecovery);
			return this;
		}
		/**
		 * @see JobDetailFactoryBean#setJobDataAsMap(Map)
		 * @param jobData
		 * @return
		 */
		public QuartzJobBuilder putJobData(Map<String, ?> jobData) {
			if (null != jobData) {
				jobDetailFactoryBean.getJobDataMap().putAll(jobData);
			}
			return this;
		}
		/**
		 * @see JobDetailFactoryBean#setJobDataAsMap(Map)
		 * @param key
		 * @param value
		 * @return
		 */
		public QuartzJobBuilder addJobData(String key, Object value) {
			jobDetailFactoryBean.getJobDataMap().put(key, value);
			return this;
		}
		/**
		 * 
		 * @return
		 */
		public JobDetailFactoryBean getJobDetailFactoryBean() {
			return jobDetailFactoryBean;
		}
		/**
		 * 
		 * @return
		 */
		public JobDetail build() {
			jobDetailFactoryBean.afterPropertiesSet();
			return jobDetailFactoryBean.getObject();
		}
	}

	/**
	 * creates a durable job detail factory bean to put into spring context
	 * <br><br>
	 * <code>
	 * {@literal @}Bean<br>
	 * public JobDetailFactoryBean simpleJobDetail() {<br>
	 *     return QuartzJobUtils.createJobDetail(SimpleJob.class, "MySimpleJob", null, "Just a Simple Job", null);<br>
	 * }<br>
	 * </code>
	 * Your job class should implement the interface {@link Job} 
	 * 
	 * @param jobClass
	 * @param jobName (optional)
	 * @param jobGroup (optional)
	 * @param jobDdescription (optional)
	 * @param jobData (optional)
	 * @return job detail factory
	 */
	public static JobDetailFactoryBean createJobDetail(Class<?> jobClass, String jobName, String jobGroup, 
			String jobDdescription, Map<String, ?> jobData) {
		return QuartzUtils.jobBuilder().jobClass(jobClass).name(jobName).group(jobGroup).description(jobDdescription)
				.putJobData(jobData).durability(true).getJobDetailFactoryBean();
	}
	
	/**
	 * returns a job detail factory bean to put into spring context
	 * 
	 * <br><br>
	 * <code>
	 * {@literal @}Bean<br>
	 * public JobDetailFactoryBean simpleJobDetail() {<br>
	 *     return QuartzJobUtils.createJobDetail(SimpleJob.class, "MySimpleJob", null, "Just a Simple Job", null, true, false);<br>
	 * }<br>
	 * </code>
	 * Your job class should implement the interface {@link Job} 
	 *
	 * @param jobClass
	 * @param jobName (optional)
	 * @param jobGroup (optional)
	 * @param jobDdescription (optional)
	 * @param jobData (optional)
	 * @param durable
	 * @param requestsRecovery
	 * 
	 * @return job detail factory
	 */
	public static JobDetailFactoryBean createJobDetail(Class<?> jobClass, String jobName, String jobGroup, 
			String jobDdescription, Map<String, ?> jobData, boolean durable, boolean requestsRecovery) {
		
		return QuartzUtils.jobBuilder().jobClass(jobClass).name(jobName).group(jobGroup).description(jobDdescription)
				.putJobData(jobData).durability(durable).requestRecovery(requestsRecovery).getJobDetailFactoryBean();
	}
	
	/**
	 * 
	 * @return build wrapper for simple trigger 
	 */
	public static QuartzSimpleTriggerBuilder simpleTriggerBuilder() {
	    return new QuartzSimpleTriggerBuilder();
	}
	
	/**
	 * build wrapper for SimpleTrigger
	 * @since 1.0.0
	 */
	public static class QuartzSimpleTriggerBuilder {
		private final SimpleTriggerFactoryBean triggerFactoryBean;
		
		public QuartzSimpleTriggerBuilder() {
			triggerFactoryBean = new SimpleTriggerFactoryBean();
		}
		/**
		 * @see SimpleTriggerFactoryBean#setJobDetail(JobDetail)
		 * @param jobDetail
		 * @return
		 */
		public QuartzSimpleTriggerBuilder jobDetail(JobDetail jobDetail) {
			triggerFactoryBean.setJobDetail(jobDetail);
			return this;
		}
		/**
		 * @see SimpleTriggerFactoryBean#setBeanName(String)
		 * @param name
		 * @return
		 */
		public QuartzSimpleTriggerBuilder beanName(String beanName) {
			triggerFactoryBean.setBeanName(beanName);
			return this;
		}
		/**
		 * @see SimpleTriggerFactoryBean#setName(String)
		 * @param name
		 * @return
		 */
		public QuartzSimpleTriggerBuilder name(String name) {
			triggerFactoryBean.setName(name);
			return this;
		}
		/**
		 * @see SimpleTriggerFactoryBean#setGroup(String)
		 * @param group
		 * @return
		 */
		public QuartzSimpleTriggerBuilder group(String group) {
			triggerFactoryBean.setGroup(group);
			return this;
		}
		/**
		 * @see SimpleTriggerFactoryBean#setDescription(String)
		 * @param description
		 * @return
		 */
		public QuartzSimpleTriggerBuilder description(String description) {
			triggerFactoryBean.setDescription(description);
			return this;
		}
		/**
		 * @see SimpleTriggerFactoryBean#setStartDelay(long)
		 * @param startDelay
		 * @return
		 */
		public QuartzSimpleTriggerBuilder startDelay(long startDelay) {
			triggerFactoryBean.setStartDelay(startDelay);
			return this;
		}
		/**
		 * @see SimpleTriggerFactoryBean#setStartTime(Date)
		 * @param startTime
		 * @return
		 */
		public QuartzSimpleTriggerBuilder startTime(Date startTime) {
			triggerFactoryBean.setStartTime(startTime);
			return this;
		}
		/**
		 * @see SimpleTriggerFactoryBean#setMisfireInstruction(int)
		 * @param misfireInstruction
		 * @return
		 */
		public QuartzSimpleTriggerBuilder misfireInstruction(int misfireInstruction) {
			triggerFactoryBean.setMisfireInstruction(misfireInstruction);
			return this;
		}
		/**
		 * @see SimpleTriggerFactoryBean#setMisfireInstructionName(String)
		 * @param misfireInstructionName
		 * @return
		 */
		public QuartzSimpleTriggerBuilder misfireInstructionName(String misfireInstructionName) {
			triggerFactoryBean.setMisfireInstructionName(misfireInstructionName);
			return this;
		}
		/**
		 * @see SimpleTriggerFactoryBean#setPriority(int)
		 * @param priority
		 * @return
		 */
		public QuartzSimpleTriggerBuilder priority(int priority) {
			triggerFactoryBean.setPriority(priority);
			return this;
		}
		/**
		 * @see SimpleTriggerFactoryBean#setRepeatCount(int)
		 * @param repeatCount
		 * @return
		 */
		public QuartzSimpleTriggerBuilder repeatCount(int repeatCount) {
			triggerFactoryBean.setRepeatCount(repeatCount);
			return this;
		}
		/**
		 * @see SimpleTriggerFactoryBean#setRepeatInterval(long)
		 * @param repeatInterval
		 * @return
		 */
		public QuartzSimpleTriggerBuilder repeatInterval(long repeatInterval) {
			triggerFactoryBean.setRepeatInterval(repeatInterval);
			return this;
		}
		
		/**
		 * @see SimpleTriggerFactoryBean#setJobDataAsMap(Map)
		 * @param jobData
		 * @return
		 */
		public QuartzSimpleTriggerBuilder putJobData(Map<String, ?> jobData) {
			if (null != jobData) {
				triggerFactoryBean.getJobDataMap().putAll(jobData);
			}
			return this;
		}

		/**
		 * @see SimpleTriggerFactoryBean#setJobDataAsMap(Map)
		 * @param key
		 * @param value
		 * @return
		 */
		public QuartzSimpleTriggerBuilder addJobData(String key, Object value) {
			triggerFactoryBean.getJobDataMap().put(key, value);
			return this;
		}

		public SimpleTriggerFactoryBean getTriggerFactoryBean() {
			return triggerFactoryBean;
		}
		
		public SimpleTrigger build() throws ParseException {
			triggerFactoryBean.afterPropertiesSet();
			return triggerFactoryBean.getObject();
		}
	}
	
	/**
	 * creates a SimpleTriggerFactoryBean regarding the parameters with {@link SimpleTrigger#REPEAT_INDEFINITELY} and
	 * {@link SimpleTrigger#MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_REMAINING_COUNT}
	 * 
	 * @see QuartzUtils#createSimpleTrigger(JobDetail, String, String, String, long, long, int, int, Map, int)
	 *  
	 * @param jobDetail
	 * @param triggerName (optional)
	 * @param triggerGroup (optional)
	 * @param triggerDescription (optional)
	 * @param startDelay
	 * @param repeatInterval
	 * @param jobData (optional)
	 * @return
	 */
	public static SimpleTriggerFactoryBean createSimpleTrigger(JobDetail jobDetail, String triggerName,
			String triggerGroup, String triggerDescription, long startDelay, long repeatInterval,
			Map<String, ?> jobData) {
		
		return QuartzUtils.simpleTriggerBuilder().jobDetail(jobDetail).name(triggerName).group(triggerGroup)
				.description(triggerDescription).startDelay(startDelay).repeatInterval(repeatInterval)
				.repeatCount(SimpleTrigger.REPEAT_INDEFINITELY)
				.misfireInstruction(SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_REMAINING_COUNT)
				.putJobData(jobData).getTriggerFactoryBean();
	}
	
	/**
	 * <br><br>
	 * <code>
	 * {@literal @}Bean<br>
	 * public SimpleTriggerFactoryBean createSimpleTrigger({@literal @}Qualifier("simpleJobDetail") JobDetail jobDetail) {<br>
	 *     return QuartzJobUtils.createSimpleTrigger(jobDetail, null, null, "Simple trigger 1", 5000L, 60000L, SimpleTrigger.REPEAT_INDEFINITELY,
	 *     SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_REMAINING_COUNT, null, 1 );<br>
	 * }<br>
	 * </code>
	 * 
	 * @param jobDetail
	 * @param triggerName (optional)
	 * @param triggerGroup (optional)
	 * @param triggerDescription (optional)
	 * @param startDelay
	 * @param repeatInterval for infinity: {@link SimpleTrigger#REPEAT_INDEFINITELY}
	 * @param repeatCount
	 * @param misfireInstruction {@link SimpleTrigger}
	 * @param jobData (optional)
	 * @param priority
	 * @return
	 */
	public static SimpleTriggerFactoryBean createSimpleTrigger(
			JobDetail jobDetail, String triggerName, String triggerGroup, String triggerDescription,
			long startDelay, long repeatInterval, int repeatCount, int misfireInstruction, Map<String, ?> jobData, int priority) {
		
		return QuartzUtils.simpleTriggerBuilder().jobDetail(jobDetail).name(triggerName).group(triggerGroup)
				.description(triggerDescription).startDelay(startDelay).repeatInterval(repeatInterval)
				.repeatCount(repeatCount).misfireInstruction(misfireInstruction).putJobData(jobData).priority(priority)
				.getTriggerFactoryBean();
	}
	
	/**
	 * 
	 * @return build wrapper for cron trigger 
	 */
	public static QuartzCronTriggerBuilder cronTriggerBuilder() {
	    return new QuartzCronTriggerBuilder();
	}
	
	/**
	 * build wrapper for CronTrigger
	 * @since 1.0.0
	 */
	public static class QuartzCronTriggerBuilder {
		private final CronTriggerFactoryBean triggerFactoryBean;
		
		public QuartzCronTriggerBuilder() {
			triggerFactoryBean = new CronTriggerFactoryBean();
		}
		
		/**
		 * @see CronTriggerFactoryBean#setJobDetail(JobDetail)
		 * @param jobDetail
		 * @return
		 */
		public QuartzCronTriggerBuilder jobDetail(JobDetail jobDetail) {
			triggerFactoryBean.setJobDetail(jobDetail);
			return this;
		}
		/**
		 * @see CronTriggerFactoryBean#setBeanName(String)
		 * @param name
		 * @return
		 */
		public QuartzCronTriggerBuilder beanName(String beanName) {
			triggerFactoryBean.setBeanName(beanName);
			return this;
		}
		/**
		 * @see CronTriggerFactoryBean#setName(String)
		 * @param name
		 * @return
		 */
		public QuartzCronTriggerBuilder name(String name) {
			triggerFactoryBean.setName(name);
			return this;
		}
		/**
		 * @see CronTriggerFactoryBean#setGroup(String)
		 * @param group
		 * @return
		 */
		public QuartzCronTriggerBuilder group(String group) {
			triggerFactoryBean.setGroup(group);
			return this;
		}
		/**
		 * @see CronTriggerFactoryBean#setDescription(String)
		 * @param description
		 * @return
		 */
		public QuartzCronTriggerBuilder description(String description) {
			triggerFactoryBean.setDescription(description);
			return this;
		}
		/**
		 * @see CronTriggerFactoryBean#setCronExpression(String)
		 * @param cronExpression
		 * @return
		 */
		public QuartzCronTriggerBuilder cronExpression(String cronExpression) {
			triggerFactoryBean.setCronExpression(cronExpression);
			return this;
		}
		/**
		 * @see CronTriggerFactoryBean#setStartDelay(long)
		 * @param startDelay
		 * @return
		 */
		public QuartzCronTriggerBuilder startDelay(long startDelay) {
			triggerFactoryBean.setStartDelay(startDelay);
			return this;
		}
		/**
		 * @see CronTriggerFactoryBean#setStartTime(Date)
		 * @param startTime
		 * @return
		 */
		public QuartzCronTriggerBuilder startTime(Date startTime) {
			triggerFactoryBean.setStartTime(startTime);
			return this;
		}
		/**
		 * @see CronTriggerFactoryBean#setMisfireInstruction(int)
		 * @param misfireInstruction
		 * @return
		 */
		public QuartzCronTriggerBuilder misfireInstruction(int misfireInstruction) {
			triggerFactoryBean.setMisfireInstruction(misfireInstruction);
			return this;
		}
		/**
		 * @see CronTriggerFactoryBean#setMisfireInstructionName(String)
		 * @param misfireInstructionName
		 * @return
		 */
		public QuartzCronTriggerBuilder misfireInstructionName(String misfireInstructionName) {
			triggerFactoryBean.setMisfireInstructionName(misfireInstructionName);
			return this;
		}
		/**
		 * @see CronTriggerFactoryBean#setPriority(int)
		 * @param priority
		 * @return
		 */
		public QuartzCronTriggerBuilder priority(int priority) {
			triggerFactoryBean.setPriority(priority);
			return this;
		}
		/**
		 * @see TimeZone#getTimeZone(String)
		 * @see CronTriggerFactoryBean#setTimeZone(TimeZone)
		 * @param timeZone
		 * @return
		 */
		public QuartzCronTriggerBuilder timeZone(String timeZone) {
			return this.timeZone(TimeZone.getTimeZone(timeZone));
		}
		/**
		 * @see CronTriggerFactoryBean#setTimeZone(TimeZone)
		 * @param timeZone
		 * @return
		 */
		public QuartzCronTriggerBuilder timeZone(TimeZone timeZone) {
			triggerFactoryBean.setTimeZone(timeZone);
			return this;
		}
		/**
		 * @see CronTriggerFactoryBean#setJobDataAsMap(Map)
		 * @param jobData
		 * @return
		 */
		public QuartzCronTriggerBuilder putJobData(Map<String, ?> jobData) {
			if (null != jobData) {
				triggerFactoryBean.getJobDataMap().putAll(jobData);
			}
			return this;
		}
		/**
		 * @see CronTriggerFactoryBean#setJobDataAsMap(Map)
		 * @param key
		 * @param value
		 * @return
		 */
		public QuartzCronTriggerBuilder addJobData(String key, Object value) {
			triggerFactoryBean.getJobDataMap().put(key, value);
			return this;
		}
		/**
		 * @see CronTriggerFactoryBean#setCalendarName(String)
		 * @param calendarName
		 * @return
		 */
		public QuartzCronTriggerBuilder calendarName(String calendarName) {
			triggerFactoryBean.setCalendarName(calendarName);
			return this;
		}
		/**
		 * 
		 * @return
		 */
		public CronTriggerFactoryBean getTriggerFactoryBean() {
			return triggerFactoryBean;
		}
		/**
		 * 
		 * @return
		 * @throws ParseException
		 */
		public CronTrigger build() throws ParseException {
			triggerFactoryBean.afterPropertiesSet();
			return triggerFactoryBean.getObject();
		}
	}
	
	/**
	 * creates a CronTriggerFactoryBean regarding the parameters with {@link CronTrigger#MISFIRE_INSTRUCTION_DO_NOTHING}
	 * @see QuartzUtils#createCronTrigger(JobDetail, String, String, String, String, int, Map, long, Date, String, int)
	 * 
	 * @param jobDetail
	 * @param triggerName (optional)
	 * @param triggerGroup (optional)
	 * @param triggerDescription (optional)
	 * @param cronExpression
	 * @param startDelay
	 * @param jobData (optional)
	 * @return
	 */
	public static CronTriggerFactoryBean createCronTrigger(JobDetail jobDetail, String triggerName, String triggerGroup,
			String triggerDescription, String cronExpression, long startDelay, Map<String, ?> jobData) {

		return QuartzUtils.cronTriggerBuilder().jobDetail(jobDetail).name(triggerName).group(triggerGroup)
				.description(triggerDescription).cronExpression(cronExpression).startDelay(startDelay)
				.misfireInstruction(CronTrigger.MISFIRE_INSTRUCTION_DO_NOTHING).putJobData(jobData)
				.getTriggerFactoryBean();
	}
	
	/**
	 * returns a cron trigger factory bean to put into spring context
	 *
	 * @param jobDetail
	 * @param triggerName (optional)
	 * @param triggerGroup (optional)
	 * @param triggerDescription (optional)
	 * @param cronExpression
	 * @param misfireInstruction see {@link CronTrigger}
	 * @param jobData
	 * @param startDelay if start delay is set, startTime will be ignored
	 * @param startTime
	 * @param timeZone
	 * @param priority
	 * @return
	 */
	public static CronTriggerFactoryBean createCronTrigger(JobDetail jobDetail, String triggerName, String triggerGroup,
			String triggerDescription, String cronExpression, int misfireInstruction, Map<String, ?> jobData,
			long startDelay, Date startTime, String timeZone, int priority) {

		return QuartzUtils.cronTriggerBuilder().jobDetail(jobDetail).name(triggerName).group(triggerGroup)
				.description(triggerDescription).cronExpression(cronExpression).startDelay(startDelay)
				.misfireInstruction(misfireInstruction).putJobData(jobData).timeZone(timeZone).startTime(startTime)
				.priority(priority).getTriggerFactoryBean();
	}
}
