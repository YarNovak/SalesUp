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
    private VapecomponyKatalogRepository vapecomponyKatalogRepository;

    @Transactional
    public void clearfinal_items(Long orderid, Long bot_id){

        List<FinalItem> items =finalItemRepository.findByOrder_IdAndBot_Id(orderid, bot_id);
        for(FinalItem item : items) {
            Vapecompony_katalog product = vapecomponyKatalogRepository.findByIdAndBot_Id(item.getVid(),bot_id).orElse(null);
            if(product != null) {
                product.setKilkist(product.getKilkist() + item.getQuantity());
                vapecomponyKatalogRepository.save(product);
            }

        }

        finalItemRepository.deleteAllByOrder_IdAndBot_Id(orderid, bot_id);

    }

    public void deletefromfinal_items(Long orderid, Long bot_id){

        finalItemRepository.deleteAllByOrder_IdAndBot_Id(orderid, bot_id);
    }
}
