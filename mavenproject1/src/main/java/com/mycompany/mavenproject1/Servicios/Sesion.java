package com.mycompany.mavenproject1.Servicios;

import com.mycompany.mavenproject1.Entidades.Usuario;

/**
 * Guarda quien inicio sesion y decide que modulos puede usar segun su rol.
 */
public final class Sesion {

    public static final String MOD_CLIENTES = "clientes";
    public static final String MOD_VEHICULOS = "vehiculos";
    public static final String MOD_MECANICOS = "mecanicos";
    public static final String MOD_REPUESTOS = "repuestos";
    public static final String MOD_CATEGORIAS = "categorias";
    public static final String MOD_PROVEEDORES = "proveedores";
    public static final String MOD_ORDENES = "ordenes";
    public static final String MOD_FACTURACION = "facturacion";
    public static final String MOD_USUARIOS = "usuarios";

    private static Usuario actual;

    private Sesion() {
    }

    public static void iniciar(Usuario usuario) {
        actual = usuario;
    }

    public static void cerrar() {
        actual = null;
    }

    public static Usuario getUsuario() {
        return actual;
    }

    public static boolean estaActiva() {
        return actual != null;
    }

    public static boolean esAdmin() {
        return actual != null && actual.esAdmin();
    }

    /** ADMIN: todo. RECEPCION: clientes, vehiculos, ordenes y facturacion. MECANICO: vehiculos y ordenes. */
    public static boolean puedeUsar(String modulo) {
        if (actual == null) {
            return false;
        }
        switch (actual.getRol()) {
            case Usuario.ROL_ADMIN:
                return true;
            case Usuario.ROL_RECEPCION:
                return MOD_CLIENTES.equals(modulo) || MOD_VEHICULOS.equals(modulo)
                        || MOD_ORDENES.equals(modulo) || MOD_FACTURACION.equals(modulo);
            case Usuario.ROL_MECANICO:
                return MOD_VEHICULOS.equals(modulo) || MOD_ORDENES.equals(modulo);
            default:
                return false;
        }
    }
}
