package io.proj3ct.SpringDemoBot.DaO;

import io.proj3ct.SpringDemoBot.Cache_my_own.CachesForDB.ButtonText;
import io.proj3ct.SpringDemoBot.DB_entities.Bot;
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
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PaymnetCallbackHandler implements CallbackHandler {


    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrdersRepository orderRepository;

    @Autowired
    private BotRepository botRepository;

    @Autowired
    private ButtonText buttonText;

    @Autowired
    private SendWhatever sendWhatever;

    @Autowired
    private MessageRegistry messageRegistry;
    @Autowired
    private TenantService tenantService;

    @Override
    public boolean support(String callbackData) {
        return callbackData.equals("PAY");
    }

    @Override
    public void handle(CallbackQuery query, Long bot_id) {

        AbsSender sender = tenantService.getSender(botRepository.findById(bot_id).orElse(null).getBotToken());

        messageRegistry.deleteMessagesAfter(query.getMessage().getChatId(), query.getMessage().getMessageId(), false, bot_id);
        messageRegistry.deleteMessagesBefore(query.getMessage().getChatId(), query.getMessage().getMessageId(), false, sender);


        System.out.println("fuck");
        Long chatId = query.getMessage().getChatId();
        int messageId = query.getMessage().getMessageId();

        if(cartItemRepository.findByChatIdAndBot_IdOrderById(chatId, bot_id).isEmpty()){
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(String.valueOf(chatId));


            sendMessage.setText("\uD83D\uDED2 Ваша корзина пуста, \n" +
                    "мы воздух без вкуса не продаем \uD83D\uDE0B");

            InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
            List<InlineKeyboardButton> rowInLine = new ArrayList<>();
            var yesButton = new InlineKeyboardButton();

            yesButton.setText(buttonText.getTexts().get("catalog"));
            yesButton.setCallbackData("ALL_KATALOG_BUTTON");

            rowInLine.add(yesButton);
            rowsInLine.add(rowInLine);

            markupInLine.setKeyboard(rowsInLine);
            sendMessage.setReplyMarkup(markupInLine);

        //    executeMessage(sendMessage);

            sendWhatever.sendhere_message(sender, chatId, "clearing",  markupInLine, null);



        }
        else{
            create_order(chatId, bot_id);
            send_pay(chatId, messageId, bot_id);
        }

    }

    private void create_order(Long chatId, Long bot_id) {

        if (!userRepository.existsByChatIdAndBot_Id(chatId, bot_id)) {
            User user = new User();
            user.setChatId(chatId);
            user.setBot(botRepository.findById(bot_id).get());
            userRepository.save(user);
        }


        //if(orderRepository.findByUser_ChatIdAndPaidEquals(userRepository.findByChatId(chatId).get().getChatId(), false).isPresent())

        if(orderRepository.findByUser_ChatIdAndPaidEqualsAndBot_Id(userRepository.findByChatIdAndBot_Id(chatId, bot_id).get().getChatId(), false, bot_id).isPresent()){

            Orders order = orderRepository.findByUser_ChatIdAndPaidEqualsAndBot_Id(chatId, false, bot_id).get();
            order.setUser(userRepository.findById(chatId).get());
            order.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            order.setStatus("inprocces");
            List<CartItem> cartItems = cartItemRepository.findByChatIdAndBot_Id(chatId, bot_id);
            System.out.println(order.getCreatedAt().toString());
            List<FinalItem> finalItems = cartItems.stream().map(cartItem -> {
                FinalItem finalItem = new FinalItem();
                finalItem.setName(cartItem.getVapecomponyKatalog().getName());
                finalItem.setCena(cartItem.getVapecomponyKatalog().getCena());
                finalItem.setQuantity(cartItem.getQuantity());
                finalItem.setBot(botRepository.findById(bot_id).get());
                finalItem.setOrder(order);
                finalItem.setVid(cartItem.getVapecomponyKatalog().getId());
                return finalItem;
            }).collect(Collectors.toList());


            order.setFinalItems(finalItems);
            order.setBot(botRepository.findById(bot_id).get());
            orderRepository.save(order);
        }
        //if(((orderRepository.findByUser_ChatIdAndPaidEquals(userRepository.findByChatId(chatId).get().getChatId(), true).isPresent()) && (orderRepository.findByUser_ChatIdAndPaidEquals(chatId, false).isEmpty()))  || (orderRepository.findByUser_ChatId(chatId).isEmpty()))
        else{
            Orders order = new Orders();
            order.setUser(userRepository.findByChatIdAndBot_Id(chatId, bot_id).get());
            order.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            order.setStatus("inprocces");
            List<CartItem> cartItems = cartItemRepository.findByChatIdAndBot_Id(chatId, bot_id);
            System.out.println(order.getCreatedAt().toString());
            List<FinalItem> finalItems = cartItems.stream().map(cartItem -> {
                FinalItem finalItem = new FinalItem();
                finalItem.setName(cartItem.getVapecomponyKatalog().getName());
                finalItem.setCena(cartItem.getVapecomponyKatalog().getCena());
                finalItem.setQuantity(cartItem.getQuantity());
                finalItem.setBot(botRepository.findById(bot_id).get());
                finalItem.setOrder(order);
                finalItem.setVid(cartItem.getVapecomponyKatalog().getId());
                return finalItem;
            }).collect(Collectors.toList());

            order.setBot(botRepository.findById(bot_id).get());
            order.setFinalItems(finalItems);
            orderRepository.save(order);
        }



        //else if(orderRepository.findByUser_ChatIdAndPaidEquals(userRepository.findByChatId(chatId).get().getChatId(), true).isPresent()) {}

    }
    public void send_pay(long chatId, int messageId, Long bot_id){

        EditMessageText message = new EditMessageText();
        message.setChatId(String.valueOf(chatId));
        message.setParseMode("MarkdownV2");
        message.setText(sendCarteditor_Text(chatId, bot_id));
        message.setMessageId(messageId);

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> row1 = new ArrayList<>();

        Bot botik = botRepository.findById(bot_id).get();

        InlineKeyboardButton editButton = new InlineKeyboardButton(buttonText.getTexts().get("cart_method"));
        editButton.setCallbackData("CARD");


        InlineKeyboardButton clearButton = new InlineKeyboardButton(buttonText.getTexts().get("cash_method"));
        clearButton.setCallbackData("CASH");


        if(botik.isCart()) row1.add(editButton);
        if(botik.isNalichka())row1.add(clearButton);
        rows.add(row1);
        if(!rows.isEmpty()){
            markup.setKeyboard(rows);
            message.setReplyMarkup(markup);
        }

        markup.setKeyboard(rows);
        message.setReplyMarkup(markup);

        if(message.getText().isEmpty()){

            return;
        }

        AbsSender sender = tenantService.getSender(botRepository.findById(bot_id).orElse(null).getBotToken());
        try {
            sender.execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }


    }

    private String sendCarteditor_Text(Long chatId, Long bot_id){



        StringBuilder sb = new StringBuilder();
        List<CartItem> items =cartItemRepository.findByChatIdAndBot_IdOrderById(chatId, bot_id);
        if(items.isEmpty()){
            sb.append(escapeMarkdown(""));
            return sb.toString();
        }
        sb.append(escapeMarkdown(buttonText.getTexts().get("cart")+":\n\n"));
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
    /*
        sb.append("\n\n\n" +
                "❗\uFE0FВНИМАНИЕ❗\uFE0F\n" +
                "\uD83D\uDE80оплата картой принимается ТОЛЬКО при заказе INPOST\n" +
                "\n\n");

     */
        return sb.toString();
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
