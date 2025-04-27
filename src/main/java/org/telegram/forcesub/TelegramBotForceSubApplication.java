package org.telegram.forcesub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class TelegramBotForceSubApplication {

	public static void main(String[] args) {
		SpringApplication.run(TelegramBotForceSubApplication.class, args);
	}

}
