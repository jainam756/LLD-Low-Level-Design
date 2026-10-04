package parkinglot;
import java.util.*;
import java.time.*;
import java.math.BigDecimal;

/** Serializes all park/exit/count operations for its exclusively owned lot. */
public final class ParkingManager {
    private record Session(ParkingTicket ticket, ParkingSpot spot) { }
    private final ParkingLot lot;
    private final CompatibilityStrategy compatibility;
    private final ParkingStrategy parkingStrategy;
    private final FeeStrategy feeStrategy;
    private final Clock clock;
    private final Map<String, Session> activeTickets = new HashMap<>();
    private final Set<String> parkedRegistrations = new HashSet<>();
    public ParkingManager(ParkingLot lot, CompatibilityStrategy compatibility, ParkingStrategy parkingStrategy,
                          FeeStrategy feeStrategy, Clock clock) {
        this.lot = Objects.requireNonNull(lot); this.compatibility = Objects.requireNonNull(compatibility);
        this.parkingStrategy = Objects.requireNonNull(parkingStrategy); this.feeStrategy = Objects.requireNonNull(feeStrategy);
        this.clock = Objects.requireNonNull(clock); lot.claimManager();
    }
    public synchronized ParkingTicket parkVehicle(Vehicle vehicle, String entryGateId) {
        Objects.requireNonNull(vehicle);
        Gate gate = lot.gate(entryGateId);
        if (parkedRegistrations.contains(vehicle.registration())) throw new IllegalStateException("Vehicle already parked");
        Set<SpotType> eligible = Set.copyOf(compatibility.compatibleTypes(vehicle.type()));
        ParkingSpot spot = parkingStrategy.findSpot(eligible, gate, lot.inventory())
                .orElseThrow(() -> new IllegalStateException("No compatible spot available"));
        if (!eligible.contains(spot.type()) || !lot.inventory().contains(spot) || !spot.isAvailable())
            throw new IllegalStateException("Strategy returned an invalid spot");
        ParkingTicket ticket = new ParkingTicket(UUID.randomUUID().toString(), vehicle, spot.id(), gate.id(),
                clock.instant(), spot.hourlyRate());
        lot.inventory().remove(spot); spot.occupy(vehicle);
        activeTickets.put(ticket.id(), new Session(ticket, spot));
        parkedRegistrations.add(vehicle.registration());
        return ticket;
    }
    public synchronized ParkingReceipt unparkVehicle(String ticketId) {
        Session session = activeTickets.get(ticketId);
        if (session == null) throw new IllegalArgumentException("Unknown or already closed ticket");
        Instant exit = clock.instant();
        if (exit.isBefore(session.ticket.entryTime())) throw new IllegalArgumentException("Exit precedes entry");
        BigDecimal amount = Objects.requireNonNull(feeStrategy.calculate(session.ticket, exit));
        if (amount.signum() < 0) throw new IllegalStateException("Invalid negative fee");
        ParkingReceipt receipt = new ParkingReceipt(ticketId, exit, amount);
        session.spot.release(); lot.inventory().add(session.spot);
        activeTickets.remove(ticketId); parkedRegistrations.remove(session.ticket.vehicle().registration());
        return receipt;
    }
    public synchronized int availableCount(SpotType type) { return lot.inventory().availableCount(type); }
    public synchronized int activeTicketCount() { return activeTickets.size(); }
}
