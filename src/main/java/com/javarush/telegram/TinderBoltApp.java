package com.javarush.telegram;

import com.javarush.telegram.ChatGPTService;
import com.javarush.telegram.DialogMode;
import com.javarush.telegram.MultiSessionTelegramBot;
import com.javarush.telegram.UserInfo;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.ArrayList;

public class TinderBoltApp extends MultiSessionTelegramBot {
    public static final String TELEGRAM_BOT_NAME = ""; //TODO: додай ім'я бота в лапках
    public static final String TELEGRAM_BOT_TOKEN = ""; //TODO: додай токен бота в лапках
    public static final String OPEN_AI_TOKEN = ""; //TODO: додай токен ChatGPT у лапках
    public DialogMode dialogMode = DialogMode.MAIN;
    public ChatGPTService chatGPTService = new ChatGPTService(OPEN_AI_TOKEN);

    public TinderBoltApp() {
        super(TELEGRAM_BOT_NAME, TELEGRAM_BOT_TOKEN);
    }

    @Override
    public void onUpdateEventReceived(Update update) {
        String text, message = update.getMessage().getText();
        switch (message) {
            case("/start"):
            {
                dialogMode = DialogMode.MAIN;
                showMainMenu("Main menu", "/start",
                        "Profile", "/profile",
                        "Opener", "/opener",
                        "Send message", "/message",
                        "Date", "/date",
                        "ChatGPT", "/gpt");
                text = loadMessage("main");
                sendPhotoMessage("main");
                sendTextMessage(text);
                return;
            }
            case("/profile"):
            {
                dialogMode = DialogMode.PROFILE;
            }
            case("/opener"):
            {
                dialogMode = DialogMode.OPENER;
            }
            case("/message"):
            {
                dialogMode = DialogMode.MESSAGE;
            }
            case("/date"):
            {
                dialogMode = DialogMode.DATE;
            }
            case("/gpt"):
            {
                dialogMode = DialogMode.GPT;
                text = loadMessage("gpt");
                //currentMode = DialogMode.GPT;
                sendPhotoMessage("gpt");
                sendTextMessage(text);
                return;
            }
        }

        if (dialogMode == DialogMode.GPT) {
            String prompt = loadPrompt("gpt");
            sendTextMessage(chatGPTService.sendMessage(prompt, message));
            return;
        }


    }

    public static void main(String[] args) throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(new TinderBoltApp());
    }
}
