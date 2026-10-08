package Modelo;

public class EmpleadoPermanente extends Empleado {
    private String afiliacion; // "AFP" o "SNP"

    public EmpleadoPermanente(String dni, String nombres, String apellidos, String codigoEmpleado, 
                              double sueldoBase, String afiliacion) {
        super(dni, nombres, apellidos, codigoEmpleado, sueldoBase);
        this.afiliacion = afiliacion;
    }

    @Override
    public double calcularIngresos() {
        return sueldoBase;
    }

    @Override
    public double calcularBonificacion() {
        return 0; // Sin bonificación fija
    }

    @Override
    public double calcularDescuento() {
        if (afiliacion.equalsIgnoreCase("AFP")) {
            return sueldoBase * 0.15;
        } else {
            return sueldoBase * 0.11;
        }
    }
}