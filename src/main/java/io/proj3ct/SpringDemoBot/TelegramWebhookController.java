package io.proj3ct.SpringDemoBot;

import io.proj3ct.SpringDemoBot.DB_entities.Bot;
import io.proj3ct.SpringDemoBot.repository.BotRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.objects.Update;

@RestController
@RequestMapping("/api/webhook")

public class TelegramWebhookController {

    private static final Logger log = LoggerFactory.getLogger(TelegramWebhookController.class);

    @Autowired
    private TenantService tenantService;

    @Autowired
    private BotRepository botRepository;

    @Autowired
    private UpdateProcessingService updateProcessingService;

    // Внедряем наши сервисы
    /*
    public TelegramWebhookController(TenantService tenantService, UpdateProcessingService updateProcessingService, BotRepository botRepository) {
        System.out.println("SUKA");
        this.tenantService = tenantService;
        this.botRepository = botRepository;
        this.updateProcessingService = updateProcessingService;
    }
     */

    /**
     * Это - ЕДИНСТВЕННАЯ точка входа для ВСЕХ ботов.
     * Telegram присылает сюда POST-запрос.
     *
     * @param botIdentifier - "uuid-bot-1" или "uuid-bot-2" из URL
     * @param update        - JSON-объект от Telegram
     */
    @PostMapping("/{botIdentifier}")
    public ResponseEntity<Void> onUpdateReceived(@PathVariable("botIdentifier") String botIdentifier,
                                                 @RequestBody Update update) {

        log.info("Получено обновление для тенанта: {}", botIdentifier);
        System.out.println("SUKA2");
        System.out.println(botIdentifier);// + " " + botRepository.findById(Long.parseLong(botIdentifier)).orElse(null).getBotToken());

        // 1. Найти, какому боту (тенанту) пришло обновление
        Bot bot = botRepository.findById(Long.parseLong(botIdentifier)).orElse(null);

        if (bot == null) {
            // Если мы не знаем такого тенанта, логируем и игнорируем
            log.warn("Получен запрос для неизвестного тенанта: {}", botIdentifier);
            // Важно вернуть 200 OK, чтобы Telegram не пытался отправить апдейт снова
            return ResponseEntity.ok().build();
        }

        // 2. Передать обновление на АСИНХРОННУЮ обработку
        // Контроллер немедленно вернет 200 OK, а логика выполнится в другом потоке
        updateProcessingService.process(bot, update);

        // 3. Немедленно ответить Telegram 200 OK.
        return ResponseEntity.ok().build();
    }
}