package dto.request;

import org.antlr.v4.runtime.misc.NotNull;

import java.math.BigDecimal;

public record CreateOrderRequest(
        @NotNull()
        BigDecimal price
) {}
