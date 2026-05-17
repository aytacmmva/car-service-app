package impl;

import com.carservice.model.Order;
import com.carservice.model.Repairer;
import dto.request.AssignRepairersRequest;
import dto.request.CreateOrderRequest;
import dto.response.OrderResponse;
import dto.response.PageResponse;
import enums.OrderSortField;
import enums.OrderStatus;
import exception.BusinessException;
import exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.carservice.mapper.OrderMapper;
import com.carservice.repository.OrderRepository;
import com.carservice.repository.RepairerRepository;
import com.carservice.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final RepairerRepository repairerRepository;
    private final OrderMapper orderMapper;

    @Override
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        log.debug("Creating new order with price: {}", request.price()); // ✅ info → debug

        Order order = Order.builder()
                .price(request.price())
                .status(OrderStatus.OPENED)
                .openingTimestamp(LocalDateTime.now())
                .build();

        Order savedOrder = orderRepository.save(order);
        log.info("Order created with id: {}", savedOrder.getId());
        return orderMapper.toResponse(savedOrder);
    }

    @Override
    @Transactional
    public OrderResponse assignRepairers(Long orderId, AssignRepairersRequest request) {
        log.debug("Assigning repairers {} to order {}", request.repairerIds(), orderId); // ✅ debug

        Order order = orderRepository.findByIdWithRepairers(orderId)
                .orElseThrow(() -> ResourceNotFoundException.order(orderId));

        validateOrderIsOpened(order, "assign repairers to");

        List<Repairer> repairers = repairerRepository.findAllByIdIn(request.repairerIds());

        Set<Long> foundIds = repairers.stream()
                .map(Repairer::getId)
                .collect(Collectors.toSet());

        Set<Long> notFoundIds = request.repairerIds().stream()
                .filter(id -> !foundIds.contains(id))
                .collect(Collectors.toSet());

        if (!notFoundIds.isEmpty()) {
            throw new ResourceNotFoundException("Repairers not found with ids: " + notFoundIds);
        }

        order.getRepairers().addAll(repairers);
        Order updatedOrder = orderRepository.save(order);
        log.info("Repairers assigned successfully to order {}", orderId);
        return orderMapper.toResponse(updatedOrder);
    }

    @Override
    @Transactional
    public OrderResponse completeOrder(Long orderId) {
        log.debug("Completing order {}", orderId); // ✅ debug

        Order order = orderRepository.findByIdWithRepairers(orderId)
                .orElseThrow(() -> ResourceNotFoundException.order(orderId));

        validateOrderIsOpened(order, "complete");

        if (!order.hasRepairers()) {
            throw new BusinessException(
                    "Cannot complete order without assigned repairers. " +
                            "Please assign at least one repairer before completing the order.");
        }

        order.setStatus(OrderStatus.COMPLETED);
        order.setCompletionTimestamp(LocalDateTime.now());

        Order completedOrder = orderRepository.save(order);
        log.info("Order {} completed successfully", orderId);
        return orderMapper.toResponse(completedOrder);
    }

    @Override
    @Transactional
    public OrderResponse cancelOrder(Long orderId) {
        log.debug("Cancelling order {}", orderId); // ✅ debug

        Order order = orderRepository.findByIdWithRepairers(orderId)
                .orElseThrow(() -> ResourceNotFoundException.order(orderId));

        validateOrderIsOpened(order, "cancel");

        order.setStatus(OrderStatus.CANCELLED);

        Order cancelledOrder = orderRepository.save(order);
        log.info("Order {} cancelled successfully", orderId);
        return orderMapper.toResponse(cancelledOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<OrderResponse> listOrders(
            int page,
            int size,
            OrderSortField sortBy,
            Sort.Direction direction,
            OrderStatus statusFilter) {

        Sort sort = Sort.by(direction, sortBy.getFieldName());
        Pageable pageable = PageRequest.of(page, size, sort);

        // ✅ Sadələşdirilmiş — şərt bir xəttə sıxıldı
        Page<Order> ordersPage = (statusFilter != null)
                ? orderRepository.findAllByStatusWithRepairers(statusFilter, pageable)
                : orderRepository.findAllWithRepairers(pageable);

        return PageResponse.from(ordersPage.map(orderMapper::toResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long orderId) {
        Order order = orderRepository.findByIdWithRepairers(orderId)
                .orElseThrow(() -> ResourceNotFoundException.order(orderId));
        return orderMapper.toResponse(order);
    }

    @Override
    public List<Order> getAllOrders() {
        return List.of();
    }

    @Override
    public Order openOrder(BigDecimal price) {
        return null;
    }

    // Yeni köməkçi metod — 3 yerdə təkrarlanan status yoxlaması
    private void validateOrderIsOpened(Order order, String action) {
        if (!order.isOpened()) {
            throw new BusinessException(
                    "Cannot " + action + " order with status: " + order.getStatus() +
                            ". Order must be in OPENED status.");
        }
    }
}
