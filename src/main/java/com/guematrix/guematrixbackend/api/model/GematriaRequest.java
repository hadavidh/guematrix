package com.guematrix.guematrixbackend.api.model;


public class GematriaRequest {

    private String text;
    private GematriaMethod method;

    public GematriaRequest() {
    }

    public GematriaRequest(String text, GematriaMethod method) {
        this.text = text;
        this.method = method;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public GematriaMethod getMethod() {
        return method;
    }

    public void setMethod(GematriaMethod method) {
        this.method = method;
    }
}