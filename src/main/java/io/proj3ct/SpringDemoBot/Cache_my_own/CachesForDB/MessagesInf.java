package io.proj3ct.SpringDemoBot.Cache_my_own.CachesForDB;


import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;


import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

@Component
@Setter
@Getter
public class MessagesInf {

    private String messageKey;

    Map<Long, Map<String, Information>> my_messages = new HashMap<>();

    @Setter
    @Getter
    public static class Information{

        private byte[] photo;

        private String photoMimeType;

        private byte[] video;

        private String videoMimeType;

        private Timestamp photo_updatedAt;
        private Timestamp video_updatedAt;

        private String text;
        private String entitiesJson;

        public Information(byte[] photo, String photoMimeType, byte[] video, Timestamp photo_updatedAt, Timestamp video_updatedAt, String videoMimeType, String text, String entitiesJson) {
            this.photo = photo;
            this.photoMimeType = photoMimeType;
            this.video = video;
            this.photo_updatedAt = photo_updatedAt;
            this.video_updatedAt = video_updatedAt;
            this.videoMimeType = videoMimeType;
            this.text = text;
            this.entitiesJson = entitiesJson;

        }



    }

   public Information getMy_messages(Long bot_id, String messageKey){

        return my_messages.get(bot_id).get(messageKey);

   }

   public void setMessagesForId(Long bot_id, Map<String, Information> messages) {

        this.my_messages.put(bot_id, messages);
   }

    public String getMessageText(Long bot_id, String key){


        return this.my_messages.get(bot_id).get(key).getText();
                //this.my_messages.get(key).getText();

    }

    public String getMessageEntity(Long bot_id, String key){
        return this.my_messages.get(bot_id).get(key).getText();
        //return my_messages.get(key).getEntitiesJson();
    }







}
