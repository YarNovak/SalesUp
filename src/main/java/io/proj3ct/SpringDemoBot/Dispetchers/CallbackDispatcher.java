package io.proj3ct.SpringDemoBot.Dispetchers;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.proj3ct.SpringDemoBot.Cache_my_own.CachesForDB.ButtonText;
import io.proj3ct.SpringDemoBot.DaO.CallbackHandler;
import io.proj3ct.SpringDemoBot.DaO.Handle_Menu;
import io.proj3ct.SpringDemoBot.config.BotConfig;
import io.proj3ct.SpringDemoBot.dopclasses.MessageRepo.MessageRegistry;
import io.proj3ct.SpringDemoBot.dopclasses.Senders.MessageComposer;
import io.proj3ct.SpringDemoBot.dopclasses.Senders.SendWhatever;
import io.proj3ct.SpringDemoBot.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CallbackDispatcher {

    @Autowired
    private List<CallbackHandler> handlers;
    @Autowired
    private VapecomponyRepository vapecomponyRepository;
    @Autowired
    private Handle_Menu handleMenu;
    @Autowired
    VapecomponyKatalogRepository vapecomponyKatalogRepository;
    @Autowired
    private BotConfig config;

    @Autowired
    CartItemRepository cartItemRepository;

    @Autowired
    private ButtonText buttonText;

    TelegramLongPollingBot bot;
    @Autowired
    private SendWhatever sendWhatever;

    @Autowired
    private MessageRegistry messageRegistry;

    public void dispatch(CallbackQuery query, TelegramLongPollingBot bot) {
        this.bot = bot;
        for(CallbackHandler handler : handlers) {

            if(handler.support(query.getData())){
                handler.handle(query, bot);
                return;
            }

        }
        for (Vapecompony vapecompony : vapecomponyRepository.findAllByBot_Id(Long.valueOf(config.getBoit()))) {

            if (vapecompony.getName().equals(query.getData())) {

                messageRegistry.deleteMessagesAfter(query.getMessage().getChatId(), query.getMessage().getMessageId(), false, bot);
               // sendChannelMessageToUser(query.getMessage().getChatId(), vapecompony.getMessageLink());

                handleMenu.handle(query.getMessage().getChatId(), vapecompony.getId(), bot);

                return;
            }

        }

        String message = query.getData();

        if (vapecomponyKatalogRepository.findVapecompony_katalogByNameAndBot_Id(message, Long.valueOf(config.getBoit())).isPresent()){
            messageRegistry.deleteMessagesAfter(query.getMessage().getChatId(), query.getMessage().getMessageId(), false, bot);
            vapecomponyKatalogRepository.findVapecompony_katalogByNameAndBot_Id(message, Long.valueOf(config.getBoit())).ifPresent(product -> {


                System.out.println("kaka");
                SendMessage msg = new SendMessage();
                msg.setParseMode("MarkdownV2");
                msg.setChatId(query.getMessage().getChatId().toString());


                if (cartItemRepository.findByChatIdAndVapecomponyKatalog_NameAndBot_Id(query.getMessage().getChatId(), vapecomponyKatalogRepository.findVapecompony_katalogByNameAndBot_Id(message, Long.valueOf(config.getBoit())).get().getName(), Long.valueOf(config.getBoit())).isPresent()) {

                    System.out.println(0);
                    CartItem cti = cartItemRepository.findByChatIdAndVapecomponyKatalog_NameAndBot_Id(query.getMessage().getChatId(), vapecomponyKatalogRepository.findVapecompony_katalogByNameAndBot_Id(message, Long.valueOf(config.getBoit())).get().getName(), Long.valueOf(config.getBoit())).get();
                    Optional<Vapecompony_katalog> production = vapecomponyKatalogRepository.findByIdAndBot_Id(cti.getVapecomponyKatalog().getId(), Long.valueOf(config.getBoit()));

                    if(!production.isPresent()){
                        return;
                    }

                   // MessageComposer.CombinedMessage combinedMessage = MessageComposer.compose(production.get().getName(), production.get().getEntitiesJson(), vapecompony.getDescription(), vapecompony.getEntitiesDescJson(), "\n\n");

                    msg.setText(sendCarteditor_Text2(query.getMessage().getChatId(), message));
/*
                    if(production.get().getEntitiesJson()!=null) {
                        try {
                            ObjectMapper mapper2 = new ObjectMapper();
                            List<MessageEntity> entities = mapper2.readValue(
                                    production.get().getEntitiesJson(),
                                    new TypeReference<List<MessageEntity>>() {
                                    }
                            );
                            msg.setEntities(entities);
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        }
                    }

 */

                    // msg.setText( sendCarteditor_Text2(query.getMessage().getChatId(), message));
                    msg.setReplyMarkup(sendCarteditor_KB2(query.getMessage().getChatId(), message));


                    sendWhatever.sendhere_readyVapecomponyKatalogMessage(bot, vapecomponyKatalogRepository.findVapecompony_katalogByNameAndBot_Id(message, Long.valueOf(config.getBoit())).get(), query.getMessage().getChatId(), sendCarteditor_Text2(query.getMessage().getChatId(), message), "MarkdownV2", Long.valueOf(config.getBoit()), sendCarteditor_KB2(query.getMessage().getChatId(), message), null);
                    //executeMessage(msg);



                } else {
                    messageRegistry.deleteMessagesAfter(query.getMessage().getChatId(), query.getMessage().getMessageId(), false, bot);
                    System.out.println("kakashki)");
                    System.out.println(1);
                    msg.setText(product.getName().replace("/", "").trim());
                        /*
                        InlineKeyboardButton addToCartBtn = new InlineKeyboardButton("🛒 Додати до кошика");
                        addToCartBtn.setCallbackData("add_to_cart:" + product.getId()); //add_to_cart:

                        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
                        markup.setKeyboard(List.of(List.of(addToCartBtn))); ////
                        msg.setReplyMarkup(markup);

                         */
                    //!11111111!11111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111

                    List<Vapecompony_katalog> l = new ArrayList<>();
                    l.add(vapecomponyKatalogRepository.findVapecompony_katalogByNameAndBot_Id(message, Long.valueOf(config.getBoit())).get());
                    ////
                    //msg.setReplyMarkup(generateProductButtons(l));
                    System.out.println("kakashki)");

                    sendWhatever.sendhere_firstAdd(bot, query.getMessage().getChatId(), product.getId(), Long.valueOf(config.getBoit()), generateProductButtons(l), null );

                }


            });
            return;

        }


        SendMessage fallback = new SendMessage(String.valueOf(query.getMessage().getChatId()), "⚠️ Невідома дія");
        try {
            bot.execute(fallback);
        }
        catch (TelegramApiException e) {
            e.printStackTrace();
        }
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
                    .append("__").append(escapeMarkdown(String.valueOf(price))).append(escapeMarkdown("zl")).append("__")
                    .append(escapeMarkdown("💰\n"));

            total += price;




        }
        sb.append(escapeMarkdown("\n")).append(escapeMarkdown(buttonText.getTexts().get("payment")) + " ").append("*__").append(escapeMarkdown(String.valueOf(total))).append("zl").append("__*");

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

    public InlineKeyboardMarkup generateProductButtons(List<Vapecompony_katalog> products) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        System.out.println("NU PIZDA(");
        System.out.println(products.size());

        System.out.println(products.get(0).getName());

        for (Vapecompony_katalog product : products) {

            InlineKeyboardButton button = new InlineKeyboardButton();
            if (product.getKilkist() > 0) {
                button.setText(buttonText.getTexts().get("first_add"));
                button.setCallbackData("add_to_cart:" + product.getId());
            } else {
                button.setText(buttonText.getTexts().get("empty_add"));
                button.setCallbackData("out_of_stock");
            }

            rows.add(Collections.singletonList(button));

        }

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(rows);
        return markup;
    }

    private void executeMessage(SendMessage message){
        try {
            bot.execute(message);
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
