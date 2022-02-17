package com.jeff_media.quizbot;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public enum Command {

    START("start","<category>"),
    LIST("list"),
    STOP("stop"),
    HELP("help"),
    ERROR(null);

    private static final String PREFIX = "-quiz";

    @Getter private final String name;
    @Getter private final String syntax;

    Command(String name, String syntax) {
        this.name = name;
        this.syntax = syntax;
    }

    Command(String name) {
        this(name,null);
    }

    @Nullable
    public static Command fromMessage(String message) {
        if(!message.toLowerCase(Locale.ROOT).startsWith(PREFIX.toLowerCase(Locale.ROOT))) return null;
        String[] split = message.split(" ");
        if(split.length==1) return HELP;
        for(Command command : values()) {
            if(command.getName() == null) continue;
            if(command.name.toLowerCase(Locale.ROOT).equals(split[1].toLowerCase(Locale.ROOT))) {
                return command;
            }
        }
        return ERROR;
    }
}
