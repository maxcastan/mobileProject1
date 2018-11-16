package edu.fsu.cs.mobile.hw5.groupone;

public class Message {

    public String getNumber() {
        return number;
    }

    public String getMessage() {
        return message;
    }

    private String number;
    private String message;

    public Message(){}

    public Message(String number, String message){
        this.number=number;
        this.message=message;
    }
}
