package service;


import dto.request.CreateGarageSlotRequest;
import dto.response.GarageSlotResponse;

import java.util.List;

public interface GarageSlotService {

    GarageSlotResponse createGarageSlot(CreateGarageSlotRequest request);

    void deleteGarageSlot(Long slotId);

    List<GarageSlotResponse> listAllSlots();
}
