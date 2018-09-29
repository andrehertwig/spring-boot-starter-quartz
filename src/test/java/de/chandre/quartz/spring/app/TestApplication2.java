package de.chandre.quartz.spring.app;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableMBeanExport;

@SpringBootApplication
@EnableAutoConfiguration
@EnableMBeanExport
@ComponentScan(basePackages={"de.chandre.quartz.spring"})
public class TestApplication2 {
	
}
