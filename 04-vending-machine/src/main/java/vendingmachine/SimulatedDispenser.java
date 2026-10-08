package vendingmachine;

public final class SimulatedDispenser implements Dispenser {
    private boolean shouldFailNextTime;

    public void failNextDispense() {
        shouldFailNextTime = true;
    }

    @Override
    public void dispense(Product product) {
        if (shouldFailNextTime) {
            shouldFailNextTime = false;
            throw new DispenseException("Mechanical dispenser failure");
        }
        System.out.println("Hardware dispensed: " + product.getName());
    }
}
