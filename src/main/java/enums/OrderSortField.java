package enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderSortField {
    ID("id"),
    PRICE("price"),
    OPENING_TIMESTAMP("openingTimestamp"),
    COMPLETION_TIMESTAMP("completionTimestamp"),
    STATUS("status");

    private final String fieldName;
}
