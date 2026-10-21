package com.mycompany.mavenproject1.Servicios;

import com.mycompany.mavenproject1.Entidades.OrdenDeTrabajo;
import java.util.ArrayList;
import java.util.List;

public class OrdenDeTrabajoServicio {

    // Instancia única compartida por todos los formularios
    private static final OrdenDeTrabajoServicio INSTANCIA = new OrdenDeTrabajoServicio();

    public static OrdenDeTrabajoServicio getInstancia() {
        return INSTANCIA;
    }

    private final List<OrdenDeTrabajo> ordenes = new ArrayList<>();
    private int contador = 1;   // código automático (luego será AUTO_INCREMENT)

    // Código que tendrá la próxima orden (para mostrarlo en el formulario).
    public String siguienteCodigo() {
        return String.format("OT-%03d", contador);
    }

    // Entrega el código y avanza el contador. Se llama solo al guardar.
    public String nuevoCodigo() {
        return String.format("OT-%03d", contador++);
    }

    // Guarda una orden nueva. Devuelve false si el código ya existe.
    public boolean guardar(OrdenDeTrabajo orden) {
        if (buscarPorCodigo(orden.getCodigo()) != null) {
            return false;
        }
        ordenes.add(orden);
        return true;
    }

    // Actualiza los datos de una orden existente (el código no cambia).
    public boolean modificar(OrdenDeTrabajo orden) {
        OrdenDeTrabajo existente = buscarPorCodigo(orden.getCodigo());
        if (existente == null) {
            return false;
        }
        existente.setCliente(orden.getCliente());
        existente.setVehiculo(orden.getVehiculo());
        existente.setMecanico(orden.getMecanico());     
        existente.setFecha(orden.getFecha());
        existente.setDescripcion(orden.getDescripcion());
        existente.setEstado(orden.getEstado());
        return true;
    }

    // Elimina una orden por código. Devuelve false si no existe.
    public boolean eliminar(String codigo) {
        OrdenDeTrabajo existente = buscarPorCodigo(codigo);
        if (existente == null) {
            return false;
        }
        ordenes.remove(existente);
        return true;
    }

    // Busca por código exacto (ignora mayúsculas).
    public OrdenDeTrabajo buscarPorCodigo(String codigo) {
        for (OrdenDeTrabajo o : ordenes) {
            if (o.getCodigo().equalsIgnoreCase(codigo)) {
                return o;
            }
        }
        return null;
    }

    // Devuelve todas las órdenes.
    public List<OrdenDeTrabajo> listar() {
        return new ArrayList<>(ordenes);
    }

    // Devuelve solo las órdenes pendientes de facturar (para el combo de FrmFactura).
    public List<OrdenDeTrabajo> listarPendientes() {
        List<OrdenDeTrabajo> pendientes = new ArrayList<>();
        for (OrdenDeTrabajo o : ordenes) {
            if (o.estaPendiente()) {
                pendientes.add(o);
            }
        }
        return pendientes;
    }

    // Cambia el estado a FACTURADA. La llama FacturaServicio al guardar una factura.
    public boolean marcarComoFacturada(String codigo) {
        OrdenDeTrabajo existente = buscarPorCodigo(codigo);
        if (existente == null) {
            return false;
        }
        existente.setEstado(OrdenDeTrabajo.ESTADO_FACTURADA);
        return true;
    }
}