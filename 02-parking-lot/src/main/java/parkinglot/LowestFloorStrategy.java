package parkinglot;
import java.util.Optional;
import java.util.Set;
public final class LowestFloorStrategy implements ParkingStrategy {
    public Optional<ParkingSpot> findSpot(Set<SpotType> types, Gate gate, ParkingSpotInventory inventory) {
        return inventory.lowestFloor(types);
    }
}
