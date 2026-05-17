package carservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.request.AssignRepairersRequest;
import dto.request.CreateOrderRequest;
import enums.OrderStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@DisplayName("Order Integration Tests")
class OrderIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:15-alpine")
                    .withDatabaseName("carservice_test")
                    .withUsername("test")
                    .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should create order and return 201")
    void shouldCreateOrderSuccessfully() throws Exception {

        CreateOrderRequest request =
                new CreateOrderRequest(new BigDecimal("250.00"));

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.price").value(250.00))
                .andExpect(jsonPath("$.status")
                        .value(OrderStatus.OPENED.name()));
    }

    @Test
    @DisplayName("Full order lifecycle - create, assign, complete")
    void fullOrderLifecycle() throws Exception {

        CreateOrderRequest createRequest =
                new CreateOrderRequest(new BigDecimal("500.00"));

        MvcResult createResult =
                mockMvc.perform(post("/api/v1/orders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(createRequest)))
                        .andExpect(status().isCreated())
                        .andReturn();

        String responseBody =
                createResult.getResponse().getContentAsString();

        Long orderId =
                objectMapper.readTree(responseBody)
                        .get("id")
                        .asLong();

        AssignRepairersRequest assignRequest =
                new AssignRepairersRequest(Set.of(1L));

        mockMvc.perform(patch("/api/v1/orders/{id}/repairers", orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(assignRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.repairers").isNotEmpty());

        mockMvc.perform(patch("/api/v1/orders/{id}/complete", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status")
                        .value(OrderStatus.COMPLETED.name()))
                .andExpect(jsonPath("$.completionTimestamp").exists());
    }

    @Test
    @DisplayName("Should return 409 when completing order without repairers")
    void shouldFailWhenCompletingWithoutRepairers() throws Exception {

        CreateOrderRequest request =
                new CreateOrderRequest(new BigDecimal("100.00"));

        MvcResult result =
                mockMvc.perform(post("/api/v1/orders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isCreated())
                        .andReturn();

        Long orderId =
                objectMapper.readTree(result.getResponse()
                                .getContentAsString())
                        .get("id")
                        .asLong();

        mockMvc.perform(patch("/api/v1/orders/{id}/complete", orderId))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Should return paginated orders")
    void shouldReturnPaginatedOrders() throws Exception {

        mockMvc.perform(get("/api/v1/orders")
                        .param("page", "0")
                        .param("size", "5")
                        .param("sortBy", "PRICE")
                        .param("direction", "DESC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(5));
    }

    @Test
    @DisplayName("Should return 400 for invalid price")
    void shouldReturn400ForInvalidPrice() throws Exception {

        String invalidRequest = "{\"price\": -50}";

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors").exists());
    }
}
