package dto.request;

import jakarta.validation.constraints.NotEmpty;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.Set;

public record AssignRepairersRequest(
        @NotNull()
        @NotEmpty(message = "At least one repairer ID must be provided")
        Set<Long> repairerIds
) {}
