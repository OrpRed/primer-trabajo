// Aporte: Sobrecarga de métodos
public class Producto {
    private int id;
    private String nombre;
    private double precio;
    private int stock;

    // Sobrecarga de constructores
    public Producto(int id, String nombre, double precio) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.stock = 0;
    }

    public Producto(int id, String nombre, double precio, int stock) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
    }

    // Sobrecarga de métodos para calcular descuento
    public double calcularDescuento(double porcentaje) {
        return this.precio - (this.precio * (porcentaje / 100));
    }

    public double calcularDescuento(double porcentaje, double cuponFijo) {
        double precioConDesc = this.calcularDescuento(porcentaje);
        return Math.max(0, precioConDesc - cuponFijo);
    }

    public int getStock() { return stock; }
    public String getNombre() { return nombre; }
    public int getId() { return id; }
}
