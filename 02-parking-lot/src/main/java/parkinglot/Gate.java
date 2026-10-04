package parkinglot;
public record Gate(String id, int x, int y) {
    public Gate { if (id == null || id.isBlank()) throw new IllegalArgumentException("Gate ID is required"); }
}
