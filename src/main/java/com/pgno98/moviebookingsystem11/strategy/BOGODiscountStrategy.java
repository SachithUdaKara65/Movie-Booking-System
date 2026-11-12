package com.pgno98.moviebookingsystem11.strategy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

/**
 * Concrete strategy for Buy One Get One (BOGO) Discount calculation
 */
public class BOGODiscountStrategy implements DiscountStrategy {
    
    @Override
    public BigDecimal calculateDiscount(BigDecimal originalAmount, BigDecimal discountValue, 
                                       BigDecimal minimumAmount, BigDecimal maximumDiscount) {
        // For BOGO, we assume the discount value represents the price of one item
        // In a movie booking context, this could be "buy 2 tickets, get 1 free"
        BigDecimal ticketPrice = discountValue; // Assuming discountValue is the ticket price
        
        // Calculate how many free tickets can be given
        BigDecimal numberOfTickets = originalAmount.divide(ticketPrice, 0, RoundingMode.DOWN);
        BigDecimal freeTickets = numberOfTickets.divide(new BigDecimal("2"), 0, RoundingMode.DOWN);
        
        BigDecimal discount = freeTickets.multiply(ticketPrice);
        
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
        return "Buy One Get One (BOGO)";
    }
    
    @Override
    public String getDiscountDescription(BigDecimal discountValue) {
        return "Buy 2 Get 1 Free (up to Rs. " + discountValue + " per ticket)";
    }
}
