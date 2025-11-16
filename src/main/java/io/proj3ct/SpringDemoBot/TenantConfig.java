package io.proj3ct.SpringDemoBot;

/**
 * Простая Java-запись (record) для хранения данных о нашем тенанте (боте).
 *
 * @param botIdentifier Уникальный UUID, который мы используем в URL
 * @param botToken      Секретный токен бота от BotFather
 * @param botName       Просто имя для логов
 */


public record TenantConfig(
        String botIdentifier,
        String botToken,
        String botName
) {
}