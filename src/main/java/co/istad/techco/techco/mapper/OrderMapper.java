package co.istad.techco.techco.mapper;

import co.istad.techco.techco.domain.Order;
import co.istad.techco.techco.features.order.dto.OrderResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = OrderItemMapper.class, unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface OrderMapper {

    @Mapping(target = "orderUuid", source = "uuid")
    OrderResponse toOrderResponse(Order order);

}
