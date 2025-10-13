package io.proj3ct.SpringDemoBot.model;

import org.apache.xmlbeans.impl.xb.xsdschema.Attribute;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import io.proj3ct.SpringDemoBot.model.User;

public interface UserRepository extends CrudRepository<User, Long> {
    //List<User> findBylastUpdated(LocalDateTime time);

    Optional<User> findByChatIdAndBot_Id(Long chatId, Long botId);
    List<User> findByLastUpdatedBeforeAndBot_Id(Timestamp lastUpdated, Long botId);

    boolean existsByChatIdAndBot_Id(Long chatId, Long botId);

    // Автоматична фільтрація по bot_id для всіх замовлень
 //   @Query("SELECT o FROM users o WHERE o.bot.id = :botId")
  //  List<User> findAllByBotId(@Param("botId") Long botId);



}
