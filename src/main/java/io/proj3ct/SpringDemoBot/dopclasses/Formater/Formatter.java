package io.proj3ct.SpringDemoBot.dopclasses.Formater;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;

import java.util.ArrayList;
import java.util.List;

@Service
public class Formatter {

    public static class FormattedMessage {
        private final String text;
        private final List<MessageEntity> entities;

        public FormattedMessage(String text, List<MessageEntity> entities) {
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

    public static FormattedMessage formatEntity(String name, String entitiesJson, int count, double cost) {
        // Формуємо текст (БЕЗ лапок!)
        String formula = name + " * " + count + " = " + cost;
        String text = name + "\n" + formula;

        List<MessageEntity> entities = new ArrayList<>();

        // 1. blockquote для формули
        int formulaOffset = text.indexOf(formula);
        MessageEntity blockquote = new MessageEntity();
        blockquote.setType("blockquote");
        blockquote.setOffset(formulaOffset);
        blockquote.setLength(formula.length());
        entities.add(blockquote);

        // 2. Стилі для name
        List<String> nameStyles = parseEntitiesJson(entitiesJson);
        int index = text.indexOf(name);
        while (index != -1) {
            for (String style : nameStyles) {
                MessageEntity entity = new MessageEntity();
                entity.setType(style);
                entity.setOffset(index);
                entity.setLength(name.length());
                entities.add(entity);
            }
            index = text.indexOf(name, index + name.length());
        }

        // 3. cost — жирний + підкреслений
        String costStr = String.valueOf(cost);
        int costStart = text.lastIndexOf(costStr);
        if (costStart != -1) {
            MessageEntity underlineCost = new MessageEntity();
            underlineCost.setType("underline");
            underlineCost.setOffset(costStart);
            underlineCost.setLength(costStr.length());
            entities.add(underlineCost);

            MessageEntity boldCost = new MessageEntity();
            boldCost.setType("bold");
            boldCost.setOffset(costStart);
            boldCost.setLength(costStr.length());
            entities.add(boldCost);
        }

        return new FormattedMessage(text, entities);
    }

    private static List<String> parseEntitiesJson(String entitiesJson) {
        List<String> styles = new ArrayList<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            List<EntityDef> defs = mapper.readValue(entitiesJson, new TypeReference<List<EntityDef>>() {});
            for (EntityDef def : defs) {
                if (def.type != null) {
                    styles.add(def.type);
                }
            }
        } catch (Exception e) {
            // якщо JSON порожній або некоректний
        }
        return styles;
    }

    private static class EntityDef {
        public String type;
    }

    public static void main(String[] args) {
        String name = "Apple";
        String entitiesJson = "[{\"type\":\"bold\"},{\"type\":\"italic\"}]"; // кілька стилів
        int count = 5;
        double cost = 12.50;

        FormattedMessage fm = formatEntity(name, entitiesJson, count, cost);

        System.out.println("Text: " + fm.getText());
        fm.getEntities().forEach(e ->
                System.out.println("Entity: " + e.getType() + " offset=" + e.getOffset() + " len=" + e.getLength())
        );
    }
}
