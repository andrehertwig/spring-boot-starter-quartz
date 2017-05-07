package de.chandre.quartz.spring;

import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

/**
 * Autowire Quartz Jobs with Spring context dependencies.
 * 
 * @see "http://stackoverflow.com/questions/6990767/inject-bean-reference-into-a-quartz-job-in-spring/15211030#15211030"
 */
public final class AutowiringSpringBeanJobFactory extends SpringBeanJobFactory implements ApplicationContextAware {
	/**
	 * Holding an auto wire capable bean spring bean factory.
	 */
	private AutowireCapableBeanFactory beanFactory;

	/**
	 * Constructor that takes an auto wire capable bean spring bean factory.
	 * @param context
	 * 				auto wire capable bean spring bean factory
	 */
	@Override
	public void setApplicationContext(final ApplicationContext context) {
		beanFactory = context.getAutowireCapableBeanFactory();
	}

	@Override
	protected Object createJobInstance(final TriggerFiredBundle bundle) throws Exception { //NOPMD
		final Object job = super.createJobInstance(bundle);
		beanFactory.autowireBean(job);
		return job;
	}
}
