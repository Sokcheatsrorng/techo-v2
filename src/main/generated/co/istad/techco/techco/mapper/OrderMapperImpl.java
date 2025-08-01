package co.istad.techco.techco.mapper;

import co.istad.techco.techco.domain.Order;
import co.istad.techco.techco.features.order.dto.OrderResponse;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-03-03T22:58:19+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.12 (Amazon.com Inc.)"
)
@Component
public class OrderMapperImpl implements OrderMapper {

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Override
    public OrderResponse toOrderResponse(Order order) {
        if ( order == null ) {
            return null;
        }

        OrderResponse.OrderResponseBuilder orderResponse = OrderResponse.builder();

        orderResponse.orderUuid( order.getUuid() );
        orderResponse.totalAmount( order.getTotalAmount() );
        orderResponse.orderItems( orderItemMapper.toOrderItemResponseList( order.getOrderItems() ) );
        if ( order.getStatus() != null ) {
            orderResponse.status( order.getStatus().name() );
        }
        orderResponse.createdAt( order.getCreatedAt() );

        return orderResponse.build();
    }
}
