package com.example.cyan.common.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document.OutputSettings;
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

    public static String cleanPlainTextPreserveWhitespace(String value) {
        if (value == null) {
            return null;
        }
        String sanitized = Jsoup.clean(value, "", Safelist.none(), preserveWhitespaceOutputSettings());
        return sanitized.isBlank() ? null : sanitized;
    }

    public static String cleanRichText(String value) {
        if (value == null) {
            return null;
        }
        String sanitized = Jsoup.clean(value.trim(), Safelist.relaxed());
        return sanitized.isBlank() ? null : sanitized;
    }

    public static String cleanRichTextPreserveWhitespace(String value) {
        if (value == null) {
            return null;
        }
        String sanitized = Jsoup.clean(value, "", Safelist.relaxed(), preserveWhitespaceOutputSettings());
        return sanitized.isBlank() ? null : sanitized;
    }

    private static OutputSettings preserveWhitespaceOutputSettings() {
        return new OutputSettings().prettyPrint(false);
    }
}
