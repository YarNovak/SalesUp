package io.proj3ct.SpringDemoBot.Cache_my_own.CachesForDB;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component

public class ButtonText {

    private Map<Long, Map<String, String>> texts = new HashMap<>();

    public void setTexts(long id, Map<String, String> texts) {

        this.texts.put(id, texts);

    }
    public Map<String, String> getTexts(long id) {
        return texts.get(id);
    }

}
