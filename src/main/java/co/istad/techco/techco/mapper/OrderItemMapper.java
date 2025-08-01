package co.istad.techco.techco.mapper;

import co.istad.techco.techco.domain.OrderItem;
import co.istad.techco.techco.features.order.orderItem.dto.OrderItemResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    @Mapping(target = "productName", source = "product.name")
    OrderItemResponse toOrderItemResponse(OrderItem orderItem);

    List<OrderItemResponse> toOrderItemResponseList(List<OrderItem> orderItems);

}
