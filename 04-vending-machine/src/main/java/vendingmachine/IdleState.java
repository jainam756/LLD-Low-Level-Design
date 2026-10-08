package vendingmachine;

public final class IdleState implements VendingMachineState {
    @Override public String name() { return "IDLE"; }

    @Override
    public void selectProduct(VendingMachine machine, String productId) {
        machine.chooseAvailableProduct(productId);
        machine.setState(new ProductSelectedState());
    }

    @Override
    public void cancel(VendingMachine machine) {
        System.out.println("Nothing to cancel");
    }
}
