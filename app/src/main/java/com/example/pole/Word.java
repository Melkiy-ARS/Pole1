package com.example.pole;

public class Word {

    private String word;
    private String description;

    public Word(String loop){
        this.word = loop.substring(0,loop.indexOf("@"));
        this.description = loop.substring(loop.indexOf("@")+1);
    }

    public String getWord() {
        return word;
    }

    public String getDescription() {
        return description;
    }

}
