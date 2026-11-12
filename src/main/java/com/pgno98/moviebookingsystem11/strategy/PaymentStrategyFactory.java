package com.pgno98.moviebookingsystem11.strategy;

import com.pgno98.moviebookingsystem11.entity.Payment.PaymentMethod;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Factory class to create payment strategies based on payment method
 * Implements the Factory Pattern to work with Strategy Pattern
 */
@Component
public class PaymentStrategyFactory {
    
    private final Map<PaymentMethod, PaymentStrategy> strategies;
    
    public PaymentStrategyFactory() {
        strategies = new HashMap<>();
        strategies.put(PaymentMethod.CREDIT_CARD, new CreditCardPaymentStrategy());
        strategies.put(PaymentMethod.DEBIT_CARD, new DebitCardPaymentStrategy());
        strategies.put(PaymentMethod.PAYPAL, new PayPalPaymentStrategy());
        strategies.put(PaymentMethod.CASH, new CashPaymentStrategy());
        strategies.put(PaymentMethod.BANK_TRANSFER, new BankTransferPaymentStrategy());
    }
    
    /**
     * Get the appropriate payment strategy for the given payment method
     * @param paymentMethod The payment method
     * @return PaymentStrategy instance
     * @throws IllegalArgumentException if payment method is not supported
     */
    public PaymentStrategy getPaymentStrategy(PaymentMethod paymentMethod) {
        PaymentStrategy strategy = strategies.get(paymentMethod);
        if (strategy == null) {
            throw new IllegalArgumentException("Unsupported payment method: " + paymentMethod);
        }
        return strategy;
    }
    
    /**
     * Get the appropriate payment strategy for the given payment method string
     * @param paymentMethodString The payment method as string
     * @return PaymentStrategy instance
     * @throws IllegalArgumentException if payment method is not supported
     */
    public PaymentStrategy getPaymentStrategy(String paymentMethodString) {
        try {
            PaymentMethod paymentMethod = PaymentMethod.valueOf(paymentMethodString.toUpperCase());
            return getPaymentStrategy(paymentMethod);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unsupported payment method: " + paymentMethodString);
        }
    }
    
    /**
     * Check if a payment method is supported
     * @param paymentMethod The payment method to check
     * @return true if supported, false otherwise
     */
    public boolean isPaymentMethodSupported(PaymentMethod paymentMethod) {
        return strategies.containsKey(paymentMethod);
    }
    
    /**
     * Get all supported payment methods
     * @return Set of supported payment methods
     */
    public java.util.Set<PaymentMethod> getSupportedPaymentMethods() {
        return strategies.keySet();
    }
}
