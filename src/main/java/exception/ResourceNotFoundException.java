package exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }

    public static ResourceNotFoundException order(Long id) {
        return new ResourceNotFoundException("Order not found with id: " + id);
    }

    public static ResourceNotFoundException repairer(Long id) {
        return new ResourceNotFoundException("Repairer not found with id: " + id);
    }

    public static ResourceNotFoundException garageSlot(Long id) {
        return new ResourceNotFoundException("Garage slot not found with id: " + id);
    }
}
