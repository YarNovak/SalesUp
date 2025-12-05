package io.proj3ct.SpringDemoBot.model;

import io.proj3ct.SpringDemoBot.config.BotConfig;
import io.proj3ct.SpringDemoBot.repository.BotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductService {



    @Autowired
    private BotRepository botRepository;

    private final VapecomponyKatalogRepository vapecomponyKatalogRepository;

    public ProductService(VapecomponyKatalogRepository vapecomponyKatalogRepository) {
        this.vapecomponyKatalogRepository = vapecomponyKatalogRepository;
    }



}
