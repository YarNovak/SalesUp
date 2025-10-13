package io.proj3ct.SpringDemoBot.model;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FinalItemRepository extends CrudRepository<FinalItem, Long> {

    // Автоматична фільтрація по bot_id для всіх замовлень
//    @Query("SELECT o FROM FinalItem o WHERE o.bot.id = :botId")
 //   List<FinalItem> findAllByBotId(@Param("botId") Long botId);

    List<FinalItem> findByOrder_IdAndBot_Id(Long id, Long boit);

    public void deleteAllByOrder_IdAndBot_Id(Long id, Long boit);

}
