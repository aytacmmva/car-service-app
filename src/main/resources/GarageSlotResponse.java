package dto.response;

import java.time.LocalDateTime;

public record GarageSlotResponse(
        Long id,
        String slotNumber,
        Boolean isActive,
        LocalDateTime createdAt
) {}
