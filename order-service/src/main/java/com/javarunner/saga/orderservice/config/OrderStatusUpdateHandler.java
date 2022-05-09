package com.javarunner.saga.orderservice.config;

import com.javarunner.saga.common.dto.OrderRequestDto;
import com.javarunner.saga.common.dto.OrderStatus;
import com.javarunner.saga.common.event.PaymentStatus;
import com.javarunner.saga.orderservice.entity.PurchaseOrder;
import com.javarunner.saga.orderservice.repository.OrderRepository;
import com.javarunner.saga.orderservice.service.OrderStatusPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Configuration
public class OrderStatusUpdateHandler {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderStatusPublisher orderStatusPublisher;

    @Transactional
    public void updateOrder(Integer id, Consumer<PurchaseOrder> consumer){
        orderRepository.findById(id).ifPresent(consumer.andThen((PurchaseOrder po) -> updateConsumerOrder(po)));
    }
    //check if the payment status is completed else mark this order as cancelled
    private void updateConsumerOrder(PurchaseOrder po) {
       boolean isPaymentComplete = PaymentStatus.PAYMENT_COMPLETED.equals(po.getPaymentStatus());
       OrderStatus orderStatus =isPaymentComplete? OrderStatus.ORDER_COMPLETED: OrderStatus.ORDER_CANCELLED;
       po.setOrderStatus(orderStatus);
       if(!isPaymentComplete){
           orderStatusPublisher.publishOrderEvent(convertEntityToDto(po),orderStatus);
       }
    }

    public OrderRequestDto convertEntityToDto(PurchaseOrder po){
        Supplier<OrderRequestDto>  orderRequestDtoSupplier = () -> new OrderRequestDto();
        OrderRequestDto orderRequestDto =orderRequestDtoSupplier.get();
        orderRequestDto.setOrderId(po.getId());
        orderRequestDto.setUserId(po.getUserId());
        orderRequestDto.setAmount(po.getPrice());
        orderRequestDto.setProductId(po.getProductId());
        return orderRequestDto;
    }
}
