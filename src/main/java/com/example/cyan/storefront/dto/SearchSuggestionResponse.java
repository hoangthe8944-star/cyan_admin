package com.example.cyan.storefront.dto;

import java.util.ArrayList;
import java.util.List;

public class SearchSuggestionResponse {

    private String keyword;
    private List<String> keywordSuggestions = new ArrayList<>();
    private List<ProductCardResponse> productSuggestions = new ArrayList<>();

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public List<String> getKeywordSuggestions() {
        return keywordSuggestions;
    }

    public void setKeywordSuggestions(List<String> keywordSuggestions) {
        this.keywordSuggestions = keywordSuggestions;
    }

    public List<ProductCardResponse> getProductSuggestions() {
        return productSuggestions;
    }

    public void setProductSuggestions(List<ProductCardResponse> productSuggestions) {
        this.productSuggestions = productSuggestions;
    }
}
