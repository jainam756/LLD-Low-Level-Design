package vendingmachine;

public final class PaymentHandler {
    private int insertedAmount;

    public void addMoney(int amount) {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive");
        insertedAmount = Math.addExact(insertedAmount, amount);
    }

    public int getInsertedAmount() { return insertedAmount; }

    public int completePayment(int price) {
        if (insertedAmount < price) {
            throw new IllegalStateException("Insufficient money");
        }
        int change = insertedAmount - price;
        insertedAmount = 0;
        return change;
    }

    public int refund() {
        int refund = insertedAmount;
        insertedAmount = 0;
        return refund;
    }
}
