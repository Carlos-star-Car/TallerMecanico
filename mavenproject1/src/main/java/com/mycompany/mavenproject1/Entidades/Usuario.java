package com.mycompany.mavenproject1.Entidades;

public class Usuario {

    public static final String ROL_ADMIN = "ADMIN";
    public static final String ROL_RECEPCION = "RECEPCION";
    public static final String ROL_MECANICO = "MECANICO";
    public static final String[] ROLES = {ROL_RECEPCION, ROL_ADMIN, ROL_MECANICO};

    private int idUsuario;
    private String nombreUsuario;
    private String nombreCompleto;
    private String rol;
    private boolean activo;

    public Usuario(int idUsuario, String nombreUsuario, String nombreCompleto,
                   String rol, boolean activo) {
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.nombreCompleto = nombreCompleto;
        this.rol = rol;
        this.activo = activo;
    }

    public int getIdUsuario() { return idUsuario; }
    public String getNombreUsuario() { return nombreUsuario; }
    public String getNombreCompleto() { return nombreCompleto; }
    public String getRol() { return rol; }
    public boolean isActivo() { return activo; }

    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }
    public void setRol(String rol) { this.rol = rol; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public boolean esAdmin() {
        return ROL_ADMIN.equals(rol);
    }

    @Override
    public String toString() {
        return nombreUsuario;
    }
}
