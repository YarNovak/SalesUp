package io.proj3ct.SpringDemoBot.model;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MenuRepository extends CrudRepository<Menu, Long> {

    //List<Menu> findByVapecompony_id(Long vapecompony_id);

}
