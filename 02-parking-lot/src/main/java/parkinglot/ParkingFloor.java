package parkinglot;
import java.util.List;
public record ParkingFloor(int number, List<ParkingSpot> spots) {
    public ParkingFloor {
        spots = List.copyOf(spots);
        if (number < 0 || spots.stream().anyMatch(s -> s.floor() != number))
            throw new IllegalArgumentException("Spot floor does not match its container");
    }
}
