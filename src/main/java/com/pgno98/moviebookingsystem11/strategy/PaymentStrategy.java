package com.pgno98.moviebookingsystem11.strategy;

import com.pgno98.moviebookingsystem11.entity.Payment;
import com.pgno98.moviebookingsystem11.entity.Booking;

import java.math.BigDecimal;

/**
 * Strategy interface for payment processing
 * Each concrete strategy implements a specific payment method
 */
public interface PaymentStrategy {
    
    /**
     * Process payment for a booking
     * @param booking The booking to process payment for
     * @param amount The amount to be charged
     * @return Payment object with transaction details
     */
    Payment processPayment(Booking booking, BigDecimal amount);
    
    /**
     * Validate payment method specific requirements
     * @param booking The booking to validate
     * @param amount The amount to validate
     * @return true if payment can be processed, false otherwise
     */
    boolean validatePayment(Booking booking, BigDecimal amount);
    
    /**
     * Get the payment method name
     * @return String representation of the payment method
     */
    String getPaymentMethodName();
    
    /**
     * Get additional fees for this payment method
     * @param amount The base amount
     * @return Additional fees to be charged
     */
    default BigDecimal getAdditionalFees(BigDecimal amount) {
        return BigDecimal.ZERO;
    }
    
    /**
     * Calculate final amount including fees
     * @param amount The base amount
     * @return Final amount including all fees
     */
    default BigDecimal calculateFinalAmount(BigDecimal amount) {
        return amount.add(getAdditionalFees(amount));
    }
}
