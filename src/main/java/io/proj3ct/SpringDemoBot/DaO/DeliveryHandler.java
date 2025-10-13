package io.proj3ct.SpringDemoBot.DaO;

import io.proj3ct.SpringDemoBot.Cache_my_own.CachesForDB.ButtonText;
import io.proj3ct.SpringDemoBot.config.BotConfig;
import io.proj3ct.SpringDemoBot.dopclasses.Senders.SendWhatever;
import io.proj3ct.SpringDemoBot.model.*;
import io.proj3ct.SpringDemoBot.repository.BotMessageRepository;
import io.proj3ct.SpringDemoBot.repository.BotRepository;
import io.proj3ct.SpringDemoBot.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendContact;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class DeliveryHandler {

    @Autowired
    private OrdersRepository orderRepository;

    @Autowired
    private VapecomponyKatalogRepository vapecomponyKatalogRepository;

    @Autowired
    private Adddelivery add_DELIVERY;

    @Autowired
    private Media media;

    @Autowired
    BotConfig config;

    @Autowired
    private Adres adres;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Wait_photo wait_photo;

    @Autowired
    private CartItemRepository cartItemRepository;

    TelegramLongPollingBot bot;
    @Autowired
    private ButtonText buttonText;

    @Autowired
    private BotMessageRepository botMessageRepository;

    @Autowired
    private SendWhatever sendWhatever;

    @Autowired
    private OrderService orderService;
    @Autowired
    private BotRepository botRepository;

    public void handle_Delivery(Update update, TelegramLongPollingBot bot) throws TelegramApiException {

        this.bot = bot;


        Long chatId = update.getMessage().getChatId();
        String messageText = update.getMessage().getText();

        Optional<Orders> order = orderRepository.findByUser_ChatIdAndPaidEqualsAndBot_Id(chatId, false, Long.valueOf(config.getBoit()));

        String catalog = buttonText.getTexts().get("catalog");
        String cart = buttonText.getTexts().get("cart");
        String payment = buttonText.getTexts().get("payment");


        if((!vapecomponyKatalogRepository.findByNameAndBot_Id(messageText, Long.valueOf(config.getBoit())).isEmpty()) ||(messageText.startsWith("/")) || (messageText.equals(catalog)) || (messageText.equals(cart)) || (messageText.equals(payment) ) && (!messageText.equals(botMessageRepository.findByMessageKeyAndBot_Id("delivery", Long.valueOf(config.getBoit())).get()))) {

            sendWhatever.sendhere_message(bot, chatId, "delivery",  null, null);
            return;
        }

        if (order.isPresent()) {
            Orders or = order.get();
            or.setDelivery(messageText);
            adres.put(chatId, messageText);
            add_DELIVERY.remove(chatId);
            orderRepository.save(or);
            if (or.getCash_card().equals("CARD")) {

                if (or.getCurrency().equals("PLN")) {
                    wait_photo.put(chatId, true);



                    /*sendText(chatId, "\uD83D\uDD0D Перешлите деньги на номер карты ниже:\n" +
                            "\n" +
                            "\uD83D\uDCF8 Пришлите СКРИНШОТ ПОДТВЕРЖДЕНИЯ в чат\n" +
                            "\uD83D\uDCC2 без подтверждения заказ НЕ АКТИВИРУЕТСЯ.\n" +
                            "\n" +
                            "\uD83D\uDCB3Номер карты\n" +
                  // sendText(chatId, config.get_exet_card());
                                    */

                    sendWhatever.sendhere_message(bot, chatId, "send_money",  null, null);
                   // wait_photo.put(chatId, true);
                    return;


                }
                else {



                    //  wait_photo.put(chatId, true);
                    /*
                    sendText(chatId, "\uD83D\uDD0D Перешлите деньги на номер карты ниже:\n" +
                            "\n" +
                            "\uD83D\uDCF8 Пришлите СКРИНШОТ ПОДТВЕРЖДЕНИЯ в чат\n" +
                            "\uD83D\uDCC2 без подтверждения заказ НЕ АКТИВИРУЕТСЯ.\n" +
                            "\n" +
                            "\uD83D\uDCB3Номер карты\n" +
                            "\uD83D\uDC47\uD83D\uDC47\uD83D\uDC47\uD83D\uDC47");
                    sendText(chatId, config.getUkr_card());

                     */
                    sendWhatever.sendhere_message(bot, chatId, "send_money",  null, null);
                    wait_photo.put(chatId, true);
                    return;

                }

            } else {
                // if(stop.getOrDefault(chatId, false)) return;

                Contact contact =  media.get(chatId);
                media.remove(chatId);
                SendMessage photo = new SendMessage();
                SendContact sendContact = new SendContact();
                sendContact.setChatId(botRepository.findById(Long.valueOf(config.getBoit())).get().getOwner().getTelegramId().toString());
                sendContact.setFirstName(contact.getFirstName());
                sendContact.setPhoneNumber(contact.getPhoneNumber());

                SendMessage photo2 = new SendMessage();

                photo.setChatId(botRepository.findById(Long.valueOf(config.getBoit())).get().getOwner().getTelegramId().toString());
                photo.setParseMode("MarkdownV2");

                // photo.setPhoto(new InputFile(media_photo.getMedia()));


                InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> rows = new ArrayList<>();

                List<InlineKeyboardButton> row1 = new ArrayList<>();
                InlineKeyboardButton editButton = new InlineKeyboardButton("Подтвердить!");
                editButton.setCallbackData("ACCEPT" + "_" + orderRepository.findByUser_ChatIdAndPaidEqualsAndBot_Id(chatId, false, Long.valueOf(config.getBoit())).get().getId());
                InlineKeyboardButton denybutton = new InlineKeyboardButton("Отлонить!");
                denybutton.setCallbackData("DENY" + "_" + orderRepository.findByUser_ChatIdAndPaidEqualsAndBot_Id(chatId, false, Long.valueOf(config.getBoit())).get().getId());

                row1.add(editButton);
                row1.add(denybutton);

                rows.add(row1);
                markup.setKeyboard(rows);
                photo.setReplyMarkup(markup);

                photo.setReplyMarkup(markup);



///////!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                try {

                    //   send_id(chatId);
                    // sendText(chatId, "\uD83D\uDC4C");
                    /*
                    sendText(chatId, "Ваш заказ в обработке\uD83E\uDD73\n" +
                            "\n" +
                            "Вслучае чего, пишите нашему одному из наших менеджеров:\n" +
                            "\n" +
                            "@manager_ambo❕\n" +
                            "@mrBaffik❕\n" +
                            "\n" +
                            "или же мы сами с вами свяжемся\uD83D\uDC7B");

                     */

                    sendWhatever.sendhere_message(bot, chatId, "congrat",  null, null);

                    // Orders ord = orderRepository.findByUser_ChatIdAndPaidEquals(chatId, false).get();
                    User us = userRepository.findByChatIdAndBot_Id(chatId, Long.valueOf(config.getBoit())).get();

                    userRepository.save(us);
                    // sendText(config.getOwnerId(),  orderRepository.findByUser_ChatId(chatId).get().getUser().getUserName() + "замовив замовлення на суму "+ sendCarteditor_Total(chatId)+"zł за допомогою "+orderRepository.findByChatId(chatId).get().getCurrency()+ "карти");

                    // photo.setCaption(orderRepository.findByUser_ChatId(chatId).get().getUser().getUserName() + "замовив замовлення на суму "+ sendCarteditor_Total(chatId)+"zł за допомогою готівки" + "\n"+sendCarteditor_Total(chatId)+);
                    photo.setText( escapeMarkdown(orderRepository.findByUser_ChatIdAndPaidEqualsAndBot_Id(chatId, false, Long.valueOf(config.getBoit())).get().getUser().getUserName() + " сделал заказ на суму " + sendCarteditor_Total(chatId) +buttonText.getTexts().get("curr") + " наличкой\n\n")  + sendCarteditor_Text(chatId)+  escapeMarkdown( "\n\n"+"Адрес: "+adres.get(chatId)));


                    adres.remove(chatId);

                    orderService.paid(orderRepository.findByUser_ChatIdAndPaidEqualsAndBot_Id(chatId, false, Long.valueOf(config.getBoit())).get().getId(), chatId);
                    bot.execute(photo);
                    bot.execute(sendContact);

                    return;

                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }

                // sendText(config.getOwnerId(), "ehh");


            }

        }


        //return;



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

    public void sendText(Long chatId, String text) throws TelegramApiException {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        bot.execute(message);
    }


    private String sendCarteditor_Text(Long chatId){



        StringBuilder sb = new StringBuilder();
        List<CartItem> items =cartItemRepository.findByChatIdAndBot_IdOrderById(chatId, Long.valueOf(config.getBoit()));
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
