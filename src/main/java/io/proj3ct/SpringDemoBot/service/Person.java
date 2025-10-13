package io.proj3ct.SpringDemoBot.service;

import io.proj3ct.SpringDemoBot.DB_entities.BotClient;
import io.proj3ct.SpringDemoBot.config.BotConfig;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;

import java.util.Objects;


public class Person {

    private final Long chat_id;
    private final Long bot_id;

    public Person(Long chat_id, Long bot_id) {
        this.chat_id = chat_id;
        this.bot_id = bot_id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person)) return false;
        Person person = (Person) o;
        return chat_id.equals(person.chat_id) && bot_id.equals(person.bot_id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chat_id, bot_id);
    }




}
