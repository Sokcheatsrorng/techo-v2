package co.istad.techco.techco.features.payment;

import co.istad.techco.techco.base.BasedMessage;
import co.istad.techco.techco.domain.*;
import co.istad.techco.techco.features.mail.MailService;
import co.istad.techco.techco.features.order.OrderRepository;
import co.istad.techco.techco.features.payment.dto.PaymentResponse;
import co.istad.techco.techco.features.product.ProductRepository;
import co.istad.techco.techco.features.stock.StockRepository;
import co.istad.techco.techco.features.user.UserRepository;
import co.istad.techco.techco.mapper.PaymentMapper;
import co.istad.techco.techco.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerTypePredicate;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final StockRepository stockRepository;
    private final PaymentMapper paymentMapper;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final MailService mailService;

    @Override
    public BasedMessage savePayment(String userUuid, String orderUuid) {

        Order order = orderRepository.findByUuid(orderUuid)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Order not found for payment"
                ));

        // Save payment if MD5 is valid
        Payment payment = new Payment();
        payment.setUuid(Utils.generateUuid());
        payment.setAmount(order.getTotalAmount());
        payment.setCurrency(Payment.CurrencyEnum.USD);
        payment.setPaymentMethod(Payment.PaymentMethod.QR_CODE);
        payment.setTransactionStatus(Payment.PaymentStatus.SUCCESS);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setOrder(order);

        paymentRepository.save(payment);

        // Update order status to PAID
        order.setStatus(Order.OrderStatus.PAID);
        order.setPaidAt(LocalDateTime.now());
        orderRepository.save(order);

        // Update Stock for each product in the order
        order.getOrderItems().forEach(
                item -> {

                    Product product = item.getProduct();

                    Stock stock = new Stock();
                    stock.setUuid(Utils.generateUuid());
                    stock.setReason(Stock.Reason.SOLD);
                    stock.setQuantityDelta(-item.getQuantity());
                    stock.setProduct(product);
                    stock.setAvailability(product.getStockQuantity() - item.getQuantity() > 0);

                    stockRepository.save(stock);

                    int updatedQuantity = product.getStockQuantity() - item.getQuantity();
                    product.setStockQuantity(updatedQuantity);
                    product.setAvailability(updatedQuantity > 0);

                    productRepository.save(product);

                }
        );

        User user = userRepository.findByUuid(userUuid)
                        .orElseThrow(
                                () -> new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "User has not been found"
                                )
                        );

        mailService.sendPaymentReceipt(user, payment, order);

        log.info("Payment successfully saved for Order UUID: {}", orderUuid);
        return new BasedMessage("Payment has been verified and saved successfully.");
    }

    @Override
    public Page<PaymentResponse> getAllPayments(int page, int size, Payment.PaymentStatus status) {

        if (page < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Page must be greater than zero"
            );
        }

        if (size < 1) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Size must be greater than one"
            );
        }

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "paymentDate"));

        Page<Payment> payments;

        if (status != null) {
            payments = paymentRepository.findByTransactionStatus(status, pageRequest);
        } else {
            payments = paymentRepository.findAll(pageRequest);
        }

        return payments.map(paymentMapper::toPaymentResponse);
    }

}

