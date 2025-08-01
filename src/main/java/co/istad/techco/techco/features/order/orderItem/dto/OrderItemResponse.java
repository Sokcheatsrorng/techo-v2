package co.istad.techco.techco.features.order.orderItem.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record OrderItemResponse(

        String productName,

        int quantity,

        BigDecimal priceAtPurchase

) {

}
