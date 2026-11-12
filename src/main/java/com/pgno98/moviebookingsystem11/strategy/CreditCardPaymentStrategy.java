package com.pgno98.moviebookingsystem11.strategy;

import com.pgno98.moviebookingsystem11.entity.Payment;
import com.pgno98.moviebookingsystem11.entity.Booking;
import com.pgno98.moviebookingsystem11.entity.Payment.PaymentMethod;
import com.pgno98.moviebookingsystem11.entity.Payment.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Concrete strategy for Credit Card payment processing
 */
public class CreditCardPaymentStrategy implements PaymentStrategy {
    
    @Override
    public Payment processPayment(Booking booking, BigDecimal amount) {
        // Simulate credit card processing
        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setAmount(calculateFinalAmount(amount));
        payment.setPaymentMethod(PaymentMethod.CREDIT_CARD);
        payment.setPaymentReference(generatePaymentReference());
        payment.setTransactionId(generateTransactionId());
        payment.setPaymentDate(LocalDateTime.now());
        
        // Simulate processing delay and success
        try {
            Thread.sleep(1000); // Simulate network delay
            payment.setStatus(PaymentStatus.COMPLETED);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            payment.setStatus(PaymentStatus.FAILED);
        }
        
        return payment;
    }
    
    @Override
    public boolean validatePayment(Booking booking, BigDecimal amount) {
        // Credit card specific validations
        if (amount.compareTo(new BigDecimal("10000")) > 0) {
            return false; // Credit limit exceeded
        }
        
        // Additional credit card validations can be added here
        return true;
    }
    
    @Override
    public String getPaymentMethodName() {
        return "Credit Card";
    }
    
    @Override
    public BigDecimal getAdditionalFees(BigDecimal amount) {
        // Credit card processing fee: 2.5% of amount
        return amount.multiply(new BigDecimal("0.025"));
    }
    
    private String generatePaymentReference() {
        return "CC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    private String generateTransactionId() {
        return "TXN-" + System.currentTimeMillis();
    }
}
