package Modelo;

public class EmpleadoVendedor extends Empleado {
    private double montoVendido;
    private double tasaComision;

    public EmpleadoVendedor(String dni, String nombres, String apellidos, String codigoEmpleado, 
                            double montoVendido, double tasaComision) {
        super(dni, nombres, apellidos, codigoEmpleado, 0); // No tiene sueldo base fijo
        this.montoVendido = montoVendido;
        this.tasaComision = tasaComision;
    }

    @Override
    public double calcularIngresos() {
        return montoVendido * tasaComision;
    }

    @Override
    public double calcularBonificacion() {
        double ingresos = calcularIngresos();
        if (montoVendido < 1000) {
            return 0;
        } else if (montoVendido <= 5000) {
            return ingresos * 0.05;
        } else {
            return ingresos * 0.10;
        }
    }

    @Override
    public double calcularDescuento() {
        double ingresos = calcularIngresos();
        if (ingresos < 1000) {
            return ingresos * 0.11;
        } else {
            return ingresos * 0.15;
        }
    }
}