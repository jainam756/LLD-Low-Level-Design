package vendingmachine;

import java.util.List;

public final class VendingMachine {
    private final Inventory inventory;
    private final PaymentHandler paymentHandler;
    private final Dispenser dispenser;

    private VendingMachineState currentState = new IdleState();
    private Product selectedProduct;

    public VendingMachine(Inventory inventory, PaymentHandler paymentHandler, Dispenser dispenser) {
        if (inventory == null || paymentHandler == null || dispenser == null) {
            throw new IllegalArgumentException("Dependencies cannot be null");
        }
        if (paymentHandler.getInsertedAmount() != 0) {
            throw new IllegalArgumentException("Payment handler must start empty");
        }
        this.inventory = inventory;
        this.paymentHandler = paymentHandler;
        this.dispenser = dispenser;
    }

    // Public API delegates to the current state.
    public void selectProduct(String productId) {
        currentState.selectProduct(this, productId);
    }

    public void insertMoney(int amount) {
        currentState.insertMoney(this, amount);
    }

    public void cancel() {
        currentState.cancel(this);
    }

    public List<Product> getAvailableProducts() {
        return inventory.getAvailableProducts();
    }

    public String getStateName() { return currentState.name(); }
    public int getInsertedMoney() { return paymentHandler.getInsertedAmount(); }
    public Product getSelectedProduct() { return selectedProduct; }

    // Package-private helpers used by state implementations.
    void setState(VendingMachineState nextState) {
        currentState = nextState;
    }

    void chooseAvailableProduct(String productId) {
        selectedProduct = inventory.getAvailableProduct(productId);
        System.out.println("Selected " + selectedProduct.getName()
                + " | Price: ₹" + selectedProduct.getPrice());
    }

    void addMoney(int amount) {
        paymentHandler.addMoney(amount);
        System.out.println("Paid: ₹" + paymentHandler.getInsertedAmount());
    }

    void dispenseIfFullyPaid() {
        if (paymentHandler.getInsertedAmount() < selectedProduct.getPrice()) {
            System.out.println("Remaining: ₹" + (selectedProduct.getPrice() - paymentHandler.getInsertedAmount()));
            return;
        }
        setState(new DispensingState());
        currentState.dispense(this);
    }

    void processDispensing() {
        try {
            dispenser.dispense(selectedProduct);
        } catch (DispenseException e) {
            int refunded = paymentHandler.refund();
            selectedProduct = null;
            setState(new IdleState());
            System.out.println("Dispense failed; refunded ₹" + refunded);
            throw new DispenseException("Dispensing failed; refunded ₹" + refunded, e);
        }

        // Update inventory and settle the sale ONLY after successful dispensing.
        // Assumes single active session and reliable dispense acknowledgement.
        inventory.reduceStock(selectedProduct.getId());
        int change = paymentHandler.completePayment(selectedProduct.getPrice());
        System.out.println("Sale complete. Change: ₹" + change);
        selectedProduct = null;
        setState(new IdleState());
    }

    void cancelOrder() {
        int refunded = paymentHandler.refund();
        selectedProduct = null;
        setState(new IdleState());
        System.out.println("Order cancelled. Refunded ₹" + refunded);
    }
}
