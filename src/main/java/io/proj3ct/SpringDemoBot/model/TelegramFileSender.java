package io.proj3ct.SpringDemoBot.model;

import io.proj3ct.SpringDemoBot.service.TelegramBot;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.io.File;

public class TelegramFileSender {

    public static void sendBackupExcelFile(TelegramBot sender, Long chatId, File file) {
        SendDocument sendDocument = new SendDocument();
        sendDocument.setChatId(chatId.toString());
        sendDocument.setCaption("📊 Ось актуальний бекап вашої бази даних");
        sendDocument.setDocument(new InputFile(file));

        try {
            sender.execute(sendDocument);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
