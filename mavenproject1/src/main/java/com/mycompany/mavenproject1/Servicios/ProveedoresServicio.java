package com.mycompany.mavenproject1.Servicios;

import com.mycompany.mavenproject1.Entidades.Proveedores;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio de proveedores con almacenamiento en memoria.
 *
 * La lista es ESTÁTICA para que todos los formularios (y todas las instancias
 * del servicio) compartan los mismos datos mientras el programa esté abierto.
 * Al cerrar la aplicación los datos se pierden.
 */
public class ProveedoresServicio {

    private static final List<Proveedores> lista = new ArrayList<>();

    /** Agrega un proveedor. Devuelve false si el código ya existe. */
    public boolean guardar(Proveedores p) {
        if (existeCodigo(p.getCodigoProveedor())) {
            return false;
        }
        lista.add(p);
        return true;
    }

    /** Reemplaza los datos del proveedor con ese código. Devuelve false si no existe. */
    public boolean modificar(Proveedores p) {
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getCodigoProveedor() == p.getCodigoProveedor()) {
                lista.set(i, p);
                return true;
            }
        }
        return false;
    }

    /** Elimina por código. Devuelve false si no existe. */
    public boolean eliminar(int CodigoProveedor) {
        return lista.removeIf(p -> p.getCodigoProveedor() == CodigoProveedor);
    }

    /** Devuelve una copia de la lista completa. */
    public List<Proveedores> listar() {
        return new ArrayList<>(lista);
    }

    /** Devuelve el proveedor con ese código, o null si no existe. */
    public Proveedores obtener(int CodigoProveedor) {
        for (Proveedores p : lista) {
            if (p.getCodigoProveedor() == CodigoProveedor) {
                return p;
            }
        }
        return null;
    }

    /**
     * Busca por código exacto (si el texto es numérico) o por nombre de empresa
     * que contenga el texto (sin distinguir mayúsculas). Lista vacía si no hay resultados.
     */
    public List<Proveedores> buscar(String criterio) {
        List<Proveedores> resultado = new ArrayList<>();
        String texto = criterio.trim().toLowerCase();

        Integer codigo = null;
        try {
            codigo = Integer.valueOf(texto);
        } catch (NumberFormatException ex) {
            // no es un código; se busca solo por nombre
        }

        for (Proveedores p : lista) {
            boolean porCodigo = codigo != null && p.getCodigoProveedor() == codigo;
            boolean porNombre = p.getNombreEmpresa().toLowerCase().contains(texto);
            if (porCodigo || porNombre) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    public boolean existeCodigo(int codigoProveedor) {
        return obtener(codigoProveedor) != null;
    }
}