package dto.response;

import java.time.LocalDateTime;

public record RepairerResponse(
        Long id,
        String name,
        LocalDateTime createdAt
) {}
