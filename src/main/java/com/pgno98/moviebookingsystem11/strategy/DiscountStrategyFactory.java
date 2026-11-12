package com.pgno98.moviebookingsystem11.strategy;

import com.pgno98.moviebookingsystem11.entity.Promotion.DiscountType;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Factory class to create discount strategies based on discount type
 * Implements the Factory Pattern to work with Strategy Pattern
 */
@Component
public class DiscountStrategyFactory {
    
    private final Map<DiscountType, DiscountStrategy> strategies;
    
    public DiscountStrategyFactory() {
        strategies = new HashMap<>();
        strategies.put(DiscountType.PERCENTAGE, new PercentageDiscountStrategy());
        strategies.put(DiscountType.FIXED_AMOUNT, new FixedAmountDiscountStrategy());
        strategies.put(DiscountType.BOGO, new BOGODiscountStrategy());
        strategies.put(DiscountType.SEASONAL, new SeasonalDiscountStrategy());
    }
    
    /**
     * Get the appropriate discount strategy for the given discount type
     * @param discountType The discount type
     * @return DiscountStrategy instance
     * @throws IllegalArgumentException if discount type is not supported
     */
    public DiscountStrategy getDiscountStrategy(DiscountType discountType) {
        DiscountStrategy strategy = strategies.get(discountType);
        if (strategy == null) {
            throw new IllegalArgumentException("Unsupported discount type: " + discountType);
        }
        return strategy;
    }
    
    /**
     * Check if a discount type is supported
     * @param discountType The discount type to check
     * @return true if supported, false otherwise
     */
    public boolean isDiscountTypeSupported(DiscountType discountType) {
        return strategies.containsKey(discountType);
    }
    
    /**
     * Get all supported discount types
     * @return Set of supported discount types
     */
    public java.util.Set<DiscountType> getSupportedDiscountTypes() {
        return strategies.keySet();
    }
}
