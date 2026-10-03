package servicio;

import Modelo.Producto;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ArchivoService {
    private static final String ARCHIVO = "stock.txt";

    // Guarda la lista de productos en el archivo de texto
    public static void guardarEnArchivo(List<Producto> productos) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARCHIVO))) {
            for (Producto p : productos) {
                // Formato: ID,Nombre,Precio,Stock
                bw.write(p.getId() + "," + p.getNombre() + "," + p.getPrecio() + "," + p.getStock());
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error al escribir stock.txt: " + e.getMessage());
        }
    }

    // Lee los productos desde el archivo de texto
    public static List<Producto> cargarDesdeArchivo() {
        List<Producto> lista = new ArrayList<>();
        File file = new File(ARCHIVO);
        if (!file.exists()) return lista;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (!linea.trim().isEmpty()) {
                    String[] d = linea.split(",");
                    int id = Integer.parseInt(d[0]);
                    String nombre = d[1];
                    double precio = Double.parseDouble(d[2]);
                    int stock = Integer.parseInt(d[3]);
                    lista.add(new Producto(id, nombre, precio, stock));
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error al leer stock.txt: " + e.getMessage());
        }
        return lista;
    }
}