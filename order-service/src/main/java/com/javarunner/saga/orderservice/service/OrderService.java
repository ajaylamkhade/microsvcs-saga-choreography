package com.javarunner.saga.orderservice.service;

import com.javarunner.saga.common.dto.OrderRequestDto;
import com.javarunner.saga.common.dto.OrderStatus;
import com.javarunner.saga.orderservice.entity.PurchaseOrder;
import com.javarunner.saga.orderservice.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Supplier;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderStatusPublisher orderStatusPublisher;

    public PurchaseOrder createOrder(OrderRequestDto orderRequestDto) {
        PurchaseOrder purchaseOrder= orderRepository.save(convertDtoToEntity(orderRequestDto));
        orderRequestDto.setOrderId(purchaseOrder.getId());
        //produce kafka event with status ORDER_CREATED
        orderStatusPublisher.publishOrderEvent(orderRequestDto,OrderStatus.ORDER_CREATED);
        return  purchaseOrder;
    }

    private PurchaseOrder convertDtoToEntity(OrderRequestDto dto){
        Supplier<PurchaseOrder> purchaseOrderSupplier = () -> new PurchaseOrder();
        PurchaseOrder newPurchaseOrder = purchaseOrderSupplier.get();
        newPurchaseOrder.setUserId(dto.getUserId());
        newPurchaseOrder.setOrderStatus(OrderStatus.ORDER_CREATED);
        newPurchaseOrder.setPrice(dto.getAmount());
        newPurchaseOrder.setProductId(dto.getProductId());

        return newPurchaseOrder;
    }

    public List<PurchaseOrder> getAllOrders() {
        return orderRepository.findAll();
    }
}
