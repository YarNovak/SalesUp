package io.proj3ct.SpringDemoBot.dopclasses.Senders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.proj3ct.SpringDemoBot.Cache_my_own.CachesForDB.MessagesInf;
import io.proj3ct.SpringDemoBot.TenantService;
import io.proj3ct.SpringDemoBot.dopclasses.MessageRepo.MessageRegistry;
import io.proj3ct.SpringDemoBot.model.Vapecompony;
import io.proj3ct.SpringDemoBot.model.VapecomponyKatalogRepository;
import io.proj3ct.SpringDemoBot.model.VapecomponyRepository;
import io.proj3ct.SpringDemoBot.model.Vapecompony_katalog;
import io.proj3ct.SpringDemoBot.repository.BotRepository;
import org.apache.commons.math3.analysis.function.Abs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageCaption;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaVideo;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.ByteArrayInputStream;
import java.util.List;

@Service
public class SendWhatever {

    @Autowired
    private MessagesInf messagesinf;

    @Autowired
    private VapecomponyRepository vapecomponyRepository;

    @Autowired
    private VapecomponyKatalogRepository vapecomponyKatalogRepository;
    @Autowired
    private MessageRegistry messageRegistry;
    @Autowired
    private TenantService tenantService;
    @Autowired
    private BotRepository botRepository;


