package controller;

import dto.request.CreateGarageSlotRequest;
import dto.response.GarageSlotResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.carservice.service.GarageSlotService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/garage-slots")
@RequiredArgsConstructor
@Tag(name = "Garage Slots", description = "Garage slot management (can be disabled via config)")
public class GarageSlotController {

    private final GarageSlotService garageSlotService;

    @PostMapping
    @Operation(summary = "Create a new garage slot")
    @ApiResponse(responseCode = "201", description = "Garage slot created")
    @ApiResponse(responseCode = "403", description = "Feature is disabled")
    @ApiResponse(responseCode = "409", description = "Slot number already exists")
    public ResponseEntity<GarageSlotResponse> createGarageSlot(
            @Valid @RequestBody CreateGarageSlotRequest request) {
        GarageSlotResponse response = garageSlotService.createGarageSlot(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{slotId}")
    @Operation(summary = "Delete a garage slot")
    @ApiResponse(responseCode = "204", description = "Garage slot deleted")
    @ApiResponse(responseCode = "403", description = "Feature is disabled")
    @ApiResponse(responseCode = "404", description = "Garage slot not found")
    public ResponseEntity<Void> deleteGarageSlot(@PathVariable Long slotId) {
        garageSlotService.deleteGarageSlot(slotId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "List all garage slots")
    public ResponseEntity<List<GarageSlotResponse>> listAllSlots() {
        List<GarageSlotResponse> response = garageSlotService.listAllSlots();
        return ResponseEntity.ok(response);
    }
}
