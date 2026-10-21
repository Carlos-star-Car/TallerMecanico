package com.mycompany.mavenproject1.Entidades;

public class Mecanicos {
    private final int codigo;
    private String nombre;
    private String apellido;
    private String telefono;
    private String especialidad;
    private float tarifa;

    public Mecanicos(int codigo, String nombre, String apellido,
                     String telefono, String especialidad, float tarifa) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.especialidad = especialidad;
        this.tarifa = tarifa;
    }

    public int getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public String getTelefono() { return telefono; }
    public String getEspecialidad() { return especialidad; }
    public float getTarifa() { return tarifa; }

    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }
    public void setTarifa(float tarifa) { this.tarifa = tarifa; }
}