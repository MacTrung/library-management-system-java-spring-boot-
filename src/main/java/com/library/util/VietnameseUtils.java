package com.library.util;

import java.text.Normalizer;

public class VietnameseUtils {
    
    public static String removeAccents(String text) {
        if (text == null) return null;
        
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{M}", "").toLowerCase();
    }
    
    public static String createSearchableText(String text) {
        if (text == null) return "";
        return removeAccents(text);
    }
}
