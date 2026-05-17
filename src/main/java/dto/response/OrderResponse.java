package dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

public record OrderResponse<OrderStatus, RepairerResponse>(
        Long id,
        BigDecimal price,
        OrderStatus status,
        LocalDateTime openingTimestamp,
        LocalDateTime completionTimestamp,
        Set<RepairerResponse> repairers,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
