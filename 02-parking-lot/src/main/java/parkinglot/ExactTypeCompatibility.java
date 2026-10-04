package parkinglot;
import java.util.Set;
public final class ExactTypeCompatibility implements CompatibilityStrategy {
    public Set<SpotType> compatibleTypes(VehicleType type) {
        return Set.of(SpotType.valueOf(type.name()));
    }
}
