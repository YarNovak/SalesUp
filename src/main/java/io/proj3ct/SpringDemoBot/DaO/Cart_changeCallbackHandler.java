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
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class Cart_changeCallbackHandler implements CallbackHandler {


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
    private OrderService orderService;
    @Autowired
    private TenantService tenantService;
    @Autowired
    private BotRepository botRepository;

    @Override
    public boolean support(String callbackData) {
        return callbackData.equals("CART_CHANGE");
    }

    @Override
    public void handle(CallbackQuery query, Long bot_id) {

        messageRegistry.deleteMessagesAfter(query.getMessage().getChatId(), query.getMessage().getMessageId(), false, bot_id);

        if(sendcart_nope(query.getMessage().getChatId(), bot_id)) return;
        sendCarteditor(query.getMessage().getChatId(), bot_id);

    }

    private boolean sendcart_nope(Long chatId, Long bot_id){
/*
        if(orderRepository.findByUser_ChatIdAndPaidEqualsAndBot_Id(chatId, false, Long.valueOf(config.getBoit())).isPresent()) {

            SendMessage sendMessage = new SendMessage(chatId.toString(), "Ваш заказ в обработке, дождитесь подтверждения\uD83D\uDE0A");
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
            orderService.deny_order(or.get().getId(), or.get().getUser().getChatId(), bot_id);
        }
        //  return true;
        return  false;


    }

    private void   sendCarteditor(Long chatId, Long bot_id){


        SendMessage message = new SendMessage();
        message.setParseMode("MarkdownV2");
        message.setChatId(String.valueOf(chatId));
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();


        List<CartItem> items = cartItemRepository.findByChatIdAndBot_IdOrderById(chatId, bot_id);
        if(items.isEmpty()){

            message.setText("\uD83D\uDED2 Ваша корзина пуста, \n" +
                    "мы воздух без вкуса не продаем \uD83D\uDE0B");

            InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();

            List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
            List<InlineKeyboardButton> rowInLine = new ArrayList<>();
            var allk = new InlineKeyboardButton();

            allk.setText(buttonText.getTexts(bot_id).get("catalog")); //"\uD83E\uDDEAКаталог"
            allk.setCallbackData("ALL_KATALOG_BUTTON");

            rowInLine.add(allk);
            rowsInLine.add(rowInLine);

            markupInLine.setKeyboard(rowsInLine);
            message.setReplyMarkup(markupInLine);

            AbsSender sender = tenantService.getSender(botRepository.findById(bot_id).orElse(null).getBotToken());
            sendWhatever.sendhere_message(bot_id, sender, chatId, "clearing",  markupInLine, null);

        }
        else{
            StringBuilder sb = new StringBuilder( escapeMarkdown(buttonText.getTexts(bot_id).get("cart") +":\n\n") );
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
                        .append("__").append(escapeMarkdown(String.valueOf(price))).append(escapeMarkdown(buttonText.getTexts(bot_id).get("curr"))).append("__")
                        .append(escapeMarkdown("💰\n"));

                total += price;

                List<InlineKeyboardButton> row = new ArrayList<>();

                InlineKeyboardButton Button = new InlineKeyboardButton(buttonText.getTexts(bot_id).get("delete"));
                Button.setCallbackData("decr_" + item.getId());

                row.add(Button);

                Button = new InlineKeyboardButton(item.getVapecomponyKatalog().getName().replace("/", "") + " × " + item.getQuantity());
                Button.setCallbackData("noop");

                row.add(Button);

                Button = new InlineKeyboardButton(buttonText.getTexts(bot_id).get("add"));
                Button.setCallbackData("incr_" + item.getId());


                row.add(Button);
                rows.add(row);

            }

            sb.append(escapeMarkdown("\n")).append(escapeMarkdown(buttonText.getTexts(bot_id).get("payment")) + " ").append("*__").append(escapeMarkdown(String.valueOf(total))).append(buttonText.getTexts(bot_id).get("curr")).append("__*");
            if(total<100) {


            }
            InlineKeyboardMarkup markup = new InlineKeyboardMarkup();



            List<InlineKeyboardButton> row = new ArrayList<>();


            InlineKeyboardButton Button = new InlineKeyboardButton(buttonText.getTexts(bot_id).get("cart")); //"\uD83D\uDED2 Корзина"
            Button.setCallbackData("SEE_CART");

            row.add(Button);
            rows.add(row);

            markup.setKeyboard(rows);
            message.setChatId(String.valueOf(chatId));
            message.setReplyMarkup(markup);
            message.setText(sb.toString());

            executeMessage(message, bot_id);
        }



    }
    private void executeMessage(SendMessage message, Long bot_id){
        AbsSender sender = tenantService.getSender(botRepository.findById(bot_id).orElse(null).getBotToken());
        try {
            sender.execute(message);
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
