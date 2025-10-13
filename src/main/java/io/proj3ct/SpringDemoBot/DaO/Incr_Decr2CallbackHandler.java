package io.proj3ct.SpringDemoBot.DaO;

import io.proj3ct.SpringDemoBot.Cache_my_own.CachesForDB.ButtonText;
import io.proj3ct.SpringDemoBot.config.BotConfig;
import io.proj3ct.SpringDemoBot.dopclasses.MessageRepo.MessageRegistry;
import io.proj3ct.SpringDemoBot.dopclasses.Senders.SendWhatever;
import io.proj3ct.SpringDemoBot.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class Incr_Decr2CallbackHandler implements CallbackHandler {

    TelegramLongPollingBot bot;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private VapecomponyKatalogRepository vapecomponyKatalogRepository;

    @Autowired
    private OrdersRepository orderRepository;

    @Autowired
    private BotConfig config;

    @Autowired
    private OrderService orderService;

    @Autowired
    private SendWhatever sendWhatever;

    @Autowired
    private ButtonText buttonText;
    @Autowired
    private MessageRegistry messageRegistry;

    @Override
    public boolean support(String callbackData) {
        return (callbackData.startsWith("incr2_") || callbackData.startsWith("decr2_"));
    }

    @Override
    public void handle(CallbackQuery query, TelegramLongPollingBot bot) {
        messageRegistry.deleteMessagesAfter(query.getMessage().getChatId(), query.getMessage().getMessageId(), false, bot);

        this.bot = bot;
        System.out.println("Саси велику пипипу");

        Long chatId = query.getMessage().getChatId();
        String callbackData = query.getData();
        int messageId = query.getMessage().getMessageId();

        if(sendcart_nope(chatId)) return;
        ///////////////////////////////
        Long itemtId = Long.parseLong(callbackData.split("_")[1]);
        if (callbackData.startsWith("incr2_")) {
            CartItem cti = cartItemRepository.findByIdAndBot_Id(itemtId, Long.valueOf(config.getBoit())).get();


            Optional<Vapecompony_katalog> product = vapecomponyKatalogRepository.findByIdAndBot_Id(cti.getVapecomponyKatalog().getId(), Long.valueOf(config.getBoit()));
            cartService.addToCart(chatId, product.get());

            EditMessageText editMessage = new EditMessageText();
            editMessage.setChatId(String.valueOf(chatId));
            editMessage.setMessageId(messageId);
            editMessage.setParseMode("MarkdownV2");

            if (cartItemRepository.findByChatIdAndVapecomponyKatalog_NameAndBot_Id(chatId, product.get().getName(), Long.valueOf(config.getBoit())).isPresent()) {

                editMessage.setText(sendCarteditor_Text2(chatId, product.get().getName()));
                editMessage.setReplyMarkup(sendCarteditor_KB2(chatId, product.get().getName()));

                sendWhatever.edithere_readyVapecomponyKatalogMessage(bot, product.get(), chatId, messageId, sendCarteditor_Text2(chatId, product.get().getName()), "MarkdownV2", Long.valueOf(config.getBoit()), sendCarteditor_KB2(chatId, product.get().getName()));

            } else {

                System.out.println("0000000000000000000000000000000000000000000000000000000");
                editMessage.setText(cti.getVapecomponyKatalog().getName().replace("/", "").trim());

                InlineKeyboardButton addToCartBtn = new InlineKeyboardButton(buttonText.getTexts().get("firs_add"));
                addToCartBtn.setCallbackData("add_to_cart:" + cti.getVapecomponyKatalog().getId()); //add_to_cart:

                InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
                markup.setKeyboard(List.of(List.of(addToCartBtn))); ////
                editMessage.setReplyMarkup(markup);

                sendWhatever.edithere_firstAdd(bot, chatId, messageId, cti.getVapecomponyKatalog().getId(), Long.valueOf(config.getBoit()), markup);
            }



        } else if (callbackData.startsWith("decr2_")) {
            System.out.println(4);
            CartItem cti = cartItemRepository.findByIdAndBot_Id(itemtId, Long.valueOf(config.getBoit())).get();
            Optional<Vapecompony_katalog> product = vapecomponyKatalogRepository.findByIdAndBot_Id(cti.getVapecomponyKatalog().getId(), Long.valueOf(config.getBoit()));
            cartService.deleteFromCart(chatId, product.get());

            EditMessageText editMessage = new EditMessageText();
            editMessage.setChatId(String.valueOf(chatId));
            editMessage.setMessageId(messageId);
            editMessage.setParseMode("MarkdownV2");

            if (cartItemRepository.findByChatIdAndVapecomponyKatalog_NameAndBot_Id(chatId, product.get().getName(), Long.valueOf(config.getBoit())).isPresent()) {

                editMessage.setText(sendCarteditor_Text2(chatId, product.get().getName()));
                editMessage.setReplyMarkup(sendCarteditor_KB2(chatId, product.get().getName()));

                sendWhatever.edithere_readyVapecomponyKatalogMessage(bot, product.get(), chatId, messageId, sendCarteditor_Text2(chatId, product.get().getName()), "MarkdownV2", Long.valueOf(config.getBoit()), sendCarteditor_KB2(chatId, product.get().getName()));

            } else {
                System.out.println("0000000000000000000000000000000000000000000000000000000");

                editMessage.setParseMode(null);


                editMessage.setText(cti.getVapecomponyKatalog().getName().replace("/", "").trim());

                InlineKeyboardButton addToCartBtn = new InlineKeyboardButton(buttonText.getTexts().get("first_add"));
                addToCartBtn.setCallbackData("add_to_cart:" + cti.getVapecomponyKatalog().getId()); //add_to_cart:

                InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
                markup.setKeyboard(List.of(List.of(addToCartBtn))); ////
                editMessage.setReplyMarkup(markup);

                System.out.println("TUT");
                sendWhatever.edithere_firstAdd(bot, chatId, messageId, cti.getVapecomponyKatalog().getId(), Long.valueOf(config.getBoit()), markup);



            }


        }

    }
    private boolean sendcart_nope(Long chatId){
/*
        if(orderRepository.findByUser_ChatIdAndPaidEqualsAndBot_Id(chatId, false, Long.valueOf(config.getBoit())).isPresent()) {

            SendMessage sendMessage = new SendMessage(chatId.toString(), "Ваш заказ в обработке, дождитесь подтверждения\uD83D\uDE0A");
            sendWhatever.sendhere_message(bot, chatId, "please_whait", null, null);
            return true;
        }
        return  false;

 */

        Optional<Orders> or = orderRepository.findByUser_ChatIdAndPaidEqualsAndBot_Id(chatId, false, Long.valueOf(config.getBoit()));
        if(or.isPresent()) {

            //SendMessage sendMessage = new SendMessage(chatId.toString(), "Ваш заказ в обработке, дождитесь подтверждения\uD83D\uDE0A");
            //  sendWhatever.sendhere_message(bot, chatId, "please_whait", null, null);
            // return true;
            orderService.deny_order(or.get().getId(), or.get().getUser().getChatId());
        }
        //  return true;
        return  false;


    }
    private String sendCarteditor_Text2(Long chatId,  String name){



        StringBuilder sb = new StringBuilder();
        List<CartItem> items = new ArrayList<>();
        CartItem prod =  cartItemRepository.findByChatIdAndVapecomponyKatalog_NameAndBot_Id(chatId, name, Long.valueOf(config.getBoit())).get();
        items.add(prod);
        if(!cartItemRepository.findByChatIdAndVapecomponyKatalog_NameAndBot_Id(chatId, name, Long.valueOf(config.getBoit())).isPresent()){

            sb.append(escapeMarkdown(name.replace("/", "").trim()));
            return sb.toString();
        }
        sb.append( escapeMarkdown(name.replace("/", "").trim()+" \n\n"));
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

        // if(total<100) {

        //   sb.append("\n\n\nЕсли платиш НАЛИЧКОЙ, закажи товара больше чем на 100zł и доставка БЕСПЛАТНАЯ))");
        //  }
        return sb.toString();
    }
    private InlineKeyboardMarkup sendCarteditor_KB2(Long chatId, String name){



        List<List<InlineKeyboardButton>> rows = new ArrayList<>();


        List<CartItem> items = new ArrayList<>();
        CartItem prod =  cartItemRepository.findByChatIdAndVapecomponyKatalog_NameAndBot_Id(chatId, name, Long.valueOf(config.getBoit())).get();
        items.add(prod);

        if(!cartItemRepository.findByChatIdAndVapecomponyKatalog_NameAndBot_Id(chatId, name, Long.valueOf(config.getBoit())).isPresent()){
            return null;
        }

        for(CartItem item : items){

            Vapecompony_katalog product = item.getVapecomponyKatalog();
            int quantity = item.getQuantity();
            double price = quantity * product.getCena();

            List<InlineKeyboardButton> row = new ArrayList<>();


            InlineKeyboardButton Button = new InlineKeyboardButton(buttonText.getTexts().get("delete"));
            Button.setCallbackData("decr2_" + item.getId());

            row.add(Button);

            Button = new InlineKeyboardButton(item.getVapecomponyKatalog().getName().replace("/", "") + " × " + item.getQuantity());
            Button.setCallbackData("noop");

            row.add(Button);

            Button = new InlineKeyboardButton(buttonText.getTexts().get("add"));
            Button.setCallbackData("incr2_" + item.getId());


            row.add(Button);
            rows.add(row);

        }

        List<InlineKeyboardButton> row = new ArrayList<>();


        InlineKeyboardButton Button = new InlineKeyboardButton(buttonText.getTexts().get("cart"));
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

    public InlineKeyboardMarkup generateProductButtons(List<Vapecompony_katalog> products) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        for (Vapecompony_katalog product : products) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            if (product.getKilkist() > 0) {
                button.setText("\uD83D\uDD25 Добавить в корзину");
                button.setCallbackData("add_to_cart:" + product.getId());
            } else {
                button.setText("❌ Нет в наличии");
                button.setCallbackData("out_of_stock");
            }

            rows.add(Collections.singletonList(button));
        }

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(rows);
        return markup;
    }

}
