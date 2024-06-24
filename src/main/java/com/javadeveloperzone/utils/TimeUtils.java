package com.javadeveloperzone.utils;

import com.javadeveloperzone.constant.ErrorMessage;
import com.javadeveloperzone.constant.TimeFormat;
import org.springframework.http.HttpStatus;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoUnit;

public class TimeUtils {
    private static final DateTimeFormatter dateTimeFormatter= DateTimeFormatter.ofPattern(TimeFormat.TIME_PATTERN).withResolverStyle(ResolverStyle.STRICT);

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
            ExceptionUtils.sendMessage(HttpStatus.BAD_REQUEST, ErrorMessage.WRONG_TIME);
        }
        return null;
    }
}
