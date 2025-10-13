package io.proj3ct.SpringDemoBot.Cache_my_own.CacheInitializer;

import io.proj3ct.SpringDemoBot.Cache_my_own.CachesForDB.ButtonText;
import io.proj3ct.SpringDemoBot.Cache_my_own.CachesForDB.MessagesInf;
import io.proj3ct.SpringDemoBot.DB_entities.BotMessage;
import io.proj3ct.SpringDemoBot.config.BotConfig;
import io.proj3ct.SpringDemoBot.repository.BotMessageRepository;
import io.proj3ct.SpringDemoBot.repository.BotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CacheDev {

    @Autowired
    private final BotRepository botRepository;
    @Autowired
    private final BotConfig botConfig;
    @Autowired
    private final ButtonText buttonText;
    @Autowired
    private MessagesInf infa;
    @Autowired
    private final BotMessageRepository botMessageRepository;


    public void initButtonText() {

        Map<String, String> buttons = botRepository.findById(Long.valueOf(botConfig.getBoit())).get().getButtonTexts();
        buttonText.setTexts(buttons);
        System.out.println(buttonText.getTexts());

    }

    public void initMessages() {

        Map<String, MessagesInf.Information> informationMap = new HashMap<>();

        List<BotMessage> list_of_messages = botMessageRepository.findByBot_Id(Long.valueOf(botConfig.getBoit()));
        for (BotMessage botMessage : list_of_messages) {

            informationMap.put(botMessage.getMessageKey(), new MessagesInf.Information(

                    botMessage.getPhoto(),
                    botMessage.getPhotoMimeType(),
                    botMessage.getVideo(),
                    botMessage.getPhoto_updatedAt(),
                    botMessage.getVideo_updatedAt(),
                    botMessage.getVideoMimeType(),
                    botMessage.getText(),
                    botMessage.getEntitiesJson()


            ));

            System.out.println(informationMap.get(botMessage.getMessageKey()).getText());
            System.out.println("0000000000000000000000000");

        }
        System.out.println("POPA");
        infa.setMy_messages(informationMap);

        for (BotMessage botMessage : list_of_messages){

            System.out.println(infa.getMy_messages().get(botMessage.getMessageKey()).getText());

        }



    }




}
