package io.proj3ct.SpringDemoBot.Cache_my_own.CachesForDB;


import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.CriteriaBuilder;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

@Component
@Setter
@Getter
public class MessagesInf {

    private String messageKey;

    Map<String, Information> my_messages = new HashMap<>();

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

    public String getMessageText(String key){


        return my_messages.get(key).getText();

    }

    public String getMessageEntity(String key){
        return my_messages.get(key).getEntitiesJson();
    }







}
