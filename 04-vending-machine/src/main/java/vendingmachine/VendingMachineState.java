package vendingmachine;

public interface VendingMachineState {
    String name();

    default void selectProduct(VendingMachine machine, String productId) {
        throw new IllegalStateException("Cannot select a product in " + name());
    }

    default void insertMoney(VendingMachine machine, int amount) {
        throw new IllegalStateException("Cannot insert money in " + name());
    }

    default void cancel(VendingMachine machine) {
        throw new IllegalStateException("Cannot cancel in " + name());
    }

    default void dispense(VendingMachine machine) {
        throw new IllegalStateException("Cannot dispense in " + name());
    }
}
