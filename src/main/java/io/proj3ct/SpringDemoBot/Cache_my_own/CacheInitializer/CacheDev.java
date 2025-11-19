package io.proj3ct.SpringDemoBot.Cache_my_own.CacheInitializer;

import io.proj3ct.SpringDemoBot.Cache_my_own.CachesForDB.ButtonText;
import io.proj3ct.SpringDemoBot.Cache_my_own.CachesForDB.MessagesInf;
import io.proj3ct.SpringDemoBot.DB_entities.Bot;
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
    private final ButtonText buttonText;
    @Autowired
    private MessagesInf infa;
    @Autowired
    private final BotMessageRepository botMessageRepository;


    public void initButtonText() {

        List<Bot> bots = botRepository.findAll();

        for(Bot bot : bots) {

            Map<String, String> buttons = botRepository.findById(bot.getId()).get().getButtonTexts();
            buttonText.setTexts(bot.getId(), buttons);
            System.out.println(buttonText.getTexts(bot.getId()));

        }





    }

    public void initMessages() {

        List<Bot> bots = botRepository.findAll();
        for(Bot bot : bots) {

            Map<String, MessagesInf.Information> informationMap = new HashMap<>();
            List<BotMessage> list_of_messages = botMessageRepository.findByBot_Id(bot.getId());

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

            infa.setMessagesForId(bot.getId(), informationMap);

        }
    }
}
