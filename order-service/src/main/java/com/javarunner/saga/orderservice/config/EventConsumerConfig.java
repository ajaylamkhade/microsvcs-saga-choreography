package com.javarunner.saga.orderservice.config;

import com.javarunner.saga.common.event.PaymentEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
public class EventConsumerConfig {

    @Autowired
    private OrderStatusUpdateHandler orderStatusUpdateHandler;

    @Bean
    public Consumer<PaymentEvent> paymentEventConsumer(){
        //lister payment-event topic
        //will check payment status
        //if payment status completed -> complete the order
        //if payment status failed -> cancel the order
    Consumer<PaymentEvent> paymentEventConsumer = paymentEvent -> orderStatusUpdateHandler.updateOrder(paymentEvent.getPaymentRequestDto().getOrderId(),
              purchaseOrder -> {
                    purchaseOrder.setPaymentStatus(paymentEvent.getPaymentStatus());
              }
            );

            return paymentEventConsumer;

    }
}
