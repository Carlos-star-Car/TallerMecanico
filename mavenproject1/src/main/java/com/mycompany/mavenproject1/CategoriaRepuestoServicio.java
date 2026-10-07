package com.mycompany.mavenproject1;

import java.util.ArrayList;
import java.util.List;

public class CategoriaRepuestoServicio {

    private static final CategoriaRepuestoServicio INSTANCIA = new CategoriaRepuestoServicio();

    private final List<CategoriaRepuesto> categorias = new ArrayList<>();

    private CategoriaRepuestoServicio() { }

    public static CategoriaRepuestoServicio getInstancia() {
        return INSTANCIA;
    }

    public List<CategoriaRepuesto> listar() {
        return new ArrayList<>(categorias);
    }

    public boolean agregar(CategoriaRepuesto c) {
        if (buscarPorCodigo(c.getCodigo()) != null) {
            return false;
        }
        categorias.add(c);
        return true;
    }

    public boolean modificar(String codigo, String nombre, String descripcion) {
        CategoriaRepuesto c = buscarPorCodigo(codigo);
        if (c == null) {
            return false;
        }
        c.setNombre(nombre);
        c.setDescripcion(descripcion);
        return true;
    }

    public boolean eliminar(String codigo) {
        CategoriaRepuesto c = buscarPorCodigo(codigo);
        if (c == null) {
            return false;
        }
        return categorias.remove(c);
    }

    public CategoriaRepuesto buscarPorCodigo(String codigo) {
        for (CategoriaRepuesto c : categorias) {
            if (c.getCodigo().equalsIgnoreCase(codigo)) {
                return c;
            }
        }
        return null;
    }

    public List<CategoriaRepuesto> buscar(String texto) {
        String t = texto.trim().toLowerCase();
        List<CategoriaRepuesto> resultado = new ArrayList<>();
        for (CategoriaRepuesto c : categorias) {
            if (c.getCodigo().equalsIgnoreCase(t) || c.getNombre().toLowerCase().contains(t)) {
                resultado.add(c);
            }
        }
        return resultado;
    }
}