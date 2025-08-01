package co.istad.techco.techco.mapper;

import co.istad.techco.techco.domain.OrderItem;
import co.istad.techco.techco.domain.Product;
import co.istad.techco.techco.features.order.orderItem.dto.OrderItemResponse;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-03-03T22:58:19+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.12 (Amazon.com Inc.)"
)
@Component
public class OrderItemMapperImpl implements OrderItemMapper {

    @Override
    public OrderItemResponse toOrderItemResponse(OrderItem orderItem) {
        if ( orderItem == null ) {
            return null;
        }

        OrderItemResponse.OrderItemResponseBuilder orderItemResponse = OrderItemResponse.builder();

        orderItemResponse.productName( orderItemProductName( orderItem ) );
        if ( orderItem.getQuantity() != null ) {
            orderItemResponse.quantity( orderItem.getQuantity() );
        }
        orderItemResponse.priceAtPurchase( orderItem.getPriceAtPurchase() );

        return orderItemResponse.build();
    }

    @Override
    public List<OrderItemResponse> toOrderItemResponseList(List<OrderItem> orderItems) {
        if ( orderItems == null ) {
            return null;
        }

        List<OrderItemResponse> list = new ArrayList<OrderItemResponse>( orderItems.size() );
        for ( OrderItem orderItem : orderItems ) {
            list.add( toOrderItemResponse( orderItem ) );
        }

        return list;
    }

    private String orderItemProductName(OrderItem orderItem) {
        if ( orderItem == null ) {
            return null;
        }
        Product product = orderItem.getProduct();
        if ( product == null ) {
            return null;
        }
        String name = product.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }
}
