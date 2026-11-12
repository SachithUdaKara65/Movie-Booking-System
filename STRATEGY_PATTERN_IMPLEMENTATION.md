# Strategy Pattern Implementation in Movie Booking System

## Overview
This document describes the implementation of the Strategy Pattern in the Movie Booking System for two major functionalities:
1. **Payment Processing System**
2. **Promotion and Discount System**

## Design Pattern Justification

### Why Strategy Pattern?

#### 1. Multiple Interchangeable Algorithms
- **Payment Processing**: Different methods (Credit Card, Debit Card, PayPal, Cash, Bank Transfer) achieve the same goal but through different implementations
- **Promotion System**: Various discount types (Percentage, Fixed Amount, BOGO, Seasonal) provide different calculation strategies

#### 2. Runtime Selection
- Users can choose their preferred payment methods during checkout
- Different promotion codes apply distinct discount strategies
- The system dynamically switches between algorithms at runtime

#### 3. Open/Closed Principle
- Easy addition of new payment methods without modifying existing code
- New promotion types can be added effortlessly without altering the core system
- Each strategy is encapsulated and operates independently

#### 4. Single Responsibility Principle
- Each strategy handles one specific payment method or discount type
- Clear separation of concerns
- Easy to test and maintain individual strategies

## Implementation Details

### Payment Processing System

#### Strategy Interface
```java
public interface PaymentStrategy {
    Payment processPayment(Booking booking, BigDecimal amount);
    boolean validatePayment(Booking booking, BigDecimal amount);
    String getPaymentMethodName();
    BigDecimal getAdditionalFees(BigDecimal amount);
    BigDecimal calculateFinalAmount(BigDecimal amount);
}
```

#### Concrete Strategies
1. **CreditCardPaymentStrategy**
   - Processing fee: 2.5% of amount
   - Credit limit: Rs. 10,000
   - Simulates network delay and validation

2. **DebitCardPaymentStrategy**
   - Processing fee: 1.5% of amount
   - Daily limit: Rs. 50,000
   - Faster processing than credit card

3. **PayPalPaymentStrategy**
   - Processing fee: 3.4% + Rs. 2.50 fixed fee
   - Transaction limit: Rs. 25,000
   - Longer processing time

4. **CashPaymentStrategy**
   - No additional fees
   - Immediate completion
   - Large transaction limit: Rs. 1,000

5. **BankTransferPaymentStrategy**
   - Fixed fee: Rs. 25 per transaction
   - Large transfer limit: Rs. 100,000
   - Immediate completion

#### Factory Pattern Integration
```java
@Component
public class PaymentStrategyFactory {
    private final Map<PaymentMethod, PaymentStrategy> strategies;
    
    public PaymentStrategy getPaymentStrategy(PaymentMethod paymentMethod) {
        return strategies.get(paymentMethod);
    }
}
```

### Promotion and Discount System

#### Strategy Interface
```java
public interface DiscountStrategy {
    BigDecimal calculateDiscount(BigDecimal originalAmount, BigDecimal discountValue, 
                               BigDecimal minimumAmount, BigDecimal maximumDiscount);
    boolean canApplyDiscount(BigDecimal originalAmount, BigDecimal minimumAmount,
                           LocalDate startDate, LocalDate endDate, 
                           Integer usageLimit, Integer usedCount);
    String getDiscountTypeName();
    String getDiscountDescription(BigDecimal discountValue);
}
```

#### Concrete Strategies
1. **PercentageDiscountStrategy**
   - Calculates percentage-based discounts
   - Applies maximum discount limits
   - Standard validation rules

2. **FixedAmountDiscountStrategy**
   - Applies fixed amount discounts
   - Respects maximum discount limits
   - Standard validation rules

3. **BOGODiscountStrategy**
   - Buy 2 Get 1 Free logic
   - Calculates free tickets based on ticket price
   - Special validation for minimum ticket requirements

4. **SeasonalDiscountStrategy**
   - Applies seasonal multipliers based on current month
   - Holiday season: 1.5x multiplier
   - Summer season: 1.2x multiplier
   - Spring season: 1.1x multiplier
   - Regular season: 1.0x multiplier

#### Factory Pattern Integration
```java
@Component
public class DiscountStrategyFactory {
    private final Map<DiscountType, DiscountStrategy> strategies;
    
    public DiscountStrategy getDiscountStrategy(DiscountType discountType) {
        return strategies.get(discountType);
    }
}
```

## Service Layer Integration

### PaymentService
```java
@Service
public class PaymentService {
    @Autowired
    private PaymentStrategyFactory paymentStrategyFactory;
    
    @Transactional
    public Payment processPayment(Booking booking, PaymentMethod paymentMethod, BigDecimal amount) {
        PaymentStrategy strategy = paymentStrategyFactory.getPaymentStrategy(paymentMethod);
        
        if (!strategy.validatePayment(booking, amount)) {
            throw new IllegalArgumentException("Payment validation failed");
        }
        
        BigDecimal finalAmount = strategy.calculateFinalAmount(amount);
        Payment payment = strategy.processPayment(booking, finalAmount);
        
        return paymentRepository.save(payment);
    }
}
```

### PromotionService
```java
@Service
public class PromotionService {
    @Autowired
    private DiscountStrategyFactory discountStrategyFactory;
    
    public BigDecimal calculateDiscountWithStrategy(Promotion promotion, BigDecimal orderAmount) {
        DiscountStrategy strategy = discountStrategyFactory.getDiscountStrategy(promotion.getDiscountType());
        
        if (!strategy.canApplyDiscount(orderAmount, promotion.getMinimumAmount(), 
                                    promotion.getStartDate(), promotion.getEndDate(),
                                    promotion.getUsageLimit(), promotion.getUsedCount())) {
            return BigDecimal.ZERO;
        }
        
        return strategy.calculateDiscount(orderAmount, promotion.getDiscountValue(),
                                       promotion.getMinimumAmount(), promotion.getMaximumDiscount());
    }
}
```

## Benefits Achieved

### 1. Extensibility
- New payment methods and promotion types can be added easily
- No modification to existing code required
- Simple factory registration for new strategies

### 2. Maintainability
- Each strategy is isolated and independently testable
- Clear separation of concerns
- Easy to debug and modify individual strategies

### 3. Flexibility
- Runtime selection of appropriate algorithms
- Dynamic strategy switching based on user choices
- Configurable strategy behavior

### 4. Clean Code
- Eliminates large if-else/switch statements
- Reduces code duplication
- Improves code readability and organization

### 5. Reusability
- Strategies can be reused across different contexts
- Common interfaces enable polymorphic behavior
- Easy to compose complex behaviors from simple strategies

## Usage Examples

### Payment Processing
```java
// In BookingController
Payment payment = paymentService.processPayment(booking, PaymentMethod.CREDIT_CARD, amount);
```

### Discount Calculation
```java
// In PromotionService
BigDecimal discount = promotionService.calculateDiscountWithStrategy(promotion, orderAmount);
```

## Future Enhancements

### Potential New Payment Strategies
- Cryptocurrency Payment
- Mobile Wallet Payment
- Installment Payment
- Gift Card Payment

### Potential New Discount Strategies
- Loyalty Points Discount
- Bulk Purchase Discount
- Time-based Discount
- Referral Discount

## Conclusion

The Strategy Pattern implementation provides a robust, extensible, and maintainable solution for handling multiple payment methods and discount types in the Movie Booking System. The design follows SOLID principles and enables easy addition of new strategies without modifying existing code, making the system highly flexible and future-proof.
