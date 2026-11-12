package com.pgno98.moviebookingsystem11.service;

import com.pgno98.moviebookingsystem11.entity.Promotion;
import com.pgno98.moviebookingsystem11.repository.PromotionRepository;
import com.pgno98.moviebookingsystem11.strategy.DiscountStrategy;
import com.pgno98.moviebookingsystem11.strategy.DiscountStrategyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PromotionService {
    
    @Autowired
    private PromotionRepository promotionRepository;
    
    @Autowired
    private DiscountStrategyFactory discountStrategyFactory;
    
    @Transactional
    public Promotion savePromotion(Promotion promotion) {
        return promotionRepository.save(promotion);
    }
    
    @Transactional
    public Promotion updatePromotion(Promotion promotion) {
        return promotionRepository.save(promotion);
    }
    
    @Transactional
    public void deletePromotion(Long id) {
        promotionRepository.deleteById(id);
    }
    
    public Optional<Promotion> findById(Long id) {
        return promotionRepository.findById(id);
    }
    
    public Optional<Promotion> findByCode(String code) {
        return promotionRepository.findByCode(code);
    }
    
    public Optional<Promotion> findValidPromotionByCode(String code) {
        return promotionRepository.findValidPromotionByCode(code, LocalDate.now());
    }
    
    public List<Promotion> getAllPromotions() {
        return promotionRepository.findAll();
    }
    
    public List<Promotion> getActivePromotions() {
        return promotionRepository.findByIsActiveTrueOrderByCreatedAtDesc();
    }
    
    public List<Promotion> getValidPromotions() {
        return promotionRepository.findAllValidPromotions(LocalDate.now());
    }
    
    public boolean existsByCode(String code) {
        return promotionRepository.existsByCode(code);
    }
    
    @Transactional
    public void incrementUsageCount(String code) {
        Optional<Promotion> promotionOpt = promotionRepository.findByCode(code);
        if (promotionOpt.isPresent()) {
            Promotion promotion = promotionOpt.get();
            promotion.setUsedCount(promotion.getUsedCount() + 1);
            promotionRepository.save(promotion);
        }
    }
    
    public BigDecimal calculateDiscount(String code, BigDecimal orderAmount) {
        Optional<Promotion> promotionOpt = findValidPromotionByCode(code);
        if (promotionOpt.isPresent()) {
            Promotion promotion = promotionOpt.get();
            
            // Try strategy pattern first, fallback to original calculation
            try {
                return calculateDiscountWithStrategy(promotion, orderAmount);
            } catch (Exception e) {
                // Fallback to original calculation for backward compatibility
                return promotion.calculateDiscount(orderAmount);
            }
        }
        return BigDecimal.ZERO;
    }
    
    /**
     * Calculate discount using the Strategy Pattern
     * @param promotion The promotion to apply
     * @param orderAmount The order amount
     * @return Calculated discount amount
     */
    public BigDecimal calculateDiscountWithStrategy(Promotion promotion, BigDecimal orderAmount) {
        try {
            // Get the appropriate discount strategy
            DiscountStrategy strategy = discountStrategyFactory.getDiscountStrategy(promotion.getDiscountType());
            
            // Validate if discount can be applied
            if (!strategy.canApplyDiscount(orderAmount, promotion.getMinimumAmount(), 
                                        promotion.getStartDate(), promotion.getEndDate(),
                                        promotion.getUsageLimit(), promotion.getUsedCount())) {
                return BigDecimal.ZERO;
            }
            
            // Calculate discount using the strategy
            return strategy.calculateDiscount(orderAmount, promotion.getDiscountValue(),
                                           promotion.getMinimumAmount(), promotion.getMaximumDiscount());
            
        } catch (IllegalArgumentException e) {
            // Fallback to original calculation for unsupported discount types
            return promotion.calculateDiscount(orderAmount);
        }
    }
    
    public boolean isValidPromotionCode(String code, BigDecimal orderAmount) {
        Optional<Promotion> promotionOpt = findValidPromotionByCode(code);
        if (promotionOpt.isPresent()) {
            Promotion promotion = promotionOpt.get();
            return promotion.canBeUsed(orderAmount);
        }
        return false;
    }
    
    public List<Promotion> getPromotionsByDateRange(LocalDate startDate, LocalDate endDate) {
        return promotionRepository.findByIsActiveTrueAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByCreatedAtDesc(
                startDate, endDate);
    }
}
