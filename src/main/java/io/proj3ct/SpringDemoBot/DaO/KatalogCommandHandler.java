package io.proj3ct.SpringDemoBot.DaO;


import io.proj3ct.SpringDemoBot.Cache_my_own.CachesForDB.ButtonText;
import io.proj3ct.SpringDemoBot.Cash.VapecomponyRepository_working_withBD.VapecomponyController;
import io.proj3ct.SpringDemoBot.TenantService;
import io.proj3ct.SpringDemoBot.config.BotConfig;
import io.proj3ct.SpringDemoBot.dopclasses.Senders.SendWhatever;
import io.proj3ct.SpringDemoBot.model.Vapecompony;
import io.proj3ct.SpringDemoBot.model.VapecomponyKatalogRepository;
import io.proj3ct.SpringDemoBot.model.VapecomponyRepository;
import io.proj3ct.SpringDemoBot.repository.BotMessageRepository;
import io.proj3ct.SpringDemoBot.repository.BotRepository;
import io.proj3ct.SpringDemoBot.service.VapecomponyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
@RequiredArgsConstructor
@Slf4j
public class KatalogCommandHandler implements ButtonsMapHandler {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final BotRepository botRepository;

    @Autowired
    private VapecomponyController vapecomponyController;

    @Autowired
    VapecomponyRepository vapecomponyRepository;

    @Autowired
    private VapecomponyKatalogRepository vapecomponyKatalogRepository;


    @Autowired
    VapecomponyRepository vape;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private VapecomponyService vapecomponyService;

    @Autowired
    private SendWhatever sendWhatever;

    @Autowired
    private ButtonText buttonText;
    @Autowired
    private TenantService tenantService;


    @Override
    public boolean support(String command, Long bot_id) {
      return buttonText.getTexts(bot_id).get("catalog").equals(command);
    }


    @Override
    @Transactional
    public void handle(Message message, Long bot_id) {

        send_compony_repository(message.getChatId(), bot_id);



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

        //executeMessage(message);
        AbsSender sender =tenantService.getSender(botRepository.findById(bot_id).orElse(null).getBotToken());
        sendWhatever.sendhere_message(bot_id,sender, chatId, "catalog",  markupInLine, null);



    }

    private void verifySavedItem(Vapecompony item) {
        boolean exists = vape.existsById(item.getId());
        log.info("Item {} exists in DB: {}", item.getId(), exists);
        if (!exists) {
            log.error("Item was not saved to DB!");
        }
    }


}
