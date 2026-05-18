package com.example.cyan.common.config;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.stereotype.Component;

import com.example.cyan.catalog.model.Category;
import com.example.cyan.catalog.model.Product;
import com.example.cyan.catalog.model.ProductOption;
import com.example.cyan.catalog.model.ProductOptionValue;
import com.example.cyan.catalog.model.ProductVariant;
import com.example.cyan.catalog.model.VariantSelection;
import com.example.cyan.common.model.MediaAsset;
import com.example.cyan.common.model.SeoMetadata;
import com.example.cyan.common.util.TextSanitizer;
import com.example.cyan.content.model.Banner;
import com.example.cyan.content.model.Editorial;
import com.example.cyan.content.model.EditorialSection;
import com.example.cyan.content.model.ProductCollection;
import com.example.cyan.order.model.Address;
import com.example.cyan.order.model.CustomerInfo;
import com.example.cyan.order.model.MomoPaymentInfo;
import com.example.cyan.order.model.Order;
import com.example.cyan.order.model.OrderItem;

@Component
public class SanitizationMongoListener extends AbstractMongoEventListener<Object> {

    @Override
    public void onBeforeConvert(org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent<Object> event) {
        Object source = event.getSource();
        if (source instanceof Category category) {
            sanitizeCategory(category);
        } else if (source instanceof Product product) {
            sanitizeProduct(product);
        } else if (source instanceof Banner banner) {
            sanitizeBanner(banner);
        } else if (source instanceof Editorial editorial) {
            sanitizeEditorial(editorial);
        } else if (source instanceof ProductCollection collection) {
            sanitizeCollection(collection);
        } else if (source instanceof Order order) {
            sanitizeOrder(order);
        }
    }

    private void sanitizeCategory(Category category) {
        category.setName(TextSanitizer.cleanPlainText(category.getName()));
        category.setSlug(TextSanitizer.cleanPlainText(category.getSlug()));
        category.setDescription(TextSanitizer.cleanPlainTextPreserveWhitespace(category.getDescription()));
        sanitizeMedia(category.getCoverMedia());
        sanitizeSeo(category.getSeo());
    }

    private void sanitizeProduct(Product product) {
        product.setName(TextSanitizer.cleanPlainText(product.getName()));
        product.setSlug(TextSanitizer.cleanPlainText(product.getSlug()));
        product.setSku(TextSanitizer.cleanPlainText(product.getSku()));
        product.setShortDescription(TextSanitizer.cleanPlainText(product.getShortDescription()));
        product.setDescription(TextSanitizer.cleanRichTextPreserveWhitespace(product.getDescription()));
        product.setBrand(TextSanitizer.cleanPlainText(product.getBrand()));
        product.setMaterial(TextSanitizer.cleanPlainText(product.getMaterial()));
        product.setGemstone(TextSanitizer.cleanPlainText(product.getGemstone()));
        sanitizeMediaList(product.getGallery());
        sanitizeSeo(product.getSeo());
        sanitizeOptions(product.getOptions());
        sanitizeVariants(product.getVariants());
    }

    private void sanitizeOptions(List<ProductOption> options) {
        if (options == null) {
            return;
        }
        options.forEach(option -> {
            option.setName(TextSanitizer.cleanPlainText(option.getName()));
            if (option.getValues() != null) {
                option.getValues().forEach(this::sanitizeOptionValue);
            }
        });
    }

    private void sanitizeOptionValue(ProductOptionValue value) {
        value.setCode(TextSanitizer.cleanPlainText(value.getCode()));
        value.setLabel(TextSanitizer.cleanPlainText(value.getLabel()));
        sanitizeMedia(value.getSwatchMedia());
    }

