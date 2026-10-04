package parkinglot;
import java.time.Instant;
import java.math.BigDecimal;
public record ParkingTicket(String id, Vehicle vehicle, String spotId, String entryGateId,
                            Instant entryTime, BigDecimal hourlyRate) { }
