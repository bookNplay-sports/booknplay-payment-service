package com.booknplay.payment.controller;

import com.booknplay.payment.dto.PaymentRequest;
import com.booknplay.payment.dto.PaymentResponse;
import com.booknplay.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/process")
    public ResponseEntity<PaymentResponse> processPayment(
            @Valid @RequestBody PaymentRequest request) {
        return ResponseEntity.ok(
                paymentService.processPayment(request)
        );
    }
    @GetMapping("/{paymentId}/status")
    public ResponseEntity<PaymentResponse> getPaymentStatus(
            @PathVariable Long paymentId) {
        return ResponseEntity.ok(
                paymentService.getPaymentStatus(paymentId)
        );
    }
}
