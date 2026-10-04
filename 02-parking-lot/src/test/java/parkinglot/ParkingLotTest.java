package parkinglot;
import java.util.*;
import java.util.concurrent.*;
import java.math.BigDecimal;
import java.time.*;

public final class ParkingLotTest {
    private static int checks;
    private static final Instant START = Instant.parse("2026-10-04T00:00:00Z");
    private static final class TestClock extends Clock {
        private Instant now = START;
        public ZoneId getZone() { return ZoneOffset.UTC; }
        public Clock withZone(ZoneId zone) { return Clock.fixed(now, zone); }
        public Instant instant() { return now; }
    }
    private static ParkingSpot spot(String id, int floor, int x, SpotType type, String rate) {
        return new ParkingSpot(id, floor, x, 0, type, new BigDecimal(rate));
    }
    private static ParkingLot lot() {
        return new ParkingLot(List.of(
            new ParkingFloor(0, List.of(spot("B",0,8,SpotType.BIKE,"10"), spot("C",0,9,SpotType.CAR,"30"))),
            new ParkingFloor(1, List.of(spot("D",1,1,SpotType.CAR,"20"), spot("T",1,6,SpotType.TRUCK,"80")))
        ), List.of(new Gate("A",0,0), new Gate("Z",10,0)));
    }
    private static ParkingManager manager(ParkingLot lot, CompatibilityStrategy compatibility,
            ParkingStrategy strategy, FeeStrategy fees, Clock clock) {
        return new ParkingManager(lot, compatibility, strategy, fees, clock);
    }
    private static Vehicle car(String id) { return new Vehicle(id, VehicleType.CAR); }
    private static void check(boolean value, String message) {
        checks++; if (!value) throw new AssertionError(message);
    }
    private static void rejects(Runnable action) {
        try { action.run(); } catch (IllegalArgumentException | IllegalStateException expected) { checks++; return; }
        throw new AssertionError("Expected rejection");
    }
    public static void main(String[] args) throws Exception {
        ParkingLot lot = lot(); TestClock clock = new TestClock();
        ParkingManager manager = manager(lot,new ExactTypeCompatibility(),new NearestSpotStrategy(),new HourlyFeeStrategy(),clock);
        rejects(() -> manager.parkVehicle(car("unknown"),"missing"));
        check(manager.activeTicketCount()==0 && manager.availableCount(SpotType.CAR)==2,"Unknown gate preserves state");
        ParkingTicket ticket = manager.parkVehicle(car("ab-1"),"A");
        check(ticket.spotId().equals("D"),"Nearest from gate A");
        rejects(() -> manager.parkVehicle(car(" AB-1 "),"A"));
        check(manager.activeTicketCount()==1 && manager.availableCount(SpotType.CAR)==1,"Duplicate entry preserves state");
        for (ParkingStrategy strategy : List.of(new NearestSpotStrategy(),new LowestFloorStrategy(),new CheapestSpotStrategy())) {
            check(strategy.findSpot(Set.of(SpotType.CAR),new Gate("Z",10,0),lot.inventory()).orElseThrow().id().equals("C"),
                    "Occupied spot removed from every index");
        }
        ParkingTicket second = manager.parkVehicle(car("ab-2"),"Z");
        check(second.spotId().equals("C"),"Nearest from gate Z");
        rejects(() -> manager.parkVehicle(car("full"),"A"));
        rejects(() -> manager.unparkVehicle("forged-id"));
        clock.now = START.minusSeconds(1);
        rejects(() -> manager.unparkVehicle(ticket.id()));
        check(manager.availableCount(SpotType.CAR)==0 && manager.activeTicketCount()==2,"Failed exit retains session");
        clock.now = START.plusSeconds(3600).plusNanos(1);
        ParkingReceipt receipt = manager.unparkVehicle(ticket.id());
        check(receipt.amount().compareTo(new BigDecimal("40"))==0,"Fraction over hour bills second hour");
        check(manager.availableCount(SpotType.CAR)==1 && manager.activeTicketCount()==1,"Exit restores inventory");
        rejects(() -> manager.unparkVehicle(ticket.id()));
        check(lot.inventory().cheapest(Set.of(SpotType.CAR)).orElseThrow().id().equals("D"),"Price index restored");
        check(lot.inventory().lowestFloor(Set.of(SpotType.CAR)).orElseThrow().id().equals("D"),"Floor index restored");
        check(lot.inventory().nearest(Set.of(SpotType.CAR),new Gate("Z",10,0)).orElseThrow().id().equals("D"),"Gate index restored");
        check(manager.parkVehicle(car("ab-1"),"A").spotId().equals("D"),"Registration reusable after exit");
        rejects(() -> manager(lot,new ExactTypeCompatibility(),new NearestSpotStrategy(),new HourlyFeeStrategy(),clock));
        ParkingManager compatible = manager(lot(),new SizeBasedCompatibility(),new NearestSpotStrategy(),new HourlyFeeStrategy(),clock);
        check(compatible.parkVehicle(new Vehicle("bike",VehicleType.BIKE),"A").spotId().equals("D"),
                "Nearest compares all compatible types, not exact type first");
        ParkingManager exact = manager(lot(),new ExactTypeCompatibility(),new NearestSpotStrategy(),new HourlyFeeStrategy(),clock);
        check(exact.parkVehicle(new Vehicle("bike",VehicleType.BIKE),"A").spotId().equals("B"),"Exact compatibility");
        ParkingManager floor = manager(lot(),new ExactTypeCompatibility(),new LowestFloorStrategy(),new HourlyFeeStrategy(),clock);
        check(floor.parkVehicle(car("floor"),"A").spotId().equals("C"),"Floor policy");
        ParkingManager cheapest = manager(lot(),new ExactTypeCompatibility(),new CheapestSpotStrategy(),new HourlyFeeStrategy(),clock);
        check(cheapest.parkVehicle(car("price"),"Z").spotId().equals("D"),"Price policy");
        HourlyFeeStrategy fees = new HourlyFeeStrategy();
        for (long seconds : new long[]{0,1,3599,3600,3601,7200}) {
            long billed = Math.max(1,(seconds+3599)/3600);
            check(fees.calculate(ticket,START.plusSeconds(seconds)).compareTo(new BigDecimal(20*billed))==0,"Fee boundary "+seconds);
        }
        ParkingManager failing = manager(lot(),new ExactTypeCompatibility(),new NearestSpotStrategy(),
                (t,e) -> { throw new IllegalStateException("Fee unavailable"); },clock);
        ParkingTicket failedTicket = failing.parkVehicle(car("fee"),"A");
        rejects(() -> failing.unparkVehicle(failedTicket.id()));
        check(failing.activeTicketCount()==1 && failing.availableCount(SpotType.CAR)==1,"Fee failure preserves occupancy");
        rejects(() -> new ParkingFloor(0,List.of(spot("bad",1,0,SpotType.CAR,"1"))));
        rejects(() -> new ParkingLot(List.of(new ParkingFloor(0,List.of(spot("same",0,0,SpotType.CAR,"1"),
                spot("same",0,1,SpotType.CAR,"1")))),List.of(new Gate("A",0,0))));
        rejects(() -> spot("bad",0,0,SpotType.CAR,"-1"));
        ParkingLot ties = new ParkingLot(List.of(new ParkingFloor(0,List.of(spot("B",0,1,SpotType.CAR,"1"),
                spot("A",0,1,SpotType.CAR,"1")))),List.of(new Gate("A",0,0)));
        ParkingManager tieManager = manager(ties,new ExactTypeCompatibility(),new NearestSpotStrategy(),fees,clock);
        check(tieManager.parkVehicle(car("tie"),"A").spotId().equals("A"),"Stable ID tie-breaker");
        ParkingManager concurrent = manager(lot(),new ExactTypeCompatibility(),new NearestSpotStrategy(),fees,Clock.fixed(START,ZoneOffset.UTC));
        ExecutorService executor = Executors.newFixedThreadPool(8);
        try {
            List<Callable<ParkingTicket>> jobs = new ArrayList<>();
            for (int i=0;i<20;i++) {
                String id = "parallel-"+i;
                jobs.add(() -> { try { return concurrent.parkVehicle(car(id),"A"); }
                    catch (IllegalStateException full) { return null; } });
            }
            Set<String> allocated = new HashSet<>();
            for (Future<ParkingTicket> future : executor.invokeAll(jobs)) {
                ParkingTicket result = future.get();
                if (result!=null) check(allocated.add(result.spotId()),"No duplicate concurrent allocation");
            }
            check(allocated.size()==2 && concurrent.activeTicketCount()==2 && concurrent.availableCount(SpotType.CAR)==0,
                    "Exactly capacity arrivals accepted");
        } finally { executor.shutdownNow(); }
        System.out.println("Passed "+checks+" Parking Lot checks.");
    }
}
