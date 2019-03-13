package com.example.sonw.myapplication;

public class Message {

    private String body;
    private String type;
    private String address;
    private long date;
    public String getBody() {
        return body;
    }
    public void setBody(String body) {
        this.body = body;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public long getDate() {
        return date;
    }
    public void setDate(long date) {
        this.date = date;
    }
    public Message(String body, String type, String address, long date) {
        super();
        this.body = body;
        this.type = type;
        this.address = address;
        this.date = date;
    }
    @Override
    public String toString() {
        return "Message [body=" + body + ", type=" + type + ", address="
                + address + ", date=" + date + "]";
    }


}