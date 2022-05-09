package com.javarunner.saga.paymentservice.service;

import com.javarunner.saga.common.dto.OrderRequestDto;
import com.javarunner.saga.common.dto.PaymentRequestDto;
import com.javarunner.saga.common.event.OrderEvent;
import com.javarunner.saga.common.event.PaymentEvent;
import com.javarunner.saga.common.event.PaymentStatus;
import com.javarunner.saga.paymentservice.entity.UserBalance;
import com.javarunner.saga.paymentservice.entity.UserTransaction;
import com.javarunner.saga.paymentservice.repository.UserBalanceRepository;
import com.javarunner.saga.paymentservice.repository.UserTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class PaymentService {

    @Autowired
    private UserBalanceRepository userBalanceRepository;

    @Autowired
    private UserTransactionRepository userTransactionRepository;

    @PostConstruct
    public void initUserBalanceInDB(){
        userBalanceRepository.saveAll(Stream.of( new UserBalance(101,5000.0),
                new UserBalance(102,3000.0),
                new UserBalance(103,4200.0),
                new UserBalance(104,20000.0),
                new UserBalance(105,999.0)
        ).collect(Collectors.toList()));
    }
    //get the userId
    //check the balance availability
    //if balance sufficient -> Payment completed and deduct amount from DB
    //if payment not sufficient -> cancel the order event and update the amount in DB
    @Transactional
    public PaymentEvent newOrderEvent(OrderEvent orderEvent) {
        OrderRequestDto orderRequestDto =orderEvent.getOrderRequestDto();
        PaymentRequestDto paymentRequestDto = new PaymentRequestDto(orderRequestDto.getUserId(),orderRequestDto.getOrderId(),orderRequestDto.getAmount());
        return userBalanceRepository.findById(orderRequestDto.getUserId()).filter(userBalance -> userBalance.getPrice() > orderRequestDto.getAmount()).
                map(userBalance -> {userBalance.setPrice(userBalance.getPrice() -orderRequestDto.getAmount());
                    userTransactionRepository.save(new UserTransaction(orderRequestDto.getOrderId(),orderRequestDto.getUserId(),orderRequestDto.getAmount()));
                    return new PaymentEvent(paymentRequestDto, PaymentStatus.PAYMENT_COMPLETED);
                }).orElse( new PaymentEvent(paymentRequestDto,PaymentStatus.PAYMENT_FAILED));

    }

    //delete the user transaction if it fails and add the deducted amount(user txn amt) back to user balance
    @Transactional
    public void cancelOrderEvent(OrderEvent orderEvent) {
        userTransactionRepository.findById(orderEvent.getOrderRequestDto().getOrderId()).ifPresent(userTransaction ->
        {
            userTransactionRepository.delete(userTransaction);
            userBalanceRepository.findById(userTransaction.getUserId()).ifPresent(userBalance -> userBalance.setPrice(userBalance.getPrice()+userTransaction.getAmount()));

        });
    }
}
