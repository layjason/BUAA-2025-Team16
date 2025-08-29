package com.example.lal.model.exceptions;

public class ServiceException extends Exception{
    public int errorCode;
    public ServiceException(String message) {
        super(message);
    }
}
