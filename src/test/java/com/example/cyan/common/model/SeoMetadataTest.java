package com.example.cyan.common.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import org.junit.jupiter.api.Test;

class SeoMetadataTest {

    @Test
    void acceptsKeywordListFromRequestStylePayload() {
        SeoMetadata seo = new SeoMetadata();

        seo.setKeywords(List.of("rose quartz", "silver", "cloud"));

        assertEquals("rose quartz, silver, cloud", seo.getKeywords());
    }

    @Test
    void trimsAndDropsBlankKeywordEntries() {
        SeoMetadata seo = new SeoMetadata();

        seo.setKeywords(List.of(" rose quartz ", "", "  ", "silver"));

        assertEquals("rose quartz, silver", seo.getKeywords());
    }

    @Test
    void clearsKeywordsWhenListIsEmpty() {
        SeoMetadata seo = new SeoMetadata();

        seo.setKeywords(List.of());

        assertNull(seo.getKeywords());
    }
}
