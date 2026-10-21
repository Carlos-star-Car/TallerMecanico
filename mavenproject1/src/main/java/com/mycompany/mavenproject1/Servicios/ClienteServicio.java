/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */



/**
 *
 * @author cxoy
 */
package com.mycompany.mavenproject1.Servicios;

import com.mycompany.mavenproject1.Entidades.Cliente;
import java.util.ArrayList;
import java.util.List;

public class ClienteServicio {
      // Instancia única compartida por todos los formularios
    private static final ClienteServicio INSTANCIA = new ClienteServicio();

    public static ClienteServicio getInstancia() {
        return INSTANCIA;
    }
    private final List<Cliente> clientes = new ArrayList<>();

    // Guarda un cliente nuevo. Devuelve false si el código ya existe.
    public boolean guardar(Cliente cliente) {
        if (buscarPorCodigo(cliente.getCodigo()) != null) {
            return false;
        }
        clientes.add(cliente);
        return true;
    }

    // Actualiza los datos de un cliente existente (el código no cambia).
    public boolean modificar(Cliente cliente) {
        Cliente existente = buscarPorCodigo(cliente.getCodigo());
        if (existente == null) {
            return false;
        }
        existente.setNombre(cliente.getNombre());
        existente.setApellido(cliente.getApellido());
        existente.setNit(cliente.getNit());
        existente.setTelefono(cliente.getTelefono());
        existente.setDireccion(cliente.getDireccion());
        existente.setCorreo(cliente.getCorreo());
        return true;
    }

    // Elimina un cliente por código. Devuelve false si no existe.
    public boolean eliminar(String codigo) {
        Cliente existente = buscarPorCodigo(codigo);
        if (existente == null) {
            return false;
        }
        clientes.remove(existente);
        return true;
    }

    // Busca por código exacto (ignora mayúsculas).
    public Cliente buscarPorCodigo(String codigo) {
        for (Cliente c : clientes) {
            if (c.getCodigo().equalsIgnoreCase(codigo)) {
                return c;
            }
        }
        return null;
    }

    // Busca clientes cuyo código sea exacto o cuyo nombre contenga el texto.
    public List<Cliente> buscar(String codigo, String nombre) {
        List<Cliente> resultado = new ArrayList<>();
        for (Cliente c : clientes) {
            boolean porCodigo = !codigo.isEmpty() && c.getCodigo().equalsIgnoreCase(codigo);
            boolean porNombre = !nombre.isEmpty()
                    && c.getNombre().toLowerCase().contains(nombre.toLowerCase());
            if (porCodigo || porNombre) {
                resultado.add(c);
            }
        }
        return resultado;
    }

    // Devuelve todos los clientes.
    public List<Cliente> listar() {
        return new ArrayList<>(clientes);
    }
}