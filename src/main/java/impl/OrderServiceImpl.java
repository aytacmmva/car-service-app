package impl;


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
import mapper.OrderMapper;
import model.Repairer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.OrderRepository;
import repository.RepairerRepository;
import service.OrderService;

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
        log.info("Creating new order with price: {}", request.price());

        com.carservice.entity.Order order = com.carservice.entity.Order.builder()
                .price(request.price())
                .status(OrderStatus.OPENED)
                .openingTimestamp(LocalDateTime.now())
                .build();

        com.carservice.entity.Order savedOrder = orderRepository.save(order);
        log.info("Order created with id: {}", savedOrder.getId());
        return orderMapper.toResponse(savedOrder);
    }

    @Override
    @Transactional
    public OrderResponse assignRepairers(Long orderId, AssignRepairersRequest request) {
        log.info("Assigning repairers {} to order {}", request.repairerIds(), orderId);

        com.carservice.entity.Order order = orderRepository.findByIdWithRepairers(orderId)
                .orElseThrow(() -> ResourceNotFoundException.order(orderId));

        if (!order.isOpened()) {
            throw new BusinessException(
                    "Cannot assign repairers to order with status: " + order.getStatus() +
                            ". Order must be in OPENED status.");
        }

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
        com.carservice.entity.Order updatedOrder = orderRepository.save(order);
        log.info("Repairers assigned successfully to order {}", orderId);
        return orderMapper.toResponse(updatedOrder);
    }

    @Override
    @Transactional
    public OrderResponse completeOrder(Long orderId) {
        log.info("Completing order {}", orderId);

        com.carservice.entity.Order order = orderRepository.findByIdWithRepairers(orderId)
                .orElseThrow(() -> ResourceNotFoundException.order(orderId));

        if (!order.isOpened()) {
            throw new BusinessException(
                    "Cannot complete order with status: " + order.getStatus() +
                            ". Order must be in OPENED status.");
        }

        if (!order.hasRepairers()) {
            throw new BusinessException(
                    "Cannot complete order without assigned repairers. " +
                            "Please assign at least one repairer before completing the order.");
        }

        order.setStatus(OrderStatus.COMPLETED);
        order.setCompletionTimestamp(LocalDateTime.now());

        com.carservice.entity.Order completedOrder = orderRepository.save(order);
        log.info("Order {} completed successfully", orderId);
        return orderMapper.toResponse(completedOrder);
    }

    @Override
    @Transactional
    public OrderResponse cancelOrder(Long orderId) {
        log.info("Cancelling order {}", orderId);

        com.carservice.entity.Order order = orderRepository.findByIdWithRepairers(orderId)
                .orElseThrow(() -> ResourceNotFoundException.order(orderId));

        if (!order.isOpened()) {
            throw new BusinessException(
                    "Cannot cancel order with status: " + order.getStatus() +
                            ". Order must be in OPENED status.");
        }

        order.setStatus(OrderStatus.CANCELLED);

        com.carservice.entity.Order cancelledOrder = orderRepository.save(order);
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

        Page<com.carservice.entity.Order> ordersPage;
        if (statusFilter != null) {
            ordersPage = orderRepository.findAllByStatusWithRepairers(statusFilter, pageable);
        } else {
            ordersPage = orderRepository.findAllWithRepairers(pageable);
        }

        Page<OrderResponse> responsePage = ordersPage.map(orderMapper::toResponse);
        return PageResponse.from(responsePage);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long orderId) {
        com.carservice.entity.Order order = orderRepository.findByIdWithRepairers(orderId)
                .orElseThrow(() -> ResourceNotFoundException.order(orderId));
        return orderMapper.toResponse(order);
    }
}
