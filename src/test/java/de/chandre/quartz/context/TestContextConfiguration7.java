package de.chandre.quartz.context;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import de.chandre.quartz.spring.QuartzSchedulerAutoConfiguration;

@Configuration
@AutoConfigureBefore(QuartzSchedulerAutoConfiguration.class)
public class TestContextConfiguration7 {
	
	@Bean("dataSource")
	public DataSource dataSource() {
		
		return new EmbeddedDatabaseBuilder().generateUniqueName(true)
	            .setType(EmbeddedDatabaseType.H2)
	            .setScriptEncoding("UTF-8")
	            .ignoreFailedDrops(true).build();
	}
	
	@Bean("otherDataSource")
	@Primary
	public DataSource otherDataSource() {
		
		return new EmbeddedDatabaseBuilder().generateUniqueName(true)
	            .setType(EmbeddedDatabaseType.H2)
	            .setScriptEncoding("UTF-8")
	            .ignoreFailedDrops(true).build();
	}
}
