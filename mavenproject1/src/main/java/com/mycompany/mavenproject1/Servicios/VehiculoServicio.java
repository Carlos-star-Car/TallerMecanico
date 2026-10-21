package com.mycompany.mavenproject1.Servicios;

import com.mycompany.mavenproject1.Entidades.Vehiculo;
import java.util.ArrayList;
import java.util.List;

public class VehiculoServicio {
   private static final List<Vehiculo> vehiculos = new ArrayList<>();

    public boolean guardar(Vehiculo v) {
        if (buscarPorPlaca(v.getPlaca()) != null) {
            return false;
        }
        vehiculos.add(v);
        return true;
    }

    public boolean modificar(Vehiculo v) {
        Vehiculo existente = buscarPorPlaca(v.getPlaca());
        if (existente == null) {
            return false;
        }
        existente.setMarca(v.getMarca());
        existente.setModelo(v.getModelo());
        existente.setColor(v.getColor());
        existente.setTipo(v.getTipo());
        existente.setCodigoCliente(v.getCodigoCliente());
        return true;
    }

    public boolean eliminar(String placa) {
        Vehiculo existente = buscarPorPlaca(placa);
        if (existente == null) {
            return false;
        }
        vehiculos.remove(existente);
        return true;
    }

    public Vehiculo buscarPorPlaca(String placa) {
        for (Vehiculo v : vehiculos) {
            if (v.getPlaca().equalsIgnoreCase(placa)) {
                return v;
            }
        }
        return null;
    }

    // Placa exacta o parte del modelo.
    public List<Vehiculo> buscar(String placa, String modelo) {
        List<Vehiculo> resultado = new ArrayList<>();
        for (Vehiculo v : vehiculos) {
            boolean porPlaca = !placa.isEmpty() && v.getPlaca().equalsIgnoreCase(placa);
            boolean porModelo = !modelo.isEmpty()
                    && v.getModelo().toLowerCase().contains(modelo.toLowerCase());
            if (porPlaca || porModelo) {
                resultado.add(v);
            }
        }
        return resultado;
    }

    public List<Vehiculo> listar() {
        return new ArrayList<>(vehiculos);
    }
}