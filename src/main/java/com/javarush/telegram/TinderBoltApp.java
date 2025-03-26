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

    private String checkEmptyMessage(Update update) {
        String message = null;
        //Check input message
        if (update.hasMessage() && update.getMessage().hasText()) {
            message = update.getMessage().getText();
        }
        //Check click on button
        if (update.hasCallbackQuery()) {
            message = getCallbackQueryButtonKey();//get button value
        }
        return message;
    }

    @Override
    public void onUpdateEventReceived(Update update) {
        String prompt, text, message = null;
        message = checkEmptyMessage(update);
        switch (message) {
            case("/start"):
            {
                dialogMode = DialogMode.MAIN;
                showMainMenu(
                        "Головне меню бота", "/start",
                        "Генерація Tinder-профілю \uD83D\uDE0E", "/profile",
                        "Повідомлення для знайомства \uD83E\uDD70", "/opener",
                        "Листування від вашого імені \uD83D\uDE08", "/message",
                        "Листування із зірками", "/date",
                        "Поставити запитання чату GPT \uD83E\uDDE0", "/gpt");
                text = loadMessage("main");
                sendPhotoMessage("main");
                sendTextMessage(text);
                return;
            }
            case("/profile"):
            {
                dialogMode = DialogMode.PROFILE;
                text = loadMessage("profile");
                sendPhotoMessage("profile");
                sendTextMessage(text);
                return;
            }
            case("/opener"):
            {
                dialogMode = DialogMode.OPENER;
                text = loadMessage("opener");
                sendPhotoMessage("opener");
                sendTextMessage(text);
                return;
            }
            case("/message"):
            {
                dialogMode = DialogMode.MESSAGE;
                text = loadMessage("message");
                sendPhotoMessage("message");
                sendTextMessage(text);
                return;
            }
            case("/date"):
            {
                dialogMode = DialogMode.DATE;
                text = loadMessage("date");
                sendPhotoMessage("date");
                sendTextButtonsMessage(text,
                        "Аріана Гранде", "date_grande",
                        "Марго Роббі", "date_robbie",
                        "Зендея", "date_zendaya",
                        "Райан Гослінг", "date_gosling",
                        "Том Харді", "date_hardy");
                return;
            }
            case("/gpt"):
            {
                dialogMode = DialogMode.GPT;
                text = loadMessage("gpt");
                sendPhotoMessage("gpt");
                sendTextMessage(text);
                return;
            }
        }

        switch (dialogMode) {
            case MAIN:
            {
                return;
            }
            case PROFILE:
            {
                return;
            }
            case MESSAGE:
            {
                return;
            }
            case DATE:
            {
                if (message.startsWith("date_"))
                {
                    sendPhotoMessage(message);
                    prompt = loadPrompt(message);
                    chatGPTService.setPrompt(prompt);
                    return;
                }
                sendTextMessage(chatGPTService.addMessage(message));
                return;
            }
            case GPT:
            {
                prompt = loadPrompt("gpt");
                sendTextMessage(chatGPTService.sendMessage(prompt, message));
            }
        }

    }

    public static void main(String[] args) throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(new TinderBoltApp());
    }
}
