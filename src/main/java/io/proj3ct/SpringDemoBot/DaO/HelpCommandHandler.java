package io.proj3ct.SpringDemoBot.DaO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class HelpCommandHandler implements CommandHandler {


    @Override
    public boolean support(String command) {
        return "/help".equals(command);
    }

    @Override
    public void handle(Message message, Long bot_id) {

        prepareAndSendMessage(message.getChatId(), "Ella Spot была создана чтобы упростить и ускорить процес покупки выдающегося \n" +
                "товара SPOT.LAB\uD83D\uDE2E\u200D\uD83D\uDCA8\n" +
                "\n" +
                "\uD83D\uDFE2 № 1 По жидкостям\n" +
                "\uD83D\uDFE2 топ ассортимент\n" +
                "\uD83D\uDFE2 быстро отвечаем\n" +
                "\n" +
                "Используй  /start и начни обозревать лабораторию\uD83D\uDE1C");

    }
    private void prepareAndSendMessage(long chatId, String textToSend){
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
       // executeMessage(message);


    }


}
