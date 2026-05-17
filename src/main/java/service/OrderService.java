package service;

import dto.request.AssignRepairersRequest;
import dto.request.CreateOrderRequest;
import dto.response.OrderResponse;
import dto.response.PageResponse;
import enums.OrderSortField;
import enums.OrderStatus;
import model.Order;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.List;

public interface OrderService {

    OrderResponse createOrder(CreateOrderRequest request);

    OrderResponse assignRepairers(Long orderId, AssignRepairersRequest request);

    OrderResponse completeOrder(Long orderId);

    OrderResponse cancelOrder(Long orderId);

    PageResponse<OrderResponse> listOrders(
            int page,
            int size,
            OrderSortField sortBy,
            Sort.Direction direction,
            OrderStatus statusFilter
    );

    OrderResponse getOrderById(Long orderId);


    List<Order> getAllOrders();

    Order openOrder(BigDecimal price);
}