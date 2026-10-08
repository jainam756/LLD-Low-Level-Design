package vendingmachine;

public interface Dispenser {
    // Only handles physical dispensing. No inventory or payment operations.
    void dispense(Product product);
}
