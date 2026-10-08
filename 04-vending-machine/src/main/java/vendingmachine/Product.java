package vendingmachine;

public final class Product {
    private final String id;
    private final String name;
    private final int price; // Whole rupees for this interview example.

    public Product(String id, String name, int price) {
        if (id == null || id.isBlank() || name == null || name.isBlank() || price <= 0) {
            throw new IllegalArgumentException("Valid id, name and positive price required");
        }
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public int getPrice() { return price; }
}
