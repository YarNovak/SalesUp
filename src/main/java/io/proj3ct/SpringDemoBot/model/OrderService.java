package io.proj3ct.SpringDemoBot.model;

import io.proj3ct.SpringDemoBot.config.BotConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class OrderService {
    @Autowired
    private final OrdersRepository ordersRepository;
    @Autowired
    private final CartItemRepository cartItemRepository;
    @Autowired
    private final CartService cartService;
    @Autowired
    private BotConfig config;


    @Autowired
    private FinalItemService finalItemService;


    public OrderService(OrdersRepository ordersRepository, CartItemRepository cartItemRepository, CartService cartService) {
        this.ordersRepository = ordersRepository;
        this.cartItemRepository = cartItemRepository;
        this.cartService = cartService;
    }

   public void createorderfromcart(Long chatId){



   }

   @Transactional
    public void paid(long itId, Long chatId){

        Orders order = ordersRepository.findByIdAndBot_Id(itId, Long.valueOf(config.getBoit())).orElse(null);
        order.setPaid(true);
        cartItemRepository.deleteByChatId(ordersRepository.findByIdAndBot_Id(itId, Long.valueOf(config.getBoit())).get().getUser().getChatId().longValue());

        System.out.println("(((");
        System.out.println(itId);

        ordersRepository.save(order);

    }

    @Transactional
    public void deny_paiment(long itId, Long chatId){

        Orders order = ordersRepository.findByIdAndBot_Id(itId, Long.valueOf(config.getBoit())).orElse(null);
     //   cartService.clearCart( ordersRepository.findByIdAndBot_Id(itId, Long.valueOf(config.getBoit())).get().getUser().getChatId().longValue());
        finalItemService.clearfinal_items(ordersRepository.findByIdAndBot_Id(itId, Long.valueOf(config.getBoit())).get().getId());
        ordersRepository.delete(order);

    }
    @Transactional
    public void deny_order(long itId, Long chatId){

        Orders order = ordersRepository.findByIdAndBot_Id(itId, Long.valueOf(config.getBoit())).orElse(null);
        //   cartService.clearCart( ordersRepository.findByIdAndBot_Id(itId, Long.valueOf(config.getBoit())).get().getUser().getChatId().longValue());
        finalItemService.deletefromfinal_items(ordersRepository.findByIdAndBot_Id(itId, Long.valueOf(config.getBoit())).get().getId());
        ordersRepository.delete(order);

    }
}
