package model;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.OrderService;

import java.io.IOException;
import java.util.List;

@WebServlet("/orders/*")
public class OrderServlet extends HttpServlet {
    private OrderService orderService; // Constructor və ya init() metodunda mənimsədin
    private ObjectMapper objectMapper = new ObjectMapper();

    // Sifarişlərin siyahısını gətirmək (GET /orders)
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<Order> orders = orderService.getAllOrders(); // Mövcud metodunuz
        resp.setContentType("application/json");
        objectMapper.writeValue(resp.getWriter(), orders);
        resp.getWriter().write("Hello World!");
    }

    // Yeni sifariş yaratmaq (POST /orders)
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // Request body-dən gələn məlumatı oxumaq
        Order orderRequest = objectMapper.readValue(req.getInputStream(), Order.class);
        Order createdOrder = orderService.openOrder(orderRequest.getPrice());

        resp.setStatus(HttpServletResponse.SC_CREATED);
        resp.setContentType("application/json");
        objectMapper.writeValue(resp.getWriter(), createdOrder);
    }
}