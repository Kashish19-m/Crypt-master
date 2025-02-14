package com.avagr.crypt;


import java.io.Serializable;

public class EncBatch implements Serializable{
    private String text;
    private String key;
    private String cip;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getKey() {
        return key;
    }

    public String getCip() {
        return cip;
    }

    public void setCip(String cip) {
        this.cip = cip;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public EncBatch(String text, String key, String cip) {
        this.text = text;
        this.key = key;
        this.cip = cip;
    }
}
