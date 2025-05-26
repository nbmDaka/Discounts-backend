// com/discount_backend/Discount_backend/dto/market/CreateMarketDto.java
package com.discount_backend.Discount_backend.dto.market;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class CreateMarketDto {
    @NotBlank @Size(max = 150)
    private String name;

    @Size(max = 255)
    private String address;

    @DecimalMin("-90.0") @DecimalMax("90.0")
    private BigDecimal latitude;

    @DecimalMin("-180.0") @DecimalMax("180.0")
    private BigDecimal longitude;

    @Size(max = 20)
    private String phone;

    @Size(max = 255)
    private String websiteUrl;

    @Size(max = 500)
    private String imageUrl;

    private Long cityId;
    // getters & setters


}
