package com.booknplay.payment.controller;

import com.booknplay.payment.dto.PaymentRequest;
import com.booknplay.payment.dto.PaymentResponse;
import com.booknplay.payment.service.PaymentService;
import com.booknplay.payment.utility.PaymentMethod;
import com.booknplay.payment.utility.PaymentStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PaymentControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(new PaymentController(paymentService))
                .setValidator(new LocalValidatorFactoryBean())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void processPayment_shouldReturnCreatedPaymentResponse() throws Exception {
        PaymentRequest request = new PaymentRequest();
        request.setBookingId(101L);
        request.setAmount(250.0);
        request.setPaymentMethod(PaymentMethod.CARD);
        request.setSimulateSuccess(true);

        PaymentResponse response = PaymentResponse.builder()
                .id(1L)
                .bookingId(101L)
                .amount(250.0)
                .paymentMethod(PaymentMethod.CARD)
                .paymentStatus(PaymentStatus.SUCCESS)
                .createdAt(LocalDateTime.of(2026, 11, 17, 1, 15, 0))
                .build();

        when(paymentService.processPayment(any(PaymentRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/payments/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.bookingId").value(101))
                .andExpect(jsonPath("$.amount").value(250.0))
                .andExpect(jsonPath("$.paymentMethod").value("CARD"))
                .andExpect(jsonPath("$.paymentStatus").value("SUCCESS"));

        verify(paymentService).processPayment(any(PaymentRequest.class));
    }

    @Test
    void getPaymentStatus_shouldReturnPaymentDetails() throws Exception {
        PaymentResponse response = PaymentResponse.builder()
                .id(5L)
                .bookingId(202L)
                .amount(500.0)
                .paymentMethod(PaymentMethod.UPI)
                .paymentStatus(PaymentStatus.PENDING)
                .createdAt(LocalDateTime.of(2026, 11, 16, 21, 10, 0))
                .build();

        when(paymentService.getPaymentStatus(5L)).thenReturn(response);

        mockMvc.perform(get("/api/payments/5/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.bookingId").value(202))
                .andExpect(jsonPath("$.amount").value(500.0))
                .andExpect(jsonPath("$.paymentMethod").value("UPI"))
                .andExpect(jsonPath("$.paymentStatus").value("PENDING"));

        verify(paymentService).getPaymentStatus(5L);
    }

    @Test
    void processPayment_shouldRejectInvalidRequest() throws Exception {
        PaymentRequest invalidRequest = new PaymentRequest();
        invalidRequest.setBookingId(101L);
        invalidRequest.setAmount(-50.0);
        invalidRequest.setPaymentMethod(PaymentMethod.CARD);
        invalidRequest.setSimulateSuccess(true);

        mockMvc.perform(post("/api/payments/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(paymentService);
    }
}
