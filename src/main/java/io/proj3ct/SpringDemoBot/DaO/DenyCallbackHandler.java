package io.proj3ct.SpringDemoBot.DaO;

import io.proj3ct.SpringDemoBot.config.BotConfig;
import io.proj3ct.SpringDemoBot.dopclasses.Senders.SendWhatever;
import io.proj3ct.SpringDemoBot.model.OrderService;
import io.proj3ct.SpringDemoBot.model.Orders;
import io.proj3ct.SpringDemoBot.model.OrdersRepository;
import io.proj3ct.SpringDemoBot.service.Send;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Component
public class DenyCallbackHandler implements CallbackHandler {

    TelegramLongPollingBot bot;


    @Autowired
    private BotConfig config;

    @Override
    public boolean support(String callbackData) {
        return callbackData.startsWith("DENY");
    }

    @Autowired
    private OrdersRepository orderRepository;

    @Autowired
    private SendWhatever sendWhatever;

    @Autowired
    private OrderService orderService;

    @Override
    public void handle(CallbackQuery query, TelegramLongPollingBot bot) {

        this.bot = bot;

        Long chatId = query.getMessage().getChatId();
        String callbackData = query.getData();
        int messageId = query.getMessage().getMessageId();

        Long itemtId = Long.parseLong(callbackData.split("_")[1]);
        //sent.remove(orderRepository.findByIdAndBot_Id(itemtId, Long.valueOf(config.getBoit())).get().getUser().getChatId());
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(orderRepository.findByIdAndBot_Id(itemtId, Long.valueOf(config.getBoit())).get().getUser().getChatId()));

        Orders order = orderRepository.findByIdAndBot_Id(itemtId,Long.valueOf(config.getBoit())).get();

        sendMessage.setText("Ваше замовлення відхилено))");

        InlineKeyboardButton newButton = new InlineKeyboardButton();
        newButton.setText("❌Отклонено");
        newButton.setCallbackData("already_pressed"); // або не ставити взагалі

        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(newButton);

        InlineKeyboardMarkup newMarkup = new InlineKeyboardMarkup();
        newMarkup.setKeyboard(List.of(row));

        EditMessageReplyMarkup editMarkup = new EditMessageReplyMarkup();
        editMarkup.setChatId(String.valueOf(chatId));
        editMarkup.setMessageId(messageId);
        editMarkup.setReplyMarkup(newMarkup);


        SendAnimation photo = new SendAnimation();

        photo.setChatId(String.valueOf(orderRepository.findByIdAndBot_Id(itemtId, Long.valueOf(config.getBoit())).get().getUser().getChatId()));
        photo.setAnimation(new InputFile("https://cs7.pikabu.ru/post_img/2014/03/01/7/1393669621_572189444.gif"));
        photo.setCaption("Ваш заказ был отклонен \uD83D\uDE22\n" +
                "Напишите одному из наших менеджеров:\n" +
                "\n" +
                "@manager_ambo⁉\uFE0F\n" +
                "@mrBaffik⁉\uFE0F\n" +
                "\n" +
                "Вскоре мы исправим это недорозумение\uD83E\uDD7A");
        orderService.deny_paiment(itemtId,  order.getUser().getChatId());
        sendWhatever.sendhere_message(bot,  order.getUser().getChatId(), "deny",  null, null);
        try {
            //  execute(sendMessage);
           // bot.execute(photo);
            bot.execute(editMarkup);

        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

    }
}
