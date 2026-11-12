package com.pgno98.moviebookingsystem11.strategy;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Strategy interface for discount calculation
 * Each concrete strategy implements a specific discount type
 */
public interface DiscountStrategy {
    
    /**
     * Calculate discount amount based on the strategy
     * @param originalAmount The original amount before discount
     * @param discountValue The discount value (percentage or fixed amount)
     * @param minimumAmount The minimum order amount required
     * @param maximumDiscount The maximum discount that can be applied
     * @return The calculated discount amount
     */
    BigDecimal calculateDiscount(BigDecimal originalAmount, BigDecimal discountValue, 
                               BigDecimal minimumAmount, BigDecimal maximumDiscount);
    
    /**
     * Validate if the discount can be applied
     * @param originalAmount The original amount
     * @param minimumAmount The minimum order amount required
     * @param startDate The promotion start date
     * @param endDate The promotion end date
     * @param usageLimit The maximum number of times this promotion can be used
     * @param usedCount The current usage count
     * @return true if discount can be applied, false otherwise
     */
    boolean canApplyDiscount(BigDecimal originalAmount, BigDecimal minimumAmount,
                           LocalDate startDate, LocalDate endDate, 
                           Integer usageLimit, Integer usedCount);
    
    /**
     * Get the discount type name
     * @return String representation of the discount type
     */
    String getDiscountTypeName();
    
    /**
     * Get the discount description for display
     * @param discountValue The discount value
     * @return Formatted description of the discount
     */
    String getDiscountDescription(BigDecimal discountValue);
}
