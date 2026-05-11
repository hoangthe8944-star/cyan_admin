package com.example.cyan.order.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class Address {

    @NotBlank
    @Size(max = 120)
    private String fullName;

    @NotBlank
    @Size(max = 20)
    private String phoneNumber;

    @NotBlank
    @Size(max = 255)
    private String line1;

    @Size(max = 255)
    private String line2;

    @NotBlank
    @Size(max = 120)
    private String ward;

    @NotBlank
    @Size(max = 120)
    private String district;

    @NotBlank
    @Size(max = 120)
    private String city;

    @NotBlank
    @Size(max = 120)
    private String country;

    @Size(max = 20)
    private String postalCode;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getLine1() {
        return line1;
    }

    public void setLine1(String line1) {
        this.line1 = line1;
    }

    public String getLine2() {
        return line2;
    }

    public void setLine2(String line2) {
        this.line2 = line2;
    }

    public String getWard() {
        return ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
}