    public void sendhere_message(Long bot_id, AbsSender sender, Long chatId, String key, InlineKeyboardMarkup markupInLine, ReplyKeyboardMarkup replyKeyboardMarkup) {

            String type = "text";

            
            if(messagesinf.getMy_messages(bot_id, key).getPhoto()!=null) {type = "photo";}
            else if(messagesinf.getMy_messages(bot_id, key).getVideo()!=null) {type = "video";}

            if(type.equals("photo")) {

                SendPhoto sendPhoto = new SendPhoto();
                sendPhoto.setChatId(chatId.toString());

                ByteArrayInputStream bais = new ByteArrayInputStream(messagesinf.getMy_messages(bot_id, key).getPhoto());
                InputFile inputFile = new InputFile(bais, "image.jpg");
                sendPhoto.setPhoto(inputFile);

                sendPhoto.setCaption(messagesinf.getMessageText(bot_id, key));

                if(messagesinf.getMessageEntity(bot_id, key)!=null){
                    try {
                        ObjectMapper mapper2 = new ObjectMapper();
                        List<MessageEntity> entities = mapper2.readValue(
                                messagesinf.getMessageEntity(bot_id, key),
                                new TypeReference<List<MessageEntity>>() {}
                        );
                        sendPhoto.setCaptionEntities(entities);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                }

                if(markupInLine != null){
                    sendPhoto.setReplyMarkup(markupInLine);
                }
                else if(replyKeyboardMarkup != null){
                    sendPhoto.setReplyMarkup(replyKeyboardMarkup);
                }

                try{

                   Message m = sender.execute(sendPhoto);
                   messageRegistry.addMessage(bot_id, m.getChatId(), m.getMessageId());

                }
                catch(TelegramApiException e){
                    e.printStackTrace();
                }


            }

            else if(type.equals("video")) {

                SendVideo sendVideo = new SendVideo();
                sendVideo.setChatId(chatId.toString());

                ByteArrayInputStream bais = new ByteArrayInputStream(messagesinf.getMy_messages(bot_id, key).getVideo());
                InputFile inputFile = new InputFile(bais, "video.mp4");
                sendVideo.setVideo(inputFile);

                sendVideo.setCaption(messagesinf.getMessageText(bot_id, key));

                if(messagesinf.getMessageEntity(bot_id, key)!=null){
                    try {
                        ObjectMapper mapper2 = new ObjectMapper();
                        List<MessageEntity> entities = mapper2.readValue(
                                messagesinf.getMessageEntity(bot_id, key),
                                new TypeReference<List<MessageEntity>>() {}
                        );
                        sendVideo.setCaptionEntities(entities);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                }

                if(markupInLine != null){
                    sendVideo.setReplyMarkup(markupInLine);
                }
                else if(replyKeyboardMarkup != null){
                    sendVideo.setReplyMarkup(replyKeyboardMarkup);
                }

                try{

                    Message m = sender.execute(sendVideo);
                    messageRegistry.addMessage(bot_id, m.getChatId(), m.getMessageId());
                }
                catch(TelegramApiException e){
                    e.printStackTrace();
                }


            }

            else{

                System.out.println("KOKONAT");
                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(chatId.toString());

                sendMessage.setText(messagesinf.getMessageText(bot_id, key));

                if(messagesinf.getMessageEntity(bot_id, key)!=null){
                    try {
                        ObjectMapper mapper2 = new ObjectMapper();
                        List<MessageEntity> entities = mapper2.readValue(
                                messagesinf.getMessageEntity(bot_id, key),
                                new TypeReference<List<MessageEntity>>() {}
                        );
                        sendMessage.setEntities(entities);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                }

                if(markupInLine != null){
                    sendMessage.setReplyMarkup(markupInLine);
                }
                else if(replyKeyboardMarkup != null){
                    sendMessage.setReplyMarkup(replyKeyboardMarkup);
                }

                try{
                   Message m = sender.execute(sendMessage);
                    messageRegistry.addMessage(bot_id, m.getChatId(), m.getMessageId());
                }
                catch(TelegramApiException e){
                    e.printStackTrace();
                }
            }


    }

    public void sendhere_vapecompony(AbsSender sender, Long chatId, Long key, Long bot_id, InlineKeyboardMarkup markupInLine, ReplyKeyboardMarkup replyKeyboardMarkup){


        Vapecompony vapecompony = vapecomponyRepository.findByIdAndBot_Id(key, bot_id).orElseThrow( () -> new RuntimeException());


        String type = "text";

        if(vapecompony.getPhoto()!=null) {type = "photo";}
        else if(vapecompony.getVideo()!=null) {type = "video";}

        if(type.equals("photo")) {

            SendPhoto sendPhoto = new SendPhoto();
            sendPhoto.setChatId(chatId.toString());

            ByteArrayInputStream bais = new ByteArrayInputStream(vapecompony.getPhoto());
            InputFile inputFile = new InputFile(bais, "image.jpg");
            sendPhoto.setPhoto(inputFile);


            MessageComposer.CombinedMessage combinedMessage = MessageComposer.compose(vapecompony.getName(), vapecompony.getEntitiesJson(), vapecompony.getDescription(), vapecompony.getEntitiesDescJson(), "\n\n");

            sendPhoto.setCaption(combinedMessage.getText());
            sendPhoto.setCaptionEntities(combinedMessage.getEntities());

            if(markupInLine != null){
                sendPhoto.setReplyMarkup(markupInLine);
            }
            else if(replyKeyboardMarkup != null){
                sendPhoto.setReplyMarkup(replyKeyboardMarkup);
            }

            try{

                Message m = sender.execute(sendPhoto);
                messageRegistry.addMessage(bot_id, m.getChatId(), m.getMessageId());

            }
            catch(TelegramApiException e){
                e.printStackTrace();
            }


        }

        else if(type.equals("video")) {

            SendVideo sendVideo = new SendVideo();
            sendVideo.setChatId(chatId.toString());

            ByteArrayInputStream bais = new ByteArrayInputStream(vapecompony.getVideo());
            InputFile inputFile = new InputFile(bais, "video.mp4");
            sendVideo.setVideo(inputFile);

            MessageComposer.CombinedMessage combinedMessage = MessageComposer.compose(vapecompony.getName(), vapecompony.getEntitiesJson(), vapecompony.getDescription(), vapecompony.getEntitiesDescJson(), "\n\n");

            sendVideo.setCaption(combinedMessage.getText());
            sendVideo.setCaptionEntities(combinedMessage.getEntities());

            if(markupInLine != null){
                sendVideo.setReplyMarkup(markupInLine);
            }
            else if(replyKeyboardMarkup != null){
                sendVideo.setReplyMarkup(replyKeyboardMarkup);
            }

            try{

               Message m = sender.execute(sendVideo);
                messageRegistry.addMessage(bot_id, m.getChatId(), m.getMessageId());
            }
            catch(TelegramApiException e){
                e.printStackTrace();
            }


        }

        else{

            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId.toString());

            MessageComposer.CombinedMessage combinedMessage = MessageComposer.compose(vapecompony.getName(), vapecompony.getEntitiesJson(), vapecompony.getDescription(), vapecompony.getEntitiesDescJson(), "\n\n");

            sendMessage.setText(combinedMessage.getText());
            sendMessage.setEntities(combinedMessage.getEntities());

            if(markupInLine != null){
                sendMessage.setReplyMarkup(markupInLine);
            }
            else if(replyKeyboardMarkup != null){
                sendMessage.setReplyMarkup(replyKeyboardMarkup);
            }

            try{
               Message m = sender.execute(sendMessage);
                messageRegistry.addMessage(bot_id, m.getChatId(), m.getMessageId());
            }
            catch(TelegramApiException e){
                e.printStackTrace();
            }
        }


    }

    public void sendhere_firstAdd(Long chatId, Long key, Long bot_id, InlineKeyboardMarkup markupInLine, ReplyKeyboardMarkup replyKeyboardMarkup)
    {

        AbsSender sender = tenantService.getSender(botRepository.findById(bot_id).orElse(null).getBotToken());
        Vapecompony_katalog vapecompony = vapecomponyKatalogRepository.findByIdAndBot_Id(key, bot_id).orElseThrow( () -> new RuntimeException());


        String type = "text";

        if(vapecompony.getPhoto()!=null) {type = "photo";}
        else if(vapecompony.getVideo()!=null) {type = "video";}

        if(type.equals("photo")) {

            SendPhoto sendPhoto = new SendPhoto();
            sendPhoto.setChatId(chatId.toString());

            ByteArrayInputStream bais = new ByteArrayInputStream(vapecompony.getPhoto());
            InputFile inputFile = new InputFile(bais, "image.jpg");
            sendPhoto.setPhoto(inputFile);


            MessageComposer.CombinedMessage combinedMessage = MessageComposer.compose(vapecompony.getName(), vapecompony.getEntitiesJson(), vapecompony.getDescription(), vapecompony.getEntitiesDescJson(), "\n\n");

            sendPhoto.setCaption(combinedMessage.getText());
            sendPhoto.setCaptionEntities(combinedMessage.getEntities());

            if(markupInLine != null){
                sendPhoto.setReplyMarkup(markupInLine);
            }
            else if(replyKeyboardMarkup != null){
                sendPhoto.setReplyMarkup(replyKeyboardMarkup);
            }

            try{

              Message m =  sender.execute(sendPhoto);
                messageRegistry.addMessage(bot_id, m.getChatId(), m.getMessageId());

            }
            catch(TelegramApiException e){
                e.printStackTrace();
            }


        }

        else if(type.equals("video")) {

            SendVideo sendVideo = new SendVideo();
            sendVideo.setChatId(chatId.toString());

            ByteArrayInputStream bais = new ByteArrayInputStream(vapecompony.getVideo());
            InputFile inputFile = new InputFile(bais, "video.mp4");
            sendVideo.setVideo(inputFile);

            MessageComposer.CombinedMessage combinedMessage = MessageComposer.compose(vapecompony.getName(), vapecompony.getEntitiesJson(), vapecompony.getDescription(), vapecompony.getEntitiesDescJson(), "\n\n");

            sendVideo.setCaption(combinedMessage.getText());
            sendVideo.setCaptionEntities(combinedMessage.getEntities());

            if(markupInLine != null){
                sendVideo.setReplyMarkup(markupInLine);
            }
            else if(replyKeyboardMarkup != null){
                sendVideo.setReplyMarkup(replyKeyboardMarkup);
            }

            try{

               Message m = sender.execute(sendVideo);
                messageRegistry.addMessage(bot_id, m.getChatId(), m.getMessageId());
            }
            catch(TelegramApiException e){
                e.printStackTrace();
            }


        }

        else{

            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId.toString());

            MessageComposer.CombinedMessage combinedMessage = MessageComposer.compose(vapecompony.getName(), vapecompony.getEntitiesJson(), vapecompony.getDescription(), vapecompony.getEntitiesDescJson(), "\n\n");

            sendMessage.setText(combinedMessage.getText());
            sendMessage.setEntities(combinedMessage.getEntities());

            if(markupInLine != null){
                sendMessage.setReplyMarkup(markupInLine);
            }
            else if(replyKeyboardMarkup != null){
                sendMessage.setReplyMarkup(replyKeyboardMarkup);
            }

            try{
               Message m = sender.execute(sendMessage);
                messageRegistry.addMessage(bot_id, m.getChatId(), m.getMessageId());
            }
            catch(TelegramApiException e){
                e.printStackTrace();
            }
        }

    }

    public void edithere_firstAdd(AbsSender sender, Long chatId, Integer messageId, Long key, Long bot_id, InlineKeyboardMarkup markupInLine) {

        Vapecompony_katalog vapecompony = vapecomponyKatalogRepository
                .findByIdAndBot_Id(key, bot_id)
                .orElseThrow(() -> new RuntimeException());

        String type = "text";
        if (vapecompony.getPhoto() != null) {
            type = "photo";
        } else if (vapecompony.getVideo() != null) {
            type = "video";
        }

        MessageComposer.CombinedMessage combinedMessage = MessageComposer.compose(
                vapecompony.getName(),
                vapecompony.getEntitiesJson(),
                vapecompony.getDescription(),
                vapecompony.getEntitiesDescJson(),
                "\n\n"
        );

        if (type.equals("photo") || type.equals("video")) {

            // Для фото та відео міняємо тільки caption
            EditMessageCaption editCaption = new EditMessageCaption();
            editCaption.setChatId(chatId.toString());
            editCaption.setMessageId(messageId);
            editCaption.setCaption(combinedMessage.getText());
            editCaption.setCaptionEntities(combinedMessage.getEntities());

            if (markupInLine != null) {
                editCaption.setReplyMarkup(markupInLine);
            }

            try {
                sender.execute(editCaption);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }

        } else {

            // Для звичайного текстового повідомлення
            EditMessageText editText = new EditMessageText();
            editText.setChatId(chatId.toString());
            editText.setMessageId(messageId);
            editText.setText(combinedMessage.getText());
            editText.setEntities(combinedMessage.getEntities());

            if (markupInLine != null) {
                editText.setReplyMarkup(markupInLine);
            }

            try {
                sender.execute(editText);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendhere_readyVapecomponyKatalogMessage(Long bot_id, Vapecompony_katalog vapecompony, Long chatId, String text, String parsemode,  AbsSender sender, InlineKeyboardMarkup markupInLine, ReplyKeyboardMarkup replyKeyboardMarkup){




        String type = "text";

        if(vapecompony.getPhoto()!=null) {type = "photo";}
        else if(vapecompony.getVideo()!=null) {type = "video";}

        if(type.equals("photo")) {

            SendPhoto sendPhoto = new SendPhoto();
            sendPhoto.setChatId(chatId.toString());

            if(parsemode!=null){
                sendPhoto.setParseMode(parsemode);
            }

            ByteArrayInputStream bais = new ByteArrayInputStream(vapecompony.getPhoto());
            InputFile inputFile = new InputFile(bais, "image.jpg");
            sendPhoto.setPhoto(inputFile);


            sendPhoto.setCaption(text);

            if(markupInLine != null){
                sendPhoto.setReplyMarkup(markupInLine);
            }
            else if(replyKeyboardMarkup != null){
                sendPhoto.setReplyMarkup(replyKeyboardMarkup);
            }

            try{

               Message m = sender.execute(sendPhoto);
                messageRegistry.addMessage(bot_id, m.getChatId(), m.getMessageId());

            }
            catch(TelegramApiException e){
                e.printStackTrace();
            }


        }

        else if(type.equals("video")) {

            SendVideo sendVideo = new SendVideo();
            sendVideo.setChatId(chatId.toString());

            ByteArrayInputStream bais = new ByteArrayInputStream(vapecompony.getVideo());
            InputFile inputFile = new InputFile(bais, "video.mp4");
            sendVideo.setVideo(inputFile);

            if(parsemode!=null){
                sendVideo.setParseMode(parsemode);
            }

            sendVideo.setCaption(text);


            if(markupInLine != null){
                sendVideo.setReplyMarkup(markupInLine);
            }
            else if(replyKeyboardMarkup != null){
                sendVideo.setReplyMarkup(replyKeyboardMarkup);
            }

            try{

               Message m = sender.execute(sendVideo);
                messageRegistry.addMessage(bot_id, m.getChatId(), m.getMessageId());
            }
            catch(TelegramApiException e){
                e.printStackTrace();
            }


        }

        else{

            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId.toString());

            if(parsemode!=null){
                sendMessage.setParseMode(parsemode);
            }

            sendMessage.setText(text);

            if(markupInLine != null){
                sendMessage.setReplyMarkup(markupInLine);
            }
            else if(replyKeyboardMarkup != null){
                sendMessage.setReplyMarkup(replyKeyboardMarkup);
            }

            try{
               Message m = sender.execute(sendMessage);
                messageRegistry.addMessage(bot_id, m.getChatId(), m.getMessageId());
            }
            catch(TelegramApiException e){
                e.printStackTrace();
            }
        }





    }

    public void edithere_readyVapecomponyKatalogMessage(



            AbsSender sender,
            Vapecompony_katalog vapecompony,
            Long chatId,
            Integer messageId,
            String text,
            String parsemode,
            Long bot_id,
            InlineKeyboardMarkup markupInLine) {

        String type = "text";



        if (vapecompony.getPhoto() != null) {
            type = "photo";
        } else if (vapecompony.getVideo() != null) {
            type = "video";
        }

        if (type.equals("photo") || type.equals("video")) {
            // для фото/відео редагуємо тільки caption
            EditMessageCaption editCaption = new EditMessageCaption();
            editCaption.setChatId(chatId.toString());
            editCaption.setMessageId(messageId);
            editCaption.setCaption(text);

            if (parsemode != null) {
                editCaption.setParseMode(parsemode);
            }

            if (markupInLine != null) {
                editCaption.setReplyMarkup(markupInLine);
            }

            try {
                sender.execute(editCaption);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }

        } else {
            // для текстових повідомлень
            EditMessageText editText = new EditMessageText();
            editText.setChatId(chatId.toString());
            editText.setMessageId(messageId);
            editText.setText(text);

            if (parsemode != null) {
                editText.setParseMode(parsemode);
            }

            if (markupInLine != null) {
                editText.setReplyMarkup(markupInLine);
            }

            try {
                sender.execute(editText);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    public void edithere_emptycart(
            AbsSender sender,
            Long chatId,
            Integer messageId,
            String key,
            InlineKeyboardMarkup markupInLine,
            ReplyKeyboardMarkup replyKeyboardMarkup, Long bot_id) {

        String type = "text";

        if (messagesinf.getMy_messages(bot_id, key).getPhoto() != null) {
            type = "photo";
        } else if (messagesinf.getMy_messages(bot_id, key).getVideo() != null) {
            type = "video";
        }

        try {

                // Якщо це текст
                EditMessageText editText = new EditMessageText();
                editText.setChatId(chatId.toString());
                editText.setMessageId(messageId);
                editText.setText(messagesinf.getMessageText(bot_id, key));

                if (messagesinf.getMessageEntity(bot_id, key) != null) {
                    ObjectMapper mapper2 = new ObjectMapper();
                    List<MessageEntity> entities = mapper2.readValue(
                            messagesinf.getMessageEntity(bot_id, key),
                            new TypeReference<List<MessageEntity>>() {}
                    );
                    editText.setEntities(entities);
                }

                if (markupInLine != null) {
                    editText.setReplyMarkup(markupInLine);
                }

             sender.execute(editText);


        } catch (TelegramApiException | JsonProcessingException e) {
            e.printStackTrace();
        }
    }
/*

    public void edithere_paymentmessage(
            TelegramLongPollingBot bot,
            Long chatId,
            Integer messageId,
            String key,
            InlineKeyboardMarkup markupInLine,
            ReplyKeyboardMarkup replyKeyboardMarkup, Long bot_id) {

        String type = "text";

        if (messagesinf.getMy_messages(bot_id, key).getPhoto() != null) {
            type = "photo";
        } else if (messagesinf.getMy_messages().get(bot_id, key) != null) {
            type = "video";
        }

        try {

            // Якщо це текст
            EditMessageText editText = new EditMessageText();
            editText.setChatId(chatId.toString());
            editText.setMessageId(messageId);
            editText.setText(messagesinf.getMessageText(bot_id, key));

            if (messagesinf.getMessageEntity(bot_id, key) != null) {
                ObjectMapper mapper2 = new ObjectMapper();
                List<MessageEntity> entities = mapper2.readValue(
                        messagesinf.getMessageEntity(bot_id, key),
                        new TypeReference<List<MessageEntity>>() {}
                );
                editText.setEntities(entities);
            }

            if (markupInLine != null) {
                editText.setReplyMarkup(markupInLine);
            }

            bot.execute(editText);


        } catch (TelegramApiException | JsonProcessingException e) {
            e.printStackTrace();
        }
    }

*/


}
