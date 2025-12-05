package com.guematrix.guematrixbackend.api.model;


public class GematriaLetterDetail {

    private String letter;
    private int value;

    public GematriaLetterDetail() {
    }

    public GematriaLetterDetail(String letter, int value) {
        this.letter = letter;
        this.value = value;
    }

    public String getLetter() {
        return letter;
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}