package com.booknplay.payment.dto;


import com.booknplay.payment.utility.PaymentMethod;
import com.booknplay.payment.utility.PaymentStatus;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
@Data
@Builder
public class PaymentResponse {
    private Long id;
    private Long bookingId;
    private Double amount;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private LocalDateTime createdAt;
}
