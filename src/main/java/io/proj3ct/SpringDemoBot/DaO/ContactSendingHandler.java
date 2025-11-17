package io.proj3ct.SpringDemoBot.DaO;


import io.proj3ct.SpringDemoBot.Cache_my_own.CachesForDB.ButtonText;
import io.proj3ct.SpringDemoBot.TenantService;
import io.proj3ct.SpringDemoBot.config.BotConfig;
import io.proj3ct.SpringDemoBot.dopclasses.Senders.SendWhatever;
import io.proj3ct.SpringDemoBot.model.*;
import io.proj3ct.SpringDemoBot.repository.BotRepository;
import io.proj3ct.SpringDemoBot.service.Adddelivery;
import io.proj3ct.SpringDemoBot.service.Media;

import io.proj3ct.SpringDemoBot.service.Wait_id;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ContactSendingHandler {


    @Autowired
    private Wait_id wait_id;

    @Autowired
    private OrdersRepository orderRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private Media media;

    @Autowired
    private Adddelivery add_DELIVERY;

    @Autowired
    private BotConfig config;
    @Autowired
    private BotRepository botRepository;
    @Autowired
    private ButtonText buttonText;
    @Autowired
    private SendWhatever sendWhatever;
    @Autowired
    private TenantService tenantService;


    public void get_contact(Update update, Long bot_id) {




        Long chatId = update.getMessage().getChatId();

        sendKeyboard(chatId, "Теперь у нас есть возможность быть к вам ближе!)", bot_id);

        wait_id.remove(chatId);

        // List<PhotoSize> photos = update.getMessage().getPhoto();
        // PhotoSize largestPhoto = photos.get(photos.size() - 1); // Найбільше за розміром
        // String fileId = largestPhoto.getFileId();

        //InputMediaPhoto inputMedia = new InputMediaPhoto(fileId);

        media.put(chatId, update.getMessage().getContact());
        if(orderRepository.findByUser_ChatIdAndPaidEqualsAndBot_Id(chatId, false, Long.valueOf(config.getBoit())).get().getCash_card().equals("CASH")) sendchoise_message2(chatId, "CASH", bot_id);
        else sendchoise_message2(chatId, "CARD", bot_id);

        return;

    }
    public void no_contact(Update update, Long bot_id) {

        AbsSender sender = tenantService.getSender(botRepository.findById(bot_id).orElse(null).getBotToken());
        SendMessage sendMessage = new SendMessage(update.getMessage().getChatId().toString(), "Что-то явно пошло не так\uD83D\uDE05");

        try{
            sender.execute(sendMessage);
        }
        catch(TelegramApiException e){
            e.printStackTrace();
        }
        return;
    }


    private void sendKeyboard(long chatId, String textToSend, Long bot_id) {


        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));

        message.setText(textToSend);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();

        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();

        // row.add("\uD83E\uDDEAКаталог");
        // row.add("\uD83D\uDCB8Оплата");
        // row.add("\uD83D\uDED2Корзина");

        row.add(buttonText.getTexts().get("catalog"));
        row.add(buttonText.getTexts().get("payment"));
        row.add(buttonText.getTexts().get("cart"));

        keyboardRows.add(row);

        keyboardMarkup.setKeyboard(keyboardRows);
        keyboardMarkup.setResizeKeyboard(true);


        message.setReplyMarkup(keyboardMarkup);


        //executeMessage(message);
        AbsSender sender = tenantService.getSender(botRepository.findById(bot_id).orElse(null).getBotToken());
        sendWhatever.sendhere_message(sender, chatId, "phone_thanks",  null, keyboardMarkup);



    }


    public void sendchoise_message2(long chatId, String cur, Long bot_id){


        add_DELIVERY.put(chatId, true);

        Orders order = orderRepository.findByUser_ChatIdAndPaidEqualsAndBot_Id(chatId, false, Long.valueOf(config.getBoit())).get();
        order.setCurrency("PLN");
        order.setCash_card(cur);

        List<CartItem> cartItems = cartItemRepository.findByChatIdAndBot_Id(chatId, Long.valueOf(config.getBoit()));
        System.out.println(order.getCreatedAt().toString());
        List<FinalItem> finalItems = cartItems.stream().map(cartItem -> {
            FinalItem finalItem = new FinalItem();
            finalItem.setName(cartItem.getVapecomponyKatalog().getName());
            finalItem.setCena(cartItem.getVapecomponyKatalog().getCena());
            finalItem.setQuantity(cartItem.getQuantity());
            finalItem.setBot(botRepository.findById(Long.valueOf(config.getBoit())).get());
            finalItem.setOrder(order);
            finalItem.setVid(cartItem.getVapecomponyKatalog().getId());
            finalItem.setMenu(cartItem.getVapecomponyKatalog().getName());
            return finalItem;
        }).collect(Collectors.toList());

        order.setFinalItems(finalItems);
        order.setBot(botRepository.findById(Long.valueOf(config.getBoit())).get());


        orderRepository.save(order);

        SendMessage sent = new SendMessage(String.valueOf(chatId), "\uD83D\uDCDD Введите адрес доставки чтобы Ella Spot прочекала ваш IP. \n" +
                "Ахахахах - шутка\uD83E\uDD2A \n" +
                "\n" +
                "\n" +
                "\uD83E\uDD14Например :\n" +
                "\n" +
                "✅m. Nowy Jork.\n" +
                "✅ul. Greenwich Street.\n" +
                "✅bufynek 180.\n" +
                "✅kod pocztowy 10007.");

        AbsSender sender = tenantService.getSender(botRepository.findById(bot_id).orElse(null).getBotToken());
        sendWhatever.sendhere_message(sender, chatId, "delivery",  null, null);

    }


}
