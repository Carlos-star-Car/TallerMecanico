package com.mycompany.mavenproject1.Servicios;

import com.mycompany.mavenproject1.Entidades.Repuesto;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio de repuestos con almacenamiento en memoria.
 * La lista es estática para que todos los formularios compartan los mismos datos
 * mientras el programa esté abierto.
 */
public class RepuestoServicio {

    private static final List<Repuesto> lista = new ArrayList<>();

    /** Agrega un repuesto. Devuelve false si el código ya existe. */
    public boolean guardar(Repuesto r) {
        if (existeCodigo(r.getCodigoRepuesto())) {
            return false;
        }
        lista.add(r);
        return true;
    }

    /** Reemplaza los datos del repuesto con ese código. Devuelve false si no existe. */
    public boolean modificar(Repuesto r) {
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getCodigoRepuesto() == r.getCodigoRepuesto()) {
                lista.set(i, r);
                return true;
            }
        }
        return false;
    }

    /** Elimina por código. Devuelve false si no existe. */
    public boolean eliminar(int codigoRepuesto) {
        return lista.removeIf(r -> r.getCodigoRepuesto() == codigoRepuesto);
    }

    /** Devuelve una copia de la lista completa. */
    public List<Repuesto> listar() {
        return new ArrayList<>(lista);
    }

    /** Devuelve el repuesto con ese código, o null si no existe. */
    public Repuesto obtener(int codigoRepuesto) {
        for (Repuesto r : lista) {
            if (r.getCodigoRepuesto() == codigoRepuesto) {
                return r;
            }
        }
        return null;
    }

    /**
     * Busca por código exacto (si el texto es numérico) o por nombre que contenga
     * el texto (sin distinguir mayúsculas). Lista vacía si no hay resultados.
     */
    public List<Repuesto> buscar(String criterio) {
        List<Repuesto> resultado = new ArrayList<>();
        String texto = criterio.trim().toLowerCase();

        Integer codigo = null;
        try {
            codigo = Integer.valueOf(texto);
        } catch (NumberFormatException ex) {
            // no es un código; se busca solo por nombre
        }

        for (Repuesto r : lista) {
            boolean porCodigo = codigo != null && r.getCodigoRepuesto() == codigo;
            boolean porNombre = r.getNombreRepuesto().toLowerCase().contains(texto);
            if (porCodigo || porNombre) {
                resultado.add(r);
            }
        }
        return resultado;
    }

    /** Resta unidades a la existencia. Devuelve false si no existe o no alcanza. */
    public boolean descontarExistencia(int codigoRepuesto, int cantidad) {
        Repuesto r = obtener(codigoRepuesto);
        if (r == null || cantidad <= 0 || r.getExistencia() < cantidad) {
            return false;
        }
        r.setExistencia(r.getExistencia() - cantidad);
        return true;
    }

    /** Suma unidades a la existencia (al anular una factura). Devuelve false si no existe. */
    public boolean devolverExistencia(int codigoRepuesto, int cantidad) {
        Repuesto r = obtener(codigoRepuesto);
        if (r == null || cantidad <= 0) {
            return false;
        }
        r.setExistencia(r.getExistencia() + cantidad);
        return true;
    }    
    public boolean existeCodigo(int codigoRepuesto) {
        return obtener(codigoRepuesto) != null;
    }
}