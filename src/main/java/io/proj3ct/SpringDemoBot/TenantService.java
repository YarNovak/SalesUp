package io.proj3ct.SpringDemoBot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.bots.AbsSender;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TenantService {
    private static final Logger log = LoggerFactory.getLogger(TenantService.class);

    // --- НАША "ФАЛЬШИВАЯ" БАЗА ДАННЫХ ---
    // Ключ - "botIdentifier" (UUID из URL), Значение - Конфигурация тенанта
    private final Map<String, TenantConfig> tenants = new ConcurrentHashMap<>();

    // Кэш "Отправителей", чтобы не создавать их на каждый запрос
    private final Map<String, AbsSender> senders = new ConcurrentHashMap<>();

    /**
     * Этот метод запускается 1 раз при старте приложения.
     * Он заполняет нашу "базу данных" тестовыми ботами.
     */
    @PostConstruct
    public void initTenants() {
        log.warn("--- ИНИЦИАЛИЗАЦИЯ ТЕСТОВЫХ ТЕНАНТОВ ---");

        // --- ⚠️⚠️⚠️ ВАШ БОТ №1 ⚠️⚠️⚠️ ---
        // 1. Замените "uuid-bot-1" на UUID (например, с https://www.uuidgenerator.net/)
        // 2. Замените "ТОКЕН_БОТА_1" на реальный токен
        addTenant(new TenantConfig(
                "13402b03-e2e9-4085-a4e7-b00689210d43", // ⬅️ ЗАМЕНИТЬ!
                "7459388858:AAGB_x6dDrWR56csaRpvN5sUHbcDxhfXs6M", // ⬅️ ЗАМЕНИТЬ!
                "Бот-Теста-Один"
        ));

        // --- ⚠️⚠️⚠️ ВАШ БОТ №2 ⚠️⚠️⚠️ ---
        // 1. Замените "uuid-bot-2" на ВТОРОЙ, ДРУГОЙ UUID
        // 2. Замените "ТОКЕН_БОТА_2" на реальный токен от ВТОРОГО бота
        addTenant(new TenantConfig(
                "3a6d264c-13bc-4a6d-9344-e89fc2bc9fb6", // ⬅️ ЗАМЕНИТЬ!
                "6728124954:AAEPA_XIRIWgs0impNbx3yRNF9SnTwYIPI4", // ⬅️ ЗАМЕНИТЬ!
                "Бот-Теста-Два"
        ));

        log.warn("--- Тестовые тенанты загружены. Не забудьте заменить токены! ---");
    }

    // Метод для добавления тенанта (в реальном приложении он бы брал из БД)
    public void addTenant(TenantConfig config) {
        tenants.put(config.botIdentifier(), config);
        log.info("Загружен тенант: {}", config.botName());
    }

    // Ищет конфиг по ID из URL
    public TenantConfig findConfigById(String botIdentifier) {
        return tenants.get(botIdentifier);
    }

    /**
     * Получает (или создает и кэширует) "Отправителя" (AbsSender) для
     * конкретного токена.
     */
    public AbsSender getSender(String botToken) {
        // computeIfAbsent - атомарно проверяет, есть ли ключ,
        // и если нет - создает, добавляет и возвращает его.
        return senders.computeIfAbsent(botToken, token -> {
            try {
                log.debug("Создание нового AbsSender для токена {}", token.substring(0, 10) + "...");
                return new DefaultAbsSender(new DefaultBotOptions()) {
                    @Override
                    public String getBotToken() {
                        return token;
                    }
                };
            } catch (Exception e) {
                log.error("Не удалось создать AbsSender: " + e.getMessage(), e);
                return null; // В реальном приложении это нужно обработать лучше
            }
        });
    }
}