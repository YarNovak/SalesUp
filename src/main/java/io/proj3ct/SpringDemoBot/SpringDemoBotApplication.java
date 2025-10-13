package io.proj3ct.SpringDemoBot;

import io.proj3ct.SpringDemoBot.model.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
@EnableTransactionManagement
public class SpringDemoBotApplication {

	public static void main(String[] args)  {

		//ConfigurableApplicationContext context = SpringApplication.run(SpringDemoBotApplication.class, args);
		//CartService cartService = context.getBean(CartService.class);
		//cartService.startCartCleanupJob();
		SpringApplication.run(SpringDemoBotApplication.class, args);



	}

}
