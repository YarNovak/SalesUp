package io.proj3ct.SpringDemoBot.repository;

import io.proj3ct.SpringDemoBot.DB_entities.Bot;
import io.proj3ct.SpringDemoBot.DB_entities.PlatformUser;
import org.apache.poi.sl.draw.geom.GuideIf;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BotRepository extends JpaRepository<Bot, Long> {
    List<Bot> findByOwner(PlatformUser owner);
    Optional<Bot> findByBotToken(String botToken);
    Optional<Bot> findFirstByOwnerIsNull();
    Optional<Bot> findByBotusername(String botUsername);
   // Optional<Bot> findBy(Long botId);
    Optional<Bot> findById(Long id);
}
