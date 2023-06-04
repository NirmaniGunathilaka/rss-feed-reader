package com.example.rssfeedreader.exceptions;

public class RSSException extends Exception{

    public RSSException(String message) {
        super(message);
    }

    public RSSException(String message, Exception ex) {
        super(message, ex);
    }
}
