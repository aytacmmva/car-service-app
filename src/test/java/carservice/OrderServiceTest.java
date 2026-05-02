package carservice;

import enums.OrderStatus;
import model.Order;
import model.Repairer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.OrderRepository;
import repository.RepairerRepository;
import service.OrderService;

import static org.junit.jupiter.api.Assertions.*;

public class OrderServiceTest {

    private OrderRepository orderRepository;
    private RepairerRepository repairerRepository;
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderRepository = new OrderRepository();
        repairerRepository = new RepairerRepository();
        orderService = new OrderService(orderRepository, repairerRepository);

        repairerRepository.addRepairer(new Repairer(1L, "John Doe"));
    }

    @Test
    void testOpenOrder_SuccessfullyOpens() {
        double price = 150.5;

        Order result = orderService.openOrder(price);

        assertNotNull(result);
        assertEquals(price, result.getPrice());
    }

    @Test
    void testAssignRepairer_SuccessfullyAssigned() {
        Order newOrder = orderService.openOrder(200.0);
        Long id = newOrder.getId();

        assertDoesNotThrow(() -> {
            orderService.assignRepairer(id, 1L);
        });
    }

    @Test
    void testCompleteOrder_ThrowsExceptionIfNoRepairer() {
        Order order = orderService.openOrder(100.0);
        Long id = order.getId();

        Exception ex = assertThrows(IllegalStateException.class, () -> {
            orderService.completeOrder(id);
        });

        assertTrue(ex.getMessage().contains("repairer"));
    }

    @Test
    void testCancelOrder_SuccessfullyCanceled() {
        Order order = orderService.openOrder(300.0);
        Long id = order.getId();

        orderService.cancelOrder(id);

        Order canceledOrder = orderRepository.findById(id);
        assertEquals(OrderStatus.CANCELLED, canceledOrder.getStatus());
    }
}



