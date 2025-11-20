package io.proj3ct.SpringDemoBot.dopclasses.MessageRepo;

import io.proj3ct.SpringDemoBot.TenantService;
import io.proj3ct.SpringDemoBot.repository.BotRepository;
import lombok.Getter;
import lombok.Setter;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class MessageRegistry {

    @Autowired
    TenantService tenantService;

    private final Map<Long, Map<Long, Deque<Integer>> >messagesToDelete = new ConcurrentHashMap<>();
    private StartMessage startMessage;
    @Autowired
    private BotRepository botRepository;

    public void addMessage(Long bot_id, Long chatId, Integer messageId) {
        messagesToDelete.get(bot_id)
                .computeIfAbsent(chatId, k -> new ConcurrentLinkedDeque<>())
                .addLast(messageId);


    }


    public void clearMessages(Long chatId) {
      //  messagesToDelete.remove(chatId);
    }


    public void deleteMessagesBefore(Long bot_id, Long chatId, Integer targetMessageId, boolean inclusive) {

        AbsSender sender = tenantService.getSender(Objects.requireNonNull(botRepository.findById(bot_id).orElse(null)).getBotToken());
        Deque<Integer> deque = messagesToDelete.get(bot_id).get(chatId);
        if (deque == null || deque.isEmpty()) return;

        // идём с начала очереди (самые старые сообщения)
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
                // дальше идут более новые сообщения — их не трогаем
                break;
            }
        }

        if (deque.isEmpty()) {
            messagesToDelete.remove(chatId);
        }


    }



    public void deleteMessagesAfter(Long chatId, Integer targetMessageId, boolean inclusive, Long bot_id) {


        Deque<Integer> deque = messagesToDelete.get(bot_id).get(chatId);

        AbsSender sender;
        sender = tenantService.getSender(Objects.requireNonNull(botRepository.findById(bot_id).orElse(null)).getBotToken());
        if (deque == null || deque.isEmpty()) return;

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
                } finally {
                    deque.remove(messageId);
                }
            }
        }

        if (deque.isEmpty()) {
            messagesToDelete.remove(chatId);
        }

/*

        Deque<Integer> deque = messagesToDelete.get(bot_id).get(chatId);
        if (deque == null || deque.isEmpty()) {
           return;}

        Iterator<Integer> it = deque.descendingIterator();

        while (it.hasNext()) {
            Integer messageId = it.next();

            // якщо дійшли до цільового
            if (messageId.equals(targetMessageId)) {
                if (inclusive) {
                    try {
                        bot.execute(new DeleteMessage(chatId.toString(), messageId));
                        System.out.println("✅ Видалено повідомлення: " + messageId);
                    } catch (Exception e) {
                        System.err.println("❌ Не вдалося видалити повідомлення " + messageId + ": " + e.getMessage());
                    }
                    it.remove();
                }
                break; // далі вже не видаляємо
            }

            // усі інші (після target) видаляємо
            try {
                bot.execute(new DeleteMessage(chatId.toString(), messageId));
                System.out.println("✅ Видалено повідомлення: " + messageId);
            } catch (Exception e) {
                System.err.println("❌ Не вдалося видалити повідомлення " + messageId + ": " + e.getMessage());
            }
            it.remove();
        }

        if (deque.isEmpty()) {
            messagesToDelete.remove(chatId);
        }
*/

    }


    private final Map<Long, StartMessage> startMessages = new ConcurrentHashMap<>();

    public void registerStartMessage(Long chatId, Integer messageId) {
        /*
        startMessages.compute(chatId, (id, old) -> {
            if (old == null) {
                return new StartMessage(chatId, messageId);
            } else {
                old.setMessageId(messageId); // оновлюємо messageId
                return old;
            }
        });
         */
    }

    public StartMessage getStartMessage(Long chatId) {
     //   return startMessages.get(chatId);
        return null;
    }

    @Setter
    @Getter
    public  static class StartMessage{

        Long chatId;
        Integer messageId;
        public StartMessage(Long chatId, Integer messageId) {
            this.chatId = chatId;
            this.messageId = messageId;
        }
    }

}
