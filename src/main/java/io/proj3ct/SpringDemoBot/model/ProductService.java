package io.proj3ct.SpringDemoBot.model;

import io.proj3ct.SpringDemoBot.config.BotConfig;
import io.proj3ct.SpringDemoBot.repository.BotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    @Autowired
    private BotConfig config;

    @Autowired
    private BotRepository botRepository;

    private final VapecomponyKatalogRepository vapecomponyKatalogRepository;

    public ProductService(VapecomponyKatalogRepository vapecomponyKatalogRepository) {
        this.vapecomponyKatalogRepository = vapecomponyKatalogRepository;
    }
    public void deleteoneitem(Long chatId, Vapecompony_katalog product) {

        var existing = vapecomponyKatalogRepository.findVapecompony_katalogByNameAndBot_Id(product.getName(), Long.valueOf(config.getBoit()));
        if (existing.isPresent()) {
            Vapecompony_katalog item = existing.get();
            item.setKilkist(item.getKilkist() - 1);
            item.setBot(botRepository.findById(Long.valueOf(config.getBoit())).get());
            vapecomponyKatalogRepository.save(item);
        }
        //else {
          //  vapecomponyKatalogRepository.save(new Vapecompony_katalog(chatId, product));
        //}


    }


}
