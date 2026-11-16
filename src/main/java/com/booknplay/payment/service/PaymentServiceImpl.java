package com.booknplay.payment.service;


import com.booknplay.payment.client.BookingClient;
import com.booknplay.payment.dto.PaymentRequest;
import com.booknplay.payment.dto.PaymentResponse;
import com.booknplay.payment.entity.Payment;
import com.booknplay.payment.exception.BookingConfirmationException;
import com.booknplay.payment.exception.PaymentNotFoundException;
import com.booknplay.payment.repository.PaymentRepository;
import com.booknplay.payment.utility.PaymentStatus;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    private final BookingClient bookingClient;

    @Override
    public PaymentResponse processPayment(PaymentRequest request) {
        // Create payment with PENDING status
        Payment payment = Payment.builder()
                .bookingId(request.getBookingId())
                .amount(request.getAmount())
                .paymentMethod(request.getPaymentMethod())
                .paymentStatus(PaymentStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
        payment = paymentRepository.save(payment);
        // Simulate payment failure
        if (Boolean.FALSE.equals(request.getSimulateSuccess())) {
            payment.setPaymentStatus(PaymentStatus.FAILED);
            payment = paymentRepository.save(payment);
            return buildPaymentResponse(payment);
        }
        // Simulate payment success and confirm booking
        try {
            bookingClient.confirmBooking(payment.getBookingId());
            payment.setPaymentStatus(PaymentStatus.SUCCESS);
            payment = paymentRepository.save(payment);
        } catch (FeignException ex) {
            throw new BookingConfirmationException(
                    ex.status(),
                    ex.contentUTF8()
            );

        }
        return buildPaymentResponse(payment);
    }
    private PaymentResponse buildPaymentResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .bookingId(payment.getBookingId())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod())
                .paymentStatus(payment.getPaymentStatus())
                .createdAt(payment.getCreatedAt())
                .build();
    }

    @Override
    public PaymentResponse getPaymentStatus(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found with id: " + paymentId));

        return PaymentResponse.builder()
                .id(payment.getId())
                .bookingId(payment.getBookingId())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod())
                .paymentStatus(payment.getPaymentStatus())
                .createdAt(payment.getCreatedAt())
                .build();
    }


}

