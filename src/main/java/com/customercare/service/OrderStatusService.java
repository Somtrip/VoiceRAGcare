package com.customercare.service;

import com.customercare.model.OrderStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class OrderStatusService {

    private final Map<String, OrderStatus> mockOrders = new HashMap<>();
    private final Random random = new Random();

    public OrderStatusService() {
        initializeMockOrders();
    }

    private void initializeMockOrders() {
        mockOrders.put("12345", new OrderStatus(
            "12345",
            "Shipped",
            "TRK123456789",
            LocalDateTime.now().plusDays(2),
            "John Doe"
        ));

        mockOrders.put("67890", new OrderStatus(
            "67890",
            "Processing",
            "TRK987654321",
            LocalDateTime.now().plusDays(3),
            "Jane Smith"
        ));

        mockOrders.put("11111", new OrderStatus(
            "11111",
            "Delivered",
            "TRK111111111",
            LocalDateTime.now().minusDays(1),
            "Bob Johnson"
        ));
    }

    public OrderStatus getOrderStatus(String orderId) {
        if (mockOrders.containsKey(orderId)) {
            return mockOrders.get(orderId);
        }

        return generateRandomOrderStatus(orderId);
    }

    private OrderStatus generateRandomOrderStatus(String orderId) {
        String[] statuses = {"Processing", "Shipped", "Out for Delivery", "Delivered"};
        String[] names = {"Customer", "John Doe", "Jane Smith", "Mike Wilson", "Sarah Davis"};

        String status = statuses[random.nextInt(statuses.length)];
        String trackingNumber = "TRK" + String.format("%09d", random.nextInt(1000000000));
        String customerName = names[random.nextInt(names.length)];

        LocalDateTime estimatedDelivery;
        switch (status) {
            case "Processing":
                estimatedDelivery = LocalDateTime.now().plusDays(3 + random.nextInt(3));
                break;
            case "Shipped":
                estimatedDelivery = LocalDateTime.now().plusDays(1 + random.nextInt(2));
                break;
            case "Out for Delivery":
                estimatedDelivery = LocalDateTime.now().plusHours(2 + random.nextInt(6));
                break;
            case "Delivered":
                estimatedDelivery = LocalDateTime.now().minusHours(1 + random.nextInt(48));
                break;
            default:
                estimatedDelivery = LocalDateTime.now().plusDays(2);
        }

        return new OrderStatus(orderId, status, trackingNumber, estimatedDelivery, customerName);
    }

    public String getOrderStatusResponse(String orderId) {
        try {
            OrderStatus order = getOrderStatus(orderId);

            StringBuilder response = new StringBuilder();
            response.append("Here's the status for order ").append(orderId).append(": ");
            response.append("Status is ").append(order.getStatus()).append(". ");

            if (order.getTrackingNumber() != null) {
                response.append("Tracking number is ").append(order.getTrackingNumber()).append(". ");
            }

            if (order.getEstimatedDelivery() != null) {
                if ("Delivered".equals(order.getStatus())) {
                    response.append("Your order was delivered.");
                } else {
                    response.append("Estimated delivery is ").append(
                        order.getEstimatedDelivery().toLocalDate().toString()
                    ).append(".");
                }
            }

            return response.toString();

        } catch (Exception e) {
            return "I'm sorry, I couldn't retrieve the status for order " + orderId + ". Please check the order number and try again.";
        }
    }
}
