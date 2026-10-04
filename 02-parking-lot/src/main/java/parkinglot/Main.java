package parkinglot;
import java.util.List;
import java.math.BigDecimal;
import java.time.Clock;
public final class Main {
    public static void main(String[] args) {
        ParkingLot lot = new ParkingLot(List.of(new ParkingFloor(0, List.of(
            new ParkingSpot("B1", 0, 1, 0, SpotType.BIKE, new BigDecimal("20.00")),
            new ParkingSpot("C1", 0, 2, 0, SpotType.CAR, new BigDecimal("50.00")),
            new ParkingSpot("C2", 0, 4, 0, SpotType.CAR, new BigDecimal("50.00")),
            new ParkingSpot("T1", 0, 8, 0, SpotType.TRUCK, new BigDecimal("100.00"))
        ))), List.of(new Gate("ENTRY", 0, 0)));
        ParkingManager manager = new ParkingManager(lot, new ExactTypeCompatibility(),
                new NearestSpotStrategy(), new HourlyFeeStrategy(), Clock.systemUTC());
        ParkingTicket ticket = manager.parkVehicle(new Vehicle("GJ-01-1234", VehicleType.CAR), "ENTRY");
        System.out.println("Parked: " + ticket);
        System.out.println("Available CAR spots: " + manager.availableCount(SpotType.CAR));
        System.out.println("Exit receipt (INR): " + manager.unparkVehicle(ticket.id()));
        System.out.println("Available CAR spots after exit: " + manager.availableCount(SpotType.CAR));
    }
}
