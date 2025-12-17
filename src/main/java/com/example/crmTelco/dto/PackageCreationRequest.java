package com.example.crmTelco.dto;

import com.example.crmTelco.entity.Package;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PackageCreationRequest {
    
    private String name;
    private String description;
    private BigDecimal price;
    private Integer dataLimitGB;
    private Integer voiceMinutes;
    private Integer smsCount;
    private Package.PackageType packageType;
    private Package.PaymentStatus paymentStatus;
    private boolean sendInvoice = true;
    
    public PackageCreationRequest(String name, String description, BigDecimal price, 
                                 Integer dataLimitGB, Integer voiceMinutes, Integer smsCount, 
                                 Package.PackageType packageType, Package.PaymentStatus paymentStatus) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.dataLimitGB = dataLimitGB;
        this.voiceMinutes = voiceMinutes;
        this.smsCount = smsCount;
        this.packageType = packageType;
        this.paymentStatus = paymentStatus;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getDataLimitGB() {
        return dataLimitGB;
    }

    public void setDataLimitGB(Integer dataLimitGB) {
        this.dataLimitGB = dataLimitGB;
    }

    public Integer getVoiceMinutes() {
        return voiceMinutes;
    }

    public void setVoiceMinutes(Integer voiceMinutes) {
        this.voiceMinutes = voiceMinutes;
    }

    public Integer getSmsCount() {
        return smsCount;
    }

    public void setSmsCount(Integer smsCount) {
        this.smsCount = smsCount;
    }

    public Package.PackageType getPackageType() {
        return packageType;
    }

    public void setPackageType(Package.PackageType packageType) {
        this.packageType = packageType;
    }

    public Package.PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }
}
