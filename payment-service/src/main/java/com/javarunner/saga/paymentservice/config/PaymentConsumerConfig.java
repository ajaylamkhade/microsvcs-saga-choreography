package com.javarunner.saga.paymentservice.config;

import com.javarunner.saga.common.dto.OrderStatus;
import com.javarunner.saga.common.event.OrderEvent;
import com.javarunner.saga.common.event.PaymentEvent;
import com.javarunner.saga.paymentservice.service.PaymentService;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Configuration
public class PaymentConsumerConfig {

    @Autowired
    private PaymentService paymentService;

    @Bean
    public Function<Flux<OrderEvent>,Flux<PaymentEvent>> paymentProcessor(){
        return orderEventFlux -> orderEventFlux.flatMap(this::processPayment);
    }

    private Mono<PaymentEvent> processPayment(OrderEvent orderEvent) {
        //get the userId
        //check the balance availabilty
        //if balance sufficient -> Payment completed and deduct amount from DB
        //if payment not sufficeinnt -> cancel the order event and update the amount in DB
        if(OrderStatus.ORDER_CREATED.equals(orderEvent.getOrderStatus())){
            return Mono.fromSupplier(() ->this.paymentService.newOrderEvent(orderEvent));
        }else {
            return Mono.fromRunnable(() -> this.paymentService.cancelOrderEvent(orderEvent));
        }

    }
}
