package Vista;

import Modelo.Producto;
import servicio.ArchivoService;
import Servicio.InventarioService;
import Servicio.ValidadorVenta;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class FrmPrincipal extends javax.swing.JFrame {

    private InventarioService inventarioService;

    // Componentes Inventario
    private JTextField txtId, txtNombre, txtPrecio, txtStock;
    private JTextArea txtSalidaInventario;
    
    // Componentes Ventas
    private JTextField txtIdVenta, txtCantidadVenta, txtClienteDni;
    private JTextArea txtBoleta;

    public FrmPrincipal() {
        super("Sistema de Gestión Comercial e Inventario MYPE");
        inventarioService = new InventarioService();
        
        // Cargar productos previos desde stock.txt al abrir
        List<Producto> cargados = ArchivoService.cargarDesdeArchivo();
        for (Producto p : cargados) {
            inventarioService.agregarProducto(p);
        }

        configurarVentana();
    }

    private void configurarVentana() {
        this.setSize(750, 550);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);

        JTabbedPane pestanas = new JTabbedPane();

        // Pestaña 1: Inventario y Stock
        pestanas.addTab("Control de Inventario", crearPanelInventario());

        // Pestaña 2: Ventas y Facturación
        pestanas.addTab("Módulo de Ventas", crearPanelVentas());

        this.add(pestanas);
        actualizarReporteInventario();
    }

    private JPanel crearPanelInventario() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        JPanel pnlCampos = new JPanel(new GridLayout(5, 2, 5, 5));

        txtId = new JTextField();
        txtNombre = new JTextField();
        txtPrecio = new JTextField();
        txtStock = new JTextField();

        pnlCampos.add(new JLabel(" Código / ID:"));
        pnlCampos.add(txtId);
        pnlCampos.add(new JLabel(" Nombre Producto:"));
        pnlCampos.add(txtNombre);
        pnlCampos.add(new JLabel(" Precio Unitario (S/):"));
        pnlCampos.add(txtPrecio);
        pnlCampos.add(new JLabel(" Stock Inicial:"));
        pnlCampos.add(txtStock);

        JButton btnGuardar = new JButton("Registrar y Guardar en Archivo");
        JButton btnLimpiar = new JButton("Limpiar Campos");
        pnlCampos.add(btnGuardar);
        pnlCampos.add(btnLimpiar);

        txtSalidaInventario = new JTextArea();
        txtSalidaInventario.setEditable(false);
        txtSalidaInventario.setFont(new Font("Monospaced", Font.PLAIN, 12));

        btnGuardar.addActionListener(e -> registrarProducto());
        btnLimpiar.addActionListener(e -> limpiarCamposInventario());

        panel.add(pnlCampos, BorderLayout.NORTH);
        panel.add(new JScrollPane(txtSalidaInventario), BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelVentas() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        JPanel pnlCampos = new JPanel(new GridLayout(4, 2, 5, 5));

        txtClienteDni = new JTextField();
        txtIdVenta = new JTextField();
        txtCantidadVenta = new JTextField();

        pnlCampos.add(new JLabel(" DNI Cliente (8 dígitos):"));
        pnlCampos.add(txtClienteDni);
        pnlCampos.add(new JLabel(" Código Producto:"));
        pnlCampos.add(txtIdVenta);
        pnlCampos.add(new JLabel(" Cantidad a Vender:"));
        pnlCampos.add(txtCantidadVenta);

        JButton btnVender = new JButton("Procesar Venta");
        pnlCampos.add(btnVender);

        txtBoleta = new JTextArea();
        txtBoleta.setEditable(false);
        txtBoleta.setFont(new Font("Monospaced", Font.PLAIN, 12));

        btnVender.addActionListener(e -> procesarVenta());

        panel.add(pnlCampos, BorderLayout.NORTH);
        panel.add(new JScrollPane(txtBoleta), BorderLayout.CENTER);
        return panel;
    }

    private void registrarProducto() {
        try {
            int id = Integer.parseInt(txtId.getText());
            String nombre = txtNombre.getText();
            double precio = Double.parseDouble(txtPrecio.getText());
            int stock = Integer.parseInt(txtStock.getText());

            Producto p = new Producto(id, nombre, precio, stock);
            inventarioService.agregarProducto(p);

            List<Producto> listaActual = ArchivoService.cargarDesdeArchivo();
            listaActual.add(p);
            ArchivoService.guardarEnArchivo(listaActual);

            JOptionPane.showMessageDialog(this, "Producto guardado con éxito en stock.txt");
            limpiarCamposInventario();
            actualizarReporteInventario();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error de datos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void procesarVenta() {
        try {
            String dni = txtClienteDni.getText();
            if (dni.length() != 8) {
                throw new IllegalArgumentException("El DNI debe tener exactamente 8 dígitos.");
            }

            int id = Integer.parseInt(txtIdVenta.getText());
            int cantidad = Integer.parseInt(txtCantidadVenta.getText());

            List<Producto> lista = ArchivoService.cargarDesdeArchivo();
            Producto productoEncontrado = null;

            for (Producto p : lista) {
                if (p.getId() == id) {
                    productoEncontrado = p;
                    break;
                }
            }

            if (productoEncontrado == null) {
                JOptionPane.showMessageDialog(this, "Producto no existe en el catálogo.");
                return;
            }

            ValidadorVenta.validarStock(productoEncontrado, cantidad);

            double subtotal = productoEncontrado.getPrecio() * cantidad;
            double igv = subtotal * 0.18;
            double total = subtotal + igv;

            productoEncontrado.setStock(productoEncontrado.getStock() - cantidad);
            ArchivoService.guardarEnArchivo(lista);

            txtBoleta.setText("=========== COMPROBANTE DE PAGO ===========\n" +
                              "Cliente DNI: " + dni + "\n" +
                              "Producto:    " + productoEncontrado.getNombre() + "\n" +
                              "Cantidad:    " + cantidad + "\n" +
                              "Precio Unit: S/ " + String.format("%.2f", productoEncontrado.getPrecio()) + "\n" +
                              "--------------------------------------------\n" +
                              "Subtotal:    S/ " + String.format("%.2f", subtotal) + "\n" +
                              "IGV (18%):   S/ " + String.format("%.2f", igv) + "\n" +
                              "TOTAL:       S/ " + String.format("%.2f", total) + "\n" +
                              "============================================");

            actualizarReporteInventario();
            JOptionPane.showMessageDialog(this, "¡Venta completada con éxito!");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Alerta de Venta", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void actualizarReporteInventario() {
        List<Producto> lista = ArchivoService.cargarDesdeArchivo();
        StringBuilder sb = new StringBuilder("=== CATÁLOGO ACTUAL EN STOCK.TXT ===\n");
        sb.append(String.format("%-10s %-25s %-12s %-8s\n", "ID", "NOMBRE", "PRECIO", "STOCK"));
        sb.append("------------------------------------------------------------\n");
        for (Producto p : lista) {
            sb.append(String.format("%-10d %-25s S/ %-9.2f %-8d\n", p.getId(), p.getNombre(), p.getPrecio(), p.getStock()));
        }
        txtSalidaInventario.setText(sb.toString());
    }

    private void limpiarCamposInventario() {
        txtId.setText("");
        txtNombre.setText("");
        txtPrecio.setText("");
        txtStock.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FrmPrincipal().setVisible(true));
    }
}