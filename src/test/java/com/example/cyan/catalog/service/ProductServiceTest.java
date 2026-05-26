package com.example.cyan.catalog.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.cyan.catalog.model.Product;
import com.example.cyan.catalog.model.ProductOption;
import com.example.cyan.catalog.model.ProductOptionValue;
import com.example.cyan.catalog.model.ProductVariant;
import com.example.cyan.catalog.model.VariantSelection;
import com.example.cyan.catalog.repository.CategoryRepository;
import com.example.cyan.catalog.repository.ProductRepository;
import com.example.cyan.common.exception.ResourceNotFoundException;
import com.example.cyan.common.model.MediaAsset;
import com.example.cyan.common.model.enums.MediaType;
import com.example.cyan.common.model.enums.ProductOptionType;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductService(productRepository, categoryRepository);
    }

    @Test
    void findsVariantStyleDetailUsingStyleOptionLabelAndVariantMedia() {
        Product product = new Product();
        product.setId("product-1");
        product.setName("Cyan Ring");
        product.setDescription("<p>Mo ta chung</p>");

        ProductOption styleOption = new ProductOption();
        styleOption.setType(ProductOptionType.STYLE);
        styleOption.setName("Style");

        ProductOptionValue styleValue = new ProductOptionValue();
        styleValue.setCode("style-01");
        styleValue.setLabel("Vintage Halo");
        styleOption.setValues(List.of(styleValue));
        product.setOptions(List.of(styleOption));

        ProductVariant variant = new ProductVariant();
        variant.setVariantCode("MODEL-01-STYLE-01");
        variant.setModelCode("MODEL-01");
        variant.setStyleCode("STYLE-01");
        variant.setProductName("Cyan Ring Variant");
        variant.setFullDescription("<p>Mo ta theo style</p>");

        VariantSelection selection = new VariantSelection();
        selection.setOptionType(ProductOptionType.STYLE);
        selection.setValueCode("STYLE-01");
        selection.setValueLabel("Vintage Halo");
        variant.setSelections(List.of(selection));

        MediaAsset image = new MediaAsset();
        image.setMediaType(MediaType.IMAGE);
        image.setUrl("https://cdn.example.com/style-01.jpg");
        image.setThumbnailUrl("https://cdn.example.com/style-01-thumb.jpg");
        variant.setMedia(List.of(image));
        product.setVariants(List.of(variant));

        when(productRepository.findById("product-1")).thenReturn(Optional.of(product));

        var response = productService.findVariantByStyleCode("product-1", "style-01");

        assertEquals("MODEL-01-STYLE-01", response.variantCode());
        assertEquals("STYLE-01", response.styleCode());
        assertEquals("Vintage Halo", response.name());
        assertEquals("<p>Mo ta theo style</p>", response.description());
        assertEquals(1, response.images().size());
        assertEquals("https://cdn.example.com/style-01.jpg", response.images().get(0).getUrl());
    }

    @Test
    void throwsWhenStyleCodeDoesNotMatchAnyActiveVariant() {
        Product product = new Product();
        product.setId("product-1");
        product.setName("Cyan Ring");
        product.setOptions(List.of());
        product.setVariants(List.of());

        when(productRepository.findById("product-1")).thenReturn(Optional.of(product));

        assertThrows(ResourceNotFoundException.class,
                () -> productService.findVariantByStyleCode("product-1", "style-missing"));
    }
}
