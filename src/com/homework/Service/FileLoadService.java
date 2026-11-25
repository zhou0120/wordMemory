package com.homework.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.homework.domain.Word;

/**
 * 文件加载服务：负责把用户提供的词库文件读进来。
 * 词库文件格式（每行）：中文释义,音标,英文单词
 * 与 data/word1.txt 保持一致，逗号分隔，无表头行。
 */
public class FileLoadService {

    /**
     * 读取词库文件，返回解析好的单词列表。
     * 每行按英文逗号分割，最多切 3 段（防止释义里带逗号被截断）。
     * 空行和格式残缺行（不足 3 段）自动跳过。
     *
     * @param filePath 文件的绝对或相对路径
     * @return 解析出的 Word 列表；文件不存在或为空时返回空列表
     */
    public List<Word> loadWords(String filePath) {
        List<Word> words = new ArrayList<>();
        if (filePath == null || filePath.isBlank()) {
            return words;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            int lineNo = 0;
            while ((line = br.readLine()) != null) {
                lineNo++;
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }
                String[] parts = line.split(",", 3);
                if (parts.length < 3) {
                    System.err.println("[FileLoadService] 第 " + lineNo + " 行格式不对（应为 3 段），跳过: " + line);
                    continue;
                }
                words.add(new Word(parts[0].trim(), parts[1].trim(), parts[2].trim()));
            }
        } catch (IOException e) {
            System.err.println("[FileLoadService] 读文件失败: " + filePath);
            e.printStackTrace();
        }
        return words;
    }

    /**
     * 判断文件是否是合法的词库文件（非空、至少能解析出 1 个单词）。
     */
    public boolean isValidWordFile(String filePath) {
        return !loadWords(filePath).isEmpty();
    }
}
