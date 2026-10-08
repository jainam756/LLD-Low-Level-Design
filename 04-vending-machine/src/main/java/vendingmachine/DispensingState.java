package vendingmachine;

public final class DispensingState implements VendingMachineState {
    @Override public String name() { return "DISPENSING"; }

    @Override
    public void dispense(VendingMachine machine) {
        machine.processDispensing();
    }

    // By default, selecting, inserting money or cancelling is rejected here.
}
