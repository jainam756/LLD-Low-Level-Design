package parkinglot;
import java.util.Set;
public final class SizeBasedCompatibility implements CompatibilityStrategy {
    public Set<SpotType> compatibleTypes(VehicleType type) {
        return switch (type) {
            case BIKE -> Set.of(SpotType.BIKE, SpotType.CAR, SpotType.TRUCK);
            case CAR -> Set.of(SpotType.CAR, SpotType.TRUCK);
            case TRUCK -> Set.of(SpotType.TRUCK);
        };
    }
}
