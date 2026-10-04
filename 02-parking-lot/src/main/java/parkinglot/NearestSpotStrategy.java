package parkinglot;
import java.util.Optional;
import java.util.Set;
public final class NearestSpotStrategy implements ParkingStrategy {
    public Optional<ParkingSpot> findSpot(Set<SpotType> types, Gate gate, ParkingSpotInventory inventory) {
        return inventory.nearest(types, gate);
    }
}
