package com.mycompany.mavenproject1.Servicios;

import com.mycompany.mavenproject1.Entidades.Mecanicos;
import java.util.ArrayList;
import java.util.List;

public class MecanicosServicio {
 private static final List<Mecanicos> mecanicos = new ArrayList<>();
     private int contador = 1;

    // Código que tendrá la próxima orden (para mostrarlo en el formulario).
    public String siguienteCodigo() {
        return String.format("OT-%03d", contador);
    }

    // Entrega el código y avanza el contador. Se llama solo al guardar.
    public String nuevoCodigo() {
        return String.format("OT-%03d", contador++);
    }

    public boolean guardar(Mecanicos mecanico) {
        if (buscarPorCodigo(mecanico.getCodigo()) != null) {
            return false;
        }
        mecanicos.add(mecanico);
        return true;
    }

    public boolean modificar(Mecanicos mecanico) {
        Mecanicos existente = buscarPorCodigo(mecanico.getCodigo());
        if (existente == null) {
            return false;
        }
        existente.setNombre(mecanico.getNombre());
        existente.setApellido(mecanico.getApellido());
        existente.setTelefono(mecanico.getTelefono());
        existente.setEspecialidad(mecanico.getEspecialidad());
        existente.setTarifa(mecanico.getTarifa());
        return true;
    }

    public boolean eliminar(int codigo) {
        Mecanicos existente = buscarPorCodigo(codigo);
        if (existente == null) {
            return false;
        }
        mecanicos.remove(existente);
        return true;
    }

    public Mecanicos buscarPorCodigo(int codigo) {
        for (Mecanicos m : mecanicos) {
            if (m.getCodigo() == codigo) {
                return m;
            }
        }
        return null;
    }

    // Atajo usado por el formulario: devuelve un mecánico o null.
    public Mecanicos buscar(int codigo) {
        return buscarPorCodigo(codigo);
    }

    // Código exacto (si codigo >= 0) o parte del nombre.
    public List<Mecanicos> buscar(int codigo, String nombre) {
        List<Mecanicos> resultado = new ArrayList<>();
        for (Mecanicos m : mecanicos) {
            boolean porCodigo = codigo >= 0 && m.getCodigo() == codigo;
            boolean porNombre = !nombre.isEmpty()
                    && m.getNombre().toLowerCase().contains(nombre.toLowerCase());
            if (porCodigo || porNombre) {
                resultado.add(m);
            }
        }
        return resultado;
    }

    public List<Mecanicos> listar() {
        return new ArrayList<>(mecanicos);
    }
}