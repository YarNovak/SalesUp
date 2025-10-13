package io.proj3ct.SpringDemoBot.dopclasses.Senders;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MessageComposer {

    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * Комбінує два тексти з Telegram entities у єдине повідомлення
     *
     * @param name             текст першої частини (може бути null)
     * @param entitiesJson     entities у JSON для name (може бути null)
     * @param description      текст другої частини (може бути null)
     * @param entitiesDescJson entities у JSON для description (може бути null)
     * @param separator        роздільник між ними (наприклад " ", "\n", " — " тощо; може бути null)
     * @return CombinedMessage з готовим текстом і об’єднаними entities
     */
    public static CombinedMessage compose(
            String name,
            String entitiesJson,
            String description,
            String entitiesDescJson,
            String separator
    ) {
        try {
            // гарантуємо, що всі текстові параметри не null
            String safeName = Optional.ofNullable(name).orElse("");
            String safeDesc = Optional.ofNullable(description).orElse("");
            String safeSeparator = Optional.ofNullable(separator).orElse("");

            // 1. Об’єднати текст
            String fullText = safeName + safeSeparator + safeDesc;

            // 2. Безпечно парсимо entities
            List<MessageEntity> nameEntities = parseEntitiesSafe(entitiesJson);
            List<MessageEntity> descEntities = parseEntitiesSafe(entitiesDescJson);

            // 3. Зсунути offset для description
            int shift = safeName.length() + safeSeparator.length();
            for (MessageEntity e : descEntities) {
                e.setOffset(e.getOffset() + shift);
            }

            // 4. Об’єднати списки
            List<MessageEntity> allEntities = new ArrayList<>();
            allEntities.addAll(nameEntities);
            allEntities.addAll(descEntities);

            return new CombinedMessage(fullText, allEntities);

        } catch (Exception e) {
            e.printStackTrace();
            // fallback: якщо щось пішло не так — повертаємо без entities
            String safeName = Optional.ofNullable(name).orElse("");
            String safeDesc = Optional.ofNullable(description).orElse("");
            String safeSeparator = Optional.ofNullable(separator).orElse("");
            return new CombinedMessage(safeName + safeSeparator + safeDesc, new ArrayList<>());
        }
    }






    // безпечний парсер JSON → завжди повертає список (ніколи null)
    private static List<MessageEntity> parseEntitiesSafe(String json) {
        if (json == null || json.trim().isEmpty()) {
            return new ArrayList<>();
        }
        try {
            List<MessageEntity> parsed = mapper.readValue(json, new TypeReference<List<MessageEntity>>() {});
            return parsed != null ? parsed : new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // DTO для повернення результату
    public static class CombinedMessage {
        private final String text;
        private final List<MessageEntity> entities;

        public CombinedMessage(String text, List<MessageEntity> entities) {
            this.text = text;
            this.entities = entities;
        }

        public String getText() {
            return text;
        }

        public List<MessageEntity> getEntities() {
            return entities;
        }
    }
}
