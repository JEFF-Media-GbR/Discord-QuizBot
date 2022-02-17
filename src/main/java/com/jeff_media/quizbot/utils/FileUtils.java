package com.jeff_media.quizbot.utils;

import com.jeff_media.quizbot.QuizBot;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Map;

public class FileUtils {

    public static InputStream getResource(String resourceName) {
        return QuizBot.class.getResourceAsStream(resourceName);
    }

    public static void saveResource(String resourceName, File target, boolean replace) {
        if(target.exists() && !replace) return;
        try {
            Files.copy(FileUtils.getResource(resourceName),target.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Could not save resource \"" + resourceName + "\" to " + target.getPath());
        }
    }

    public static Map<String,Object> loadYaml(File file) {
        Yaml yaml = new Yaml();
        try(InputStreamReader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
            return yaml.load(reader);
        } catch(IOException exception) {
            throw new RuntimeException("Could not load YAML file \"" + file.getPath() + "\"",exception);
        }
    }

}
