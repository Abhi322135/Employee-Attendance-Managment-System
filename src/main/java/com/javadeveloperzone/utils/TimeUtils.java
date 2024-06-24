package com.javadeveloperzone.utils;

import org.springframework.http.HttpStatus;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoUnit;

public class TimeUtils {
    private static final DateTimeFormatter dateTimeFormatter= DateTimeFormatter.ofPattern("HH.mm.ss").withResolverStyle(ResolverStyle.STRICT);

    public static String formatTime(LocalTime time){
        return time.format(dateTimeFormatter);
    }

    public static Long timeDifference(LocalTime time,LocalTime time2){
        return ChronoUnit.SECONDS.between(time,time2);
    }

    public static LocalTime parseTime(String time){
        try {
            return LocalTime.parse(time,dateTimeFormatter);
        } catch (Exception e) {
            ExceptionUtils.sendMessage(HttpStatus.BAD_REQUEST,"The requested time is not in correct format");
        }
        return null;
    }
}
