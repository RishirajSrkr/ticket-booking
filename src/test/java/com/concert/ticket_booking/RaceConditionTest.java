package com.concert.ticket_booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RaceConditionTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testConcurrentBooking() throws InterruptedException {
        // Setup
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJyaXNoaUB0ZXN0LmNvbSIsInJvbGUiOiJVU0VSIiwiaWF0IjoxNzY4OTM3ODQ5LCJleHAiOjE3NjkwMjQyNDl9.tlhlXMT804xY1ODjV5oDW7AfzMhA5s9NTyhEHUcJT24"; // Get from /api/auth/login
        Long seatId = 5L;
        int numberOfUsers = 100;

        ExecutorService executor = Executors.newFixedThreadPool(numberOfUsers);
        CountDownLatch latch = new CountDownLatch(numberOfUsers);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        // Simulate 100 users booking same seat
        for (int i = 0; i < numberOfUsers; i++) {
            executor.submit(() -> {
                try {
                    latch.countDown();
                    latch.await(); // All threads start at same time

                    HttpHeaders headers = new HttpHeaders();
                    headers.set("Authorization", "Bearer " + token);
                    headers.setContentType(MediaType.APPLICATION_JSON);

                    String body = "{\"showId\":1,\"seatId\":" + seatId + "}";
                    HttpEntity<String> request = new HttpEntity<>(body, headers);

                    ResponseEntity<String> response = restTemplate.postForEntity(
                            "/api/bookings",
                            request,
                            String.class
                    );

                    if (response.getStatusCode() == HttpStatus.CREATED) {
                        successCount.incrementAndGet();
                        System.out.println("✅ SUCCESS: " + Thread.currentThread().getName());
                    } else {
                        failureCount.incrementAndGet();
                        System.out.println("❌ FAILED: " + response.getBody());
                    }
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                    System.out.println("❌ ERROR: " + e.getMessage());
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(30, TimeUnit.SECONDS);

        // Results
        System.out.println("\n========== RACE CONDITION TEST RESULTS ==========");
        System.out.println("Total requests: " + numberOfUsers);
        System.out.println("✅ Successful bookings: " + successCount.get());
        System.out.println("❌ Failed bookings: " + failureCount.get());
        System.out.println("=================================================");

        // Verify only 1 succeeded
        assert successCount.get() == 1 : "FAIL: Multiple users booked same seat!";
        assert failureCount.get() == 99 : "FAIL: Wrong number of failures!";

        System.out.println("✅ TEST PASSED: Redis locking prevented race condition!");
    }
}