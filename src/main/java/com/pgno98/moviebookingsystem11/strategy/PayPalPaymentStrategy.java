package com.pgno98.moviebookingsystem11.strategy;

import com.pgno98.moviebookingsystem11.entity.Payment;
import com.pgno98.moviebookingsystem11.entity.Booking;
import com.pgno98.moviebookingsystem11.entity.Payment.PaymentMethod;
import com.pgno98.moviebookingsystem11.entity.Payment.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Concrete strategy for PayPal payment processing
 */
public class PayPalPaymentStrategy implements PaymentStrategy {
    
    @Override
    public Payment processPayment(Booking booking, BigDecimal amount) {
        // Simulate PayPal processing
        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setAmount(calculateFinalAmount(amount));
        payment.setPaymentMethod(PaymentMethod.PAYPAL);
        payment.setPaymentReference(generatePaymentReference());
        payment.setTransactionId(generateTransactionId());
        payment.setPaymentDate(LocalDateTime.now());
        
        // Simulate processing delay and success
        try {
            Thread.sleep(1200); // Simulate network delay
            payment.setStatus(PaymentStatus.COMPLETED);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            payment.setStatus(PaymentStatus.FAILED);
        }
        
        return payment;
    }
    
    @Override
    public boolean validatePayment(Booking booking, BigDecimal amount) {
        // PayPal specific validations
        if (amount.compareTo(new BigDecimal("25000")) > 0) {
            return false; // PayPal transaction limit
        }
        
        // Additional PayPal validations can be added here
        return true;
    }
    
    @Override
    public String getPaymentMethodName() {
        return "PayPal";
    }
    
    @Override
    public BigDecimal getAdditionalFees(BigDecimal amount) {
        // PayPal processing fee: 3.4% + fixed fee
        BigDecimal percentageFee = amount.multiply(new BigDecimal("0.034"));
        BigDecimal fixedFee = new BigDecimal("2.50");
        return percentageFee.add(fixedFee);
    }
    
    private String generatePaymentReference() {
        return "PP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    private String generateTransactionId() {
        return "TXN-" + System.currentTimeMillis();
    }
}
