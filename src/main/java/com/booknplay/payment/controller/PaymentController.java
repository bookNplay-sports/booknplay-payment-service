package com.booknplay.payment.controller;

import com.booknplay.payment.dto.PaymentRequest;
import com.booknplay.payment.dto.PaymentResponse;
import com.booknplay.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@Tag(
        name = "Payment Management",
        description = "APIs for payment processing and status retrieval"
)
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    @Operation(
            summary = "Process a payment",
            description = "Allows a user to process a payment for a booking."
    )
    @PostMapping("/process")
    public ResponseEntity<PaymentResponse> processPayment(
            @Valid @RequestBody PaymentRequest request) {
        return ResponseEntity.ok(
                paymentService.processPayment(request)
        );
    }
    @Operation(
            summary = "Get payment status",
            description = "Allows a user to retrieve the status of a payment."
    )
    @GetMapping("/{paymentId}/status")
    public ResponseEntity<PaymentResponse> getPaymentStatus(
            @PathVariable Long paymentId) {
        return ResponseEntity.ok(
                paymentService.getPaymentStatus(paymentId)
        );
    }
}
