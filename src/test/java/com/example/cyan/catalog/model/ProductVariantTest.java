package com.example.cyan.catalog.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class ProductVariantTest {

    @Test
    void trimsCodesBeforeValidationWouldRun() {
        ProductVariant variant = new ProductVariant();

        variant.setVariantCode("  VAR-001  ");
        variant.setProductName("  Cyan Ring  ");
        variant.setFullDescription("<p>  Mo ta <strong>chi tiet</strong>  </p>");
        variant.setModelCode("  MODEL-01  ");
        variant.setStyleCode("  STYLE-01  ");

        assertEquals("VAR-001", variant.getVariantCode());
        assertEquals("Cyan Ring", variant.getProductName());
        assertEquals("<p>  Mo ta <strong>chi tiet</strong>  </p>", variant.getFullDescription());
        assertEquals("MODEL-01", variant.getModelCode());
        assertEquals("STYLE-01", variant.getStyleCode());
    }

    @Test
    void clearsBlankVariantCode() {
        ProductVariant variant = new ProductVariant();

        variant.setVariantCode("   ");

        assertNull(variant.getVariantCode());
    }

    @Test
    void keepsLargeIncomingVariantCodeUnvalidatedAtModelLevel() {
        ProductVariant variant = new ProductVariant();

        variant.setVariantCode("X".repeat(300));

        assertEquals("X".repeat(300), variant.getVariantCode());
    }
}
