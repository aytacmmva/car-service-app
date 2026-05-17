package mapper;

import com.carservice.entity.Order;

import dto.response.OrderResponse;
import dto.response.RepairerResponse;
import model.Repairer;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface OrderMapper {

    OrderResponse toResponse(Order order);

    RepairerResponse toRepairerResponse(Repairer repairer);
}
