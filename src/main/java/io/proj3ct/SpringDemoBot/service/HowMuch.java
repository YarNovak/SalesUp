package io.proj3ct.SpringDemoBot.service;

import io.proj3ct.SpringDemoBot.config.BotConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class HowMuch {



    private final Map<Person, Boolean> wait_howmuchyouhave = new HashMap<>();
    public void clear() {
        wait_howmuchyouhave.clear();
    }

    public boolean getOrDefault(Long chatId, boolean f, Long bot_id) {
        return wait_howmuchyouhave.getOrDefault(new Person(chatId, bot_id), false);
    }

    public void put(Long chatId, boolean f, Long bot_id) {
        wait_howmuchyouhave.put(new Person(chatId, bot_id), true);
    }

    public void remove(Long chatId, Long bot_id) {
        wait_howmuchyouhave.remove(new Person(chatId, bot_id));
    }
}
