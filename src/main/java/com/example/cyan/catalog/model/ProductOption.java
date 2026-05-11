package com.example.cyan.catalog.model;

import java.util.ArrayList;
import java.util.List;

import com.example.cyan.common.model.enums.ProductOptionType;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ProductOption {

    @NotNull
    private ProductOptionType type;

    @NotBlank
    @Size(max = 80)
    private String name;

    @NotEmpty
    @Valid
    private List<ProductOptionValue> values = new ArrayList<>();

    public ProductOptionType getType() {
        return type;
    }

    public void setType(ProductOptionType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ProductOptionValue> getValues() {
        return values;
    }

    public void setValues(List<ProductOptionValue> values) {
        this.values = values;
    }
}
