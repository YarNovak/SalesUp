package io.proj3ct.SpringDemoBot.model;

import io.proj3ct.SpringDemoBot.config.BotConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class FinalItemService {

    @Autowired
    private FinalItemRepository finalItemRepository;

    @Autowired
    private BotConfig config;

    @Autowired
    private VapecomponyKatalogRepository vapecomponyKatalogRepository;

    @Transactional
    public void clearfinal_items(Long orderid){

        List<FinalItem> items =finalItemRepository.findByOrder_IdAndBot_Id(orderid, Long.valueOf(config.getBoit()));
        for(FinalItem item : items) {
            Vapecompony_katalog product = vapecomponyKatalogRepository.findByIdAndBot_Id(item.getVid(),Long.valueOf(config.getBoit())).orElse(null);
            if(product != null) {
                product.setKilkist(product.getKilkist() + item.getQuantity());
                vapecomponyKatalogRepository.save(product);
            }

        }

        finalItemRepository.deleteAllByOrder_IdAndBot_Id(orderid, Long.valueOf(config.getBoit()));

    }

    public void deletefromfinal_items(Long orderid){

        finalItemRepository.deleteAllByOrder_IdAndBot_Id(orderid, Long.valueOf(config.getBoit()));
    }
}
