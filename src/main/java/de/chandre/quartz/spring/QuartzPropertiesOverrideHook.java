package de.chandre.quartz.spring;

import java.util.Properties;

/**
 * A hook to override some properties, maybe with instance-specific values after application start-up
 * @author André
 * @since 1.0.0
 */
public interface QuartzPropertiesOverrideHook {
	
	/**
	 * This method will be called after all properties are loaded, if configured correctly
	 * 
	 * @param quartzProperties loaded quartz properties (<b>could be <code>null</code>!</b>)
	 * @return overridden properties
	 */
	Properties override(Properties quartzProperties);
}
