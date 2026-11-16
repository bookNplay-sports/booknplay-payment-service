package com.booknplay.payment.service;

import com.booknplay.payment.dto.PaymentRequest;
import com.booknplay.payment.dto.PaymentResponse;
public interface PaymentService {

    PaymentResponse processPayment(PaymentRequest request);

    PaymentResponse getPaymentStatus(Long paymentId);
}