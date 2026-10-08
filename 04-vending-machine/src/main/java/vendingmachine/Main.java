package vendingmachine;

public final class Main {
    public static void main(String[] args) {
        Inventory inventory = new Inventory();
        inventory.addProduct(new Product("COKE", "Coke", 50), 2);
        inventory.addProduct(new Product("WATER", "Water", 20), 5);

        SimulatedDispenser dispenser = new SimulatedDispenser();
        VendingMachine machine = new VendingMachine(inventory, new PaymentHandler(), dispenser);

        System.out.println("=== Successful purchase ===");
        machine.selectProduct("WATER");
        machine.selectProduct("COKE"); // Product can change before paying.
        machine.insertMoney(20);
        machine.insertMoney(40); // Total ₹60; automatically dispenses and returns ₹10.
        System.out.println("Coke stock: " + inventory.getQuantity("COKE"));

        System.out.println("\n=== Cancel after partial payment ===");
        machine.selectProduct("WATER");
        machine.insertMoney(10);
        machine.cancel(); // Refund ₹10.

        System.out.println("\n=== Mechanical failure ===");
        machine.selectProduct("COKE");
        dispenser.failNextDispense();
        try {
            machine.insertMoney(50);
        } catch (DispenseException e) {
            System.out.println("User notified: " + e.getMessage());
        }
        System.out.println("Coke stock: " + inventory.getQuantity("COKE"));
        System.out.println("Final state: " + machine.getStateName());
    }
}
