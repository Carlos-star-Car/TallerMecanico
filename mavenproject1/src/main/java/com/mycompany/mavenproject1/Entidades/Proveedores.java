package com.mycompany.mavenproject1.Entidades;

public class Proveedores {

    private int CodigoProveedor;
    private String nombreEmpresa;
    private String nombreContacto;
    private String telefono;
    private String correo;
    private String direccion;

    public Proveedores() {
    }

    public Proveedores(int codigoProveedor, String nombreEmpresa, String nombreContacto,
                       String telefono, String correo, String direccion) {
        this.CodigoProveedor = codigoProveedor;
        this.nombreEmpresa = nombreEmpresa;
        this.nombreContacto = nombreContacto;
        this.telefono = telefono;
        this.correo = correo;
        this.direccion = direccion;
    }

    public int getCodigoProveedor() {
        return CodigoProveedor;
    }

    public void setCodigoProveedor(int codigoProveedor) {
        this.CodigoProveedor = codigoProveedor;
    }

    public String getNombreEmpresa() {
        return nombreEmpresa;
    }

    public void setNombreEmpresa(String nombreEmpresa) {
        this.nombreEmpresa = nombreEmpresa;
    }

    public String getNombreContacto() {
        return nombreContacto;
    }

    public void setNombreContacto(String nombreContacto) {
        this.nombreContacto = nombreContacto;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
}