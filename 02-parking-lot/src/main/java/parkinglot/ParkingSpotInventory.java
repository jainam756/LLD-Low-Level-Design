package parkinglot;
import java.util.*;

/** Only free spots are indexed. Every ordering uses immutable metadata plus a unique ID. */
public final class ParkingSpotInventory {
    private final Map<SpotType, NavigableSet<ParkingSpot>> byType = new EnumMap<>(SpotType.class);
    private final Map<SpotType, NavigableSet<ParkingSpot>> byFloor = new EnumMap<>(SpotType.class);
    private final Map<SpotType, NavigableSet<ParkingSpot>> byPrice = new EnumMap<>(SpotType.class);
    private final Map<String, Map<SpotType, NavigableSet<ParkingSpot>>> byDistance = new HashMap<>();
    private final Set<ParkingSpot> available = new HashSet<>();
    public static Comparator<ParkingSpot> floorOrder() {
        return Comparator.comparingInt(ParkingSpot::floor).thenComparing(ParkingSpot::id);
    }
    public static Comparator<ParkingSpot> priceOrder() {
        return Comparator.comparing(ParkingSpot::hourlyRate).thenComparing(ParkingSpot::id);
    }
    public static Comparator<ParkingSpot> distanceOrder(Gate gate) {
        return Comparator.comparingLong((ParkingSpot s) -> s.distanceFrom(gate)).thenComparing(ParkingSpot::id);
    }
    private static Map<SpotType, NavigableSet<ParkingSpot>> index(Comparator<ParkingSpot> comparator) {
        Map<SpotType, NavigableSet<ParkingSpot>> result = new EnumMap<>(SpotType.class);
        for (SpotType type : SpotType.values()) result.put(type, new TreeSet<>(comparator));
        return result;
    }
    ParkingSpotInventory(List<ParkingSpot> spots, List<Gate> gates) {
        byType.putAll(index(Comparator.comparing(ParkingSpot::id)));
        byFloor.putAll(index(floorOrder())); byPrice.putAll(index(priceOrder()));
        for (Gate gate : gates) byDistance.put(gate.id(), index(distanceOrder(gate)));
        for (ParkingSpot spot : spots) add(spot);
    }
    private List<NavigableSet<ParkingSpot>> indexes(ParkingSpot spot) {
        List<NavigableSet<ParkingSpot>> result = new ArrayList<>();
        result.add(byType.get(spot.type())); result.add(byFloor.get(spot.type())); result.add(byPrice.get(spot.type()));
        for (var index : byDistance.values()) result.add(index.get(spot.type()));
        return result;
    }
    void remove(ParkingSpot spot) {
        if (!available.remove(spot)) throw new IllegalStateException("Spot not in available inventory");
        indexes(spot).forEach(set -> set.remove(spot));
    }
    void add(ParkingSpot spot) {
        if (!spot.isAvailable() || !available.add(spot)) throw new IllegalStateException("Spot cannot be added");
        indexes(spot).forEach(set -> set.add(spot));
    }
    boolean contains(ParkingSpot spot) { return available.contains(spot); }
    public int availableCount(SpotType type) { return byType.get(type).size(); }
    private Optional<ParkingSpot> best(Map<SpotType, NavigableSet<ParkingSpot>> index,
            Set<SpotType> eligible, Comparator<ParkingSpot> comparator) {
        return eligible.stream().map(index::get).filter(set -> !set.isEmpty())
                .map(NavigableSet::first).min(comparator);
    }
    public Optional<ParkingSpot> nearest(Set<SpotType> types, Gate gate) {
        var index = byDistance.get(gate.id());
        if (index == null) throw new IllegalArgumentException("Unknown entry gate");
        return best(index, types, distanceOrder(gate));
    }
    public Optional<ParkingSpot> lowestFloor(Set<SpotType> types) { return best(byFloor, types, floorOrder()); }
    public Optional<ParkingSpot> cheapest(Set<SpotType> types) { return best(byPrice, types, priceOrder()); }
}
