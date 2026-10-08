package Modelo;

public abstract class Empleado extends Persona {
    protected String codigoEmpleado;
    protected double sueldoBase;

    public Empleado(String dni, String nombres, String apellidos, String codigoEmpleado, double sueldoBase) {
        super(dni, nombres, apellidos);
        this.codigoEmpleado = codigoEmpleado;
        this.sueldoBase = sueldoBase;
    }

    // Métodos abstractos según Actividad 12
    public abstract double calcularIngresos();
    public abstract double calcularBonificacion();
    public abstract double calcularDescuento();

    // Método no abstracto para el cálculo final
    public double calcularSueldoNeto() {
        return calcularIngresos() + calcularBonificacion() - calcularDescuento();
    }

    @Override
    public String mostrarDatos() {
        return "EMPLEADO [" + codigoEmpleado + "]: " + nombres + " " + apellidos + 
               " | Sueldo Neto: S/ " + String.format("%.2f", calcularSueldoNeto());
    }

    public String getCodigoEmpleado() { return codigoEmpleado; }
    public double getSueldoBase() { return sueldoBase; }
}