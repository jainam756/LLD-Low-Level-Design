package parkinglot;
import java.time.Instant;
import java.math.BigDecimal;
public record ParkingReceipt(String ticketId, Instant exitTime, BigDecimal amount) { }
