package com.booknplay.payment.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name = "booking-service")
public interface BookingClient {

    @PutMapping("/api/bookings/{bookingId}/confirm")
    void confirmBooking(@PathVariable("bookingId") Long bookingId);
}
