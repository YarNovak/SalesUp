package io.proj3ct.SpringDemoBot.dopclasses.MessageRepo;

import io.proj3ct.SpringDemoBot.TenantService;
import io.proj3ct.SpringDemoBot.repository.BotRepository;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

@Service
public class MessageRegistry {

    @Autowired
    private TenantService tenantService;

    @Autowired
    private BotRepository botRepository;

    /**
     * messagesToDelete:
     * botId -> (chatId -> queue of messageIds)
     */
    private final Map<Long, Map<Long, Deque<Integer>>> messagesToDelete = new ConcurrentHashMap<>();


    // ============================================================
    //                 ADD MESSAGE
    // ============================================================

    public void addMessage(Long botId, Long chatId, Integer messageId) {
        messagesToDelete
                .computeIfAbsent(botId, k -> new ConcurrentHashMap<>())
                .computeIfAbsent(chatId, k -> new ConcurrentLinkedDeque<>())
                .addLast(messageId);
    }


    // ============================================================
    //                 DELETE BEFORE
    // ============================================================

    public void deleteMessagesBefore(Long botId, Long chatId, Integer targetMessageId, boolean inclusive) {

        Map<Long, Deque<Integer>> byBot = messagesToDelete.get(botId);
        if (byBot == null) return;

        Deque<Integer> deque = byBot.get(chatId);
        if (deque == null || deque.isEmpty()) return;

        AbsSender sender = tenantService.getSender(
                botRepository.findById(botId).orElseThrow().getBotToken()
        );

        Iterator<Integer> it = deque.iterator();

        while (it.hasNext()) {
            Integer messageId = it.next();

            boolean shouldDelete = inclusive
                    ? messageId <= targetMessageId
                    : messageId < targetMessageId;

            if (shouldDelete) {
                try {
                    sender.execute(new DeleteMessage(chatId.toString(), messageId));
                    System.out.println("✅ Видалено повідомлення: " + messageId);
                } catch (Exception e) {
                    System.err.println("❌ Не вдалося видалити повідомлення " + messageId + ": " + e.getMessage());
                }
                it.remove();
            } else {
                break; // далі тільки новіші повідомлення
            }
        }

        if (deque.isEmpty()) {
            byBot.remove(chatId);
        }
    }


    // ============================================================
    //                 DELETE AFTER
    // ============================================================

    public void deleteMessagesAfter(Long chatId, Integer targetMessageId, boolean inclusive, Long botId) {

        Map<Long, Deque<Integer>> byBot = messagesToDelete.get(botId);
        if (byBot == null) return;

        Deque<Integer> deque = byBot.get(chatId);
        if (deque == null || deque.isEmpty()) return;

        AbsSender sender = tenantService.getSender(
                botRepository.findById(botId).orElseThrow().getBotToken()
        );

        // копія, бо видаляємо під час ітерації
        for (Integer messageId : List.copyOf(deque)) {

            boolean shouldDelete = inclusive
                    ? messageId >= targetMessageId
                    : messageId > targetMessageId;

            if (shouldDelete) {
                try {
                    sender.execute(new DeleteMessage(chatId.toString(), messageId));
                    System.out.println("✅ Видалено повідомлення: " + messageId);
                } catch (Exception e) {
                    System.err.println("❌ Не вдалося видалити повідомлення " + messageId + ": " + e.getMessage());
                }
                deque.remove(messageId);
            }
        }

        if (deque.isEmpty()) {
            byBot.remove(chatId);
        }
    }


    // ============================================================
    //              START MESSAGE (якщо треба)
    // ============================================================

    private final Map<Long, StartMessage> startMessages = new ConcurrentHashMap<>();

    public void registerStartMessage(Long chatId, Integer messageId) {
        startMessages.compute(chatId, (id, old) -> {
            if (old == null) {
                return new StartMessage(chatId, messageId);
            } else {
                old.setMessageId(messageId);
                return old;
            }
        });
    }

    public StartMessage getStartMessage(Long chatId) {
        return startMessages.get(chatId);
    }


    @Setter
    @Getter
    public static class StartMessage {
        private Long chatId;
        private Integer messageId;

        public StartMessage(Long chatId, Integer messageId) {
            this.chatId = chatId;
            this.messageId = messageId;
        }
    }
}
