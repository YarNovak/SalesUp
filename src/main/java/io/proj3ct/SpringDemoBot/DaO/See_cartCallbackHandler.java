package io.proj3ct.SpringDemoBot.DaO;

import io.proj3ct.SpringDemoBot.Cache_my_own.CachesForDB.ButtonText;
import io.proj3ct.SpringDemoBot.TenantService;
import io.proj3ct.SpringDemoBot.config.BotConfig;
import io.proj3ct.SpringDemoBot.dopclasses.MessageRepo.MessageRegistry;
import io.proj3ct.SpringDemoBot.dopclasses.Senders.SendWhatever;
import io.proj3ct.SpringDemoBot.model.*;
import io.proj3ct.SpringDemoBot.repository.BotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class See_cartCallbackHandler implements CallbackHandler {


    @Autowired
    private VapecomponyRepository vapecomponyRepository;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrdersRepository orderRepository;

    @Autowired
    private CartItemRepository cartItemRepository;


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
       return callbackData.equals("SEE_CART");
    }

    @Override
    public void handle(CallbackQuery query, Long bot_id) {

        messageRegistry.deleteMessagesAfter(query.getMessage().getChatId(), query.getMessage().getMessageId(), false, bot_id);


             if(sendcart_nope(query.getMessage().getChatId(), bot_id)) return;
             getCartView(query.getMessage().getChatId(), bot_id);
    }

    private boolean sendcart_nope(Long chatId, Long bot_id){
/*
        if(orderRepository.findByUser_ChatIdAndPaidEqualsAndBot_Id(chatId, false, Long.valueOf(config.getBoit())).isPresent()) {

            SendMessage sendMessage = new SendMessage(chatId.toString(), "Ваш заказ в обработке, дождитесь подтверждения\uD83D\uDE0A");
            sendMessage.setParseMode("MarkdownV2");

            sendWhatever.sendhere_message(bot, chatId, "please_whait", null, null);

            return true;
        }
        return  false;

 */
        Optional<Orders> or = orderRepository.findByUser_ChatIdAndPaidEqualsAndBot_Id(chatId, false, bot_id);
        if(or.isPresent()) {

            //SendMessage sendMessage = new SendMessage(chatId.toString(), "Ваш заказ в обработке, дождитесь подтверждения\uD83D\uDE0A");
            //  sendWhatever.sendhere_message(bot, chatId, "please_whait", null, null);
            // return true;
            orderService.deny_order(or.get().getId(), or.get().getUser().getChatId());
        }
        //  return true;
        return  false;


    }
    private void getCartView(long chatId, Long bot_id) {

        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setParseMode("MarkdownV2");


        List<CartItem> items = cartItemRepository.findByChatIdAndBot_IdOrderById(chatId, bot_id);
        if(items.isEmpty()){
            message.setText("\uD83D\uDED2 Вашаfffffffff корзина пуста, \n" +
                    "мы воздух без вкуса не продаем \uD83D\uDE0B");



            List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
            List<InlineKeyboardButton> rowInLine = new ArrayList<>();
            var allk = new InlineKeyboardButton();

            allk.setText(buttonText.getTexts().get("catalog")); //!!!!!!!!!!111
            allk.setCallbackData("ALL_KATALOG_BUTTON");

            rowInLine.add(allk);
            rowsInLine.add(rowInLine);

            markupInLine.setKeyboard(rowsInLine);
            message.setReplyMarkup(markupInLine);


            AbsSender sender = tenantService.getSender(botRepository.findById(bot_id).orElse(null).getBotToken());
            sendWhatever.sendhere_message(sender, chatId, "clearing", markupInLine, null);




        }

        else{

            StringBuilder sb = new StringBuilder(escapeMarkdown( buttonText.getTexts().get("cart") + ":\n\n"));
            double total = 0.0;

            for(CartItem item : items){

                Vapecompony_katalog product = item.getVapecomponyKatalog();
                int quantity = item.getQuantity();
                double price = quantity * product.getCena();
                StringBuilder productName = new StringBuilder(product.getName().replace("/", "").trim());

                sb.append("> ")
                        .append(escapeMarkdown(productName.toString()))
                        .append(escapeMarkdown("  ×  "))
                        .append(escapeMarkdown(String.valueOf(quantity)))
                        .append(escapeMarkdown(" → "))
                        .append("__").append(escapeMarkdown(String.valueOf(price))).append(escapeMarkdown(buttonText.getTexts().get("curr"))).append("__")
                        .append(escapeMarkdown("💰\n"));

                total += price;

            }
            sb.append(escapeMarkdown("\n")).append(escapeMarkdown(buttonText.getTexts().get("payment")) + " ").append("*__").append(escapeMarkdown(String.valueOf(total))).append(buttonText.getTexts().get("curr")).append("__*");

            // Кнопки
            InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rows = new ArrayList<>();

            // Ряд 1: Змінити та
            List<InlineKeyboardButton> row1 = new ArrayList<>();
            InlineKeyboardButton editButton = new InlineKeyboardButton(buttonText.getTexts().get("change"));
            editButton.setCallbackData("CART_CHANGE");
            InlineKeyboardButton clearButton = new InlineKeyboardButton(buttonText.getTexts().get("clear"));
            clearButton.setCallbackData("CART_CLEAR");
            row1.add(editButton);
            row1.add(clearButton);

            // Ряд 2: Оплата
            List<InlineKeyboardButton> row2 = new ArrayList<>();
            InlineKeyboardButton payButton = new InlineKeyboardButton(buttonText.getTexts().get("payment"));
            payButton.setCallbackData("PAY");
            row2.add(payButton);

            rows.add(row1);
            rows.add(row2);
            markup.setKeyboard(rows);


            message.setText(sb.toString());
            message.setReplyMarkup(markup);


            AbsSender sender = tenantService.getSender(botRepository.findById(bot_id).orElse(null).getBotToken());
            executeMessage(message, sender);


        }


    }
    private void executeMessage(SendMessage message, AbsSender sender){
        try {
            Message m =  sender.execute(message);
            messageRegistry.addMessage(m.getChatId(), m.getMessageId());
        } catch (TelegramApiException e) {

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
