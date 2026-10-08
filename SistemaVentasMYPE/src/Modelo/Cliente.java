package Modelo;

public class Cliente extends Persona {
    private String telefono;
    private String email;

    public Cliente(String dni, String nombres, String apellidos, String telefono, String email) {
        super(dni, nombres, apellidos);
        this.telefono = telefono;
        this.email = email;
    }

    @Override
    public String mostrarDatos() {
        return "CLIENTE: " + nombres + " " + apellidos + " | DNI: " + dni + " | Tel: " + telefono;
    }

    // Getters y Setters
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}