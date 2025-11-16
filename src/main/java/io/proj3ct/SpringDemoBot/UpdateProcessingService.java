package io.proj3ct.SpringDemoBot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
@EnableAsync
public class UpdateProcessingService {
    private static final Logger log = LoggerFactory.getLogger(UpdateProcessingService.class);

    private final TenantService tenantService;

    public UpdateProcessingService(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    /**
     * Вся логика бота - здесь.
     * Аннотация @Async("taskExecutor") указывает Spring
     * выполнить этот метод в отдельном потоке из пула "taskExecutor".
     */
    @Async("taskExecutor")
    public void process(TenantConfig config, Update update) {
        log.debug("Начало обработки (асинхронно) для: {}", config.botName());

        // Здесь ваша "старая" логика бота
        if (update.hasMessage() && update.getMessage().hasText()) {
            long chatId = update.getMessage().getChatId();
            String text = update.getMessage().getText();

            String responseText;
            if (text.equals("/start")) {
                // Логика, специфичная для тенанта
                responseText = "Привет! Я " + config.botName() + " (" + config.botIdentifier() + ")";
            } else {
                responseText = "Эхо от " + config.botName() + ": " + text;
            }

            // 1. Создаем метод
            SendMessage message = new SendMessage(String.valueOf(chatId), responseText);

            // 2. Получаем "отправителя" для этого конкретного токена
            AbsSender sender = tenantService.getSender(config.botToken());
            if (sender == null) {
                log.error("Не удалось получить AbsSender для тенанта: {}", config.botIdentifier());
                return;
            }

            // 3. Отправляем ответ
            try {
                sender.execute(message);
                log.debug("Ответ отправлен для {}", config.botName());
            } catch (TelegramApiException e) {
                log.error("Не удалось отправить сообщение: " + e.getMessage(), e);
            }
        }
    }
}