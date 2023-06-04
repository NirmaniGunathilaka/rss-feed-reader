package com.example.rssfeedreader.exceptionhandling.exceptions;

public class RSSFeedException extends Exception{

    public RSSFeedException(String message) {
        super(message);
    }

    public RSSFeedException(String message, Exception ex) {
        super(message, ex);
    }
}
