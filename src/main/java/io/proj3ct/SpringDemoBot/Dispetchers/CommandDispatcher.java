package io.proj3ct.SpringDemoBot.Dispetchers;

import io.proj3ct.SpringDemoBot.DaO.CommandHandler;
import io.proj3ct.SpringDemoBot.TenantService;
import io.proj3ct.SpringDemoBot.config.BotConfig;
import io.proj3ct.SpringDemoBot.dopclasses.MessageRepo.MessageRegistry;
import io.proj3ct.SpringDemoBot.model.*;
import io.proj3ct.SpringDemoBot.repository.BotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CommandDispatcher {

    @Autowired
    private List<CommandHandler> handlers;

    @Autowired
    VapecomponyKatalogRepository vapecomponyKatalogRepository;

    @Autowired
    CartItemRepository cartItemRepository;

    @Autowired
    private BotConfig config;
    @Autowired
            private MessageRegistry messageRegistry;
    @Autowired
    private TenantService tenantService;
    @Autowired
    private BotRepository botRepository;


    public void dispatch(Message message, Long bot_id) {



        String text = message.getText();
        for(CommandHandler handler : handlers) {
            System.out.println("asdasdasd1");
            if(handler.support(text)) {
                System.out.println("asdasdasd2");
                handler.handle(message, bot_id);
                return;
            }
        }



        if (vapecomponyKatalogRepository.findVapecompony_katalogByNameAndBot_Id(message.getText(), Long.valueOf(config.getBoit())).isPresent()){

            vapecomponyKatalogRepository.findVapecompony_katalogByNameAndBot_Id(message.getText(), Long.valueOf(config.getBoit())).ifPresent(product -> {
                SendMessage msg = new SendMessage();
                msg.setChatId(message.getChatId().toString());


                if (cartItemRepository.findByChatIdAndVapecomponyKatalog_NameAndBot_Id(message.getChatId(), vapecomponyKatalogRepository.findVapecompony_katalogByNameAndBot_Id(message.getText(), Long.valueOf(config.getBoit())).get().getName(), Long.valueOf(config.getBoit())).isPresent()) {

                    System.out.println(0);
                    CartItem cti = cartItemRepository.findByChatIdAndVapecomponyKatalog_NameAndBot_Id(message.getChatId(), vapecomponyKatalogRepository.findVapecompony_katalogByNameAndBot_Id(message.getText(), Long.valueOf(config.getBoit())).get().getName(), Long.valueOf(config.getBoit())).get();
                    Optional<Vapecompony_katalog> production = vapecomponyKatalogRepository.findByIdAndBot_Id(cti.getVapecomponyKatalog().getId(), Long.valueOf(config.getBoit()));


                    msg.setText(sendCarteditor_Text2(message.getChatId(), message.getText()));
                    msg.setReplyMarkup(sendCarteditor_KB2(message.getChatId(), message.getText()));


                } else {
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
                    l.add(vapecomponyKatalogRepository.findVapecompony_katalogByNameAndBot_Id(message.getText(), Long.valueOf(config.getBoit())).get());
                    ////
                    msg.setReplyMarkup(generateProductButtons(l));

                }

                AbsSender sender = tenantService.getSender(botRepository.findById(bot_id).orElse(null).getBotToken());

                executeMessage(msg, sender);
            });
            return;

        }






        SendMessage fallback = new SendMessage(message.getChatId().toString(), "Что-то явно пошло не так, таких команд не знаю\uD83D\uDE05");
        AbsSender sender = tenantService.getSender(botRepository.findById(bot_id).orElse(null).getBotToken());
        try{
            Message sm = sender.execute(fallback);
            messageRegistry.addMessage(sm.getChatId(), sm.getMessageId());
        }
        catch(TelegramApiException e) {
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
        sb.append(escapeMarkdown(name.replace("/", "").trim()+": \n\n"));
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
                    .append("*__").append(escapeMarkdown(String.valueOf(price))).append(escapeMarkdown("zl")).append("__*")
                    .append(escapeMarkdown("💰\n"));

            total += price;




        }

        sb.append(escapeMarkdown("\n")).append(escapeMarkdown("✅Вместе: ")).append(escapeMarkdown(String.valueOf(total))).append("zl");
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


            InlineKeyboardButton Button = new InlineKeyboardButton("-");
            Button.setCallbackData("decr2_" + item.getId());

            row.add(Button);

            Button = new InlineKeyboardButton(item.getVapecomponyKatalog().getName().replace("/", "") + " × " + item.getQuantity());
            Button.setCallbackData("noop");

            row.add(Button);

            Button = new InlineKeyboardButton("+");
            Button.setCallbackData("incr2_" + item.getId());


            row.add(Button);
            rows.add(row);

        }

        List<InlineKeyboardButton> row = new ArrayList<>();


        InlineKeyboardButton Button = new InlineKeyboardButton("\uD83D\uDED2 Корзина");
        Button.setCallbackData("SEE_CART");

        row.add(Button);
        rows.add(row);


        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(rows);
        return markup;
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

    private void executeMessage(SendMessage message, AbsSender sender){
        try {
            sender.execute(message);
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

