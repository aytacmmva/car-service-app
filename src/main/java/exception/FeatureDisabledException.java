package exception;

public class FeatureDisabledException extends RuntimeException {
    public FeatureDisabledException(String featureName) {
        super("Feature is disabled: " + featureName);
    }
}
