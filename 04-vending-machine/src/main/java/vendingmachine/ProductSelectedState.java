package vendingmachine;

public final class ProductSelectedState implements VendingMachineState {
    @Override public String name() { return "PRODUCT_SELECTED"; }

    @Override
    public void selectProduct(VendingMachine machine, String productId) {
        // Switching products is permitted until the first payment.
        machine.chooseAvailableProduct(productId);
    }

    @Override
    public void insertMoney(VendingMachine machine, int amount) {
        machine.addMoney(amount); // Validate before leaving PRODUCT_SELECTED.
        machine.setState(new AcceptingPaymentState());
        machine.dispenseIfFullyPaid();
    }

    @Override
    public void cancel(VendingMachine machine) {
        machine.cancelOrder();
    }
}
