package Servicio;


import Modelo.Producto;

// Aporte: Manejo de errores y excepciones personalizadas
class StockInsuficienteException extends Exception {
    public StockInsuficienteException(String mensaje) {
        super(mensaje);
    }
}

public class ValidadorVenta {
    public static void validarStock(Producto p, int cantidad) throws StockInsuficienteException {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("Error: La cantidad solicitada debe ser mayor a cero.");
        }
        if (p.getStock() < cantidad) {
            throw new StockInsuficienteException("Error de Stock: " + p.getNombre() + 
                                                " solo tiene " + p.getStock() + " unidades disponibles.");
        }
    }
}