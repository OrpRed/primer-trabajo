package Vista;

import Modelo.Cliente;
import Modelo.EmpleadoVendedor;
import Modelo.Producto;
import Servicio.ArchivoService;
import Servicio.InventarioService;
import Servicio.ValidadorVenta;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FrmPrincipal extends javax.swing.JFrame {

    private InventarioService inventarioService;
    private EmpleadoVendedor cajeroActivo;

    // Componentes Inventario
    private JTextField txtId, txtNombre, txtPrecio, txtStock;
    private JTextArea txtSalidaInventario;
    
    // Componentes Ventas
    private JTextField txtIdVenta, txtCantidadVenta, txtClienteDni, txtClienteNombre;
    private JTextArea txtBoleta;

    public FrmPrincipal() {
        super("Sistema de Gestión Comercial e Inventario MYPE");
        inventarioService = new InventarioService();
        
        // Empleado que atiende (evidencia de uso de EmpleadoVendedor)
        cajeroActivo = new EmpleadoVendedor("72345678", "Gino", "Navarro", "VEND-001", 0, 0.05);

        // Cargar productos previos desde stock.txt al abrir
        List<Producto> cargados = ArchivoService.cargarDesdeArchivo();
        for (Producto p : cargados) {
            inventarioService.agregarProducto(p);
        }

        configurarVentana();
    }

    private void configurarVentana() {
        this.setSize(850, 600);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);

        JTabbedPane pestanas = new JTabbedPane();
        pestanas.addTab("Control de Inventario (Buscar / Editar)", crearPanelInventario());
        pestanas.addTab("Módulo de Ventas e Historial", crearPanelVentas());

        this.add(pestanas);
        actualizarReporteInventario();
    }

    private JPanel crearPanelInventario() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        JPanel pnlCampos = new JPanel(new GridLayout(6, 2, 5, 5));

        txtId = new JTextField();
        txtNombre = new JTextField();
        txtPrecio = new JTextField();
        txtStock = new JTextField();

        pnlCampos.add(new JLabel(" Código / ID del Producto:"));
        pnlCampos.add(txtId);
        pnlCampos.add(new JLabel(" Nombre del Producto:"));
        pnlCampos.add(txtNombre);
        pnlCampos.add(new JLabel(" Precio Unitario (S/):"));
        pnlCampos.add(txtPrecio);
        pnlCampos.add(new JLabel(" Stock Disponible:"));
        pnlCampos.add(txtStock);

        JButton btnGuardar = new JButton("Registrar Nuevo");
        JButton btnBuscar = new JButton("Buscar por ID");
        JButton btnModificar = new JButton("Actualizar / Modificar");
        JButton btnLimpiar = new JButton("Limpiar Campos");

        pnlCampos.add(btnGuardar);
        pnlCampos.add(btnBuscar);
        pnlCampos.add(btnModificar);
        pnlCampos.add(btnLimpiar);

        txtSalidaInventario = new JTextArea();
        txtSalidaInventario.setEditable(false);
        txtSalidaInventario.setFont(new Font("Monospaced", Font.PLAIN, 12));

        btnGuardar.addActionListener(e -> registrarProducto());
        btnBuscar.addActionListener(e -> buscarProducto());
        btnModificar.addActionListener(e -> modificarProducto());
        btnLimpiar.addActionListener(e -> limpiarCamposInventario());

        panel.add(pnlCampos, BorderLayout.NORTH);
        panel.add(new JScrollPane(txtSalidaInventario), BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelVentas() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        JPanel pnlCampos = new JPanel(new GridLayout(6, 2, 5, 5));

        txtClienteDni = new JTextField();
        txtClienteNombre = new JTextField();
        txtIdVenta = new JTextField();
        txtCantidadVenta = new JTextField();

        pnlCampos.add(new JLabel(" DNI Cliente (8 dígitos):"));
        pnlCampos.add(txtClienteDni);
        pnlCampos.add(new JLabel(" Nombres del Cliente:"));
        pnlCampos.add(txtClienteNombre);
        pnlCampos.add(new JLabel(" Código Producto:"));
        pnlCampos.add(txtIdVenta);
        pnlCampos.add(new JLabel(" Cantidad a Vender:"));
        pnlCampos.add(txtCantidadVenta);

        JButton btnVender = new JButton("Procesar Venta y Boleta");
        JButton btnVerHistorial = new JButton("Ver Historial (ventas.txt)");

        pnlCampos.add(btnVender);
        pnlCampos.add(btnVerHistorial);

        txtBoleta = new JTextArea();
        txtBoleta.setEditable(false);
        txtBoleta.setFont(new Font("Monospaced", Font.PLAIN, 12));

        btnVender.addActionListener(e -> procesarVenta());
        btnVerHistorial.addActionListener(e -> txtBoleta.setText(ArchivoService.leerHistorialVentas()));

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

            List<Producto> listaActual = ArchivoService.cargarDesdeArchivo();
            for (Producto p : listaActual) {
                if (p.getId() == id) {
                    JOptionPane.showMessageDialog(this, "El ID " + id + " ya existe. Use 'Actualizar'.", "Alerta", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            Producto nuevo = new Producto(id, nombre, precio, stock);
            listaActual.add(nuevo);
            ArchivoService.guardarEnArchivo(listaActual);

            JOptionPane.showMessageDialog(this, "Producto guardado con éxito en stock.txt");
            limpiarCamposInventario();
            actualizarReporteInventario();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error de datos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buscarProducto() {
        try {
            int id = Integer.parseInt(txtId.getText());
            List<Producto> lista = ArchivoService.cargarDesdeArchivo();
            for (Producto p : lista) {
                if (p.getId() == id) {
                    txtNombre.setText(p.getNombre());
                    txtPrecio.setText(String.valueOf(p.getPrecio()));
                    txtStock.setText(String.valueOf(p.getStock()));
                    JOptionPane.showMessageDialog(this, "Producto encontrado: " + p.getNombre());
                    return;
                }
            }
            JOptionPane.showMessageDialog(this, "No se encontró ningún producto con ID: " + id);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ingrese un ID numérico válido para buscar.");
        }
    }

    private void modificarProducto() {
        try {
            int id = Integer.parseInt(txtId.getText());
            String nuevoNombre = txtNombre.getText();
            double nuevoPrecio = Double.parseDouble(txtPrecio.getText());
            int nuevoStock = Integer.parseInt(txtStock.getText());

            List<Producto> lista = ArchivoService.cargarDesdeArchivo();
            boolean encontrado = false;

            for (Producto p : lista) {
                if (p.getId() == id) {
                    p.setStock(nuevoStock);
                    // Actualizamos datos
                    encontrado = true;
                    break;
                }
            }

            if (encontrado) {
                // Reescribimos con los datos modificados
                List<Producto> actualizada = new ArrayList<>();
                for (Producto p : lista) {
                    if (p.getId() == id) {
                        actualizada.add(new Producto(id, nuevoNombre, nuevoPrecio, nuevoStock));
                    } else {
                        actualizada.add(p);
                    }
                }
                ArchivoService.guardarEnArchivo(actualizada);
                JOptionPane.showMessageDialog(this, "Producto actualizado con éxito en stock.txt");
                limpiarCamposInventario();
                actualizarReporteInventario();
            } else {
                JOptionPane.showMessageDialog(this, "No se encontró el ID para modificar.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al actualizar: " + ex.getMessage());
        }
    }

    private void procesarVenta() {
        try {
            String dni = txtClienteDni.getText();
            String nombresCli = txtClienteNombre.getText();
            if (dni.length() != 8) {
                throw new IllegalArgumentException("El DNI debe tener exactamente 8 dígitos.");
            }
            if (nombresCli.trim().isEmpty()) {
                throw new IllegalArgumentException("Ingrese el nombre del cliente.");
            }

            // Uso directo de la clase Cliente (Polimorfismo / POO)
            Cliente cliente = new Cliente(dni, nombresCli, "", "-", "-");

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

            // Validar stock con excepción
            ValidadorVenta.validarStock(productoEncontrado, cantidad);

            // Cálculos
            double subtotal = productoEncontrado.getPrecio() * cantidad;
            double igv = subtotal * 0.18;
            double total = subtotal + igv;

            // Descontar y sincronizar
            productoEncontrado.setStock(productoEncontrado.getStock() - cantidad);
            ArchivoService.guardarEnArchivo(lista);

            String fechaHora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

            // Generar Comprobante
            String boleta = "=========== COMPROBANTE DE PAGO DIGITAL ===========\n" +
                            "Fecha/Hora:  " + fechaHora + "\n" +
                            "Atendido por:" + cajeroActivo.mostrarDatos() + "\n" +
                            "Cliente:     " + cliente.mostrarDatos() + "\n" +
                            "---------------------------------------------------\n" +
                            "Producto:    " + productoEncontrado.getNombre() + " (Cant: " + cantidad + ")\n" +
                            "Precio Unit: S/ " + String.format("%.2f", productoEncontrado.getPrecio()) + "\n" +
                            "Subtotal:    S/ " + String.format("%.2f", subtotal) + "\n" +
                            "IGV (18%):   S/ " + String.format("%.2f", igv) + "\n" +
                            "TOTAL:       S/ " + String.format("%.2f", total) + "\n" +
                            "===================================================";

            txtBoleta.setText(boleta);

            // Guardar en el archivo ventas.txt (Persistencia de historial)
            String logVenta = "[" + fechaHora + "] DNI:" + dni + " | Prod:" + productoEncontrado.getNombre() + 
                              " | Cant:" + cantidad + " | Total: S/" + String.format("%.2f", total);
            ArchivoService.registrarVentaEnHistorial(logVenta);

            actualizarReporteInventario();
            JOptionPane.showMessageDialog(this, "¡Venta completada y registrada en ventas.txt!");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Alerta de Venta", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void actualizarReporteInventario() {
        List<Producto> lista = ArchivoService.cargarDesdeArchivo();
        StringBuilder sb = new StringBuilder("=== CATÁLOGO ACTUAL EN STOCK.TXT ===\n");
        sb.append(String.format("%-8s %-25s %-12s %-8s\n", "ID", "NOMBRE", "PRECIO", "STOCK"));
        sb.append("----------------------------------------------------------\n");
        for (Producto p : lista) {
            sb.append(String.format("%-8d %-25s S/ %-9.2f %-8d\n", p.getId(), p.getNombre(), p.getPrecio(), p.getStock()));
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