package com.example.lal;

import com.example.lal.Controller.ConsoleController;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LalApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context=SpringApplication.run(LalApplication.class, args);
		ConsoleController consoleController = context.getBean(ConsoleController.class);
		consoleController.startConsole();
	}

}
