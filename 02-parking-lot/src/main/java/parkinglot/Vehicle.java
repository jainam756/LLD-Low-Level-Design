package parkinglot;
import java.util.Objects;
import java.util.Locale;
public record Vehicle(String registration, VehicleType type) {
    public Vehicle {
        Objects.requireNonNull(registration); Objects.requireNonNull(type);
        registration = registration.trim().toUpperCase(Locale.ROOT);
        if (registration.isEmpty()) throw new IllegalArgumentException("Registration is required");
    }
}
