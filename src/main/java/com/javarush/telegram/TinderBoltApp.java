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
import java.util.List;

public class TinderBoltApp extends MultiSessionTelegramBot {
    public static final String TELEGRAM_BOT_NAME = ""; //TODO: додай ім'я бота в лапках
    public static final String TELEGRAM_BOT_TOKEN = ""; //TODO: додай токен бота в лапках
    public static final String OPEN_AI_TOKEN = ""; //TODO: додай токен ChatGPT у лапках
    public DialogMode dialogMode = DialogMode.MAIN;
    public ChatGPTService chatGPTService = new ChatGPTService(OPEN_AI_TOKEN);
    private String telegram_message = null;
    private List<String> array_message;


    public TinderBoltApp() {
        super(TELEGRAM_BOT_NAME, TELEGRAM_BOT_TOKEN);
    }


    @Override
    public void onUpdateEventReceived(Update update) {
        String prompt, text;
        telegram_message = getMessageText();
        switch (telegram_message) {
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
                sendTextButtonsMessage(text,
                        "Запросити на побачення", "message_date",
                        "Відповісти на повідомлення", "message_next");
                array_message = new ArrayList<>();
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
                String querry  = getCallbackQueryButtonKey();
                if(querry.startsWith("message_"))
                {
                    prompt = loadPrompt(querry);
                    String history_chat = String.join("/n/n", array_message);
                    Message msg = sendTextMessage("Обробляю твій запит........");
                    updateTextMessage(msg, chatGPTService.sendMessage(prompt, history_chat));
                }
                array_message.add(telegram_message);
                return;
            }
            case DATE:
            {
                String querry  = getCallbackQueryButtonKey();
                if (querry.startsWith("date_"))
                {
                    sendPhotoMessage(querry);
                    prompt = loadPrompt(querry);
                    chatGPTService.setPrompt(prompt);
                    return;
                }
                Message msg = sendTextMessage("Формую відповідь........");
                updateTextMessage(msg, chatGPTService.addMessage(telegram_message));
                return;
            }
            case GPT:
            {
                prompt = loadPrompt("gpt");
                sendTextMessage(chatGPTService.sendMessage(prompt, telegram_message));
            }
        }

    }

    public static void main(String[] args) throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(new TinderBoltApp());
    }
}
