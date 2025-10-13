package io.proj3ct.SpringDemoBot.repository;

import io.proj3ct.SpringDemoBot.DB_entities.BotMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BotMessageRepository extends JpaRepository<BotMessage, Long> {

    boolean existsByMessageKeyAndBot_Id(final String messageKey, final Long botId);
    Optional<BotMessage> findByMessageKeyAndBot_Id(final String messageKey, final Long botId);
    List<BotMessage> findByBot_Id(Long id);

}
