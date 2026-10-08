package Servicio;

import Modelo.Producto;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ArchivoService {
    private static final String ARCHIVO_STOCK = "stock.txt";
    private static final String ARCHIVO_VENTAS = "ventas.txt";

    // Guarda o actualiza el catálogo completo en stock.txt
    public static void guardarEnArchivo(List<Producto> productos) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARCHIVO_STOCK))) {
            for (Producto p : productos) {
                bw.write(p.getId() + "," + p.getNombre() + "," + p.getPrecio() + "," + p.getStock());
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error al escribir stock.txt: " + e.getMessage());
        }
    }

    // Carga los productos desde stock.txt
    public static List<Producto> cargarDesdeArchivo() {
        List<Producto> lista = new ArrayList<>();
        File file = new File(ARCHIVO_STOCK);
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

    // Registra una línea en el historial ventas.txt (modo append)
    public static void registrarVentaEnHistorial(String lineaVenta) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARCHIVO_VENTAS, true))) {
            pw.println(lineaVenta);
        } catch (IOException e) {
            System.err.println("Error al escribir ventas.txt: " + e.getMessage());
        }
    }

    // Lee todo el historial de ventas.txt
    public static String leerHistorialVentas() {
        File file = new File(ARCHIVO_VENTAS);
        if (!file.exists()) return "No hay ventas registradas aún en ventas.txt.";

        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                sb.append(linea).append("\n");
            }
        } catch (IOException e) {
            return "Error al leer historial: " + e.getMessage();
        }
        return sb.toString();
    }
}