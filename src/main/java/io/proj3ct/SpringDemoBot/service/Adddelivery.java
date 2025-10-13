package io.proj3ct.SpringDemoBot.service;

import io.proj3ct.SpringDemoBot.config.BotConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class Adddelivery {

    @Autowired
    private BotConfig config;

    private final Map<Person, Boolean> add_DELIVERY = new HashMap<>();

    public void clear() {
        add_DELIVERY.clear();
    }

    public boolean getOrDefault(Long chatId, boolean f) {
        return add_DELIVERY.getOrDefault(new Person(chatId, Long.valueOf(config.getBoit())), false);
    }

    public void put(Long chatId, boolean f) {
        add_DELIVERY.put(new Person(chatId, Long.valueOf(config.getBoit())), true);
    }

    public void remove(Long chatId) {
        add_DELIVERY.remove(new Person(chatId, Long.valueOf(config.getBoit())));
    }
}
