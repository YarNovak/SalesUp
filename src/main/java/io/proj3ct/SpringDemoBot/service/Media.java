package io.proj3ct.SpringDemoBot.service;

import io.proj3ct.SpringDemoBot.config.BotConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Contact;

import java.util.HashMap;
import java.util.Map;

@Component
public class Media {


    private final Map<Person, Contact> media = new HashMap<>();

    public void clear() {
        media.clear();
    }

    public Contact get(Long chatId, Long bot_id) {
        return media.get(new Person(chatId, bot_id));
    }

    public void put(Long chatId, Contact contact, Long bot_id) {
        media.put(new Person(chatId, bot_id), contact);
    }

    public void remove(Long chatId, Long bot_id) {
        media.remove(new Person(chatId, bot_id));
    }

}
