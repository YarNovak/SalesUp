package io.proj3ct.SpringDemoBot.DaO;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
@Component

public class CreateBotCallbackhandle implements CallbackHandler{
/*
    @Autowired
    private PlatformUserRepository userRepository;
    @Autowired
    private BotRepository botRepository;
*/
    @Override
    public boolean support(String callbackData) {
        return "CREATE_BOT".equals(callbackData);
    }

    @Override
    public void handle(CallbackQuery query, Long bot_id) {
/*
        Long telegramId = query.getFrom().getId();
        Optional<PlatformUser> userOpt = userRepository.findByTelegramId(telegramId);
        if(userOpt.isEmpty()) return;

        PlatformUser user = userOpt.get();

        Optional<Bot> freeBotOpt = botRepository.findFirstByOwnerIsNull();
        if(freeBotOpt.isEmpty()) {
            send(bot, query.getMessage().getChatId().toString(),
                    "⚠️ Нажаль, усі шаблонні боти вже зайняті.\nСпробуй пізніше або звернись до адміністратора.");
            return;
        }

        Bot freeBot = freeBotOpt.get();
        freeBot.setOwner(user);
        freeBot.setSubscriptionStatus("free");
        freeBot.setCurrentPrice(BigDecimal.ZERO);
        freeBot.setRegistrationDate(LocalDateTime.now());
        freeBot.setPaymentDue(LocalDateTime.now().plusDays(7));

        botRepository.save(freeBot);

        String botUsername = extractUsernameFromToken(freeBot.getBotToken());
        String link = "https://t.me/" + botUsername + "?start=start" + user.getId();

        String text =
        "🎉 Твій бот успішно створений!\n\n" +
                "Запусти його тут:\n" + link + "\n\n" +
                "🔓 Пробний період: 7 днів.";

        InlineKeyboardButton goToBotButton = new InlineKeyboardButton("🚀 Відкрити бота");
        goToBotButton.setUrl(link);

        InlineKeyboardButton editFunctionalityButton = new InlineKeyboardButton("⚙ Змінити функціонал бота");
        editFunctionalityButton.setCallbackData("EDIT_BOT_FUNCTION:" + freeBot.getBotusername());

        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        keyboard.add(Collections.singletonList(goToBotButton));
        keyboard.add(Collections.singletonList(editFunctionalityButton));

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(keyboard);

        SendMessage message = new SendMessage();
        message.setChatId(query.getMessage().getChatId().toString());
        message.setText(text);
        message.setReplyMarkup(markup);

        try {
            bot.execute(message);
        }
        catch (TelegramApiException e) {
            e.printStackTrace();
        }
        */


    }
    /*
    private void send(TelegramLongPollingBot bot, String chatId, String text) {
        try {
            bot.execute(new SendMessage(chatId, text));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    private String extractUsernameFromToken(String token) {

        Bot need_bot = botRepository.findByBotToken(token).get();
        return need_bot.getBotusername();
    }

     */


}
