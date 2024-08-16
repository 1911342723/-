package com.java_project.reggie.common;


public class CustomException extends RuntimeException{
    public CustomException(String message){
        System.out.println(message);
    }
}
