package com.mycompany.mavenproject1.Entidades;

public class Cliente {
    private final String codigo;
    private String nombre;
    private String apellido;
    private String nit;
    private String telefono;
    private String direccion;
    private String correo;

    public Cliente(String codigo, String nombre, String apellido, String nit,
                   String telefono, String direccion, String correo) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.apellido = apellido;
        this.nit = nit;
        this.telefono = telefono;
        this.direccion = direccion;
        this.correo = correo;
    }

   

    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public String getNit() { return nit; }
    public String getTelefono() { return telefono; }
    public String getDireccion() { return direccion; }
    public String getCorreo() { return correo; }

    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public void setNit(String nit) { this.nit = nit; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public void setCorreo(String correo) { this.correo = correo; }

    // Útil para mostrar el nombre completo en el formulario de factura
    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }
}