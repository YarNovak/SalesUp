package io.proj3ct.SpringDemoBot.DaO;

import io.proj3ct.SpringDemoBot.Cache_my_own.CachesForDB.ButtonText;
import io.proj3ct.SpringDemoBot.TenantService;
import io.proj3ct.SpringDemoBot.config.BotConfig;
import io.proj3ct.SpringDemoBot.dopclasses.MessageRepo.MessageRegistry;
import io.proj3ct.SpringDemoBot.dopclasses.Senders.SendWhatever;
import io.proj3ct.SpringDemoBot.model.CartService;
import io.proj3ct.SpringDemoBot.model.OrderService;
import io.proj3ct.SpringDemoBot.model.Orders;
import io.proj3ct.SpringDemoBot.model.OrdersRepository;
import io.proj3ct.SpringDemoBot.repository.BotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class Cart_clearCallbackHandler implements CallbackHandler {

    @Autowired
    private CartService cartService;

    @Autowired
    private OrdersRepository orderRepository;

    @Autowired
    private OrderService orderService;

    @Autowired
    private SendWhatever sendWhatever;
    @Autowired
    private ButtonText buttonText;
    @Autowired
    private MessageRegistry messageRegistry;
    @Autowired
    private TenantService tenantService;
    @Autowired
    private BotRepository botRepository;

    @Override
    public boolean support(String callbackData) {
        return "CART_CLEAR".equals(callbackData);
    }

    @Override
    public void handle(CallbackQuery query, Long bot_id) {

        messageRegistry.deleteMessagesAfter(query.getMessage().getChatId(), query.getMessage().getMessageId(), false, bot_id);

        Long chatId = query.getMessage().getChatId();
        int messageId = query.getMessage().getMessageId();

        if(sendcart_nope(chatId, bot_id)) return;
/////////////////////////////////////////////////////////////////////////
        cartService.clearCart(chatId);
        EditMessageText editMessage = new EditMessageText();
        editMessage.setChatId(String.valueOf(chatId));
        editMessage.setParseMode("HTML");
        editMessage.setText("\uD83D\uDED2 Ваша корзина пуста, \n" +
                "мы воздух без вкуса не продаем \uD83D\uDE0B");

        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();
        var allk = new InlineKeyboardButton();

        allk.setText(buttonText.getTexts(bot_id).get("catalog"));
        allk.setCallbackData("ALL_KATALOG_BUTTON");


        rowInLine.add(allk);
        rowsInLine.add(rowInLine);

        markupInLine.setKeyboard(rowsInLine);
        editMessage.setReplyMarkup(markupInLine);


        editMessage.setMessageId(Math.toIntExact(messageId));

        AbsSender sender = tenantService.getSender(botRepository.findById(bot_id).orElse(null).getBotToken());

        sendWhatever.edithere_emptycart(sender, chatId, messageId, "clearing", markupInLine, null, bot_id);


/*
        try {
            bot.execute(editMessage);

        } catch (TelegramApiException e) {
            System.out.println("LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL");
            throw new RuntimeException(e);
        }
 */

    }

    private boolean sendcart_nope(Long chatId, Long bot_id){
        /*
        if(orderRepository.findByUser_ChatIdAndPaidEqualsAndBot_Id(chatId, false, Long.valueOf(config.getBoit())).isPresent()) {

            SendMessage sendMessage = new SendMessage(chatId.toString(), "Ваш заказ в обработке, дождитесь подтверждения\uD83D\uDE0A");
            sendMessage.setParseMode("HTML");
            sendWhatever.sendhere_message(bot_id,bot, chatId, "please_whait", null, null);
            return true;
        }
        return  false;

*/
        Optional<Orders> or = orderRepository.findByUser_ChatIdAndPaidEqualsAndBot_Id(chatId, false, bot_id);
        if(or.isPresent()) {

            //SendMessage sendMessage = new SendMessage(chatId.toString(), "Ваш заказ в обработке, дождитесь подтверждения\uD83D\uDE0A");
            //  sendWhatever.sendhere_message(bot_id,bot, chatId, "please_whait", null, null);
            // return true;
            orderService.deny_order(or.get().getId(), or.get().getUser().getChatId());
        }
        //  return true;
        return  false;
    }

}
