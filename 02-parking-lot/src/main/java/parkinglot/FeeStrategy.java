package parkinglot;
import java.time.Instant;
import java.math.BigDecimal;
@FunctionalInterface
public interface FeeStrategy {
    BigDecimal calculate(ParkingTicket ticket, Instant exitTime);
}
