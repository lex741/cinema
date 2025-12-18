package com.lex.cinema.service;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PricingPolicy {
    public BigDecimal total(BigDecimal seatPrice, int seatsCount) {
        if (seatPrice == null) return BigDecimal.ZERO;
        return seatPrice.multiply(BigDecimal.valueOf(seatsCount));
    }
}
