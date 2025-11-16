package io.proj3ct.SpringDemoBot.service;


import io.proj3ct.SpringDemoBot.Cache_my_own.CacheInitializer.CacheDev;
import io.proj3ct.SpringDemoBot.DB_entities.Bot;
import io.proj3ct.SpringDemoBot.DaO.*;
import io.proj3ct.SpringDemoBot.Dispetchers.CallbackDispatcher;
import io.proj3ct.SpringDemoBot.Dispetchers.CommandDispatcher;
import io.proj3ct.SpringDemoBot.AsyncConfig;
import io.proj3ct.SpringDemoBot.config.BotConfig;
import io.proj3ct.SpringDemoBot.dopclasses.MessageRepo.MessageRegistry;
import io.proj3ct.SpringDemoBot.model.*;
import io.proj3ct.SpringDemoBot.model.User;
import io.proj3ct.SpringDemoBot.TelegramWebhookController;
import io.proj3ct.SpringDemoBot.repository.BotRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.Optional;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AdsRepository adsRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    // @Autowired
    // private BinRepository binRepository;
    @Autowired
    private VapecomponyKatalogRepository vapecomponyKatalogRepository;
    @Autowired
    private VapecomponyRepository vapecomponyRepository;
    @Autowired
    private VapecomponyRepository vapecomponyyRepository;
    @Autowired
    private CartService cartService;
    @Autowired
    private ProductService productService;
    @Autowired
    private OrdersRepository orderRepository;
    @Autowired
    private OrderService orderService;
    @Autowired
    private FinalItemRepository finalItemRepository;
    @Autowired
    private DataSource dataSource;
    @Autowired
    private ExcelExporterViaCrudRepositories excelExporterViaRepositories;
    @Autowired
    ExelExporter exelExporter;
    @Autowired
    private Media media;
    @Autowired
    private Adres adres;

    @Autowired
    private MessageRegistry messageRegistry;





    final BotConfig config;

    @Autowired
    private Send sent;

    private final Map<Long, Boolean> pendingAdd = new HashMap<>();
    private final Map<Long, Boolean> pendingAdd2 = new HashMap<>();
    private final Map<Long, Boolean> pendingDelete = new HashMap<>();

    private final Map<Long, Boolean> pendingAdd_product = new HashMap<>();
    private final Map<Long, Boolean> pendingAdd2_product = new HashMap<>();
    private final Map<Long, Boolean> pendingAdd3_product = new HashMap<>();
    private final Map<Long, Boolean> pendingDelete_product = new HashMap<>();
   // private final Map<Long, Boolean> add_DELIVERY = new HashMap<>();
   // private final Map<Long, Boolean> wait_photo = new HashMap<>();
   // public final Map<Long, Boolean> sent = new HashMap<>();
   // private final Map<Long, Boolean> wait_id = new HashMap<>();
  //  private final Map<Long, Boolean> wait_howmuchyouhave = new HashMap<>();

   // private final Map<Long, Contact> media = new HashMap<>();
    private final Map<Long, Boolean> advertisement = new HashMap<>();
   // private final Map<Long, String> adres = new HashMap<>();


    private Vapecompony_katalog levyproduct = new Vapecompony_katalog();
    private Vapecompony levybutton = new Vapecompony();


    static final String HELP_TEXT = "Ella Spot была создана чтобы упростить и ускорить процес покупки выдающегося \n" +
            "товара SPOT.LAB\uD83D\uDE2E\u200D\uD83D\uDCA8\n" +
            "\n" +
            "\uD83D\uDFE2 № 1 По жидкостям\n" +
            "\uD83D\uDFE2 топ ассортимент\n" +
            "\uD83D\uDFE2 быстро отвечаем\n" +
            "\n" +
            "Используй  /start и начни обозревать лабораторию\uD83D\uDE1C";

    static final String YES_BUTTON = "YES_BUTTON";
    static final String ALL_KATALOG_BUTTON = "ALL_KATALOG_BUTTON";
    static final String NO_BUTTON = "NO_BUTTON";
    static final String ERROR_TEXT = "Error occurred: ";
    static final String CHASER_BUTTON = "CHASER_BUTTON";
    static final String WOZOL_BUTTON = "WOZOL_BUTTON";
    static final String SIGINAH_BUTTON = "SIGINAH_BUTTON";
    static final String PODONKI_BUTTON = "PODONKI_BUTTON";
    static final String NOVA_BUTTON = "NOVA_BUTTON";
    static final String TOLKINAH_BUTTON = "TOLKINAH_BUTTON";
    static final String CART_CLEAR = "CART_CLEAR";
    static final String CART_CHANGE = "CART_CHANGE";
    static final String SEE_CART = "SEE_CART";
    static final String PAY = "PAY";
    static final String CARD = "CARD";
    static final String CASH = "CASH";
    private static final String CURRENCY = "PLN";
    private static final String PLN = "PLN";
    private static final String GRN = "GRN";
    private static final String ACCEPT = "ACCEPT";
    private static final String DENY = "DENY";
    private static boolean work = true;

    @Autowired
    private CommandDispatcher commandDispatcher;
    @Autowired
    private CallbackDispatcher callbackDispatcher;
    @Autowired
    private ContactSendingHandler contactSendingHandler;
    @Autowired
    private Wait_id wait_id;
    @Autowired
    private DeliveryHandler deliveryHandler;
    @Autowired
    private Adddelivery add_DELIVERY;

    @Autowired
    private HowmuchyouhaveHandler howmuchyouhaveHandler;
    @Autowired
    private PhotoHandler photoHandler;
    @Autowired
    private Wait_photo wait_photo;
    @Autowired
    private BotRepository botRepository;

    @Autowired
    private CacheDev cacheDev;

    public TelegramBot(BotConfig config) {
        this.config = config;
      //  this.callbackDispatcher = callbackDispatcher;
       // this.commandDispatcher = commandDispatcher;
        List<BotCommand> listofCommands = new ArrayList<>();
        listofCommands.add(new BotCommand("/start", ""));
        // listofCommands.add(new BotCommand("/mydata", "get your data stored"));
        //  listofCommands.add(new BotCommand("/deletedata", "delete my data"));
     //   listofCommands.add(new BotCommand("/help", "информация о нас)"));
        // listofCommands.add(new BotCommand("/settings", "set your preferences"));
        //  listofCommands.add(new BotCommand("/register", "goood"));
        try {
            this.execute(new SetMyCommands(listofCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Error setting bot's command list: " + e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {

        if(update.hasMessage()){

            messageRegistry.addMessage(update.getMessage().getChatId(), update.getMessage().getMessageId());

        }

        Bot bot = botRepository.findById(Long.valueOf(config.getBoit())).get();


        

        if(bot.isActive()){
            System.out.println("22222222222222222222222222");
        }
        else{
            cacheDev.initButtonText();
            cacheDev.initMessages();

            Bot botik =  botRepository.findById(Long.valueOf(config.getBoit())).get();
            botik.setActive(true);
            botRepository.save(botik);

        }



        if( update.hasMessage() && update.getMessage().hasContact()){

            if(wait_id.getOrDefault(update.getMessage().getChatId(), false)){

                contactSendingHandler.get_contact(update, this);

            }
            else {
                contactSendingHandler.no_contact(update, this);
            }
        }

        else if (update.hasMessage() && update.getMessage().hasPhoto()){

            long chatId = update.getMessage().getChatId();
            System.out.println("02020202020202");
            if (wait_photo.getOrDefault(chatId, false)){

                photoHandler.photo_Handle(update, this);

            }



        }

        else if(update.hasMessage() && update.getMessage().hasText()) {


            if(add_DELIVERY.getOrDefault(update.getMessage().getChatId(), false)){

                try {
                    System.out.println("SOOOOOOOOOS");
                    deliveryHandler.handle_Delivery(update, this);
                }
                catch (TelegramApiException e) {
                    e.printStackTrace();
                }


                return;
            }


            commandDispatcher.dispatch(update.getMessage(), this);
        }
        else if(update.hasCallbackQuery()){
            callbackDispatcher.dispatch(update.getCallbackQuery(), this);
        }

    }

    @Scheduled(cron = "${cron.scheduler}")
    private void sendAds(){

        var ads = adsRepository.findAll();
        var users = userRepository.findAll();
        var adasd = vapecomponyyRepository.findAll();
        var asdas = vapecomponyKatalogRepository.findAll();
        for(Ads ad: ads) {
            for (User user: users){
                prepareAndSendMessage(user.getChatId(), ad.getAd());
            }
        }

    }



    @Scheduled(cron = "0 */15 * * * *")
    private void del_timeout(){

        Timestamp now = Timestamp.from(Instant.now());
        Timestamp nowMinusMin = Timestamp.from(now.toInstant().minus(1, ChronoUnit.MINUTES));
        for(User user: userRepository.findByLastUpdatedBeforeAndBot_Id(nowMinusMin, Long.valueOf(config.getBoit()))){
            if((user.getLastUpdated().before(nowMinusMin)) && (orderRepository.findByUser_ChatIdAndPaidEqualsAndBot_Id(user.getChatId(), false, Long.valueOf(config.getBoit())).isEmpty())){
                cartService.clearCart(user.getChatId());

            }
            add_DELIVERY.remove(user.getChatId());
            wait_photo.remove(user.getChatId());
            sent.remove(user.getChatId());
            wait_id.remove(user.getChatId());
            media.remove(user.getChatId());
            adres.remove(user.getChatId());


        }

    }
    @Scheduled(cron = "1 */20 * * * *")
    private void delorders_timeout(){
            /*
        Timestamp now = Timestamp.from(Instant.now());
        Timestamp nowMinusMin = Timestamp.from(now.toInstant().minus(1, ChronoUnit.MINUTES));
        for(Orders order : orderRepository.findByCreatedAtBefore(nowMinusMin)){
            if((order.getCreatedAt().before(nowMinusMin)) && (!order.isPaid())){
                cartService.clearCart(order.getUser().getChatId());
                orderRepository.delete(order);
            }
        }
             */

        for(User user : userRepository.findAll()){

            System.out.println("BEATCH");
            Optional<Orders> or = orderRepository.findByUser_ChatIdAndPaidEqualsAndBot_Id(user.getChatId(), false, Long.valueOf(config.getBoit()));
            if(or.isEmpty()){continue;}
            Orders order = or.get();


            if(order.getDelivery() == null){

                orderService.deny_paiment(order.getId(), order.getUser().getChatId());
                add_DELIVERY.remove(user.getChatId());
                wait_photo.remove(user.getChatId());
                sent.remove(user.getChatId());
                wait_id.remove(user.getChatId());
                media.remove(user.getChatId());

            }

            else if(wait_photo.getOrDefault(user.getChatId(), false)){

                orderService.deny_paiment(order.getId(), order.getUser().getChatId());
                add_DELIVERY.remove(user.getChatId());
                wait_photo.remove(user.getChatId());
                sent.remove(user.getChatId());
                wait_id.remove(user.getChatId());
                media.remove(user.getChatId());

            }



            //    orderRepository.findByUser_ChatIdAndPaidEquals(chatId, false).get().getId())
            //  orderRepository.findByUser_ChatIdAndPaidEquals(order.getUser().getChatId(), false).get().getId();

        }

    }

    private void prepareAndSendMessage(Long chatId, String ad){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId.toString());
        sendMessage.setText(ad);
        sendMessage.setParseMode("MarkdownV2");

        try {
            this.execute(sendMessage);
        }
        catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }
    private String escapeMarkdown(String text) {
        return text.replace("\\", "\\\\")  // Екрануємо зворотні слеші
                .replace("_", "\\_")
                .replace("*", "\\*")
                .replace("[", "\\[")
                .replace("]", "\\]")
                .replace("(", "\\(")
                .replace(")", "\\)")
                .replace("~", "\\~")
                .replace("`", "\\`")
                .replace(">", "\\>")
                .replace("#", "\\#")
                .replace("+", "\\+")
                .replace("-", "\\-")
                .replace("=", "\\=")
                .replace("|", "\\|")
                .replace("{", "\\{")
                .replace("}", "\\}")
                .replace(".", "\\.")
                .replace("!", "\\!");
    }


}

