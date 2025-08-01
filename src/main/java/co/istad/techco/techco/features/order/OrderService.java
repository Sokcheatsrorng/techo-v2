package co.istad.techco.techco.features.order;

import co.istad.techco.techco.base.BasedMessage;
import co.istad.techco.techco.domain.Order;
import co.istad.techco.techco.features.order.dto.OrderResponse;

import java.util.List;

public interface OrderService {

    OrderResponse placeOrder(String userUuid, String cartUuid);

    List<OrderResponse> getOrdersByUser(String userUuid);

    BasedMessage updateOrderStatus(String orderUuid, String status);

    BasedMessage cancelOrder(String orderUuid);

    void processOrderStock(Order order);

    OrderResponse getByUuid(String uuid, String userUuid);
}
