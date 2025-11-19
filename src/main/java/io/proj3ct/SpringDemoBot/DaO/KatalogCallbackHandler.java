package io.proj3ct.SpringDemoBot.DaO;

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
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
@Component

public class KatalogCallbackHandler implements CallbackHandler {


    @Autowired
    private VapecomponyRepository vapecomponyRepository;

    @Autowired
    private OrdersRepository orderRepository;

    @Autowired
            private OrderService orderService;



    @Autowired
    private SendWhatever sendWhatever;
    @Autowired
    private MessageRegistry messageRegistry;
    @Autowired
    private TenantService tenantService;
    @Autowired
    private BotRepository botRepository;


    @Override
    public boolean support(String command) {
        return "ALL_KATALOG_BUTTON".equals(command);
    }

    @Override
    public void handle(CallbackQuery query, Long bot_id) {

        messageRegistry.deleteMessagesAfter(query.getMessage().getChatId(), query.getMessage().getMessageId(), false, bot_id);

        if(sendcart_nope(query.getMessage().getChatId(), bot_id)) return;
        send_compony_repository(query.getMessage().getChatId(), bot_id);


    }



    private void send_compony_repository(long chatId, Long bot_id) {

       SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Готов к апгрейду реальности?\n" +
                "\n" +
                "⚡\uFE0FНажимай – и подключайся \uD83E\uDDEC\n" +
                "\n" +
                "\uD83C\uDF21\uFE0FЧТО ЕСТЬ В ЛАБОРАТОРИИ?\uD83E\uDDEA");

        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();


        long k = 0;
        List<Vapecompony> vcr = StreamSupport.stream(vapecomponyRepository.findAllByBot_Id(bot_id).spliterator(), false)
                .collect(Collectors.toList());

        while(vcr.size()>0){


            List<InlineKeyboardButton> rowInLine = new ArrayList<>();

            var yesButton = new InlineKeyboardButton();

            vcr.get(0).getName();
            yesButton.setText(vcr.get(0).getName());
            yesButton.setCallbackData(vcr.get(0).getName());
            vcr.remove(0);
            rowInLine.add(yesButton);

            rowsInLine.add(rowInLine);

        }

        markupInLine.setKeyboard(rowsInLine);
        message.setReplyMarkup(markupInLine);

        // executeMessage(message);
        AbsSender sender = tenantService.getSender(botRepository.findById(bot_id).orElse(null).getBotToken());
        sendWhatever.sendhere_message(bot_id,sender, chatId, "catalog",  markupInLine, null);

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
            orderService.deny_order(or.get().getId(), or.get().getUser().getChatId());
        }
        //  return true;
        return  false;


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
