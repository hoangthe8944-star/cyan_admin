package com.example.cyan.common.util;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

public final class TextSanitizer {

    private TextSanitizer() {
    }

    public static String cleanPlainText(String value) {
        if (value == null) {
            return null;
        }
        String sanitized = Jsoup.clean(value.trim(), Safelist.none());
        return sanitized.isBlank() ? null : sanitized;
    }

    public static String cleanRichText(String value) {
        if (value == null) {
            return null;
        }
        String sanitized = Jsoup.clean(value.trim(), Safelist.relaxed());
        return sanitized.isBlank() ? null : sanitized;
    }
}
