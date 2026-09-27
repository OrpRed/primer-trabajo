// Aporte: Uso de colecciones en memoria
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventarioService {
    private Map<Integer, Producto> productos;

    public InventarioService() {
        this.productos = new HashMap<>();
    }

    public void agregarProducto(Producto p) {
        productos.put(p.getId(), p);
    }

    public List<Producto> listarProductosConBajoStock(int umbralMinimo) {
        List<Producto> bajoStock = new ArrayList<>();
        for (Producto p : productos.values()) {
            if (p.getStock() <= umbralMinimo) {
                bajoStock.add(p);
            }
        }
        return bajoStock;
    }
}
