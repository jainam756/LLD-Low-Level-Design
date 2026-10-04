package parkinglot;
import java.time.*;
import java.math.BigDecimal;
public final class HourlyFeeStrategy implements FeeStrategy {
    /** Each started hour is billed, with a minimum of one hour, including zero duration. */
    public BigDecimal calculate(ParkingTicket ticket, Instant exitTime) {
        Duration duration = Duration.between(ticket.entryTime(), exitTime);
        if (duration.isNegative()) throw new IllegalArgumentException("Exit precedes entry");
        long hours = duration.getSeconds() / 3600;
        if (duration.getSeconds() % 3600 != 0 || duration.getNano() != 0) hours++;
        return ticket.hourlyRate().multiply(BigDecimal.valueOf(Math.max(1, hours)));
    }
}
