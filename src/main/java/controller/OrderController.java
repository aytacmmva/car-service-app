package controller;


import dto.request.AssignRepairersRequest;
import dto.request.CreateOrderRequest;
import dto.response.OrderResponse;
import dto.response.PageResponse;
import enums.OrderSortField;
import enums.OrderStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Car service order management API")
public class OrderController {

    private final com.carservice.service.OrderService orderService;

    @PostMapping
    @Operation(summary = "Create a new order", description = "Opens a new service order with the given price")
    @ApiResponse(responseCode = "201", description = "Order created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request data")
    public ResponseEntity<OrderResponse> createOrder(
            @Valid @RequestBody CreateOrderRequest request) {
        OrderResponse response = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{orderId}/repairers")
    @Operation(summary = "Assign repairers to order",
            description = "Assigns one or more repairers to an OPENED order")
    @ApiResponse(responseCode = "200", description = "Repairers assigned successfully")
    @ApiResponse(responseCode = "404", description = "Order or repairer not found")
    @ApiResponse(responseCode = "409", description = "Order is not in OPENED status")
    public ResponseEntity<OrderResponse> assignRepairers(
            @PathVariable Long orderId,
            @Valid @RequestBody AssignRepairersRequest request) {
        OrderResponse response = orderService.assignRepairers(orderId, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{orderId}/complete")
    @Operation(summary = "Complete an order",
            description = "Marks an OPENED order with assigned repairers as COMPLETED")
    @ApiResponse(responseCode = "200", description = "Order completed successfully")
    @ApiResponse(responseCode = "404", description = "Order not found")
    @ApiResponse(responseCode = "409", description = "Order cannot be completed")
    public ResponseEntity<OrderResponse> completeOrder(@PathVariable Long orderId) {
        OrderResponse response = orderService.completeOrder(orderId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{orderId}/cancel")
    @Operation(summary = "Cancel an order",
            description = "Cancels an OPENED order")
    @ApiResponse(responseCode = "200", description = "Order cancelled successfully")
    @ApiResponse(responseCode = "404", description = "Order not found")
    @ApiResponse(responseCode = "409", description = "Order is not in OPENED status")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable Long orderId) {
        OrderResponse response = orderService.cancelOrder(orderId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "List orders with pagination",
            description = "Returns paginated list of orders, sortable by ID, price, timestamps, and status")
    public ResponseEntity<PageResponse<OrderResponse>> listOrders(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "ID") OrderSortField sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "ASC") Sort.Direction direction,
            @Parameter(description = "Filter by status") @RequestParam(required = false) OrderStatus status) {

        PageResponse<OrderResponse> response = orderService.listOrders(page, size, sortBy, direction, status);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "Get order by ID")
    @ApiResponse(responseCode = "200", description = "Order found")
    @ApiResponse(responseCode = "404", description = "Order not found")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long orderId) {
        OrderResponse response = orderService.getOrderById(orderId);
        return ResponseEntity.ok(response);
    }
}

