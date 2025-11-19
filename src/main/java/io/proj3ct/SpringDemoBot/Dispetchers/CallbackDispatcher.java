package io.proj3ct.SpringDemoBot.Dispetchers;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.proj3ct.SpringDemoBot.Cache_my_own.CachesForDB.ButtonText;
import io.proj3ct.SpringDemoBot.DaO.CallbackHandler;
import io.proj3ct.SpringDemoBot.DaO.Handle_Menu;
import io.proj3ct.SpringDemoBot.TenantService;
import io.proj3ct.SpringDemoBot.config.BotConfig;
import io.proj3ct.SpringDemoBot.dopclasses.MessageRepo.MessageRegistry;
import io.proj3ct.SpringDemoBot.dopclasses.Senders.MessageComposer;
import io.proj3ct.SpringDemoBot.dopclasses.Senders.SendWhatever;
import io.proj3ct.SpringDemoBot.model.*;
import io.proj3ct.SpringDemoBot.repository.BotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.*;

@Component
@RequiredArgsConstructor
public class CallbackDispatcher {

    @Autowired
    private List<CallbackHandler> handlers;

    @Autowired
    TenantService tenantService;

    @Autowired
    private BotRepository botRepository;

    @Autowired
    private VapecomponyRepository vapecomponyRepository;
    @Autowired
    private Handle_Menu handleMenu;
    @Autowired
    VapecomponyKatalogRepository vapecomponyKatalogRepository;

    @Autowired
    CartItemRepository cartItemRepository;

    @Autowired
    private ButtonText buttonText;


    @Autowired
    private SendWhatever sendWhatever;

    @Autowired
    private MessageRegistry messageRegistry;

    public void dispatch(CallbackQuery query, Long bot_id) {

        for(CallbackHandler handler : handlers) {

            if(handler.support(query.getData())){
                handler.handle(query, bot_id);
                return;
            }

        }
        for (Vapecompony vapecompony : vapecomponyRepository.findAllByBot_Id(bot_id)) {

            if (vapecompony.getName().equals(query.getData())) {

                messageRegistry.deleteMessagesAfter(query.getMessage().getChatId(), query.getMessage().getMessageId(), false, bot_id);
               // sendChannelMessageToUser(query.getMessage().getChatId(), vapecompony.getMessageLink());

                handleMenu.handle(query.getMessage().getChatId(), vapecompony.getId(), bot_id);

                return;
            }

        }

        String message = query.getData();

        if (vapecomponyKatalogRepository.findVapecompony_katalogByNameAndBot_Id(message, bot_id).isPresent()){
            messageRegistry.deleteMessagesAfter(query.getMessage().getChatId(), query.getMessage().getMessageId(), false, bot_id);
            vapecomponyKatalogRepository.findVapecompony_katalogByNameAndBot_Id(message, bot_id).ifPresent(product -> {


                System.out.println("kaka");
                SendMessage msg = new SendMessage();
                msg.setParseMode("MarkdownV2");
                msg.setChatId(query.getMessage().getChatId().toString());


                if (cartItemRepository.findByChatIdAndVapecomponyKatalog_NameAndBot_Id(query.getMessage().getChatId(), vapecomponyKatalogRepository.findVapecompony_katalogByNameAndBot_Id(message, bot_id).get().getName(), bot_id).isPresent()) {

                    System.out.println(0);
                    CartItem cti = cartItemRepository.findByChatIdAndVapecomponyKatalog_NameAndBot_Id(query.getMessage().getChatId(), vapecomponyKatalogRepository.findVapecompony_katalogByNameAndBot_Id(message, bot_id).get().getName(), bot_id).get();
                    Optional<Vapecompony_katalog> production = vapecomponyKatalogRepository.findByIdAndBot_Id(cti.getVapecomponyKatalog().getId(), bot_id);

                    if(!production.isPresent()){
                        return;
                    }

                   // MessageComposer.CombinedMessage combinedMessage = MessageComposer.compose(production.get().getName(), production.get().getEntitiesJson(), vapecompony.getDescription(), vapecompony.getEntitiesDescJson(), "\n\n");

                    msg.setText(sendCarteditor_Text2(query.getMessage().getChatId(), message, bot_id));
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
                    msg.setReplyMarkup(sendCarteditor_KB2(query.getMessage().getChatId(), message, bot_id));

                    AbsSender sender = tenantService.getSender(botRepository.findById(bot_id).orElse(null).getBotToken());
                    sendWhatever.sendhere_readyVapecomponyKatalogMessage(vapecomponyKatalogRepository.findVapecompony_katalogByNameAndBot_Id(message, bot_id).get(), query.getMessage().getChatId(), sendCarteditor_Text2(query.getMessage().getChatId(), message, bot_id), "MarkdownV2", sender, sendCarteditor_KB2(query.getMessage().getChatId(), message, bot_id), null);
                    //executeMessage(msg);



                } else {
                    messageRegistry.deleteMessagesAfter(query.getMessage().getChatId(), query.getMessage().getMessageId(), false, bot_id);
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
                    l.add(vapecomponyKatalogRepository.findVapecompony_katalogByNameAndBot_Id(message, bot_id).get());
                    ////
                    //msg.setReplyMarkup(generateProductButtons(l));
                    System.out.println("kakashki)");

                    sendWhatever.sendhere_firstAdd(bot_id, query.getMessage().getChatId(), product.getId(), generateProductButtons(l, bot_id), null );

                }


            });
            return;

        }


        SendMessage fallback = new SendMessage(String.valueOf(query.getMessage().getChatId()), "⚠️ Невідома дія");
        AbsSender sender = tenantService.getSender(Objects.requireNonNull(botRepository.findById(bot_id).orElse(null)).getBotToken());
        try {
            sender.execute(fallback);
        }
        catch (TelegramApiException e) {
            e.printStackTrace();
        }
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
                    .append("__").append(escapeMarkdown(String.valueOf(price))).append(escapeMarkdown("zl")).append("__")
                    .append(escapeMarkdown("💰\n"));

            total += price;




        }
        sb.append(escapeMarkdown("\n")).append(escapeMarkdown(buttonText.getTexts(bot_id).get("payment")) + " ").append("*__").append(escapeMarkdown(String.valueOf(total))).append("zl").append("__*");

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


            InlineKeyboardButton Button = new InlineKeyboardButton(buttonText.getTexts(bot_id).get("delete"));
            Button.setCallbackData("decr2_" + item.getId());

            row.add(Button);

            Button = new InlineKeyboardButton(item.getVapecomponyKatalog().getName().replace("/", "") + " × " + item.getQuantity());
            Button.setCallbackData("noop");

            row.add(Button);

            Button = new InlineKeyboardButton(buttonText.getTexts(bot_id).get("add"));
            Button.setCallbackData("incr2_" + item.getId());


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

    public InlineKeyboardMarkup generateProductButtons(List<Vapecompony_katalog> products, long bot_id) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        System.out.println("NU PIZDA(");
        System.out.println(products.size());

        System.out.println(products.get(0).getName());

        for (Vapecompony_katalog product : products) {

            InlineKeyboardButton button = new InlineKeyboardButton();
            if (product.getKilkist() > 0) {
                button.setText(buttonText.getTexts(bot_id).get("first_add"));
                button.setCallbackData("add_to_cart:" + product.getId());
            } else {
                button.setText(buttonText.getTexts(bot_id).get("empty_add"));
                button.setCallbackData("out_of_stock");
            }

            rows.add(Collections.singletonList(button));

        }

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
