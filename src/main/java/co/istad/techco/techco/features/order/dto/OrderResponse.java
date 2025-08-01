package co.istad.techco.techco.features.order.dto;

import co.istad.techco.techco.features.order.orderItem.dto.OrderItemResponse;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record OrderResponse(

        String orderUuid,

        BigDecimal totalAmount,

        List<OrderItemResponse> orderItems,

        String status,

        LocalDateTime createdAt
) {
}
