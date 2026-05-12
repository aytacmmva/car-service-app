package carservice;

import enums.OrderStatus;
import model.Order;
import model.Repairer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import repository.OrderRepository;
import repository.RepairerRepository;

import service.OrderService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private RepairerRepository repairerRepository;

    @InjectMocks
    private OrderService orderService;

    private Order order;

    @BeforeEach
    void setUp() {

        order = new Order(1L, 150.0);

    }

    @Test
    void testOpenOrder_SuccessfullyOpens() {

        when(orderRepository.save(any(Order.class)))
                .thenReturn(order);

        Order result = orderService.openOrder(150.0);

        assertNotNull(result);

        assertEquals(150.0, result.getPrice());

        verify(orderRepository, times(1))
                .save(any(Order.class));
    }

    @Test
    void testAssignRepairer_SuccessfullyAssigned() {

        Repairer repairer =
                new Repairer(1L, "John Doe");

        when(orderRepository.findById(1L))
                .thenReturn(order);

        when(repairerRepository.findById(1L))
                .thenReturn(repairer);

        assertDoesNotThrow(() -> {
            orderService.assignRepairer(1L, 1L);
        });

        verify(orderRepository, times(1))
                .findById(1L);

        verify(repairerRepository, times(1))
                .findById(1L);
    }

    @Test
    void testCompleteOrder_ThrowsExceptionIfNoRepairer() {

        when(orderRepository.findById(1L))
                .thenReturn(order);

        Exception ex = assertThrows(
                IllegalStateException.class,
                () -> {
                    orderService.completeOrder(1L);
                }
        );

        assertTrue(
                ex.getMessage().contains("repairer")
        );
    }

    @Test
    void testCancelOrder_SuccessfullyCanceled() {

        when(orderRepository.findById(1L))
                .thenReturn(order);

        orderService.cancelOrder(1L);

        assertEquals(
                OrderStatus.CANCELLED,
                order.getStatus()
        );
    }
}



