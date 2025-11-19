package io.proj3ct.SpringDemoBot.DaO;

import org.telegram.telegrambots.meta.api.objects.Message;

public interface ButtonsMapHandler {

    boolean support(String command, Long bot_id);
    void handle(Message message, Long bot_id);

}
