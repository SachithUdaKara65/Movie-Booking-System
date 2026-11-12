package com.pgno98.moviebookingsystem11.strategy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Month;

/**
 * Concrete strategy for Seasonal Discount calculation
 * Applies different discount rates based on the season/period
 */
public class SeasonalDiscountStrategy implements DiscountStrategy {
    
    @Override
    public BigDecimal calculateDiscount(BigDecimal originalAmount, BigDecimal discountValue, 
                                       BigDecimal minimumAmount, BigDecimal maximumDiscount) {
        // Get seasonal multiplier based on current month
        BigDecimal seasonalMultiplier = getSeasonalMultiplier();
        
        // Calculate base discount
        BigDecimal baseDiscount = originalAmount.multiply(discountValue.divide(new BigDecimal("100")));
        
        // Apply seasonal multiplier
        BigDecimal discount = baseDiscount.multiply(seasonalMultiplier);
        
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
        return "Seasonal Discount";
    }
    
    @Override
    public String getDiscountDescription(BigDecimal discountValue) {
        String season = getCurrentSeason();
        return season + " Special: " + discountValue + "% off (seasonal bonus applied)";
    }
    
    /**
     * Get seasonal multiplier based on current month
     * @return Multiplier for seasonal discount
     */
    private BigDecimal getSeasonalMultiplier() {
        Month currentMonth = LocalDate.now().getMonth();
        
        switch (currentMonth) {
            case DECEMBER:
            case JANUARY:
                return new BigDecimal("1.5"); // Holiday season bonus
            case JUNE:
            case JULY:
            case AUGUST:
                return new BigDecimal("1.2"); // Summer season bonus
            case MARCH:
            case APRIL:
            case MAY:
                return new BigDecimal("1.1"); // Spring season bonus
            default:
                return new BigDecimal("1.0"); // Regular season
        }
    }
    
    /**
     * Get current season name
     * @return String representation of current season
     */
    private String getCurrentSeason() {
        Month currentMonth = LocalDate.now().getMonth();
        
        switch (currentMonth) {
            case DECEMBER:
            case JANUARY:
                return "Holiday";
            case JUNE:
            case JULY:
            case AUGUST:
                return "Summer";
            case MARCH:
            case APRIL:
            case MAY:
                return "Spring";
            default:
                return "Regular";
        }
    }
}
