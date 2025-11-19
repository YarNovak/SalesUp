package io.proj3ct.SpringDemoBot.DaO;

import io.proj3ct.SpringDemoBot.Cache_my_own.CachesForDB.ButtonText;
import io.proj3ct.SpringDemoBot.config.BotConfig;
import io.proj3ct.SpringDemoBot.dopclasses.Senders.SendWhatever;
import io.proj3ct.SpringDemoBot.model.*;
import io.proj3ct.SpringDemoBot.repository.BotRepository;
import io.proj3ct.SpringDemoBot.service.Adres;
import io.proj3ct.SpringDemoBot.service.Media;
import io.proj3ct.SpringDemoBot.service.Send;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendContact;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Component
public class PhotoHandler {

    TelegramLongPollingBot bot;
    @Autowired
    BotConfig config;
    @Autowired
    Media media;
    @Autowired
    private OrdersRepository orderRepository;
    @Autowired
    private Adres adres;

    @Autowired
    private BotRepository botRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ButtonText buttonText;

    @Autowired
    private SendWhatever sendWhatever;

    @Autowired
    private OrderService orderService;


    public void photo_Handle(Update update, Long bot_id) {


        Long chatId = update.getMessage().getChatId();

        Contact contact =  media.get(chatId, bot_id);
        media.remove(chatId, bot_id);
        SendContact sendContact = new SendContact();
        sendContact.setChatId(botRepository.findById(Long.valueOf(config.getBoit())).get().getOwner().getTelegramId().toString());
        sendContact.setFirstName(contact.getFirstName());
        sendContact.setPhoneNumber(contact.getPhoneNumber());


        List<PhotoSize> photos = update.getMessage().getPhoto();
        PhotoSize largestPhoto = photos.get(photos.size() - 1); // Найбільше за розміром
        String fileId = largestPhoto.getFileId();

        // photo.setCaption(orderRepository.findByUser_ChatIdAndPaidEquals(chatId, false).get().getUser().getUserName() + " замовив замовлення на сумуу " + sendCarteditor_Total(chatId) + "zł за допомогою " + orderRepository.findByUser_ChatIdAndPaidEquals(chatId, false).get().getCurrency() + " карти\n\n   " + sendCarteditor_Text(chatId));





        SendPhoto photo2 = new SendPhoto();
        photo2.setParseMode("MarkdownV2");

        photo2.setChatId(botRepository.findById(Long.valueOf(config.getBoit())).get().getOwner().getTelegramId().toString());

        photo2.setPhoto(new InputFile(fileId));
        photo2.setCaption(escapeMarkdown(orderRepository.findByUser_ChatIdAndPaidEqualsAndBot_Id(chatId, false, Long.valueOf(config.getBoit())).get().getUser().getUserName() + " сделал заказ на суму " + sendCarteditor_Total(chatId) + buttonText.getTexts(bot_id).get("curr") + " с помощью онлайн метода оплаты\n\n") + sendCarteditor_Text(chatId, bot_id)+ escapeMarkdown("\n\n"+"Адрес: "+adres.get(chatId, bot_id)));

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        InlineKeyboardButton editButton = new InlineKeyboardButton("Подтвердить!");
        editButton.setCallbackData("ACCEPT" + "_" + orderRepository.findByUser_ChatIdAndPaidEqualsAndBot_Id(chatId, false, Long.valueOf(config.getBoit())).get().getId());
        InlineKeyboardButton denybutton = new InlineKeyboardButton("Отклонить!");
        denybutton.setCallbackData("DENY" + "_" + orderRepository.findByUser_ChatIdAndPaidEqualsAndBot_Id(chatId, false, Long.valueOf(config.getBoit())).get().getId());

        row1.add(editButton);
        row1.add(denybutton);

        rows.add(row1);
        markup.setKeyboard(rows);
        photo2.setReplyMarkup(markup);

        photo2.setReplyMarkup(markup);
        adres.remove(chatId, bot_id);

        orderService.paid(orderRepository.findByUser_ChatIdAndPaidEqualsAndBot_Id(chatId, false, Long.valueOf(config.getBoit())).get().getId(), chatId);



        try {
            //send_id(chatId);
            //sendText(config.getOwnerId(),  orderRepository.findByUser_ChatId(chatId).get().getUser().getUserName() + "замовив замовлення на суму "+ sendCarteditor_Total(chatId)+"zł за допомогою "+orderRepository.findByChatId(chatId).get().getCurrency()+ "карти");
            //   sendText(update.getMessage().getChatId(), "Ваше замовлення оброблюється");
           // sendText(chatId, "\uD83D\uDC4C");
            /*
            sendText(chatId, "Ваш заказ в обработке\uD83E\uDD73\n" +
                    "\n" +
                    "Вслучае чего, пишите одному из наших менеджеров:\n" +
                    "\n" +
                    "@manager_ambo❕\n" +
                    "@mrBaffik❕\n" +
                    "\n" +
                    "или же мы сами с вами свяжемся\uD83D\uDC7B");

             */

            sendWhatever.sendhere_message(bot_id,bot, chatId, "congrat",  null, null);


            User us = userRepository.findByChatIdAndBot_Id(chatId, Long.valueOf(config.getBoit())).get();

            userRepository.save(us);


            bot.execute(photo2);
            bot.execute(sendContact);

        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

        // sendText(config.getOwnerId(), "ehh");

    }
    private String sendCarteditor_Text(Long chatId, Long bot_id){



        StringBuilder sb = new StringBuilder();
        List<CartItem> items =cartItemRepository.findByChatIdAndBot_IdOrderById(chatId, Long.valueOf(config.getBoit()));
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
                    .append("*__").append(escapeMarkdown(String.valueOf(price))).append(escapeMarkdown(buttonText.getTexts(bot_id).get("curr"))).append("__*")
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
    private double sendCarteditor_Total(Long chatId){


        double total = 0.0;
        StringBuilder sb = new StringBuilder();
        List<CartItem> items =cartItemRepository.findByChatIdAndBot_IdOrderById(chatId, Long.valueOf(config.getBoit()));
        if(items.isEmpty()){

            return total;
        }


        for(CartItem item : items){

            Vapecompony_katalog product = item.getVapecomponyKatalog();
            int quantity = item.getQuantity();
            double price = quantity * product.getCena();

            total += price;




        }

        return total;
    }
    private void sendText(Long chatId, String text){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(text);
        sendMessage.setChatId(chatId.toString());
        try{
            bot.execute(sendMessage);
        }
        catch (TelegramApiException e){
            e.printStackTrace();
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
