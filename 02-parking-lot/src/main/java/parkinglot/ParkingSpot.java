package parkinglot;
import java.math.BigDecimal;
import java.util.Objects;
public final class ParkingSpot {
    private final String id;
    private final int floor, x, y;
    private final SpotType type;
    private final BigDecimal hourlyRate;
    private Vehicle vehicle;
    public ParkingSpot(String id, int floor, int x, int y, SpotType type, BigDecimal hourlyRate) {
        if (id == null || id.isBlank() || floor < 0) throw new IllegalArgumentException("Invalid spot ID/floor");
        this.id = id; this.floor = floor; this.x = x; this.y = y;
        this.type = Objects.requireNonNull(type); this.hourlyRate = Objects.requireNonNull(hourlyRate);
        if (hourlyRate.signum() < 0) throw new IllegalArgumentException("Negative rate");
    }
    public String id() { return id; }
    public int floor() { return floor; }
    public SpotType type() { return type; }
    public BigDecimal hourlyRate() { return hourlyRate; }
    public long distanceFrom(Gate gate) {
        return Math.abs((long)x - gate.x()) + Math.abs((long)y - gate.y());
    }
    public boolean isAvailable() { return vehicle == null; }
    void occupy(Vehicle vehicle) {
        if (!isAvailable()) throw new IllegalStateException("Spot occupied");
        this.vehicle = Objects.requireNonNull(vehicle);
    }
    void release() {
        if (isAvailable()) throw new IllegalStateException("Spot already free");
        vehicle = null;
    }
}
