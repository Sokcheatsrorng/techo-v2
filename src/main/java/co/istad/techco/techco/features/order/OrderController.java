package co.istad.techco.techco.features.order;

import co.istad.techco.techco.base.BasedMessage;
import co.istad.techco.techco.features.order.dto.OrderRequest;
import co.istad.techco.techco.features.order.dto.OrderResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.beanutils.BaseDynaBeanMapDecorator;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/place-order/{userUuid}/{cartUuid}")
    public OrderResponse placeOrder(@PathVariable(name = "userUuid") String userUuid,
                                    @PathVariable(name = "cartUuid") String cartUuid) {

        return orderService.placeOrder(userUuid, cartUuid);

    }

    @GetMapping("/get-by-user/{userUuid}")
    public List<OrderResponse> getOrderByUser(@PathVariable(name = "userUuid") String userUuid) {

        return orderService.getOrdersByUser(userUuid);

    }

    @PutMapping("/update-status/{orderUuid}")
    public BasedMessage updateOrderStatus(@PathVariable(name = "orderUuid") String orderUuid,
                                          @RequestParam(name = "status") String status) {

        return orderService.updateOrderStatus(orderUuid, status);

    }

    @PutMapping("/cancel-order/{orderUuid}")
    public BasedMessage cancel(@PathVariable(name = "orderUuid") String orderUuid) {

        return orderService.cancelOrder(orderUuid);

    }

    @GetMapping("/{uuid}/{userUuid}")
    public OrderResponse getByUuid(@PathVariable(name = "uuid") String uuid,
                                   @PathVariable(name = "userUuid") String userUuid) {

        return orderService.getByUuid(uuid, userUuid);

    }


}
