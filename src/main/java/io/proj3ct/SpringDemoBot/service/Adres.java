package io.proj3ct.SpringDemoBot.service;

import io.proj3ct.SpringDemoBot.config.BotConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Contact;

import java.util.HashMap;
import java.util.Map;
@Component
public class Adres {

    @Autowired
    private BotConfig config;

    private final Map<Person, String> adres = new HashMap<>();

    public void clear() {
        adres.clear();
    }

    public String get(Long chatId) {
        return adres.get(new Person(chatId, Long.valueOf(config.getBoit())));
    }

    public void put(Long chatId, String adresa) {
        adres.put(new Person(chatId, Long.valueOf(config.getBoit())), adresa);
    }

    public void remove(Long chatId) {
        adres.remove(new Person(chatId, Long.valueOf(config.getBoit())));
    }
}
