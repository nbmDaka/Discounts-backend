package com.discount_backend.Discount_backend.controller;

import com.discount_backend.Discount_backend.dto.discount.DiscountDto;
import com.discount_backend.Discount_backend.model.user.CustomUserDetails;
import com.discount_backend.Discount_backend.service.LovedDiscountService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/discounts")
public class LovedDiscountController {

    private final LovedDiscountService service;

    public LovedDiscountController(LovedDiscountService service) {
        this.service = service;
    }

    @PostMapping("/{discountId}/love")
    @ResponseStatus(HttpStatus.CREATED)
    public void loveDiscount(
            @PathVariable Long discountId,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        service.addLove(currentUser.getId(), discountId);
    }

    @GetMapping("/loved")
    public List<DiscountDto> getLovedDiscounts(
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        return service.getLovedDiscounts(currentUser.getId());
    }

    @DeleteMapping("/{discountId}/love")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unlovedDiscount(
            @PathVariable Long discountId,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        service.removeLove(currentUser.getId(), discountId);
    }
}
