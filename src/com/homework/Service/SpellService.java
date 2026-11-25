package com.homework.Service;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.homework.domain.Word;

public class SpellService {
    private List<Word> wordList;
    BufferedReader br;
    public SpellService() {

    }
    public SpellService(String filePath){
        wordList = new ArrayList<>();
        this.loadWords(filePath);
    }
    /** 直接用现成的单词列表（多个词库合并后背诵时用）。 */
    public SpellService(List<Word> words){
        this.wordList = (words == null) ? new ArrayList<>() : words;
    }
    private void loadWords(String filePath) {
        try{
            br = new BufferedReader(new FileReader(filePath));
            String line;
            while((line = br.readLine()) != null){
                String word = line.split(",")[0];
                String phonetic = line.split(",")[1];
                String translate = line.split(",")[2];
                wordList.add(new Word(word, phonetic, translate));
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if(br != null)try {br.close();} catch (IOException e) {e.printStackTrace();}
        }
    }
    public Word findWord(int index){
        return wordList.get(index);
    }
    public int getTotalCount(){
        return wordList.size();
    }
}
