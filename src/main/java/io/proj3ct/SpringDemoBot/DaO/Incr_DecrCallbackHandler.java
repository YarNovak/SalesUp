package io.proj3ct.SpringDemoBot.DaO;

import io.proj3ct.SpringDemoBot.Cache_my_own.CachesForDB.ButtonText;
import io.proj3ct.SpringDemoBot.Cache_my_own.CachesForDB.MessagesInf;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class Incr_DecrCallbackHandler implements CallbackHandler {



    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private VapecomponyKatalogRepository vapecomponyKatalogRepository;

    @Autowired
    private OrdersRepository orderRepository;

    @Autowired
    private ButtonText buttonText;

    @Autowired
    private SendWhatever sendWhatever;
    @Autowired
    private MessagesInf messagesInf;
    @Autowired
    private MessageRegistry messageRegistry;
    @Autowired
    private TenantService tenantService;
    @Autowired
    private BotRepository botRepository;

    @Override
    public boolean support(String callbackData) {
        return (callbackData.startsWith("incr_") || callbackData.startsWith("decr_"));
    }

    @Override
    public void handle(CallbackQuery query,  Long bot_id) {


        AbsSender sender = tenantService.getSender(botRepository.findById(bot_id).orElse(null).getBotToken());
        messageRegistry.deleteMessagesAfter(query.getMessage().getChatId(), query.getMessage().getMessageId(), false, bot_id);


            Long chatId = query.getMessage().getChatId();
            String callbackData = query.getData();
            int messageId = query.getMessage().getMessageId();

        System.out.println("YAWAHUI");

        if(sendcart_nope(chatId, bot_id)) return;
        ///////////////////////////////
        Long itemtId = Long.parseLong(callbackData.split("_")[1]);
        if (callbackData.startsWith("incr_")) {
            CartItem cti = cartItemRepository.findByIdAndBot_Id(itemtId, bot_id).get();
            Optional<Vapecompony_katalog> product = vapecomponyKatalogRepository.findByIdAndBot_Id(cti.getVapecomponyKatalog().getId(), bot_id);
            cartService.addToCart(chatId, product.get());

            EditMessageText editMessage = new EditMessageText();
            editMessage.setChatId(String.valueOf(chatId));
            editMessage.setMessageId(messageId);
            editMessage.setParseMode("MarkdownV2");
            editMessage.setText(sendCarteditor_Text(chatId, bot_id));
            editMessage.setText(sendCarteditor_Text(chatId, bot_id));
            editMessage.setReplyMarkup(sendCarteditor_KB(chatId, bot_id));

            if(editMessage.getText().isEmpty()){

                return;
            }

            try {
                sender.execute(editMessage);

            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }

        } else if (callbackData.startsWith("decr_")) {
            CartItem cti = cartItemRepository.findByIdAndBot_Id(itemtId, bot_id).get();
            Optional<Vapecompony_katalog> product = vapecomponyKatalogRepository.findByIdAndBot_Id(cti.getVapecomponyKatalog().getId(), bot_id);
            cartService.deleteFromCart(chatId, product.get());

            EditMessageText editMessage = new EditMessageText();
            editMessage.setChatId(String.valueOf(chatId));
            editMessage.setMessageId(messageId);
            editMessage.setText(sendCarteditor_Text(chatId, bot_id));
            editMessage.setParseMode("MarkdownV2");
            editMessage.setReplyMarkup(sendCarteditor_KB(chatId, bot_id));

            if(editMessage.getText().isEmpty()){

                sendWhatever.edithere_emptycart(sender, chatId, messageId, "clearing", sendCarteditor_KB(chatId, bot_id), null, bot_id);

                return;
            }

            try {
                sender.execute(editMessage);

            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }


        }


    }
    private boolean sendcart_nope(Long chatId, Long bot_id){

        if(orderRepository.findByUser_ChatIdAndPaidEqualsAndBot_Id(chatId, false, bot_id).isPresent()) {
            AbsSender sender = tenantService.getSender(botRepository.findById(bot_id).orElse(null).getBotToken());
            SendMessage sendMessage = new SendMessage(chatId.toString(), "Ваш заказ в обработке, дождитесь подтверждения\uD83D\uDE0A");
            sendMessage.setParseMode("MarkdownV2");
            sendWhatever.sendhere_message(bot_id,sender, chatId, "please_whait", null, null);
            return true;
        }
        return  false;


    }

    private String sendCarteditor_Text(Long chatId, Long bot_id){



        StringBuilder sb = new StringBuilder();
        List<CartItem> items =cartItemRepository.findByChatIdAndBot_IdOrderById(chatId, bot_id);
        if(items.isEmpty()){
            sb.append(escapeMarkdown(""));
            return sb.toString();
        }
        sb.append(escapeMarkdown(buttonText.getTexts(bot_id).get("cart")+":\n\n"));
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




        }

        sb.append(escapeMarkdown("\n")).append(escapeMarkdown(buttonText.getTexts(bot_id).get("payment")) + " ").append("*__").append(escapeMarkdown(String.valueOf(total))).append(buttonText.getTexts(bot_id).get("curr")).append("__*");
    /*
        sb.append("\n\n\n" +
                "❗\uFE0FВНИМАНИЕ❗\uFE0F\n" +
                "\uD83D\uDE80оплата картой принимается ТОЛЬКО при заказе INPOST\n" +
                "\n\n");

     */
        return sb.toString();
    }

    private InlineKeyboardMarkup sendCarteditor_KB(Long chatId, Long bot_id){



        List<List<InlineKeyboardButton>> rows = new ArrayList<>();


        List<CartItem> items = cartItemRepository.findByChatIdAndBot_IdOrderById(chatId, bot_id);

        if(items.isEmpty()){




            InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();

            List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
            List<InlineKeyboardButton> rowInLine = new ArrayList<>();
            var allk = new InlineKeyboardButton();

            allk.setText(buttonText.getTexts(bot_id).get("catalog"));
            allk.setCallbackData("ALL_KATALOG_BUTTON");

            rowInLine.add(allk);
            rowsInLine.add(rowInLine);

            markupInLine.setKeyboard(rowsInLine);
            return(markupInLine);

        }

        for(CartItem item : items){

            Vapecompony_katalog product = item.getVapecomponyKatalog();
            int quantity = item.getQuantity();
            double price = quantity * product.getCena();

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

        List<InlineKeyboardButton> row = new ArrayList<>();


        InlineKeyboardButton Button = new InlineKeyboardButton(buttonText.getTexts(bot_id).get("cart"));
        Button.setCallbackData("SEE_CART");

        row.add(Button);
        rows.add(row);


        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(rows);
        return markup;
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
