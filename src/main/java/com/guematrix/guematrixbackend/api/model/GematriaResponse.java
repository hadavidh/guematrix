package com.guematrix.guematrixbackend.api.model;

import java.util.List;

public class GematriaResponse {

    private String text;
    private GematriaMethod method;
    private int value;
    private List<GematriaLetterDetail> details;

    public GematriaResponse() {
    }

    public GematriaResponse(String text,
                            GematriaMethod method,
                            int value,
                            List<GematriaLetterDetail> details) {
        this.text = text;
        this.method = method;
        this.value = value;
        this.details = details;
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

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public List<GematriaLetterDetail> getDetails() {
        return details;
    }

    public void setDetails(List<GematriaLetterDetail> details) {
        this.details = details;
    }
}