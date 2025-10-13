package io.proj3ct.SpringDemoBot.service;

import io.proj3ct.SpringDemoBot.config.BotConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Contact;

import java.util.HashMap;
import java.util.Map;

@Component
public class Media {

    @Autowired
    private BotConfig config;

    private final Map<Person, Contact> media = new HashMap<>();

    public void clear() {
        media.clear();
    }

    public Contact get(Long chatId) {
        return media.get(new Person(chatId, Long.valueOf(config.getBoit())));
    }

    public void put(Long chatId, Contact contact) {
        media.put(new Person(chatId, Long.valueOf(config.getBoit())), contact);
    }

    public void remove(Long chatId) {
        media.remove(new Person(chatId, Long.valueOf(config.getBoit())));
    }

}