    private void sanitizeVariants(List<ProductVariant> variants) {
        if (variants == null) {
            return;
        }
        variants.forEach(variant -> {
            variant.setVariantCode(TextSanitizer.cleanPlainText(variant.getVariantCode()));
            variant.setModelCode(TextSanitizer.cleanPlainText(variant.getModelCode()));
            variant.setStyleCode(TextSanitizer.cleanPlainText(variant.getStyleCode()));
            sanitizeMediaList(variant.getMedia());
            if (variant.getSelections() != null) {
                variant.getSelections().forEach(this::sanitizeSelection);
            }
        });
    }

    private void sanitizeSelection(VariantSelection selection) {
        selection.setValueCode(TextSanitizer.cleanPlainText(selection.getValueCode()));
        selection.setValueLabel(TextSanitizer.cleanPlainText(selection.getValueLabel()));
    }

    private void sanitizeBanner(Banner banner) {
        banner.setTitle(TextSanitizer.cleanPlainText(banner.getTitle()));
        banner.setSlug(TextSanitizer.cleanPlainText(banner.getSlug()));
        banner.setRedirectUrl(TextSanitizer.cleanPlainText(banner.getRedirectUrl()));
        banner.setCtaLabel(TextSanitizer.cleanPlainText(banner.getCtaLabel()));
        sanitizeMedia(banner.getMedia());
    }

    private void sanitizeEditorial(Editorial editorial) {
        editorial.setTitle(TextSanitizer.cleanPlainText(editorial.getTitle()));
        editorial.setSlug(TextSanitizer.cleanPlainText(editorial.getSlug()));
        editorial.setSummary(TextSanitizer.cleanPlainText(editorial.getSummary()));
        editorial.setBody(TextSanitizer.cleanRichTextPreserveWhitespace(editorial.getBody()));
        sanitizeMedia(editorial.getCoverMedia());
        sanitizeSeo(editorial.getSeo());
        if (editorial.getTopics() != null) {
            editorial.setTopics(editorial.getTopics().stream().map(TextSanitizer::cleanPlainText).toList());
        }
        if (editorial.getSections() != null) {
            editorial.getSections().forEach(this::sanitizeSection);
        }
    }

    private void sanitizeCollection(ProductCollection collection) {
        collection.setName(TextSanitizer.cleanPlainText(collection.getName()));
        collection.setSlug(TextSanitizer.cleanPlainText(collection.getSlug()));
        collection.setSummary(TextSanitizer.cleanPlainText(collection.getSummary()));
        collection.setDescription(TextSanitizer.cleanRichTextPreserveWhitespace(collection.getDescription()));
        sanitizeMedia(collection.getCoverMedia());
        sanitizeSeo(collection.getSeo());
        if (collection.getProductIds() != null) {
            collection.setProductIds(collection.getProductIds().stream()
                    .map(TextSanitizer::cleanPlainText)
                    .toList());
        }
    }

    private void sanitizeSection(EditorialSection section) {
        section.setHeading(TextSanitizer.cleanPlainText(section.getHeading()));
        section.setSubHeading(TextSanitizer.cleanPlainText(section.getSubHeading()));
        section.setContent(TextSanitizer.cleanRichTextPreserveWhitespace(section.getContent()));
        sanitizeMediaList(section.getMedia());
    }

    private void sanitizeOrder(Order order) {
        order.setOrderCode(TextSanitizer.cleanPlainText(order.getOrderCode()));
        order.setCurrency(TextSanitizer.cleanPlainText(order.getCurrency()));
        order.setNote(TextSanitizer.cleanPlainText(order.getNote()));
        sanitizeCustomer(order.getCustomer());
        sanitizeAddress(order.getShippingAddress());
        sanitizeAddress(order.getBillingAddress());
        if (order.getItems() != null) {
            order.getItems().forEach(this::sanitizeOrderItem);
        }
        sanitizeMomo(order.getMomoPayment());
    }

    private void sanitizeCustomer(CustomerInfo customer) {
        if (customer == null) {
            return;
        }
        customer.setFullName(TextSanitizer.cleanPlainText(customer.getFullName()));
        customer.setEmail(TextSanitizer.cleanPlainText(customer.getEmail()));
        customer.setPhoneNumber(TextSanitizer.cleanPlainText(customer.getPhoneNumber()));
    }

