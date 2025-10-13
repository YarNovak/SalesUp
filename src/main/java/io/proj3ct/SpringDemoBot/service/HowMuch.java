package io.proj3ct.SpringDemoBot.service;

import io.proj3ct.SpringDemoBot.config.BotConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class HowMuch {

    @Autowired
    private BotConfig config;

    private final Map<Person, Boolean> wait_howmuchyouhave = new HashMap<>();
    public void clear() {
        wait_howmuchyouhave.clear();
    }

    public boolean getOrDefault(Long chatId, boolean f) {
        return wait_howmuchyouhave.getOrDefault(new Person(chatId, Long.valueOf(config.getBoit())), false);
    }

    public void put(Long chatId, boolean f) {
        wait_howmuchyouhave.put(new Person(chatId, Long.valueOf(config.getBoit())), true);
    }

    public void remove(Long chatId) {
        wait_howmuchyouhave.remove(new Person(chatId, Long.valueOf(config.getBoit())));
    }
}
