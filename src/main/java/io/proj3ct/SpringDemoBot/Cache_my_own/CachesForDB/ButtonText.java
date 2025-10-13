package io.proj3ct.SpringDemoBot.Cache_my_own.CachesForDB;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Setter
@Getter
public class ButtonText {

    private Map<String, String> texts = new HashMap<>();

}
