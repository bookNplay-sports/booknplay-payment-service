package com.booknplay.payment.dto;

import com.booknplay.payment.utility.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
@Data
public class PaymentRequest {
    @NotNull
    private Long bookingId;
    @NotNull
    @Positive
    private Double amount;
    @NotNull
    private PaymentMethod paymentMethod;
    @NotNull
    private Boolean simulateSuccess;
}
