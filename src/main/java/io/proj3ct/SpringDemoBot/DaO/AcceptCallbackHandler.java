package io.proj3ct.SpringDemoBot.DaO;

import io.proj3ct.SpringDemoBot.TenantService;
import io.proj3ct.SpringDemoBot.config.BotConfig;
import io.proj3ct.SpringDemoBot.dopclasses.Senders.SendWhatever;
import io.proj3ct.SpringDemoBot.model.OrderService;
import io.proj3ct.SpringDemoBot.model.Orders;
import io.proj3ct.SpringDemoBot.model.OrdersRepository;
import io.proj3ct.SpringDemoBot.repository.BotRepository;
import io.proj3ct.SpringDemoBot.service.Send;
import io.proj3ct.SpringDemoBot.service.TelegramBot.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.dao.PersistenceExceptionTranslationAutoConfiguration;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Component
public class AcceptCallbackHandler implements CallbackHandler {


    @Autowired
    private OrdersRepository orderRepository;

    @Autowired
    private TenantService tenantService;

    @Autowired
    private OrderService orderService;
    @Autowired
    private SendWhatever sendWhatever;
    @Autowired
    private BotRepository botRepository;

    @Override
    public boolean support(String callbackData) {
        return callbackData.startsWith("ACCEPT");
    }

    @Override
    public void handle(CallbackQuery query, Long bot_id) {


        Long chatId = query.getMessage().getChatId();
        String callbackData = query.getData();
        int messageId = query.getMessage().getMessageId();

        Long itemtId = Long.parseLong(callbackData.split("_")[1]);
        //sent.remove(orderRepository.findByIdAndBot_Id(itemtId, Long.valueOf(config.getBoit())).get().getUser().getChatId());
        //orderService.paid(itemtId, chatId);

        System.out.println(itemtId);


        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(orderRepository.findByIdAndBot_Id(itemtId,bot_id).get().getUser().getChatId()));
        sendMessage.setText("Ваш заказ принят \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\n" +
                "\nОжидайте дальнейшей информации на счет " +
                "доставки, либо жижи в тумбочке не будет \uD83D\uDE01");

        InlineKeyboardButton newButton = new InlineKeyboardButton();
        newButton.setText("✅Принято");
        newButton.setCallbackData("already_pressed"); // або не ставити взагалі

        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(newButton);

        InlineKeyboardMarkup newMarkup = new InlineKeyboardMarkup();
        newMarkup.setKeyboard(List.of(row));

        EditMessageReplyMarkup editMarkup = new EditMessageReplyMarkup();
        editMarkup.setChatId(String.valueOf(chatId));
        editMarkup.setMessageId(messageId);
        editMarkup.setReplyMarkup(newMarkup);

        SendAnimation sa = new SendAnimation();
        sa.setAnimation(new InputFile("https://i.gifer.com/7GNa.gif"));
        sa.setChatId(String.valueOf(orderRepository.findByIdAndBot_Id(itemtId,bot_id).get().getUser().getChatId()));

            Orders order = orderRepository.findByIdAndBot_Id(itemtId,bot_id).get();
            order.setStatus("payed");;
            orderRepository.save(order);

        sa.setCaption("Ваш заказ принят \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\n" +
                "\nОжидайте дальнейшей информации на счет " +
                "доставки, либо жижи в тумбочке не будет \uD83D\uDE01");


        AbsSender sender = tenantService.getSender(botRepository.findById(bot_id).orElse(null).getBotToken());

        try {
            // sendText(orderRepository.findById(itemtId).get().getUser().getChatId(), "\uD83D\uDC4C");
            // execute(sendMessage);
           // bot.execute(
             //       sa
           // );


            sendWhatever.sendhere_message(sender, order.getUser().getChatId(), "accept",  null, null);
            sender.execute(editMarkup);

        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }


    }
    private String escapeMarkdown(String text) {
        return text.replace("\\", "\\\\")  // Екрануємо зворотні слеші
                .replace("_", "\\_")
                .replace("*", "\\*")
                .replace("[", "\\[")
                .replace("]", "\\]")
                .replace("(", "\\(")
                .replace(")", "\\)")
                .replace("~", "\\~")
                .replace("`", "\\`")
                .replace(">", "\\>")
                .replace("#", "\\#")
                .replace("+", "\\+")
                .replace("-", "\\-")
                .replace("=", "\\=")
                .replace("|", "\\|")
                .replace("{", "\\{")
                .replace("}", "\\}")
                .replace(".", "\\.")
                .replace("!", "\\!");
    }
}
