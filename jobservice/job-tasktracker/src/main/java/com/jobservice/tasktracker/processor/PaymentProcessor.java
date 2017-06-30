package com.jobservice.tasktracker.processor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Created by ningyang on 2017/7/1.
 */
@Service
@Slf4j
public class PaymentProcessor {

    public void processPayment(long amount, String account){
        log.info(String.format("process account: %s add amount %d)", account, amount));
    }
}
