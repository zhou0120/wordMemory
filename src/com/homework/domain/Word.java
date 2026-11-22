package com.homework.domain;

public class Word {
    private String word;
    private String phonetic;
    private String translate;
    public Word(){}
    public Word(Word word){
        this.word=word.getWord();
        this.phonetic=word.getPhonetic();
        this.translate=word.getTranslate();
    }
    public Word(String word,String phonetic,String translate){
        this.word=word;
        this.phonetic=phonetic;
        this.translate=translate;
    }
    public String getWord() {
        return word;
    }
    public void setWord(String word) {
        this.word = word;
    }
    public String getPhonetic() {
        return phonetic;
    }
    public void setPhonetic(String phonetic) {
        this.phonetic = phonetic;
    }
    public String getTranslate() {
        return translate;
    }
    public void setTranslate(String translate) {
        this.translate = translate;
    }
}
