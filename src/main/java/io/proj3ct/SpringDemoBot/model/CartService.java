package io.proj3ct.SpringDemoBot.model;

import io.proj3ct.SpringDemoBot.config.BotConfig;
import io.proj3ct.SpringDemoBot.repository.BotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Service
public class CartService {

    @Autowired
    private final CartItemRepository cartRepo;
    @Autowired
    private final VapecomponyKatalogRepository vapecomponyKatalogRepository;
    @Autowired
    private final UserRepository userRepository;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    @Autowired
    private BotRepository botRepository;

    @Autowired
    BotConfig config;


    public CartService(CartItemRepository cartRepo, VapecomponyKatalogRepository vapecomponyKatalogRepository, UserRepository userRepository) {
        this.cartRepo = cartRepo;
        this.vapecomponyKatalogRepository = vapecomponyKatalogRepository;
        this.userRepository = userRepository;
    }



    public void addToCart(Long chatId, Vapecompony_katalog product) {
        var existing = cartRepo.findByChatIdAndVapecomponyKatalog_IdAndBot_Id(chatId, product.getId(), Long.valueOf(config.getBoit()));
        var se_user = userRepository.findByChatIdAndBot_Id(chatId, Long.valueOf(config.getBoit()));

        var existings = vapecomponyKatalogRepository.findVapecompony_katalogByNameAndBot_Id(product.getName(), Long.valueOf(config.getBoit()));
        Vapecompony_katalog items = existings.get();

        if(items.getKilkist() <= 0) return;

        if (existing.isPresent()) {
          //  deleteoneitem(chatId, product);
            CartItem item = existing.get();
            item.setQuantity(item.getQuantity() + 1);
            item.setBot(botRepository.findById(Long.valueOf(config.getBoit())).get());
            cartRepo.save(item);
            deleteoneitem(chatId, product);
            User user = se_user.get();




        } else {

            CartItem item = new CartItem();
            item.setChatId(chatId);
            item.setBot(botRepository.findById(Long.valueOf(config.getBoit())).get());
            item.setVapecomponyKatalog(product);
            cartRepo.save(item);
            deleteoneitem(chatId, product);
        }
    }
    public void deleteFromCart(Long chatId, Vapecompony_katalog product){

        var existing = cartRepo.findByChatIdAndVapecomponyKatalog_IdAndBot_Id(chatId, product.getId(), Long.valueOf(config.getBoit()));
        var se_user = userRepository.findByChatIdAndBot_Id(chatId, Long.valueOf(config.getBoit()));

       // var existings = vapecomponyKatalogRepository.findVapecompony_katalogByName(product.getName());
       // Vapecompony_katalog items = existings.get();


        if (existing.isPresent()) {
            //  deleteoneitem(chatId, product);
            CartItem item = existing.get();
            if(item.getQuantity() <= 0) return;
            item.setQuantity(item.getQuantity() - 1);
            if(item.getQuantity() <=0) cartRepo.delete(item);
            else  cartRepo.save(item);
            addoneitem(chatId, product);
            User user = se_user.get();




        } else {
            CartItem item = new CartItem();
            item.setChatId(chatId);
            item.setBot(botRepository.findById(Long.valueOf(config.getBoit())).get());
            item.setVapecomponyKatalog(product);
            cartRepo.save(item);
            addoneitem(chatId, product);
        }

    }




    public List<CartItem> getCart(Long chatId) {
        return cartRepo.findByChatIdAndBot_Id(chatId, Long.valueOf(config.getBoit()));
    }







    @Transactional
    public void deleteoneitem(Long chatId, Vapecompony_katalog product) {

        var existing = vapecomponyKatalogRepository.findVapecompony_katalogByNameAndBot_Id(product.getName(), Long.valueOf(config.getBoit()));
        if (existing.isPresent()) {
            Vapecompony_katalog item = existing.get();
            item.setKilkist(item.getKilkist() - 1);
            vapecomponyKatalogRepository.save(item);


        }

    }

    @Transactional
    public void addoneitem(Long chatId, Vapecompony_katalog product) {

        var existing = vapecomponyKatalogRepository.findVapecompony_katalogByNameAndBot_Id(product.getName(), Long.valueOf(config.getBoit()));
        if (existing.isPresent()) {
            Vapecompony_katalog item = existing.get();
            item.setKilkist(item.getKilkist() + 1);
            vapecomponyKatalogRepository.save(item);


        }

    }



    @Transactional
    public void clearCart(Long chatId) {


        List<CartItem> items =cartRepo.findByChatIdAndBot_Id(chatId, Long.valueOf(config.getBoit()));
        for(CartItem item : items) {
            Vapecompony_katalog product = item.getVapecomponyKatalog();
            product.setKilkist(product.getKilkist() + item.getQuantity());
            vapecomponyKatalogRepository.save(product);
        }

        cartRepo.deleteByChatId(chatId);

    }
    /*
    public void startCartCleanupJob() {
        scheduler.scheduleAtFixedRate(() -> {
            Timestamp now = Timestamp.from(Instant.now());
            Timestamp nowMinusMin = Timestamp.from(now.toInstant().minus(1, ChronoUnit.MINUTES));
            var impostors = userRepository.findByLastUpdatedBefore(nowMinusMin);

            List<User> impostor = impostors.subList(0, impostors.size()-1);
            for (User us : impostors) {
                clearCart(us.getChatId());
            }
        }, 0, 1, TimeUnit.MINUTES);
    }
    */

/*
    public void del_timeout(){

        Timestamp localtime = Timestamp.from(Instant.now());

        for(User user : userRepository.findAll()) {

           cartRepo.deleteByChatId(user.getChatId());


        }


    }

 */


}