    private void sanitizeAddress(Address address) {
        if (address == null) {
            return;
        }
        address.setFullName(TextSanitizer.cleanPlainText(address.getFullName()));
        address.setPhoneNumber(TextSanitizer.cleanPlainText(address.getPhoneNumber()));
        address.setLine1(TextSanitizer.cleanPlainText(address.getLine1()));
        address.setLine2(TextSanitizer.cleanPlainText(address.getLine2()));
        address.setWard(TextSanitizer.cleanPlainText(address.getWard()));
        address.setDistrict(TextSanitizer.cleanPlainText(address.getDistrict()));
        address.setCity(TextSanitizer.cleanPlainText(address.getCity()));
        address.setCountry(TextSanitizer.cleanPlainText(address.getCountry()));
        address.setPostalCode(TextSanitizer.cleanPlainText(address.getPostalCode()));
    }

    private void sanitizeOrderItem(OrderItem item) {
        item.setProductId(TextSanitizer.cleanPlainText(item.getProductId()));
        item.setVariantCode(TextSanitizer.cleanPlainText(item.getVariantCode()));
        item.setProductName(TextSanitizer.cleanPlainText(item.getProductName()));
        item.setThumbnailUrl(TextSanitizer.cleanPlainText(item.getThumbnailUrl()));
    }

    private void sanitizeMomo(MomoPaymentInfo momoPayment) {
        if (momoPayment == null) {
            return;
        }
        momoPayment.setPartnerCode(TextSanitizer.cleanPlainText(momoPayment.getPartnerCode()));
        momoPayment.setRequestId(TextSanitizer.cleanPlainText(momoPayment.getRequestId()));
        momoPayment.setMomoOrderId(TextSanitizer.cleanPlainText(momoPayment.getMomoOrderId()));
        momoPayment.setOrderInfo(TextSanitizer.cleanPlainText(momoPayment.getOrderInfo()));
        momoPayment.setRedirectUrl(TextSanitizer.cleanPlainText(momoPayment.getRedirectUrl()));
        momoPayment.setIpnUrl(TextSanitizer.cleanPlainText(momoPayment.getIpnUrl()));
        momoPayment.setPayUrl(TextSanitizer.cleanPlainText(momoPayment.getPayUrl()));
        momoPayment.setDeeplink(TextSanitizer.cleanPlainText(momoPayment.getDeeplink()));
        momoPayment.setQrCodeUrl(TextSanitizer.cleanPlainText(momoPayment.getQrCodeUrl()));
        momoPayment.setExtraData(TextSanitizer.cleanPlainText(momoPayment.getExtraData()));
        momoPayment.setLang(TextSanitizer.cleanPlainText(momoPayment.getLang()));
        momoPayment.setMessage(TextSanitizer.cleanPlainText(momoPayment.getMessage()));
    }

    private void sanitizeSeo(SeoMetadata seo) {
        if (seo == null) {
            return;
        }
        seo.setTitle(TextSanitizer.cleanPlainText(seo.getTitle()));
        seo.setDescription(TextSanitizer.cleanPlainTextPreserveWhitespace(seo.getDescription()));
        seo.setKeywords(TextSanitizer.cleanPlainText(seo.getKeywords()));
    }

    private void sanitizeMediaList(List<MediaAsset> assets) {
        if (assets == null) {
            return;
        }
        assets.forEach(this::sanitizeMedia);
    }

    private void sanitizeMedia(MediaAsset mediaAsset) {
        if (mediaAsset == null) {
            return;
        }
        mediaAsset.setUrl(TextSanitizer.cleanPlainText(mediaAsset.getUrl()));
        mediaAsset.setThumbnailUrl(TextSanitizer.cleanPlainText(mediaAsset.getThumbnailUrl()));
        mediaAsset.setAltText(TextSanitizer.cleanPlainText(mediaAsset.getAltText()));
    }
}
