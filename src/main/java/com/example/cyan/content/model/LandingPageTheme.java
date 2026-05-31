package com.example.cyan.content.model;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.example.cyan.common.model.BaseDocument;
import com.example.cyan.common.model.MediaAsset;
import com.example.cyan.common.model.SeoMetadata;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Document("landing_page_themes")
public class LandingPageTheme extends BaseDocument {

    @NotBlank
    @Size(max = 180)
    private String name;

    @Indexed(unique = true)
    @Size(max = 200)
    private String slug;

    @Size(max = 500)
    private String description;

    private boolean active;

    @Size(max = 180)
    private String heroTitle;

    @Size(max = 500)
    private String heroSubtitle;

    @Valid
    private MediaAsset heroMedia;

    @Valid
    private ThemePalette palette = new ThemePalette();

    @Valid
    private ThemeTypography typography = new ThemeTypography();

    @Valid
    private ThemeButtonStyle buttonStyle = new ThemeButtonStyle();

    @Size(max = 20_000)
    private String customCss;

    @Valid
    private SeoMetadata seo;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getHeroTitle() {
        return heroTitle;
    }

    public void setHeroTitle(String heroTitle) {
        this.heroTitle = heroTitle;
    }

    public String getHeroSubtitle() {
        return heroSubtitle;
    }

    public void setHeroSubtitle(String heroSubtitle) {
        this.heroSubtitle = heroSubtitle;
    }

    public MediaAsset getHeroMedia() {
        return heroMedia;
    }

    public void setHeroMedia(MediaAsset heroMedia) {
        this.heroMedia = heroMedia;
    }

    public ThemePalette getPalette() {
        return palette;
    }

    public void setPalette(ThemePalette palette) {
        this.palette = palette;
    }

    public ThemeTypography getTypography() {
        return typography;
    }

    public void setTypography(ThemeTypography typography) {
        this.typography = typography;
    }

    public ThemeButtonStyle getButtonStyle() {
        return buttonStyle;
    }

    public void setButtonStyle(ThemeButtonStyle buttonStyle) {
        this.buttonStyle = buttonStyle;
    }

    public String getCustomCss() {
        return customCss;
    }

    public void setCustomCss(String customCss) {
        this.customCss = customCss;
    }

    public SeoMetadata getSeo() {
        return seo;
    }

    public void setSeo(SeoMetadata seo) {
        this.seo = seo;
    }

    public static class ThemePalette {

        @Size(max = 32)
        private String primaryColor;

        @Size(max = 32)
        private String secondaryColor;

        @Size(max = 32)
        private String accentColor;

        @Size(max = 32)
        private String backgroundColor;

        @Size(max = 32)
        private String surfaceColor;

        @Size(max = 32)
        private String textColor;

        public String getPrimaryColor() {
            return primaryColor;
        }

        public void setPrimaryColor(String primaryColor) {
            this.primaryColor = primaryColor;
        }

        public String getSecondaryColor() {
            return secondaryColor;
        }

        public void setSecondaryColor(String secondaryColor) {
            this.secondaryColor = secondaryColor;
        }

        public String getAccentColor() {
            return accentColor;
        }

        public void setAccentColor(String accentColor) {
            this.accentColor = accentColor;
        }

        public String getBackgroundColor() {
            return backgroundColor;
        }

        public void setBackgroundColor(String backgroundColor) {
            this.backgroundColor = backgroundColor;
        }

        public String getSurfaceColor() {
            return surfaceColor;
        }

        public void setSurfaceColor(String surfaceColor) {
            this.surfaceColor = surfaceColor;
        }

        public String getTextColor() {
            return textColor;
        }

        public void setTextColor(String textColor) {
            this.textColor = textColor;
        }
    }

    public static class ThemeTypography {

        @Size(max = 120)
        private String headingFontFamily;

        @Size(max = 120)
        private String bodyFontFamily;

        @Size(max = 32)
        private String baseFontSize;

        @Size(max = 32)
        private String headingScale;

        public String getHeadingFontFamily() {
            return headingFontFamily;
        }

        public void setHeadingFontFamily(String headingFontFamily) {
            this.headingFontFamily = headingFontFamily;
        }

        public String getBodyFontFamily() {
            return bodyFontFamily;
        }

        public void setBodyFontFamily(String bodyFontFamily) {
            this.bodyFontFamily = bodyFontFamily;
        }

        public String getBaseFontSize() {
            return baseFontSize;
        }

        public void setBaseFontSize(String baseFontSize) {
            this.baseFontSize = baseFontSize;
        }

        public String getHeadingScale() {
            return headingScale;
        }

        public void setHeadingScale(String headingScale) {
            this.headingScale = headingScale;
        }
    }

    public static class ThemeButtonStyle {

        @Size(max = 32)
        private String borderRadius;

        @Size(max = 32)
        private String paddingY;

        @Size(max = 32)
        private String paddingX;

        @Size(max = 32)
        private String shadow;

        public String getBorderRadius() {
            return borderRadius;
        }

        public void setBorderRadius(String borderRadius) {
            this.borderRadius = borderRadius;
        }

        public String getPaddingY() {
            return paddingY;
        }

        public void setPaddingY(String paddingY) {
            this.paddingY = paddingY;
        }

        public String getPaddingX() {
            return paddingX;
        }

        public void setPaddingX(String paddingX) {
            this.paddingX = paddingX;
        }

        public String getShadow() {
            return shadow;
        }

        public void setShadow(String shadow) {
            this.shadow = shadow;
        }
    }
}
