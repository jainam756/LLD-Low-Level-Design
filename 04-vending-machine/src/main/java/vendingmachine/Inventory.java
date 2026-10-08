package vendingmachine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Inventory {
    private static final class StockItem {
        final Product product;
        int quantity;

        StockItem(Product product, int quantity) {
            this.product = product;
            this.quantity = quantity;
        }
    }

    private final Map<String, StockItem> stockByProductId = new HashMap<>();

    public void addProduct(Product product, int quantity) {
        if (product == null || quantity < 0) {
            throw new IllegalArgumentException("Invalid product or quantity");
        }
        if (stockByProductId.containsKey(product.getId())) {
            throw new IllegalArgumentException("Product id already exists");
        }
        stockByProductId.put(product.getId(), new StockItem(product, quantity));
    }

    public Product getAvailableProduct(String productId) {
        StockItem item = stockByProductId.get(productId);
        if (item == null || item.quantity == 0) {
            throw new IllegalArgumentException("Product unavailable: " + productId);
        }
        return item.product;
    }

    public List<Product> getAvailableProducts() {
        List<Product> products = new ArrayList<>();
        for (StockItem item : stockByProductId.values()) {
            if (item.quantity > 0) products.add(item.product);
        }
        return products;
    }

    public int getQuantity(String productId) {
        StockItem item = stockByProductId.get(productId);
        if (item == null) throw new IllegalArgumentException("Unknown product: " + productId);
        return item.quantity;
    }

    public void reduceStock(String productId) {
        // A single physical machine serves one active order at a time.
        StockItem item = stockByProductId.get(productId);
        if (item == null || item.quantity == 0) {
            throw new IllegalStateException("No stock left for " + productId);
        }
        item.quantity--;
    }
}
