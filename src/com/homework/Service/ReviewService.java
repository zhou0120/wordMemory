package com.homework.Service;

import com.homework.domain.Word;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 错题复习服务：负责读写 data/WrongWord.txt。
 *
 * 文件格式统一为逗号分隔，和词库文件保持一致：中文释义,音标,英文单词
 * 例如：苹果,/ˈæpl/,apple
 *
 * 兼容说明：早期写入的是空格分隔（苹果 /ˈæpl/ apple），
 * 读取时会先按逗号切，切不出 3 段再退回按空白切，老文件也能继续读。
 */
public class ReviewService {

    /** 错题文件路径：写和读都用这一个常量，避免两边路径不一致。 */
    static final String WRONG_WORD_FILE = "data/WrongWord.txt";

    public ReviewService() {
        // 构造方法不再打开文件流：读取时才开、读完就关，
        // 否则文件句柄一直被占用，Windows 上还会导致文件改不动。
    }

    /** 打印当前错题集（你原来的调试入口，保留）。 */

    public List<Word> LoadWrongWords() {
        List<Word> wordList = new ArrayList<>();
        File file = new File(WRONG_WORD_FILE);
        if (!file.exists()) {
            return wordList;                     // 还没背错过单词，正常返回空列表
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Word w = parseLine(line);
                if (w != null) {
                    wordList.add(w);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("读取错题文件失败: " + file.getAbsolutePath(), e);
        }
        return wordList;
    }

    /**
     * 把一行解析成 Word，解析不出来返回 null（调用方负责跳过）。
     * 先按逗号切（新格式），不行再按空白切（兼容旧的空格格式）。
     */
    private Word parseLine(String line) {
        if (line == null) {
            return null;
        }
        line = line.trim();
        if (line.isEmpty()) {
            return null;
        }

        String[] parts = line.split(",", 3);     // 新格式：中文,音标,英文
        if (parts.length < 3) {
            parts = line.split("\\s+", 3);       // 老格式：中文 音标 英文（\s+ 容忍多个空格）
        }
        if (parts.length < 3) {
            System.err.println("[ReviewService] 格式不对，跳过: " + line);
            return null;
        }
        return new Word(parts[0].trim(), parts[1].trim(), parts[2].trim());
    }

    /**
     * 追加一条错题（写和读统一用逗号分隔）。
     * 每次写完立即关流，不用像以前那样在窗口里一直挂着 BufferedWriter。
     */
    public void saveWrongWord(Word word) {
        if (word == null) {
            return;
        }
        File dir = new File(WRONG_WORD_FILE).getParentFile();
        if (dir != null && !dir.exists() && !dir.mkdirs()) {
            System.err.println("[ReviewService] 创建目录失败: " + dir.getAbsolutePath());
            return;
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(WRONG_WORD_FILE, true))) {
            bw.write(word.getWord() + "," + word.getPhonetic() + "," + word.getTranslate());
            bw.newLine();                        // 用系统换行符，避免手动拼 "\n"
        } catch (IOException e) {
            System.err.println("[ReviewService] 写入错题失败: " + e.getMessage());
        }
    }

    public boolean clearWrongWord() {
        File file = new File(WRONG_WORD_FILE);
        File dir = file.getParentFile();
        if (dir != null && !dir.exists() && !dir.mkdirs()) {
            System.err.println("[ReviewService] 创建目录失败: " + dir.getAbsolutePath());
            return false;
        }
        try (FileWriter fw = new FileWriter(file, false)) {
            fw.write("");                    // 显式写空串，语义更清楚
            return true;
        } catch (IOException e) {
            System.err.println("[ReviewService] 清空错题失败: " + e.getMessage());
            return false;
        }
    }
}
