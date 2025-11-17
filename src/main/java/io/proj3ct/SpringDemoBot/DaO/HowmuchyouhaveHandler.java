package io.proj3ct.SpringDemoBot.DaO;

import io.proj3ct.SpringDemoBot.TenantService;
import io.proj3ct.SpringDemoBot.config.BotConfig;
import io.proj3ct.SpringDemoBot.model.Orders;
import io.proj3ct.SpringDemoBot.model.OrdersRepository;
import io.proj3ct.SpringDemoBot.model.VapecomponyKatalogRepository;
import io.proj3ct.SpringDemoBot.repository.BotRepository;
import io.proj3ct.SpringDemoBot.service.HowMuch;
import io.proj3ct.SpringDemoBot.service.Wait_id;
import org.apache.commons.math3.analysis.function.Abs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class HowmuchyouhaveHandler {

    @Autowired
    private HowMuch wait_howmuchyouhave;
    @Autowired
    private Wait_id wait_id;

    @Autowired
    private BotRepository botRepository;

    @Autowired
    private OrdersRepository orderRepository;
    @Autowired
    private VapecomponyKatalogRepository vapecomponyKatalogRepository;
    @Autowired
    private TenantService tenantService;

    public void handle_howmuchyouhave(Update update, Long bot_id) {

        AbsSender sender = tenantService.getSender(botRepository.findById(bot_id).orElse(null).getBotToken());
        Long chatId = update.getMessage().getChatId();
        String messageText = update.getMessage().getText();

        Optional<Orders> order = orderRepository.findByUser_ChatIdAndPaidEqualsAndBot_Id(chatId, false, bot_id);
        if(((!vapecomponyKatalogRepository.findByNameAndBot_Id(messageText, bot_id).isEmpty()) ||(messageText.startsWith("/")) || (messageText.equals("\uD83E\uDDEAКаталог") || (messageText.equals("\uD83D\uDED2Корзина")) || (messageText.equals("\uD83D\uDCB8Оплата")) ) && (!messageText.equals("Подскажите, пожалуйста, какую сумму планируете дать курьеру? Чтобы он смог приготовить сдачу заранее \uD83D\uDE0A")))) {

            sendText(chatId, "Подскажите, пожалуйста, какую сумму планируете дать курьеру? Чтобы он смог приготовить сдачу заранее \uD83D\uDE0A", sender);
            return;
        }
        if (order.isPresent()){
            Orders or = order.get();
            or.setBot(botRepository.findById(bot_id).get());
            orderRepository.save(or);
            //wait_howmuchyouhave.remove(chatId);
            send_id(chatId, sender);
            return;
        }


    }
    public void send_id(Long chatId, AbsSender sender) {

        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText("\uD83D\uDC64Сбрось свой номер телефона\n" +
                "скорое жми «\uD83D\uDCF1Контакт» \n" +
                "ПОД ПОЛЕМ ВВОДА\uD83D\uDC47\n" +
                "\n" +
                "\n" +
                "И так анекдот от Ella Spot\uD83D\uDE43\uD83D\uDC47\n" +
                "\n" +
                "«Моя вейп-ботиха говорит, что я слишком часто парю… Но кто она, чтобы судить? Она сама из облака сделана.» …. Конец  шутки)\uD83D\uDE05\n" +
                "\n" +
                "\n" +
                "\uD83D\uDCAC  Мы синхронизируемся. Мы уже рядом.");

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);

        KeyboardButton contactButton = new KeyboardButton("\uD83D\uDCF1Контакт");
        contactButton.setRequestContact(true); // << це головне

        KeyboardRow row = new KeyboardRow();
        row.add(contactButton);

        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(row);

        keyboardMarkup.setKeyboard(keyboard);
        message.setReplyMarkup(keyboardMarkup);

        try {
            wait_id.put(chatId, true);
            sender.execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

    }
    public void sendText(long chatId, String text, AbsSender sender){

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(text);

        try{
            sender.execute(sendMessage);
        }
        catch (TelegramApiException e){
            e.printStackTrace();
        }

    }

}
