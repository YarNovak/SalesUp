package io.proj3ct.SpringDemoBot.service;

import io.proj3ct.SpringDemoBot.config.BotConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Contact;

import java.util.HashMap;
import java.util.Map;
@Component
public class Adres {

    private final Map<Person, String> adres = new HashMap<>();

    public void clear() {
        adres.clear();
    }

    public String get(Long chatId, Long bot_id) {
        return adres.get(new Person(chatId, bot_id));
    }

    public void put(Long chatId, String adresa, Long bot_id) {
        adres.put(new Person(chatId, bot_id), adresa);
    }

    public void remove(Long chatId, Long bot_id) {
        adres.remove(new Person(chatId, bot_id));
    }
}
