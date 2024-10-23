package com.example.Project4.exception;

public class InformationNotFoundException extends RuntimeException{
    public InformationNotFoundException (String message){
        super(message);
    }
}
