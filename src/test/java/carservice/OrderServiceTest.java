package carservice;

import dto.request.AssignRepairersRequest;
import dto.request.CreateOrderRequest;
import dto.response.OrderResponse;
import enums.OrderStatus;
import exception.BusinessException;
import exception.ResourceNotFoundException;
import impl.OrderServiceImpl;
import com.carservice.mapper.OrderMapper;
import com.carservice.model.Order;
import com.carservice.model.Repairer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.carservice.repository.OrderRepository;
import com.carservice.repository.RepairerRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderService Unit Tests")
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private RepairerRepository repairerRepository;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    private OrderResponse orderResponse;
    private Repairer repairer;

    @BeforeEach
    void setUp() {
        repairer = Repairer.builder()
                .id(1L)
                .name("Test Repairer")
                .build();

        orderResponse = new OrderResponse(
                1L, new BigDecimal("100.00"), OrderStatus.OPENED,
                LocalDateTime.now(), null, Set.of(), LocalDateTime.now(), LocalDateTime.now()
        );
    }


    private Order createTestOrder(OrderStatus status) {
        return Order.builder()
                .id(1L)
                .price(new BigDecimal("100.00"))
                .status(status)
                .openingTimestamp(LocalDateTime.now())
                .repairers(new HashSet<>())
                .build();
    }

    @Nested
    @DisplayName("createOrder tests")
    class CreateOrderTests {

        @Test
        @DisplayName("Should create order successfully")
        void shouldCreateOrderSuccessfully() {
            CreateOrderRequest request = new CreateOrderRequest(new BigDecimal("150.00"));
            Order openedOrder = createTestOrder(OrderStatus.OPENED);

            when(orderRepository.save(any())).thenReturn(openedOrder);
            when(orderMapper.toResponse(any())).thenReturn(orderResponse);

            OrderResponse result = orderService.createOrder(request);

            assertThat(result).isNotNull();
            verify(orderRepository, times(1)).save(any());
            verify(orderMapper, times(1)).toResponse(any());
        }
    }

    @Nested
    @DisplayName("assignRepairers tests")
    class AssignRepairersTests {

        @Test
        @DisplayName("Should assign repairers to opened order")
        void shouldAssignRepairersToOpenedOrder() {
            AssignRepairersRequest request = new AssignRepairersRequest(Set.of(1L));
            Order openedOrder = createTestOrder(OrderStatus.OPENED);

            when(orderRepository.findByIdWithRepairers(1L)).thenReturn(Optional.of(openedOrder));
            when(repairerRepository.findAllByIdIn(Set.of(1L))).thenReturn(List.of(repairer));
            when(orderRepository.save(any())).thenReturn(openedOrder);
            when(orderMapper.toResponse(any())).thenReturn(orderResponse);

            OrderResponse result = orderService.assignRepairers(1L, request);

            assertThat(result).isNotNull();
            verify(orderRepository, times(1)).save(any());
            verify(orderMapper, times(1)).toResponse(any());
        }

        @Test
        @DisplayName("Should throw BusinessException when order is not opened")
        void shouldThrowWhenOrderNotOpened() {
            AssignRepairersRequest request = new AssignRepairersRequest(Set.of(1L));
            Order completedOrder = createTestOrder(OrderStatus.COMPLETED); // Tamamilə fərqli referans

            when(orderRepository.findByIdWithRepairers(1L)).thenReturn(Optional.of(completedOrder));

            assertThatThrownBy(() -> orderService.assignRepairers(1L, request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("OPENED");

            verify(orderRepository, never()).save(any()); // Save metodunun çağrılmadığını mütləq yoxlayırıq
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when order not found")
        void shouldThrowWhenOrderNotFound() {
            AssignRepairersRequest request = new AssignRepairersRequest(Set.of(1L));
            when(orderRepository.findByIdWithRepairers(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> orderService.assignRepairers(99L, request))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("99");
        }
    }

    @Nested
    @DisplayName("completeOrder tests")
    class CompleteOrderTests {

        @Test
        @DisplayName("Should complete order with repairers assigned")
        void shouldCompleteOrderWithRepairers() {
            Order openedOrder = createTestOrder(OrderStatus.OPENED);
            openedOrder.getRepairers().add(repairer);

            when(orderRepository.findByIdWithRepairers(1L)).thenReturn(Optional.of(openedOrder));
            when(orderRepository.save(any())).thenReturn(openedOrder);
            when(orderMapper.toResponse(any())).thenReturn(orderResponse);

            OrderResponse result = orderService.completeOrder(1L);

            assertThat(result).isNotNull();
            assertThat(openedOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED);
            assertThat(openedOrder.getCompletionTimestamp()).isNotNull();

            verify(orderRepository, times(1)).save(any());
            verify(orderMapper, times(1)).toResponse(any());
        }

        @Test
        @DisplayName("Should throw BusinessException when no repairers assigned")
        void shouldThrowWhenNoRepairersAssigned() {
            Order openedOrder = createTestOrder(OrderStatus.OPENED);
            when(orderRepository.findByIdWithRepairers(1L)).thenReturn(Optional.of(openedOrder));

            assertThatThrownBy(() -> orderService.completeOrder(1L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("repairer");

            verify(orderRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw BusinessException when order already cancelled")
        void shouldThrowWhenOrderCancelled() {
            Order cancelledOrder = createTestOrder(OrderStatus.CANCELLED);
            when(orderRepository.findByIdWithRepairers(1L)).thenReturn(Optional.of(cancelledOrder));

            assertThatThrownBy(() -> orderService.completeOrder(1L))
                    .isInstanceOf(BusinessException.class);

            verify(orderRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("cancelOrder tests")
    class CancelOrderTests {

        @Test
        @DisplayName("Should cancel opened order")
        void shouldCancelOpenedOrder() {
            Order openedOrder = createTestOrder(OrderStatus.OPENED);

            when(orderRepository.findByIdWithRepairers(1L)).thenReturn(Optional.of(openedOrder));
            when(orderRepository.save(any())).thenReturn(openedOrder);
            when(orderMapper.toResponse(any())).thenReturn(orderResponse);

            OrderResponse result = orderService.cancelOrder(1L);

            assertThat(result).isNotNull();
            assertThat(openedOrder.getStatus()).isEqualTo(OrderStatus.CANCELLED);

            verify(orderRepository, times(1)).save(any());
            verify(orderMapper, times(1)).toResponse(any());
        }

        @Test
        @DisplayName("Should throw BusinessException when order already completed")
        void shouldThrowWhenOrderCompleted() {
            Order completedOrder = createTestOrder(OrderStatus.COMPLETED);
            when(orderRepository.findByIdWithRepairers(1L)).thenReturn(Optional.of(completedOrder));

            assertThatThrownBy(() -> orderService.cancelOrder(1L))
                    .isInstanceOf(BusinessException.class);

            verify(orderRepository, never()).save(any());
        }
    }
}