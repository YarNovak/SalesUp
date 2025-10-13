package io.proj3ct.SpringDemoBot.DaO;


import io.proj3ct.SpringDemoBot.config.BotConfig;
import io.proj3ct.SpringDemoBot.dopclasses.Senders.SendWhatever;
import io.proj3ct.SpringDemoBot.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class Handle_Menu {

    @Autowired
    VapecomponyRepository vapecomponyRepository;

    @Autowired
    VapecomponyKatalogRepository vapecomponyKatalogRepository;

    @Autowired
    private SendWhatever sendWhatever;

    @Autowired
    private BotConfig config;

    TelegramLongPollingBot bot;

    public void handle(Long chatId, Long vp_id, TelegramLongPollingBot bot){
        this.bot = bot;
        List<Vapecompony_katalog> l =  vapecomponyKatalogRepository.findByVapecompony_idAndBot_Id(vp_id, Long.valueOf(config.getBoit()));

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setParseMode("HTML");
        message.setText("Готов к апгрейду реальности?\n" +
                "\n" +
                "⚡\uFE0FНажимай – и подключайся \uD83E\uDDEC\n" +
                "\n" +
                "\uD83C\uDF21\uFE0FЧТО ЕСТЬ В ЛАБОРАТОРИИ?\uD83E\uDDEA");

        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();

        for(Vapecompony_katalog vcr : l){

            List<InlineKeyboardButton> rowInLine = new ArrayList<>();

            var yesButton = new InlineKeyboardButton();

            vcr.getName();
            yesButton.setText(vcr.getName());
            yesButton.setCallbackData(vcr.getName());
            rowInLine.add(yesButton);

            rowsInLine.add(rowInLine);

        }



        markupInLine.setKeyboard(rowsInLine);
        message.setReplyMarkup(markupInLine);

        //executeMessage(message);

        sendWhatever.sendhere_vapecompony(bot, chatId, vp_id, Long.valueOf(config.getBoit()), markupInLine, null);


    }


    private void executeMessage(SendMessage message) {
        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


}
