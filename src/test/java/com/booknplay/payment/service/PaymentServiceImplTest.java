package com.booknplay.payment.service;

import com.booknplay.payment.client.BookingClient;
import com.booknplay.payment.dto.PaymentRequest;
import com.booknplay.payment.dto.PaymentResponse;
import com.booknplay.payment.entity.Payment;
import com.booknplay.payment.exception.PaymentNotFoundException;
import com.booknplay.payment.repository.PaymentRepository;
import com.booknplay.payment.utility.PaymentMethod;
import com.booknplay.payment.utility.PaymentStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private BookingClient bookingClient;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Test
    void processPayment_shouldReturnSuccess_whenBookingIsConfirmed() {
        PaymentRequest request = new PaymentRequest();
        request.setBookingId(101L);
        request.setAmount(250.0);
        request.setPaymentMethod(PaymentMethod.CARD);
        request.setSimulateSuccess(true);

        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment payment = invocation.getArgument(0);
            payment.setId(1L);
            payment.setPaymentStatus(PaymentStatus.SUCCESS);
            return payment;
        });

        PaymentResponse response = paymentService.processPayment(request);

        assertEquals(1L, response.getId());
        assertEquals(101L, response.getBookingId());
        assertEquals(250.0, response.getAmount());
        assertEquals(PaymentMethod.CARD, response.getPaymentMethod());
        assertEquals(PaymentStatus.SUCCESS, response.getPaymentStatus());

        verify(paymentRepository, times(2)).save(any(Payment.class));
        verify(bookingClient).confirmBooking(101L);
    }

    @Test
    void processPayment_shouldReturnFailed_whenSimulationFails() {
        PaymentRequest request = new PaymentRequest();
        request.setBookingId(202L);
        request.setAmount(100.0);
        request.setPaymentMethod(PaymentMethod.UPI);
        request.setSimulateSuccess(false);

        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment payment = invocation.getArgument(0);
            payment.setId(2L);
            return payment;
        });

        PaymentResponse response = paymentService.processPayment(request);

        assertEquals(2L, response.getId());
        assertEquals(202L, response.getBookingId());
        assertEquals(PaymentStatus.FAILED, response.getPaymentStatus());

        verify(paymentRepository, times(2)).save(any(Payment.class));
        verify(bookingClient, never()).confirmBooking(anyLong());
    }

    @Test
    void getPaymentStatus_shouldReturnPaymentDetails_whenPaymentExists() {
        Payment payment = Payment.builder()
                .id(5L)
                .bookingId(303L)
                .amount(500.0)
                .paymentMethod(PaymentMethod.WALET)
                .paymentStatus(PaymentStatus.PENDING)
                .createdAt(LocalDateTime.of(2026, 11, 17, 1, 30, 0))
                .build();

        when(paymentRepository.findById(5L)).thenReturn(Optional.of(payment));

        PaymentResponse response = paymentService.getPaymentStatus(5L);

        assertEquals(5L, response.getId());
        assertEquals(303L, response.getBookingId());
        assertEquals(500.0, response.getAmount());
        assertEquals(PaymentMethod.WALET, response.getPaymentMethod());
        assertEquals(PaymentStatus.PENDING, response.getPaymentStatus());
    }

    @Test
    void getPaymentStatus_shouldThrowException_whenPaymentDoesNotExist() {
        when(paymentRepository.findById(999L)).thenReturn(Optional.empty());

        PaymentNotFoundException exception = assertThrows(PaymentNotFoundException.class,
                () -> paymentService.getPaymentStatus(999L));

        assertEquals("Payment not found with id: 999", exception.getMessage());
    }
}
