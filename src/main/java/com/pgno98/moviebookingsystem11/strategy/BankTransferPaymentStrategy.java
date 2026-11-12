package com.pgno98.moviebookingsystem11.strategy;

import com.pgno98.moviebookingsystem11.entity.Payment;
import com.pgno98.moviebookingsystem11.entity.Booking;
import com.pgno98.moviebookingsystem11.entity.Payment.PaymentMethod;
import com.pgno98.moviebookingsystem11.entity.Payment.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Concrete strategy for Bank Transfer payment processing
 */
public class BankTransferPaymentStrategy implements PaymentStrategy {
    
    @Override
    public Payment processPayment(Booking booking, BigDecimal amount) {
        // Simulate bank transfer processing
        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setAmount(calculateFinalAmount(amount));
        payment.setPaymentMethod(PaymentMethod.BANK_TRANSFER);
        payment.setPaymentReference(generatePaymentReference());
        payment.setTransactionId(generateTransactionId());
        payment.setPaymentDate(LocalDateTime.now());
        
        // Bank transfers may take time, but we'll simulate immediate completion
        payment.setStatus(PaymentStatus.COMPLETED);
        
        return payment;
    }
    
    @Override
    public boolean validatePayment(Booking booking, BigDecimal amount) {
        // Bank transfer specific validations
        if (amount.compareTo(new BigDecimal("100000")) > 0) {
            return false; // Large transfer limit
        }
        
        return true;
    }
    
    @Override
    public String getPaymentMethodName() {
        return "Bank Transfer";
    }
    
    @Override
    public BigDecimal getAdditionalFees(BigDecimal amount) {
        // Bank transfer fee: Rs. 25 per transaction
        return new BigDecimal("25.00");
    }
    
    private String generatePaymentReference() {
        return "BT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    private String generateTransactionId() {
        return "TXN-" + System.currentTimeMillis();
    }
}
