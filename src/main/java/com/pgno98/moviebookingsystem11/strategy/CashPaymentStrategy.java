package com.pgno98.moviebookingsystem11.strategy;

import com.pgno98.moviebookingsystem11.entity.Payment;
import com.pgno98.moviebookingsystem11.entity.Booking;
import com.pgno98.moviebookingsystem11.entity.Payment.PaymentMethod;
import com.pgno98.moviebookingsystem11.entity.Payment.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Concrete strategy for Cash payment processing
 */
public class CashPaymentStrategy implements PaymentStrategy {
    
    @Override
    public Payment processPayment(Booking booking, BigDecimal amount) {
        // Cash payment is immediate
        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setAmount(amount); // No additional fees for cash
        payment.setPaymentMethod(PaymentMethod.CASH);
        payment.setPaymentReference(generatePaymentReference());
        payment.setTransactionId(generateTransactionId());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setStatus(PaymentStatus.COMPLETED); // Cash is always completed immediately
        
        return payment;
    }
    
    @Override
    public boolean validatePayment(Booking booking, BigDecimal amount) {
        // Cash payment validations
        if (amount.compareTo(new BigDecimal("1000")) > 0) {
            return false; // Large cash transactions may require additional verification
        }
        
        return true;
    }
    
    @Override
    public String getPaymentMethodName() {
        return "Cash";
    }
    
    @Override
    public BigDecimal getAdditionalFees(BigDecimal amount) {
        // No additional fees for cash payments
        return BigDecimal.ZERO;
    }
    
    private String generatePaymentReference() {
        return "CASH-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    private String generateTransactionId() {
        return "TXN-" + System.currentTimeMillis();
    }
}
