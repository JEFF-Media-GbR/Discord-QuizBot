package com.jeff_media.quizbot;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public enum Command {

    START("start"),
    STOP("stop"),
    HELP(null),
    ERROR(null);

    private static final String PREFIX = "-quiz";

    @Getter private final String name;

    Command(String name) {
        this.name=name;
    }

    @Nullable
    public Command fromMessage(String message) {
        if(!message.toLowerCase(Locale.ROOT).startsWith(PREFIX.toLowerCase(Locale.ROOT))) return null;
        String[] split = message.split(" ");
        if(split.length==1) return HELP;
        for(Command command : values()) {
            if(command.name.toLowerCase(Locale.ROOT).equals(split[1].toLowerCase(Locale.ROOT))) {
                return command;
            }
        }
        return ERROR;
    }
}
