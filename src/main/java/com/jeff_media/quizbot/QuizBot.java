package com.jeff_media.quizbot;

import com.jeff_media.quizbot.data.Game;
import com.jeff_media.quizbot.utils.FileUtils;
import com.sun.nio.file.ExtendedCopyOption;
import lombok.Getter;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.yaml.snakeyaml.Yaml;

import javax.security.auth.login.LoginException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;

public class QuizBot {

    @Getter private static QuizBot instance;
    @Getter private final JDA jda;
    @Getter private Map<String,Object> config;
    @Getter GameManager gameManager;

    public void test() {
        Game game = Game.fromFile("asd",new File("categories","spigot.yml"),0);
        System.out.println(game);
    }

    public QuizBot() {
        instance = this;
        saveDefaultConfig();
        config = loadConfig();
        try {
            jda = JDABuilder.createDefault((String) config.get("bot-token")).build();
        } catch (LoginException e) {
            throw new RuntimeException("Could not connect to Discord",e);
        }
        gameManager = new GameManager();
        jda.addEventListener(gameManager);
    }

    private void saveDefaultConfig() {
        FileUtils.saveResource("/config.yml",new File("config.yml"),false);
    }

    private Map<String,Object> loadConfig() {
        try {
            return new Yaml().load(new FileReader("config.yml",StandardCharsets.UTF_8));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Could not read config.yml");
        } catch (IOException e) {
            throw new RuntimeException("Could not load Charset UTF8");
        }
    }


}
