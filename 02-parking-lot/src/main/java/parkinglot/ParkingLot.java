package parkinglot;
import java.util.*;
public final class ParkingLot {
    private final List<ParkingFloor> floors;
    private final Map<String, Gate> gates = new LinkedHashMap<>();
    private final ParkingSpotInventory inventory;
    private boolean managed;
    public ParkingLot(List<ParkingFloor> floors, List<Gate> gates) {
        this.floors = List.copyOf(floors);
        if (floors.isEmpty() || gates.isEmpty()) throw new IllegalArgumentException("Need floors and gates");
        Set<Integer> floorIds = new HashSet<>(); Set<String> spotIds = new HashSet<>();
        List<ParkingSpot> spots = new ArrayList<>();
        for (ParkingFloor floor : floors) {
            if (!floorIds.add(floor.number())) throw new IllegalArgumentException("Duplicate floor");
            for (ParkingSpot spot : floor.spots()) {
                if (!spotIds.add(spot.id()) || !spot.isAvailable()) throw new IllegalArgumentException("Duplicate/occupied spot");
                spots.add(spot);
            }
        }
        for (Gate gate : gates)
            if (this.gates.putIfAbsent(gate.id(), gate) != null) throw new IllegalArgumentException("Duplicate gate");
        inventory = new ParkingSpotInventory(spots, List.copyOf(gates));
    }
    public List<ParkingFloor> floors() { return floors; }
    Gate gate(String id) {
        Gate gate = gates.get(id);
        if (gate == null) throw new IllegalArgumentException("Unknown gate");
        return gate;
    }
    ParkingSpotInventory inventory() { return inventory; }
    synchronized void claimManager() {
        if (managed) throw new IllegalStateException("One manager per lot");
        managed = true;
    }
}
