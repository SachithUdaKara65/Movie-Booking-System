package com.pgno98.moviebookingsystem11.strategy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

/**
 * Concrete strategy for Percentage Discount calculation
 */
public class PercentageDiscountStrategy implements DiscountStrategy {
    
    @Override
    public BigDecimal calculateDiscount(BigDecimal originalAmount, BigDecimal discountValue, 
                                       BigDecimal minimumAmount, BigDecimal maximumDiscount) {
        // Calculate percentage discount
        BigDecimal discount = originalAmount.multiply(discountValue.divide(new BigDecimal("100")));
        
        // Apply maximum discount limit if set
        if (maximumDiscount != null && discount.compareTo(maximumDiscount) > 0) {
            discount = maximumDiscount;
        }
        
        // Ensure discount doesn't exceed order amount
        if (discount.compareTo(originalAmount) > 0) {
            discount = originalAmount;
        }
        
        return discount.setScale(2, RoundingMode.HALF_UP);
    }
    
    @Override
    public boolean canApplyDiscount(BigDecimal originalAmount, BigDecimal minimumAmount,
                                   LocalDate startDate, LocalDate endDate, 
                                   Integer usageLimit, Integer usedCount) {
        // Check minimum amount requirement
        if (minimumAmount != null && originalAmount.compareTo(minimumAmount) < 0) {
            return false;
        }
        
        // Check date validity
        LocalDate today = LocalDate.now();
        if (startDate != null && today.isBefore(startDate)) {
            return false;
        }
        if (endDate != null && today.isAfter(endDate)) {
            return false;
        }
        
        // Check usage limit
        if (usageLimit != null && usedCount != null && usedCount >= usageLimit) {
            return false;
        }
        
        return true;
    }
    
    @Override
    public String getDiscountTypeName() {
        return "Percentage Discount";
    }
    
    @Override
    public String getDiscountDescription(BigDecimal discountValue) {
        return discountValue + "% off";
    }
}
