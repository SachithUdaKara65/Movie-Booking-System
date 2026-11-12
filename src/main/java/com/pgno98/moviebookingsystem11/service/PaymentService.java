package com.pgno98.moviebookingsystem11.service;

import com.pgno98.moviebookingsystem11.entity.Booking;
import com.pgno98.moviebookingsystem11.entity.Payment;
import com.pgno98.moviebookingsystem11.entity.Payment.PaymentMethod;
import com.pgno98.moviebookingsystem11.repository.PaymentRepository;
import com.pgno98.moviebookingsystem11.strategy.PaymentStrategy;
import com.pgno98.moviebookingsystem11.strategy.PaymentStrategyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    @Autowired
    private PaymentStrategyFactory paymentStrategyFactory;
    
    @Transactional
    public Payment processPayment(Booking booking, String paymentMethodString, BigDecimal amount) {
        try {
            // Convert string to PaymentMethod enum
            PaymentMethod paymentMethod = PaymentMethod.valueOf(paymentMethodString.toUpperCase());
            return processPayment(booking, paymentMethod, amount);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid payment method: " + paymentMethodString, e);
        }
    }
    
    @Transactional
    public Payment processPayment(Booking booking, PaymentMethod paymentMethod, BigDecimal amount) {
        try {
            // Get the appropriate payment strategy
            PaymentStrategy strategy = paymentStrategyFactory.getPaymentStrategy(paymentMethod);
            
            // Validate payment
            if (!strategy.validatePayment(booking, amount)) {
                throw new IllegalArgumentException("Payment validation failed for " + strategy.getPaymentMethodName());
            }
            
            // Calculate final amount including fees
            BigDecimal finalAmount = strategy.calculateFinalAmount(amount);
            
            // Process payment using the strategy
            Payment payment = strategy.processPayment(booking, finalAmount);
            
            // Save payment to database
            return paymentRepository.save(payment);
            
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Payment processing failed: " + e.getMessage(), e);
        }
    }
    
    public Optional<Payment> findById(Long id) {
        return paymentRepository.findById(id);
    }
    
    public Optional<Payment> findByBookingId(Long bookingId) {
        return paymentRepository.findByBookingId(bookingId);
    }
    
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }
    
    public List<Payment> getPaymentsByMethod(PaymentMethod paymentMethod) {
        return paymentRepository.findByPaymentMethod(paymentMethod);
    }
    
    @Transactional
    public Payment updatePayment(Payment payment) {
        return paymentRepository.save(payment);
    }
    
    @Transactional
    public void deletePayment(Long id) {
        paymentRepository.deleteById(id);
    }
    
    /**
     * Get payment method information including fees
     * @param paymentMethod The payment method
     * @param amount The amount to calculate fees for
     * @return Payment method information
     */
    public PaymentMethodInfo getPaymentMethodInfo(PaymentMethod paymentMethod, BigDecimal amount) {
        PaymentStrategy strategy = paymentStrategyFactory.getPaymentStrategy(paymentMethod);
        BigDecimal fees = strategy.getAdditionalFees(amount);
        BigDecimal finalAmount = strategy.calculateFinalAmount(amount);
        
        return new PaymentMethodInfo(
            strategy.getPaymentMethodName(),
            amount,
            fees,
            finalAmount
        );
    }
    
    /**
     * Inner class to hold payment method information
     */
    public static class PaymentMethodInfo {
        private final String methodName;
        private final BigDecimal baseAmount;
        private final BigDecimal fees;
        private final BigDecimal finalAmount;
        
        public PaymentMethodInfo(String methodName, BigDecimal baseAmount, BigDecimal fees, BigDecimal finalAmount) {
            this.methodName = methodName;
            this.baseAmount = baseAmount;
            this.fees = fees;
            this.finalAmount = finalAmount;
        }
        
        public String getMethodName() { return methodName; }
        public BigDecimal getBaseAmount() { return baseAmount; }
        public BigDecimal getFees() { return fees; }
        public BigDecimal getFinalAmount() { return finalAmount; }
    }
}
