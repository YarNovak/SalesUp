package io.proj3ct.SpringDemoBot;

import io.proj3ct.SpringDemoBot.Cache_my_own.CacheInitializer.CacheDev;
import io.proj3ct.SpringDemoBot.DB_entities.Bot;
import io.proj3ct.SpringDemoBot.DaO.ContactSendingHandler;
import io.proj3ct.SpringDemoBot.DaO.DeliveryHandler;
import io.proj3ct.SpringDemoBot.DaO.HowmuchyouhaveHandler;
import io.proj3ct.SpringDemoBot.DaO.PhotoHandler;
import io.proj3ct.SpringDemoBot.Dispetchers.CallbackDispatcher;
import io.proj3ct.SpringDemoBot.Dispetchers.CommandDispatcher;
import io.proj3ct.SpringDemoBot.dopclasses.MessageRepo.MessageRegistry;
import io.proj3ct.SpringDemoBot.model.*;
import io.proj3ct.SpringDemoBot.repository.BotRepository;
import io.proj3ct.SpringDemoBot.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@EnableAsync
public class UpdateProcessingService {
    private static final Logger log = LoggerFactory.getLogger(UpdateProcessingService.class);

    private final TenantService tenantService;


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

    public UpdateProcessingService(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    /**
     * Вся логика бота - здесь.
     * Аннотация @Async("taskExecutor") указывает Spring
     * выполнить этот метод в отдельном потоке из пула "taskExecutor".
     */
    @Async("taskExecutor")
    public void process(Bot bot, Update update) {

        log.debug("Начало обработки (асинхронно) для: {}", bot.getName());
        AbsSender sender = tenantService.getSender(bot.getBotToken());
        /*

        // Здесь ваша "старая" логика бота
        if (update.hasMessage() && update.getMessage().hasText()) {
            long chatId = update.getMessage().getChatId();
            String text = update.getMessage().getText();

            String responseText;
            if (text.equals("/start")) {
                // Логика, специфичная для тенанта
                responseText = "Привет! Я " + bot.getName() + " (" + bot.getId() + ")";
            } else {
                responseText = "Эхо от " + bot.getName() + ": " + text;
            }

            // 1. Создаем метод
            SendMessage message = new SendMessage(String.valueOf(chatId), responseText);

            // 2. Получаем "отправителя" для этого конкретного токена
            AbsSender sender = tenantService.getSender(bot.getBotToken());
            if (sender == null) {
                log.error("Не удалось получить AbsSender для тенанта: {}", bot.getId());
                return;
            }

            // 3. Отправляем ответ
            try {
                sender.execute(message);
                log.debug("Ответ отправлен для {}", bot.getName());
            } catch (TelegramApiException e) {
                log.error("Не удалось отправить сообщение: " + e.getMessage(), e);
            }
        }
        */

        if(update.hasMessage()){

            messageRegistry.addMessage(bot.getId(), update.getMessage().getChatId(), update.getMessage().getMessageId());

        }





        if(bot.isActive()){
            System.out.println("22222222222222222222222222");
        }
        else{
            cacheDev.initButtonText();
            cacheDev.initMessages();

            bot.setActive(true);
            botRepository.save(bot);

        }



        if( update.hasMessage() && update.getMessage().hasContact()){

            if(wait_id.getOrDefault(update.getMessage().getChatId(), false, bot.getId())){

                contactSendingHandler.get_contact(update, bot.getId());

            }
            else {
                contactSendingHandler.no_contact(update, bot.getId());
            }
        }

        else if (update.hasMessage() && update.getMessage().hasPhoto()){

            long chatId = update.getMessage().getChatId();
            System.out.println("02020202020202");
            if (wait_photo.getOrDefault(chatId, false, bot.getId() )){

                photoHandler.photo_Handle(update, bot.getId());

            }



        }

        else if(update.hasMessage() && update.getMessage().hasText()) {

            if(add_DELIVERY.getOrDefault(update.getMessage().getChatId(), false,bot.getId())){

                try {
                    System.out.println("SOOOOOOOOOS");
                    deliveryHandler.handle_Delivery(update, sender, bot.getId());
                }
                catch (TelegramApiException e) {
                    e.printStackTrace();
                }


                return;
            }

            System.out.println("POIHALY");
            commandDispatcher.dispatch(update.getMessage(), bot.getId());
        }
        else if(update.hasCallbackQuery()){
            callbackDispatcher.dispatch(update.getCallbackQuery(), bot.getId());
        }

    }





    @Scheduled(cron = "0 */15 * * * *")
    public void del_timeout(){

        Timestamp now = Timestamp.from(Instant.now());
        Timestamp nowMinusMin = Timestamp.from(now.toInstant().minus(1, ChronoUnit.MINUTES));

        for(Bot bot: botRepository.findAll()){

            for(User user: userRepository.findByLastUpdatedBeforeAndBot_Id(nowMinusMin, bot.getId())){
                if((user.getLastUpdated().before(nowMinusMin)) && (orderRepository.findByUser_ChatIdAndPaidEqualsAndBot_Id(user.getChatId(), false, bot.getId()).isEmpty())){
                    cartService.clearCart(user.getChatId());

                }
                add_DELIVERY.remove(user.getChatId(), bot.getId());
                wait_photo.remove(user.getChatId(), bot.getId());
                sent.remove(user.getChatId(), bot.getId());
                wait_id.remove(user.getChatId(), bot.getId());
                media.remove(user.getChatId(), bot.getId());
                adres.remove(user.getChatId(), bot.getId());


            }

        }



    }
    @Scheduled(cron = "1 */20 * * * *")
    public void delorders_timeout(){
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

    for(Bot bot: botRepository.findAll()){

        for(User user : userRepository.findAll()){

            System.out.println("BEATCH");
            Optional<Orders> or = orderRepository.findByUser_ChatIdAndPaidEqualsAndBot_Id(user.getChatId(), false, bot.getId());
            if(or.isEmpty()){continue;}
            Orders order = or.get();


            if(order.getDelivery() == null){

                orderService.deny_paiment(order.getId(), order.getUser().getChatId());
                add_DELIVERY.remove(user.getChatId(), bot.getId());
                wait_photo.remove(user.getChatId(), bot.getId());
                sent.remove(user.getChatId(), bot.getId());
                wait_id.remove(user.getChatId(), bot.getId());
                media.remove(user.getChatId(), bot.getId());

            }

            else if(wait_photo.getOrDefault(user.getChatId(), false, bot.getId())){

                orderService.deny_paiment(order.getId(), order.getUser().getChatId());
                add_DELIVERY.remove(user.getChatId(), bot.getId());
                wait_photo.remove(user.getChatId(), bot.getId());
                sent.remove(user.getChatId(), bot.getId());
                wait_id.remove(user.getChatId(), bot.getId());
                media.remove(user.getChatId(), bot.getId());

            }



            //    orderRepository.findByUser_ChatIdAndPaidEquals(chatId, false).get().getId())
            //  orderRepository.findByUser_ChatIdAndPaidEquals(order.getUser().getChatId(), false).get().getId();

        }


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