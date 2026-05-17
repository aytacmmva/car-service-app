package impl;


import config.AppProperties;
import dto.request.CreateGarageSlotRequest;
import dto.response.GarageSlotResponse;
import exception.BusinessException;
import exception.FeatureDisabledException;
import exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mapper.GarageSlotMapper;
import model.GarageSlot;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.GarageSlotRepository;
import service.GarageSlotService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GarageSlotServiceImpl implements GarageSlotService {

    private final GarageSlotRepository garageSlotRepository;
    private final GarageSlotMapper garageSlotMapper;
    private final AppProperties appProperties;

    @Override
    @Transactional
    public GarageSlotResponse createGarageSlot(CreateGarageSlotRequest request) {
        checkFeatureEnabled();
        log.info("Creating garage slot: {}", request.slotNumber());

        if (garageSlotRepository.existsBySlotNumber(request.slotNumber())) {
            throw new BusinessException("Garage slot already exists with number: " + request.slotNumber());
        }

        GarageSlot slot = GarageSlot.builder()
                .slotNumber(request.slotNumber())
                .isActive(true)
                .build();

        GarageSlot savedSlot = garageSlotRepository.save(slot);
        log.info("Garage slot created with id: {}", savedSlot.getId());
        return garageSlotMapper.toResponse(savedSlot);
    }


    @Override
    @Transactional
    public void deleteGarageSlot(Long slotId) {
        checkFeatureEnabled();
        log.info("Deleting garage slot with id: {}", slotId);

        GarageSlot slot = garageSlotRepository.findById(slotId)
                .orElseThrow(() -> ResourceNotFoundException.garageSlot(slotId));

        garageSlotRepository.delete(slot);
        log.info("Garage slot {} deleted successfully", slotId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GarageSlotResponse> listAllSlots() {
        return garageSlotRepository.findAll().stream()
                .map(garageSlotMapper::toResponse)
                .collect(Collectors.toList());
    }

    private void checkFeatureEnabled() {
        if (!appProperties.isGarageSlotManagementEnabled()) {
            throw new FeatureDisabledException("Garage Slot Management");
        }
    }
}
