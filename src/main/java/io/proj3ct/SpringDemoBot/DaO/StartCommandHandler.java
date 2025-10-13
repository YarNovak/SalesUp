package io.proj3ct.SpringDemoBot.DaO;


import io.proj3ct.SpringDemoBot.Cache_my_own.CachesForDB.ButtonText;
import io.proj3ct.SpringDemoBot.Cache_my_own.CachesForDB.MessagesInf;
import io.proj3ct.SpringDemoBot.config.BotConfig;
import io.proj3ct.SpringDemoBot.dopclasses.MessageRepo.MessageRegistry;
import io.proj3ct.SpringDemoBot.dopclasses.Senders.SendWhatever;
import io.proj3ct.SpringDemoBot.model.User;
import io.proj3ct.SpringDemoBot.model.UserRepository;

import io.proj3ct.SpringDemoBot.repository.BotRepository;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.Timestamp;
import java.util.*;

@Component
public class StartCommandHandler implements CommandHandler {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ButtonText buttonText;

    TelegramLongPollingBot bot;

    @Autowired
    BotConfig botConfig;

    @Autowired
    BotRepository botRepository;

    @Autowired
    private MessagesInf messagesInf;

    @Autowired
    private SendWhatever sendWhatever;

    @Autowired
    private MessageRegistry messageRegistry;

    @Override
    public boolean support(String command) {

      return "/start".equals(command);
    }

    @Override
    public void handle(Message message, TelegramLongPollingBot bot) {

        messageRegistry.deleteMessagesAfter(message.getChatId(), message.getMessageId(), false, bot);
        messageRegistry.deleteMessagesBefore(message.getChatId(), message.getMessageId(), false, bot);


        System.out.println("holaaaaaaaaaaaaaaaaaaaaaaaaaaa");

        registerUser(message);
        this.bot = bot;
        startCommandReceived(message.getChatId(), message.getChat().getFirstName());



    }

    private void registerUser(Message msg) {

        if(userRepository.findByChatIdAndBot_Id(msg.getChatId(), Long.valueOf(botConfig.getBoit())).isEmpty()){

            var chatId = msg.getChatId();
            var chat = msg.getChat();

            User user = new User();

            user.setChatId(chatId);
            user.setFirstName(chat.getFirstName());
            user.setLastName(chat.getLastName());
            user.setUserName(chat.getUserName());
            user.setRegisteredAt(new Timestamp(System.currentTimeMillis()));
            user.setBot(botRepository.findById(Long.valueOf(botConfig.getBoit())).get());

            userRepository.save(user);

        }
    }
    private void startCommandReceived(long chatId, String name) {

        Map<String, String> my_messages = new HashMap<>();



        String answer = ("Приветик, " + name +", меня зовут Ella Spot.\uD83D\uDC4B\n");

        answer = messagesInf.getMessageText("greeting");

        sendMessage(chatId, answer);

    }
    private void sendMessage(long chatId, String textToSend) {
/*
        SendMessage message = new SendMessage();


        message.setChatId(String.valueOf(chatId));

        message.setText(textToSend);



        if(messagesInf.getMessageEntity("greeting")!=null){
            try {
                ObjectMapper mapper2 = new ObjectMapper();
                List<MessageEntity> entities = mapper2.readValue(
                        messagesInf.getMessageEntity("greeting"),
                        new TypeReference<List<MessageEntity>>() {}
                );
                message.setEntities(entities);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

 */

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();

        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();
/*
        row.add("\uD83E\uDDEAКаталог");
        row.add("\uD83D\uDCB8Оплата");
        row.add(buttonText.getTexts().get("cart"));


 */
        row.add(buttonText.getTexts().get("catalog"));
        row.add(buttonText.getTexts().get("payment"));
        row.add(buttonText.getTexts().get("cart"));

        keyboardRows.add(row);

        keyboardMarkup.setKeyboard(keyboardRows);
        keyboardMarkup.setResizeKeyboard(true);

/*
        message.setReplyMarkup(keyboardMarkup);




        executeMessage(message);

 */

        sendWhatever.sendhere_message(bot, chatId, "greeting", null,  keyboardMarkup);
        katalog_all(chatId);

    }
    private void executeMessage(SendMessage message) {
        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    private void katalog_all(long chatId) {
/*
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));

        message.setText(messagesInf.getMessageText("menu"));

        if(messagesInf.getMessageEntity("menu")!=null){
            try {
                ObjectMapper mapper2 = new ObjectMapper();
                List<MessageEntity> entities = mapper2.readValue(
                        messagesInf.getMessageEntity("menu"),
                        new TypeReference<List<MessageEntity>>() {}
                );
                message.setEntities(entities);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

 */

        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();



        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();
        var allk = new InlineKeyboardButton();

        allk.setText(buttonText.getTexts().get("catalog"));
        allk.setCallbackData("ALL_KATALOG_BUTTON");



        rowInLine.add(allk);
        rowsInLine.add(rowInLine);

        markupInLine.setKeyboard(rowsInLine);
       // message.setReplyMarkup(markupInLine);

        //executeMessage(message);

        sendWhatever.sendhere_message(bot, chatId, "menu", markupInLine,  null );
        System.out.println("(((");
    }




}
