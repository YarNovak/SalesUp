package io.proj3ct.SpringDemoBot.DaO;

import io.proj3ct.SpringDemoBot.Cache_my_own.CachesForDB.ButtonText;
import io.proj3ct.SpringDemoBot.TenantService;
import io.proj3ct.SpringDemoBot.config.BotConfig;
import io.proj3ct.SpringDemoBot.dopclasses.MessageRepo.MessageRegistry;
import io.proj3ct.SpringDemoBot.dopclasses.Senders.SendWhatever;
import io.proj3ct.SpringDemoBot.model.Orders;
import io.proj3ct.SpringDemoBot.model.OrdersRepository;

import io.proj3ct.SpringDemoBot.repository.BotRepository;
import io.proj3ct.SpringDemoBot.service.HowMuch;
import io.proj3ct.SpringDemoBot.service.Wait_id;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Component
public class CashCallbackHandler implements CallbackHandler{



    @Autowired
    private OrdersRepository orderRepository;

    @Autowired
    private HowMuch wait_howmuchyouhave;



    @Autowired
    Wait_id wait_id;

    @Autowired
    private ButtonText buttonText;

    @Autowired
    private SendWhatever sendWhatever;
    @Autowired
    private MessageRegistry messageRegistry;
    @Autowired
    private TenantService tenantService;
    @Autowired
    private BotRepository botRepository;

    @Override
    public boolean support(String callbackData) {
       return callbackData.equals("CASH");
    }

    @Override
    public void handle(CallbackQuery query, Long bot_id) {

         messageRegistry.deleteMessagesAfter(query.getMessage().getChatId(), query.getMessage().getMessageId(), false, bot_id);

        Long chatId = query.getMessage().getChatId();

        Orders or = orderRepository.findByUser_ChatIdAndPaidEqualsAndBot_Id(chatId, false, bot_id).get();
        or.setCash_card("CASH");
        orderRepository.save(or);
        send_id(chatId, bot_id);

       // send_howmuchyouhave(chatId);

    }


    public void send_id(Long chatId, Long bot_id){

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

        KeyboardButton contactButton = new KeyboardButton(buttonText.getTexts().get("contact"));
        contactButton.setRequestContact(true); // << це головне

        KeyboardRow row = new KeyboardRow();
        row.add(contactButton);

        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(row);

        keyboardMarkup.setKeyboard(keyboard);
        message.setReplyMarkup(keyboardMarkup);


        wait_id.put(chatId, true);


        AbsSender sender = tenantService.getSender(botRepository.findById(bot_id).orElse(null).getBotToken());

        sendWhatever.sendhere_message(sender, chatId, "phone",  null, keyboardMarkup);
        //bot.execute(message);


    }

}


