package io.proj3ct.SpringDemoBot.service;

import io.proj3ct.SpringDemoBot.config.BotConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class Wait_id {


    private final Map<Person, Boolean> wait_id = new HashMap<>();

    public void clear() {
        wait_id.clear();
    }

    public boolean getOrDefault(Long chatId, boolean f, Long bot_id) {
        return wait_id.getOrDefault(new Person(chatId, bot_id), false);
    }

    public void put(Long chatId, boolean f, Long bot_id) {
        wait_id.put(new Person(chatId, bot_id), true);
    }

    public void remove(Long chatId, Long bot_id) {
        wait_id.remove(new Person(chatId, bot_id));
    }
}
