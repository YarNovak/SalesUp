package io.proj3ct.SpringDemoBot.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VapecomponyRepository extends JpaRepository<Vapecompony, Long> {

    Optional<Vapecompony> findVapecomponyByName(String name);
    List<Vapecompony> findAllByBot_Id(Long id);
    Optional<Vapecompony> findByIdAndBot_Id(Long idA,Long idB);
    Optional<Vapecompony> findByNameAndBot_Id(String name,Long id);
    //  @Query("SELECT o FROM Vapecompony o WHERE o.bot.id = :botId")
    //  List<Vapecompony> findAllByBotId(@Param("botId") Long botId);
}
