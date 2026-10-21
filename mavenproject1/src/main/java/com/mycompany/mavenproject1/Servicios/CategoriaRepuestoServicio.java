package com.mycompany.mavenproject1.Servicios;

import com.mycompany.mavenproject1.Entidades.CategoriaRepuesto;
import java.util.ArrayList;
import java.util.List;

public class CategoriaRepuestoServicio {

    private static final CategoriaRepuestoServicio INSTANCIA = new CategoriaRepuestoServicio();

    public static CategoriaRepuestoServicio getInstancia() {
        return INSTANCIA;
    }

    private final List<CategoriaRepuesto> categorias = new ArrayList<>();

    public boolean guardar(CategoriaRepuesto c) {
        if (buscarPorCodigo(c.getCodigo()) != null) {
            return false;
        }
        categorias.add(c);
        return true;
    }

    public boolean modificar(CategoriaRepuesto c) {
        CategoriaRepuesto existente = buscarPorCodigo(c.getCodigo());
        if (existente == null) {
            return false;
        }
        existente.setNombre(c.getNombre());
        existente.setDescripcion(c.getDescripcion());
        return true;
    }

    public boolean eliminar(int codigo) {
        CategoriaRepuesto existente = buscarPorCodigo(codigo);
        if (existente == null) {
            return false;
        }
        categorias.remove(existente);
        return true;
    }

    public CategoriaRepuesto buscarPorCodigo(int codigo) {
        for (CategoriaRepuesto c : categorias) {
            if (c.getCodigo() == codigo) {
                return c;
            }
        }
        return null;
    }

    // Código exacto (si codigo >= 0) o parte del nombre.
    public List<CategoriaRepuesto> buscar(int codigo, String nombre) {
        List<CategoriaRepuesto> resultado = new ArrayList<>();
        String texto = nombre.toLowerCase();
        for (CategoriaRepuesto c : categorias) {
            boolean porCodigo = codigo >= 0 && c.getCodigo() == codigo;
            boolean porNombre = !texto.isEmpty()
                    && c.getNombre().toLowerCase().contains(texto);
            if (porCodigo || porNombre) {
                resultado.add(c);
            }
        }
        return resultado;
    }

    public List<CategoriaRepuesto> listar() {
        return new ArrayList<>(categorias);
    }
}