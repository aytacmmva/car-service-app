package dto.response;

import lombok.Builder;
import java.time.LocalDateTime;

@Builder
public record GarageSlotResponse(
        Long id,
        String slotNumber,
        Boolean isActive,
        LocalDateTime createdAt
) {}