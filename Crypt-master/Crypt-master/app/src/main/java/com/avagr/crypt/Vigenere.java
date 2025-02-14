package com.avagr.crypt;

import java.util.Random;

import static java.lang.Character.toLowerCase;
import static java.lang.Character.toUpperCase;

public class Vigenere {
    private String text;
    private String key;

    public Vigenere(String text, String key) {
        this.text = text;
        this.key = key;
    }

    private String randomKey() {
        char[] chars1 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
        StringBuilder sb1 = new StringBuilder();
        Random random1 = new Random();
        for (int i = 0; i < 16; i++)
        {
            char c1 = chars1[random1.nextInt(chars1.length)];
            sb1.append(c1);
        }
        return(sb1.toString());
    }

    public EncBatch getVigText() {
        return VigenereEncrypt(text, key);
    }

    public EncBatch getVigDec() {
        return VigenereDecrypt(text, key);
    }

    private EncBatch VigenereEncrypt(String text, String key) {
        if (key.equals("")) {
            key = randomKey();
        }
        StringBuilder textres = new StringBuilder();
        for (int i = 0, j = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c >= 'A' && c <= 'Z') {
                char t = (char)((toUpperCase(c) + toUpperCase(key.charAt(j)) - 2 * 'A') % 26 + 'A');
                textres.append(t);
            } else if (c >= 'a' && c <= 'z') {
                char t = (char)((toLowerCase(c) + toLowerCase(key.charAt(j)) - 2 * 'a') % 26 + 'a');
                textres.append(t);
            } else {
                textres.append(c);
            }
            j = ++j % key.length();
        }
        EncBatch result = new EncBatch(textres.toString(), key, "Vigenere");
        return result;
    }

    private EncBatch VigenereDecrypt(String text, String key) {
        StringBuilder textres = new StringBuilder();
        for (int i = 0, j = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c >= 'A' && c <= 'Z') {
                char t = (char)((toUpperCase(c) - toUpperCase(key.charAt(j)) + 26) % 26 + 'A');
                textres.append(t);
            } else if (c >= 'a' && c <= 'z') {
                char t = (char)((toLowerCase(c) - toLowerCase(key.charAt(j)) + 26) % 26 + 'a');
                textres.append(t);
            } else {
                textres.append(c);
            }
            j = ++j % key.length();
        }
        EncBatch result = new EncBatch(textres.toString(), key, "Vigenere");
        return result;
    }
}
