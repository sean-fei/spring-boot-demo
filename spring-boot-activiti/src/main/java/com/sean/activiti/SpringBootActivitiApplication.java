package com.sean.activiti;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"com.sean.activiti"})
@EnableAutoConfiguration(exclude = {
        org.activiti.spring.boot.SecurityAutoConfiguration.class,
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
})
public class SpringBootActivitiApplication {

    public static void main(String[] args) {
        ApplicationContext app = SpringApplication.run(SpringBootActivitiApplication.class, args);
//        SpringContextUtils.setApplicationContext(app);
    }

}
