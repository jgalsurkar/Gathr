package com.gathr.gathr;

public class GathrException extends Exception {
    public String error = "";
    public GathrException(String message){
        error = message;
    }

    @Override
    public String getMessage() {
        return error;
    }
}
