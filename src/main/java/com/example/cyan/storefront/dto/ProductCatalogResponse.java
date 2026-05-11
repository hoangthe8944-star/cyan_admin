package com.example.cyan.storefront.dto;

import java.util.ArrayList;
import java.util.List;

public class ProductCatalogResponse {

    private long total;
    private List<ProductCardResponse> items = new ArrayList<>();

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<ProductCardResponse> getItems() {
        return items;
    }

    public void setItems(List<ProductCardResponse> items) {
        this.items = items;
    }
}
