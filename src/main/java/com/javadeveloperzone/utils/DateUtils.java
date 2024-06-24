package com.javadeveloperzone.utils;

import com.javadeveloperzone.constant.ErrorMessage;
import com.javadeveloperzone.constant.TimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;

public class DateUtils {
    private static final DateTimeFormatter dateTimeFormatter= DateTimeFormatter.ofPattern(TimeFormat.DATE_PATTERN).withResolverStyle(ResolverStyle.STRICT);
    public static LocalDate parseDate(String date){
        try {
            dateTimeFormatter.parse(date);
            return LocalDate.parse(date,dateTimeFormatter);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ErrorMessage.WRONG_DATE);
        }
    }
    public static String parseTodaysDate(){
        LocalDate currentDate = LocalDate.now();
        return currentDate.format(dateTimeFormatter);
    }

    public static Integer getHours(){
        LocalDateTime localDateTime=LocalDateTime.now();
        return localDateTime.getHour();
    }

    public static boolean compareDates(LocalDate date,LocalDate date1,boolean canBeEqual){
        if(date.isEqual(date1) && canBeEqual)
            return true;
        return date.isBefore(date1);
    }
    public static boolean compareDates(String date,String data1,boolean canBeEqual){
        return compareDates(parseDate(date),parseDate(data1),canBeEqual);
    }
    public static String getDayOfTheWeek(String date){
        LocalDate date1=parseDate(date);
        return date1.getDayOfWeek().toString();
    }
}
