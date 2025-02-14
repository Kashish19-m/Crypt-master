package com.avagr.crypt;

import java.util.Random;

public class Caesar {
    private String text;
    private String key;

    public Caesar (String text, String key) {
        this.text = text;
        this.key = key;
    }

    public EncBatch getCaesarText () {
       return CaesarEncrypt(text, key);
    }

    public EncBatch getCaesarDec () {
        return CaesarEncrypt(text, Integer.toString(26 - Integer.parseInt(key) % 26));
    }

    private EncBatch CaesarEncrypt (String text, String key) {
        StringBuilder textres = new StringBuilder();
        Random rand = new Random();
        int keygen;
        if (key.equals("")) {
            keygen = rand.nextInt(1000000);
            key = Integer.toString(keygen);
        } else {
            keygen = Integer.parseInt(key);
        }
        int workkey = keygen % 26 + 26;
        for (char i: text.toCharArray()) {
            if (Character.isLetter(i)) {
                if (Character.isUpperCase(i)) {
                    textres.append((char) ('A' + (i - 'A' + workkey) % 26));
                } else {
                    textres.append((char) ('a' + (i - 'a' + workkey) % 26));
                }
            }
            else {
                textres.append(i);
            }
        }
        EncBatch result = new EncBatch(textres.toString(), key, "Caesar");
        return result;
    }
}
