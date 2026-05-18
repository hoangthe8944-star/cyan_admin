package com.example.cyan.catalog.model;

import com.example.cyan.common.model.enums.ProductOptionType;
import com.example.cyan.common.util.TextSanitizer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class VariantSelection {

    @NotNull
    private ProductOptionType optionType;

    @NotBlank
    @Size(max = 80)
    private String valueCode;

    @NotBlank
    @Size(max = 120)
    private String valueLabel;

    public ProductOptionType getOptionType() {
        return optionType;
    }

    public void setOptionType(ProductOptionType optionType) {
        this.optionType = optionType;
    }

    public String getValueCode() {
        return valueCode;
    }

    public void setValueCode(String valueCode) {
        this.valueCode = TextSanitizer.cleanPlainText(valueCode);
    }

    public String getValueLabel() {
        return valueLabel;
    }

    public void setValueLabel(String valueLabel) {
        this.valueLabel = TextSanitizer.cleanPlainText(valueLabel);
    }
}
