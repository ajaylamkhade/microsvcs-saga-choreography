package com.javarunner.saga.paymentservice.repository;

import com.javarunner.saga.paymentservice.entity.UserBalance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserBalanceRepository extends JpaRepository<UserBalance ,Integer> {
}
