package com.example.cyan.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class TextSanitizerTest {

    @Test
    void preservesWhitespaceForPlainTextDescriptions() {
        String value = "  Dong dau.\nDong sau giu nguyen.\n  ";

        String sanitized = TextSanitizer.cleanPlainTextPreserveWhitespace(value);

        assertEquals(value, sanitized);
    }

    @Test
    void preservesWhitespaceForProductShortDescription() {
        String value = "  Mo ta ngan dong 1.\nMo ta ngan dong 2.  ";

        String sanitized = TextSanitizer.cleanPlainTextPreserveWhitespace(value);

        assertEquals(value, sanitized);
    }

    @Test
    void preservesWhitespaceForRichTextDescriptions() {
        String value = "  <p>Mo ta dau tien</p>\n  <p>Mo ta thu hai</p>  ";

        String sanitized = TextSanitizer.cleanRichTextPreserveWhitespace(value);

        assertEquals(value, sanitized);
    }

    @Test
    void returnsNullWhenPreservedPlainTextIsOnlyWhitespace() {
        assertNull(TextSanitizer.cleanPlainTextPreserveWhitespace("   \n\t  "));
    }
}
