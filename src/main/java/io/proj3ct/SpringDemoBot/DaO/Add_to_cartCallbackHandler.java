package io.proj3ct.SpringDemoBot.DaO;

import io.proj3ct.SpringDemoBot.Cache_my_own.CachesForDB.ButtonText;
import io.proj3ct.SpringDemoBot.TenantService;

import io.proj3ct.SpringDemoBot.dopclasses.Formater.Formatter;
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
import java.util.Optional;

import static io.proj3ct.SpringDemoBot.dopclasses.Formater.Formatter.formatEntity;

@Component
public class Add_to_cartCallbackHandler implements CallbackHandler {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private VapecomponyKatalogRepository vapecomponyKatalogRepository;

    @Autowired
    private OrdersRepository orderRepository;



    @Autowired
    private SendWhatever sendWhatever;

    @Autowired
    private ButtonText buttonText;
    @Autowired
    private MessageRegistry messageRegistry;

    @Autowired
    private FinalItemService finalItemService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private TenantService tenantService;
    @Autowired
    private BotRepository botRepository;

    @Override
    public boolean support(String callbackData) {
       return callbackData.startsWith("add_to_cart:");
    }

    @Autowired
    private Formatter formatter;

    @Override
    public void handle(CallbackQuery query, Long bot_id) {

        AbsSender sender = tenantService.getSender(botRepository.findById(bot_id).orElse(null).getBotToken());

        messageRegistry.deleteMessagesAfter(query.getMessage().getChatId(), query.getMessage().getMessageId(), false, bot_id);

            Long chatId = query.getMessage().getChatId();
            String callbackData = query.getData();
            int messageId = query.getMessage().getMessageId();


        if(sendcart_nope(chatId, bot_id)) return;

        System.out.println("Саси хуй");

        System.out.println(3);
        Long productId = Long.parseLong(callbackData.split(":")[1]);
        Optional<Vapecompony_katalog> product = vapecomponyKatalogRepository.findByIdAndBot_Id(productId, bot_id);

        cartService.addToCart(chatId, product.get());
        Optional<User> userik = userRepository.findByChatIdAndBot_Id(chatId, bot_id);
        User us = userik.get();
        us.setLastUpdated(new Timestamp(System.currentTimeMillis()));

        userRepository.save(us);


        Optional<CartItem> crti = cartItemRepository.findByChatIdAndVapecomponyKatalog_NameAndBot_Id(chatId, product.get().getName(), bot_id);

        EditMessageText editMessage = new EditMessageText();
        editMessage.setChatId(String.valueOf(chatId));
        editMessage.setMessageId(messageId);
        editMessage.setParseMode("MarkdownV2");

        Formatter.FormattedMessage fm = formatEntity(crti.get().getVapecomponyKatalog().getName(), crti.get().getVapecomponyKatalog().getEntitiesJson(), crti.get().getQuantity(), crti.get().getVapecomponyKatalog().getCena());




        editMessage.setText(sendCarteditor_Text2(chatId, product.get().getName(), bot_id));
        // editMessage.setText(fm.getText());
        //editMessage.setEntities(fm.getEntities());
        editMessage.setReplyMarkup(sendCarteditor_KB2(chatId, product.get().getName(), bot_id));


        sendWhatever.edithere_readyVapecomponyKatalogMessage(sender, product.get(), chatId, messageId, sendCarteditor_Text2(chatId, product.get().getName(), bot_id), "MarkdownV2", bot_id, sendCarteditor_KB2(chatId, product.get().getName(), bot_id));

/*
        try {
            bot.execute(editMessage);

        } catch (TelegramApiException e) {
            System.out.println("LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL");
            throw new RuntimeException(e);

        }
        */

    }
    private boolean sendcart_nope(Long chatId, Long bot_id){
/*
        if(orderRepository.findByUser_ChatIdAndPaidEqualsAndBot_Id(chatId, false, Long.valueOf(config.getBoit())).isPresent()) {

            SendMessage sendMessage = new SendMessage(chatId.toString(), "Ваш заказ в обработке, дождитесь подтверждения\uD83D\uDE0A");
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
    private String sendCarteditor_Text2(Long chatId,  String name, Long bot_id){



        StringBuilder sb = new StringBuilder();
        List<CartItem> items = new ArrayList<>();
        CartItem prod =  cartItemRepository.findByChatIdAndVapecomponyKatalog_NameAndBot_Id(chatId, name, bot_id).get();
        items.add(prod);
        if(!cartItemRepository.findByChatIdAndVapecomponyKatalog_NameAndBot_Id(chatId, name, bot_id).isPresent()){

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

    private InlineKeyboardMarkup sendCarteditor_KB2(Long chatId, String name, Long bot_id){



        List<List<InlineKeyboardButton>> rows = new ArrayList<>();


        List<CartItem> items = new ArrayList<>();
        CartItem prod =  cartItemRepository.findByChatIdAndVapecomponyKatalog_NameAndBot_Id(chatId, name, bot_id).get();
        items.add(prod);

        if(!cartItemRepository.findByChatIdAndVapecomponyKatalog_NameAndBot_Id(chatId, name, bot_id).isPresent()){
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

}
