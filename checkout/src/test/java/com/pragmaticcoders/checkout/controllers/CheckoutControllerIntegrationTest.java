package com.pragmaticcoders.checkout.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pragmaticcoders.checkout.dtos.ItemDTO;
import com.pragmaticcoders.checkout.dtos.promotions.BundlePromotionDTO;
import com.pragmaticcoders.checkout.dtos.promotions.QuantityPromotionDTO;
import com.pragmaticcoders.checkout.entities.Item;
import com.pragmaticcoders.checkout.mapper.ItemMapper;
import com.pragmaticcoders.checkout.mapper.promotions.BundlePromotionPromotionMapper;
import com.pragmaticcoders.checkout.mapper.promotions.QuantityPromotionMapper;
import com.pragmaticcoders.checkout.repositories.ItemRepository;
import com.pragmaticcoders.checkout.repositories.promotions.BundlePromotionRepository;
import com.pragmaticcoders.checkout.repositories.promotions.QuantityPromotionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CheckoutControllerIntegrationTest {

    private static final String CHECKOUT_MAIN_PATH = "/checkout";
    private static final String SCAN_PATH = "/scan";
    private static final String TOTAL_PATH = "/total";
    private static final String FINALIZE_PATH = "/finalize";
    private static final String ITEM_PATH = "/item";
    private static final String QUANTITY_PROMOTIONS_PATH = "/quantity_promotions";
    private static final String BUNDLE_PROMOTIONS_PATH = "/bundle_promotions";
    private static final String MESSAGE_FIELD = "$.message";
    private static final String NAME_ERROR_MESSAGE = "NAME: Name must not be blank";
    private static final String PRICE_ERROR_MESSAGE = "PRICE: Price must not be null";
    private static final String ITEM_ID_ERROR_MESSAGE = "ITEMID: Item ID must not be null";
    private static final String REQUIRED_QUANTITY_ERROR_MESSAGE =
            "REQUIREDQUANTITY: Required quantity must be at least 1";
    private static final String FIRST_BUNDLE_ITEM_ID_ERROR_MESSAGE =
            "FIRSTBUNDLEITEMID: First bundle item ID must not be null";
    private static final String BUNDLE_PRICE_ERROR_MESSAGE =
            "BUNDLEPRICE: Bundle price must be a positive value";
    private static final String FIRST_PRODUCT_NAME = "Product A";
    private static final String SECOND_PRODUCT_NAME = "Product B";
    private static final BigDecimal PRODUCT_A_DEFAULT_PRICE = BigDecimal.valueOf(40.0);
    private static final BigDecimal PRODUCT_B_DEFAULT_PRICE = BigDecimal.valueOf(10.0);
    private static final int QUANTITY_PROMOTION_PRODUCT_A_QUANTITY = 3;
    private static final int QUANTITY_PROMOTION_A_DISCOUNT_PRICE = 90;
    private static final int QUANTITY_PROMOTION_PRODUCT_B_QUANTITY = 2;
    private static final int QUANTITY_PROMOTION_B_DISCOUNT_PRICE = 15;
    private static final int BUNDLED_PROMOTION_PRODUCT_A_QUANTITY = 1;
    private static final int BUNDLED_PROMOTION_PRODUCT_B_QUANTITY = 1;
    private static final int BUNDLE_PRICE = 25;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private BundlePromotionRepository bundlePromotionRepository;

    @Autowired
    private BundlePromotionPromotionMapper bundlePromotionPromotionMapper;

    @Autowired
    private QuantityPromotionRepository quantityPromotionRepository;

    @Autowired
    private QuantityPromotionMapper quantityPromotionMapper;

    private MockHttpSession session;
    private Long productAId;
    private Long productBId;

    @BeforeEach
    void setup() {
        session = new MockHttpSession();
        initializeTestData();
    }

    private void initializeTestData() {
        itemRepository.deleteAll();
        bundlePromotionRepository.deleteAll();

        Item productA = itemRepository.save(itemMapper.toItem(
                ItemDTO.builder().name(FIRST_PRODUCT_NAME).price(PRODUCT_A_DEFAULT_PRICE).build()
        ));
        productAId = productA.getId();

        Item productB = itemRepository.save(itemMapper.toItem(
                ItemDTO.builder().name(SECOND_PRODUCT_NAME).price(PRODUCT_B_DEFAULT_PRICE).build()
        ));
        productBId = productB.getId();

        quantityPromotionRepository.save(quantityPromotionMapper.toQuantityPromotion(
                QuantityPromotionDTO.builder()
                        .itemId(productA.getId())
                        .requiredQuantity(QUANTITY_PROMOTION_PRODUCT_A_QUANTITY)
                        .quantityPromotionPrice(BigDecimal.valueOf(QUANTITY_PROMOTION_A_DISCOUNT_PRICE))
                        .build()
        ));

        quantityPromotionRepository.save(quantityPromotionMapper.toQuantityPromotion(
                QuantityPromotionDTO.builder()
                        .itemId(productB.getId())
                        .requiredQuantity(QUANTITY_PROMOTION_PRODUCT_B_QUANTITY)
                        .quantityPromotionPrice(BigDecimal.valueOf(QUANTITY_PROMOTION_B_DISCOUNT_PRICE))
                        .build()
        ));

        bundlePromotionRepository.save(bundlePromotionPromotionMapper.toBundlePromotion(
                BundlePromotionDTO.builder()
                        .firstBundleItemId(productA.getId())
                        .firstItemRequiredQuantity(BUNDLED_PROMOTION_PRODUCT_A_QUANTITY)
                        .secondBundleItemId(productB.getId())
                        .secondItemRequiredQuantity(BUNDLED_PROMOTION_PRODUCT_B_QUANTITY)
                        .bundlePrice(BigDecimal.valueOf(BUNDLE_PRICE))
                        .build()
        ));
    }

    @Test
    void shouldScanItemAndReturnAcceptedResponse() throws Exception {
        // Given/When/Then
        mockMvc.perform(MockMvcRequestBuilders.post(CHECKOUT_MAIN_PATH + SCAN_PATH)
                        .param("itemId", "1")
                        .session(session))
                .andExpect(status().isAccepted());
    }

    @Test
    void shouldReturnTotalPriceSuccessfullyWithDiscounts() throws Exception {
        // Given
        final double expectedTotal = 100.0;

        for (int i = 0; i < 3; i++) {
            scanItem(productAId);
        }
        scanItem(productBId);

        // When/Then
        mockMvc.perform(MockMvcRequestBuilders.get(CHECKOUT_MAIN_PATH + TOTAL_PATH)
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(expectedTotal)));
    }

    @Test
    void shouldReturnTotalPriceSuccessfullyWithBundleDiscountsUsingFinalize() throws Exception {
        // Given
        final double expectedTotal = 65.00;

        scanItem(productAId);
        scanItem(productAId);
        scanItem(productBId);

        // When/Then
        mockMvc.perform(MockMvcRequestBuilders.post(CHECKOUT_MAIN_PATH + FINALIZE_PATH)
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$['Total Cost']", is(expectedTotal)));
    }

    @Test
    void shouldReturnTotalPriceSuccessfullyWithFourProductAAndFourProductB() throws Exception {
        // Given
        final double expectedTotal = 160.00;

        for (int i = 0; i < 4; i++) {
            scanItem(productAId);
        }

        for (int i = 0; i < 4; i++) {
            scanItem(productBId);
        }

        // When/Then
        mockMvc.perform(MockMvcRequestBuilders.get(CHECKOUT_MAIN_PATH + TOTAL_PATH)
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(expectedTotal)));
    }

    @Test
    void shouldReturnTotalPriceSuccessfullyWithSixAProductsDiscountsUsingFinalize() throws Exception {
        // Given
        final double expectedTotal = 180.00;

        for (int i = 0; i < 6; i++) {
            scanItem(productAId);
        }


        // When/Then
        mockMvc.perform(MockMvcRequestBuilders.post(CHECKOUT_MAIN_PATH + FINALIZE_PATH)
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$['Total Cost']", is(expectedTotal)));
    }

    @Test
    void shouldReturnTotalPriceSuccessfullyWithFiveAProductsDiscountsUsingFinalize() throws Exception {
        // Given
        final double expectedTotal = 170.00;

        for (int i = 0; i < 5; i++) {
            scanItem(productAId);
        }


        // When/Then
        mockMvc.perform(MockMvcRequestBuilders.post(CHECKOUT_MAIN_PATH + FINALIZE_PATH)
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$['Total Cost']", is(expectedTotal)));
    }

    @Test
    void shouldReturnTotalPriceSuccessfullyWithTwoProductAAndTwoProductB() throws Exception {
        // Given
        final double expectedTotal = 115;

        // 3 x 30 discount = 90
        for (int i = 0; i < 3; i++) {
            scanItem(productAId);
        }

        // 15 disc and 10 = 25
        for (int i = 0; i < 3; i++) {
            scanItem(productBId);
        }

        // When/Then
        mockMvc.perform(MockMvcRequestBuilders.get(CHECKOUT_MAIN_PATH + TOTAL_PATH)
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(expectedTotal)));
    }

    @Test
    void shouldFinalizePurchaseAndClearCart() throws Exception {
        // Given
        final int expectedTotalDiscount = 0;

        scanItem(productAId);

        mockMvc.perform(MockMvcRequestBuilders.post(CHECKOUT_MAIN_PATH + FINALIZE_PATH)
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$['Total Cost']", is(PRODUCT_A_DEFAULT_PRICE.doubleValue())))
                .andExpect(jsonPath("$['Total Discount']", is(expectedTotalDiscount)));
    }

    @Test
    void shouldCreateNewItemAndReturnCreatedResponse() throws Exception {
        // Given
        final String newItemName = "Test Item";
        final double priceOfItem = 10.99;

        ItemDTO newItem = ItemDTO.builder()
                .name(newItemName)
                .price(BigDecimal.valueOf(priceOfItem))
                .build();

        // When/Then
        mockMvc.perform(MockMvcRequestBuilders.post(CHECKOUT_MAIN_PATH + ITEM_PATH)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(newItem)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(newItemName)))
                .andExpect(jsonPath("$.price", is(priceOfItem)));
    }

    @Test
    void shouldCreateNewQuantityPromotionAndReturnCreatedResponse() throws Exception {
        // Given
        final long ITEM_ID = 1L;
        final int REQUIRED_QUANTITY = 3;
        final double PROMOTION_PRICE = 19.99;

        QuantityPromotionDTO promotion = QuantityPromotionDTO.builder()
                .itemId(ITEM_ID)
                .requiredQuantity(REQUIRED_QUANTITY)
                .quantityPromotionPrice(BigDecimal.valueOf(PROMOTION_PRICE))
                .build();

        // When/Then
        mockMvc.perform(MockMvcRequestBuilders.post(CHECKOUT_MAIN_PATH + QUANTITY_PROMOTIONS_PATH)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(promotion)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.itemId", is((int) ITEM_ID)))
                .andExpect(jsonPath("$.requiredQuantity", is(REQUIRED_QUANTITY)))
                .andExpect(jsonPath("$.quantityPromotionPrice", is(PROMOTION_PRICE)));
    }

    @Test
    void shouldCreateNewBundlePromotionAndReturnCreatedResponse() throws Exception {
        // Given
        final int firstBundleItemId = 1;
        final int firstItemRequiredQuantity = 3;
        final int secondBundleItemId = 2;
        final int secondItemRequiredQuantity = 2;
        final double bundlePrice = 50.00;

        BundlePromotionDTO promotion = BundlePromotionDTO.builder()
                .firstBundleItemId((long) firstBundleItemId)
                .firstItemRequiredQuantity(firstItemRequiredQuantity)
                .secondBundleItemId((long) secondBundleItemId)
                .secondItemRequiredQuantity(secondItemRequiredQuantity)
                .bundlePrice(BigDecimal.valueOf(bundlePrice))
                .build();

        // When/Then
        mockMvc.perform(MockMvcRequestBuilders.post(CHECKOUT_MAIN_PATH + BUNDLE_PROMOTIONS_PATH)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(promotion)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstBundleItemId", is(firstBundleItemId)))
                .andExpect(jsonPath("$.firstItemRequiredQuantity", is(firstItemRequiredQuantity)))
                .andExpect(jsonPath("$.secondBundleItemId", is(secondBundleItemId)))
                .andExpect(jsonPath("$.secondItemRequiredQuantity", is(secondItemRequiredQuantity)))
                .andExpect(jsonPath("$.bundlePrice", is(bundlePrice)));
    }

    @Test
    void shouldFailToCreateNewItemWhenNameIsInvalid() throws Exception {
        // Given
        ItemDTO invalidItem = ItemDTO.builder()
                .price(BigDecimal.valueOf(10.99))
                .build();

        // When/Then
        mockMvc.perform(MockMvcRequestBuilders.post(CHECKOUT_MAIN_PATH + ITEM_PATH)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalidItem)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(MESSAGE_FIELD).value(NAME_ERROR_MESSAGE));
    }

    @Test
    void shouldFailToCreateNewItemWhenPriceIsInvalid() throws Exception {
        // Given
        final String testName = "Test Item";

        ItemDTO invalidItem = ItemDTO.builder()
                .name(testName)
                .build();

        // When/Then
        mockMvc.perform(MockMvcRequestBuilders.post(CHECKOUT_MAIN_PATH + ITEM_PATH)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalidItem)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(MESSAGE_FIELD).value(PRICE_ERROR_MESSAGE));
    }

    @Test
    void shouldFailToCreateQuantityPromotionWhenItemIdIsNull() throws Exception {
        // Given
        final double quantityPromotionPrice = 19.99;

        QuantityPromotionDTO invalidPromotion = QuantityPromotionDTO.builder()
                .requiredQuantity(3)
                .quantityPromotionPrice(BigDecimal.valueOf(quantityPromotionPrice))
                .build();

        // When/Then
        mockMvc.perform(MockMvcRequestBuilders.post(CHECKOUT_MAIN_PATH + QUANTITY_PROMOTIONS_PATH)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalidPromotion)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(MESSAGE_FIELD).value(ITEM_ID_ERROR_MESSAGE));
    }

    @Test
    void shouldFailToCreateQuantityPromotionWhenRequiredQuantityIsNegative() throws Exception {
        // Given
        final long itemId = 1L;
        final int invalidRequiredQuantity = -1;
        final BigDecimal quantityPromotionPrice = BigDecimal.valueOf(19.99);

        QuantityPromotionDTO invalidPromotion = QuantityPromotionDTO.builder()
                .itemId(itemId)
                .requiredQuantity(invalidRequiredQuantity)
                .quantityPromotionPrice(quantityPromotionPrice)
                .build();

        // When/Then
        mockMvc.perform(MockMvcRequestBuilders.post(CHECKOUT_MAIN_PATH + QUANTITY_PROMOTIONS_PATH)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalidPromotion)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(MESSAGE_FIELD).value(REQUIRED_QUANTITY_ERROR_MESSAGE));
    }

    @Test
    void shouldFailToCreateBundlePromotionWhenFirstBundleItemIdIsNull() throws Exception {
        // Given
        final int firstItemRequiredQuantity = 1;
        final long secondBundleItemId = 2L;
        final int secondItemRequiredQuantity = 1;
        final BigDecimal bundlePrice = BigDecimal.valueOf(20.0);

        BundlePromotionDTO invalidPromotion = BundlePromotionDTO.builder()
                .firstItemRequiredQuantity(firstItemRequiredQuantity)
                .secondBundleItemId(secondBundleItemId)
                .secondItemRequiredQuantity(secondItemRequiredQuantity)
                .bundlePrice(bundlePrice)
                .build();

        // When/Then
        mockMvc.perform(MockMvcRequestBuilders.post(CHECKOUT_MAIN_PATH + BUNDLE_PROMOTIONS_PATH)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalidPromotion)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(MESSAGE_FIELD).value(FIRST_BUNDLE_ITEM_ID_ERROR_MESSAGE));
    }

    @Test
    void shouldFailToCreateBundlePromotionWhenBundlePriceIsNegative() throws Exception {
        // Given
        final long firstBundleItemId = 1L;
        final int firstItemRequiredQuantity = 1;
        final long secondBundleItemId = 2L;
        final int secondItemRequiredQuantity = 1;
        final BigDecimal invalidBundlePrice = BigDecimal.valueOf(-10.0);

        BundlePromotionDTO invalidPromotion = BundlePromotionDTO.builder()
                .firstBundleItemId(firstBundleItemId)
                .firstItemRequiredQuantity(firstItemRequiredQuantity)
                .secondBundleItemId(secondBundleItemId)
                .secondItemRequiredQuantity(secondItemRequiredQuantity)
                .bundlePrice(invalidBundlePrice)
                .build();

        // When/Then
        mockMvc.perform(MockMvcRequestBuilders.post(CHECKOUT_MAIN_PATH + BUNDLE_PROMOTIONS_PATH)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalidPromotion)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(MESSAGE_FIELD).value(BUNDLE_PRICE_ERROR_MESSAGE));
    }

    private void scanItem(Long itemId) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(CHECKOUT_MAIN_PATH + SCAN_PATH)
                        .param("itemId", itemId.toString())
                        .session(session))
                .andExpect(status().isAccepted());
    }
}
