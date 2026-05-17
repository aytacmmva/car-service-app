package dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.intellij.lang.annotations.Pattern;

public record CreateGarageSlotRequest(
        @NotBlank(message = "Slot number is required")
        @Size(min = 2, max = 20, message = "Slot number must be between 2 and 20 characters")
        @Pattern("")
        String slotNumber
) {}
