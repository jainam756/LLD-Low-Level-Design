package parkinglot;
import java.util.Set;
@FunctionalInterface
public interface CompatibilityStrategy {
    Set<SpotType> compatibleTypes(VehicleType vehicleType);
}
