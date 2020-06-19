package com.example.fireconnect;

public class Message {

    private String author;
    private String text;
    private Boolean isLink = false;

    public Message(String author, String text){
        this.author = author;
        this.text = text;
    }

    public Message(String author, String text, boolean isLink){
        this.author = author;
        this.text = text;
        this.isLink = isLink;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setIsLink(Boolean isLink){
        this.isLink = isLink;
    }

    public Boolean isDownloadLink(){
        return isLink;
    }
}
