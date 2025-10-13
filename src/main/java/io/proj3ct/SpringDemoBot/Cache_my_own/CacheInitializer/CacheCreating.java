package io.proj3ct.SpringDemoBot.Cache_my_own.CacheInitializer;

import io.proj3ct.SpringDemoBot.Cache_my_own.CachesForDB.ButtonText;
import io.proj3ct.SpringDemoBot.config.BotConfig;
import io.proj3ct.SpringDemoBot.repository.BotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class CacheCreating implements CommandLineRunner {

    @Autowired
    private final CacheDev cacheDev;



    @Override
    public void run(String... args) throws Exception {
        log.info("Executing startup commands...");

        cacheDev.initButtonText();
        cacheDev.initMessages();
        System.out.println();
    }


}
