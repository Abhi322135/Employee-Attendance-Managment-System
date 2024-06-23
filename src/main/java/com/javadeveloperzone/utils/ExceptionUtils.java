package com.javadeveloperzone.utils;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ExceptionUtils {
    public static void sendMessage(HttpStatus status,String message){
        throw new ResponseStatusException(status,message);
    }
    public static void sendMessage(String message){
        throw new RuntimeException(message);
    }
}
