package vendingmachine;

public final class AcceptingPaymentState implements VendingMachineState {
    @Override public String name() { return "ACCEPTING_PAYMENT"; }

    @Override
    public void insertMoney(VendingMachine machine, int amount) {
        machine.addMoney(amount);
        machine.dispenseIfFullyPaid();
    }

    @Override
    public void cancel(VendingMachine machine) {
        machine.cancelOrder();
    }
}
