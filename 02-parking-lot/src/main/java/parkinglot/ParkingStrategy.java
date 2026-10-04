package parkinglot;
import java.util.Optional;
import java.util.Set;
@FunctionalInterface
public interface ParkingStrategy {
    Optional<ParkingSpot> findSpot(Set<SpotType> eligibleTypes, Gate entryGate, ParkingSpotInventory inventory);
}
