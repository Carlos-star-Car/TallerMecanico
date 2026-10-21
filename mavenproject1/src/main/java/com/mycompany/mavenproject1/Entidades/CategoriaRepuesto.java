package com.mycompany.mavenproject1.Entidades;

public class CategoriaRepuesto {
    private final int codigo;
    private String nombre;
    private String descripcion;

    public CategoriaRepuesto(int codigo, String nombre, String descripcion) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public int getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }

    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    // Para que el combo de Repuesto muestre algo legible
    @Override
    public String toString() {
        return codigo + " - " + nombre;
    }
}