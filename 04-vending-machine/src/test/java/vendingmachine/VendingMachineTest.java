package vendingmachine;

/** Dependency-free smoke tests: run with java -cp out vendingmachine.VendingMachineTest */
public final class VendingMachineTest {
    private static int checks;

    private static void check(boolean condition, String message) {
        checks++;
        if (!condition) throw new AssertionError(message);
    }

    private static void expectIllegalState(Runnable operation) {
        try {
            operation.run();
            throw new AssertionError("Expected IllegalStateException");
        } catch (IllegalStateException expected) {
            checks++;
        }
    }

    public static void main(String[] args) {
        Inventory inventory = new Inventory();
        inventory.addProduct(new Product("COKE", "Coke", 50), 2);
        inventory.addProduct(new Product("WATER", "Water", 20), 1);
        SimulatedDispenser dispenser = new SimulatedDispenser();
        VendingMachine machine = new VendingMachine(inventory, new PaymentHandler(), dispenser);

        expectIllegalState(() -> machine.insertMoney(10)); // No selection.
        machine.selectProduct("COKE");
        check(machine.getStateName().equals("PRODUCT_SELECTED"), "Selection state");
        machine.selectProduct("WATER"); // Can switch before first payment.
        check(machine.getSelectedProduct().getId().equals("WATER"), "Product switched");
        machine.insertMoney(10);
        check(machine.getStateName().equals("ACCEPTING_PAYMENT"), "Payment state");
        check(machine.getInsertedMoney() == 10, "Inserted amount");
        expectIllegalState(() -> machine.selectProduct("COKE")); // Locked during payment.
        machine.cancel();
        check(machine.getInsertedMoney() == 0, "Refund clears balance");
        check(machine.getStateName().equals("IDLE"), "Cancel resets state");
        check(inventory.getQuantity("WATER") == 1, "Cancel does not consume stock");

        machine.selectProduct("COKE");
        machine.insertMoney(20);
        machine.insertMoney(40); // Dispensed: ₹10 change.
        check(inventory.getQuantity("COKE") == 1, "Successful sale reduces stock");
        check(machine.getInsertedMoney() == 0, "Payment settled");
        check(machine.getStateName().equals("IDLE"), "Sale resets state");

        machine.selectProduct("COKE");
        dispenser.failNextDispense();
        try {
            machine.insertMoney(50);
            throw new AssertionError("Expected dispense failure");
        } catch (DispenseException expected) {
            checks++;
        }
        check(inventory.getQuantity("COKE") == 1, "Failure doesn't consume stock");
        check(machine.getInsertedMoney() == 0, "Failure refunds payment");
        check(machine.getStateName().equals("IDLE"), "Failure resets state");

        machine.selectProduct("WATER");
        machine.insertMoney(20);
        check(inventory.getQuantity("WATER") == 0, "Last item sold");
        try {
            machine.selectProduct("WATER");
            throw new AssertionError("Expected out-of-stock rejection");
        } catch (IllegalArgumentException expected) {
            checks++;
        }
        check(machine.getStateName().equals("IDLE"), "Invalid selection leaves state unchanged");

        System.out.println("PASS: " + checks + " checks");
    }
}
