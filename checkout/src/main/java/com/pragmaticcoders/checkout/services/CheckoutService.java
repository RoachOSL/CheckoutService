package com.pragmaticcoders.checkout.services;

import com.pragmaticcoders.checkout.components.SessionCart;
import com.pragmaticcoders.checkout.dtos.ItemDTO;
import com.pragmaticcoders.checkout.dtos.promotions.BundlePromotionDTO;
import com.pragmaticcoders.checkout.dtos.promotions.QuantityPromotionDTO;
import com.pragmaticcoders.checkout.entities.Item;
import com.pragmaticcoders.checkout.entities.promotions.BundlePromotion;
import com.pragmaticcoders.checkout.entities.promotions.QuantityPromotion;
import com.pragmaticcoders.checkout.enums.ReceiptKey;
import com.pragmaticcoders.checkout.exceptionhandling.exceptions.ItemNotFoundException;
import com.pragmaticcoders.checkout.mapper.ItemMapper;
import com.pragmaticcoders.checkout.mapper.promotions.BundlePromotionPromotionMapper;
import com.pragmaticcoders.checkout.mapper.promotions.QuantityPromotionMapper;
import com.pragmaticcoders.checkout.repositories.ItemRepository;
import com.pragmaticcoders.checkout.repositories.promotions.BundlePromotionRepository;
import com.pragmaticcoders.checkout.repositories.promotions.QuantityPromotionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CheckoutService {

    private final BundlePromotionRepository bundlePromotionRepository;
    private final BundlePromotionPromotionMapper bundlePromotionPromotionMapper;
    private final QuantityPromotionRepository quantityPromotionRepository;
    private final QuantityPromotionMapper quantityPromotionMapper;
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final SessionCart sessionCart;

    @Transactional
    public Item createItem(ItemDTO itemDTO) {
        Item item = itemMapper.toItem(itemDTO);
        return itemRepository.save(item);
    }

    @Transactional
    public BundlePromotion createBundlePromotion(BundlePromotionDTO bundlePromotionDTO) {
        BundlePromotion promotion = bundlePromotionPromotionMapper.toBundlePromotion(bundlePromotionDTO);
        return bundlePromotionRepository.save(promotion);
    }

    @Transactional
    public QuantityPromotion createQuantityPromotion(QuantityPromotionDTO quantityPromotionDTO) {
        QuantityPromotion promotion = quantityPromotionMapper.toQuantityPromotion(quantityPromotionDTO);
        return quantityPromotionRepository.save(promotion);
    }

    public void scanItem(Long itemId) {
        sessionCart.addItem(itemId, 1);
    }

    public BigDecimal calculateTotalPrice() {
        BigDecimal total = BigDecimal.ZERO;

        Map<Long, List<QuantityPromotion>> quantityPromotions = getQuantityPromotionsGroupedByItem();
        Map<Long, List<BundlePromotion>> bundlePromotions = getBundlePromotionsGroupedByItem();

        for (Map.Entry<Long, Integer> entry : sessionCart.getScannedItems().entrySet()) {
            total = total.add(calculateItemTotal(entry, quantityPromotions, bundlePromotions));
        }

        return total.max(BigDecimal.ZERO);
    }

    private Map<Long, List<QuantityPromotion>> getQuantityPromotionsGroupedByItem() {
        return quantityPromotionRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(QuantityPromotion::getItemId));
    }

    private Map<Long, List<BundlePromotion>> getBundlePromotionsGroupedByItem() {
        return bundlePromotionRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(BundlePromotion::getFirstBundleItemId));
    }

    private BigDecimal calculateItemTotal(Map.Entry<Long, Integer> entry,
                                          Map<Long, List<QuantityPromotion>> quantityPromotions,
                                          Map<Long, List<BundlePromotion>> bundlePromotions) {
        Long itemId = entry.getKey();
        Integer quantity = entry.getValue();

        if (quantity <= 0) return BigDecimal.ZERO;

        Item item = getItemById(itemId);
        return applyPromotions(item, quantity,
                quantityPromotions.getOrDefault(itemId, List.of()),
                bundlePromotions.getOrDefault(itemId, List.of()));
    }

    private Item getItemById(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() ->
                new ItemNotFoundException("Item with ID " + itemId + " not found."));
    }

    private BigDecimal applyPromotions(Item item, int quantity, List<QuantityPromotion> quantityPromotions,
                                       List<BundlePromotion> bundlePromotions) {
        BigDecimal totalPrice = BigDecimal.ZERO;
        BigDecimal discount = BigDecimal.ZERO;

        BigDecimal quantityPromotionPrice = handleQuantityPromotions(item, quantity, quantityPromotions);
        if (quantityPromotionPrice.compareTo(BigDecimal.ZERO) > 0) {
            return quantityPromotionPrice;
        }

        BigDecimal[] bundleResults = handleBundlePromotions(item, quantity, bundlePromotions, discount);
        totalPrice = totalPrice.add(bundleResults[0]);
        quantity = bundleResults[1].intValue();
        discount = bundleResults[2];

        if (quantity > 0) {
            totalPrice = totalPrice.add(item.getPrice().multiply(BigDecimal.valueOf(quantity)));
        }

        sessionCart.addDiscount(item.getId(), discount);

        return totalPrice;
    }

    private BigDecimal handleQuantityPromotions(Item item, int quantity, List<QuantityPromotion> quantityPromotions) {
        BigDecimal totalPrice = BigDecimal.ZERO;
        BigDecimal discount;

        for (QuantityPromotion promotion : quantityPromotions) {
            if (quantity >= promotion.getRequiredQuantity()) {
                int applicableSets = quantity / promotion.getRequiredQuantity();
                int remainingQuantity = quantity % promotion.getRequiredQuantity();

                BigDecimal promoPrice = promotion.getQuantityPromotionPrice()
                        .multiply(BigDecimal.valueOf(applicableSets));
                BigDecimal regularPrice = item.getPrice()
                        .multiply(BigDecimal.valueOf(remainingQuantity));

                BigDecimal originalPrice = item.getPrice()
                        .multiply(BigDecimal.valueOf(quantity));
                discount = originalPrice.subtract(promoPrice.add(regularPrice));
                totalPrice = promoPrice.add(regularPrice);

                sessionCart.addDiscount(item.getId(), discount);
                return totalPrice;
            }
        }

        return totalPrice;
    }

    private BigDecimal[] handleBundlePromotions(Item item, int quantity, List<BundlePromotion> bundlePromotions, BigDecimal discount) {
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (BundlePromotion promotion : bundlePromotions) {
            int bundleCount = Math.min(
                    quantity / promotion.getFirstItemRequiredQuantity(),
                    sessionCart.getScannedItems()
                            .getOrDefault(promotion.getSecondBundleItemId(), 0)
                            / promotion.getSecondItemRequiredQuantity()
            );

            if (bundleCount > 0) {
                BigDecimal bundlePrice = promotion.getBundlePrice()
                        .multiply(BigDecimal.valueOf(bundleCount));

                sessionCart.addItem(promotion.getSecondBundleItemId(),
                        -bundleCount * promotion.getSecondItemRequiredQuantity());
                quantity -= bundleCount * promotion.getFirstItemRequiredQuantity();

                BigDecimal firstItemPrice = item.getPrice()
                        .multiply(BigDecimal.valueOf((long) bundleCount * promotion.getFirstItemRequiredQuantity()));
                Item secondItem = getItemById(promotion.getSecondBundleItemId());
                BigDecimal secondItemPrice = secondItem.getPrice()
                        .multiply(BigDecimal.valueOf((long) bundleCount * promotion.getSecondItemRequiredQuantity()));

                BigDecimal bundleOriginalPrice = firstItemPrice.add(secondItemPrice);
                discount = discount.add(bundleOriginalPrice.subtract(bundlePrice));

                totalPrice = totalPrice.add(bundlePrice);

                sessionCart.addBundleItem(secondItem.getId(), bundleCount, secondItem.getPrice());
            }
        }

        return new BigDecimal[]{totalPrice, BigDecimal.valueOf(quantity), discount};
    }

    public Map<String, Object> finalizePurchase() {
        List<Map<String, Object>> receiptItems = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        BigDecimal totalDiscount = BigDecimal.ZERO;

        Map<Long, List<QuantityPromotion>> quantityPromotions = getQuantityPromotionsGroupedByItem();
        Map<Long, List<BundlePromotion>> bundlePromotions = getBundlePromotionsGroupedByItem();

        for (Map.Entry<Long, Integer> entry : sessionCart.getScannedItems().entrySet()) {
            Map<String, Object> itemResult = processScannedItem(entry, quantityPromotions, bundlePromotions);
            if (itemResult != null) {
                receiptItems.add(itemResult);
                total = total.add((BigDecimal) itemResult.get(ReceiptKey.TOTAL_COST.getKey()));
                totalDiscount = totalDiscount.add((BigDecimal) itemResult.getOrDefault(ReceiptKey.TOTAL_DISCOUNT.getKey(), BigDecimal.ZERO));
            }
        }

        for (Map.Entry<Long, Integer> bundleEntry : sessionCart.getBundleItems().entrySet()) {
            receiptItems.add(processBundleItem(bundleEntry));
        }

        LinkedHashMap<String, Object> finalReceipt = buildFinalReceipt(receiptItems, total, totalDiscount);

        sessionCart.clear();

        return finalReceipt;
    }

    private Map<String, Object> processScannedItem(Map.Entry<Long, Integer> entry,
                                                   Map<Long, List<QuantityPromotion>> quantityPromotions,
                                                   Map<Long, List<BundlePromotion>> bundlePromotions) {
        Long itemId = entry.getKey();
        Integer quantity = entry.getValue();

        if (quantity <= 0) return null;

        Item item = getItemById(itemId);
        BigDecimal itemTotal = applyPromotions(item, quantity,
                quantityPromotions.getOrDefault(itemId, List.of()),
                bundlePromotions.getOrDefault(itemId, List.of()));
        BigDecimal itemDiscount = sessionCart.getDiscount(itemId);

        LinkedHashMap<String, Object> receiptEntry = new LinkedHashMap<>();
        receiptEntry.put(ReceiptKey.ITEM_NAME.getKey(), item.getName());
        receiptEntry.put(ReceiptKey.QUANTITY.getKey(), quantity);
        receiptEntry.put(ReceiptKey.TOTAL_COST.getKey(), itemTotal);
        receiptEntry.put(ReceiptKey.TOTAL_DISCOUNT.getKey(), itemDiscount);

        return receiptEntry;
    }

    private Map<String, Object> processBundleItem(Map.Entry<Long, Integer> bundleEntry) {
        Long bundleItemId = bundleEntry.getKey();
        Integer bundleQuantity = bundleEntry.getValue();

        Item bundleItem = getItemById(bundleItemId);

        LinkedHashMap<String, Object> bundleEntryMap = new LinkedHashMap<>();
        bundleEntryMap.put(ReceiptKey.ITEM_NAME.getKey(), bundleItem.getName());
        bundleEntryMap.put(ReceiptKey.QUANTITY.getKey(), bundleQuantity);
        bundleEntryMap.put(ReceiptKey.TOTAL_COST.getKey(), BigDecimal.ZERO);

        return bundleEntryMap;
    }

    private LinkedHashMap<String, Object> buildFinalReceipt(List<Map<String, Object>> receiptItems,
                                                            BigDecimal total,
                                                            BigDecimal totalDiscount) {
        LinkedHashMap<String, Object> finalReceipt = new LinkedHashMap<>();
        finalReceipt.put(ReceiptKey.ITEMS.getKey(), receiptItems);
        finalReceipt.put(ReceiptKey.TOTAL_COST.getKey(), total);
        finalReceipt.put(ReceiptKey.TOTAL_DISCOUNT.getKey(), totalDiscount);

        return finalReceipt;
    }
}
