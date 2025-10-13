package io.proj3ct.SpringDemoBot.service;

import io.proj3ct.SpringDemoBot.config.BotConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class Wait_id {

    @Autowired
    private BotConfig config;

    private final Map<Person, Boolean> wait_id = new HashMap<>();

    public void clear() {
        wait_id.clear();
    }

    public boolean getOrDefault(Long chatId, boolean f) {
        return wait_id.getOrDefault(new Person(chatId, Long.valueOf(config.getBoit())), false);
    }

    public void put(Long chatId, boolean f) {
        wait_id.put(new Person(chatId, Long.valueOf(config.getBoit())), true);
    }

    public void remove(Long chatId) {
        wait_id.remove(new Person(chatId, Long.valueOf(config.getBoit())));
    }
}
