package de.chandre.quartz.jobs;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

/**
 * simple example of Quartz job
 * @author Andre
 *
 */
public class SimpleJob implements Job 
{
	private static final Log LOGGER = LogFactory.getLog(SimpleJob.class);
	
    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
    	LOGGER.info("start executing job: " + jobExecutionContext.getJobDetail().getKey().getName());
	}
}
