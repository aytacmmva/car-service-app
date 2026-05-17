package mapper;

import dto.response.GarageSlotResponse;
import model.GarageSlot;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface GarageSlotMapper {

    GarageSlotResponse toResponse(GarageSlot garageSlot);
}

