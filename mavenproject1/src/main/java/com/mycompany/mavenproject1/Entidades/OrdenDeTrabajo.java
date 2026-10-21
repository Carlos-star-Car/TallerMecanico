package com.mycompany.mavenproject1.Entidades;

import java.time.LocalDate;

public class OrdenDeTrabajo {

    public static final String ESTADO_PENDIENTE = "PENDIENTE";
    public static final String ESTADO_FACTURADA = "FACTURADA";

    private final String codigo;
    private Cliente cliente;
    private Vehiculo vehiculo;          // opcional, puede ser null
    private Mecanicos mecanico;         // NUEVO
    private LocalDate fecha;
    private String descripcion;
    private String estado;

    public OrdenDeTrabajo(String codigo, Cliente cliente, LocalDate fecha, String descripcion) {
        this.codigo = codigo;
        this.cliente = cliente;
        this.fecha = fecha;
        this.descripcion = descripcion;
        this.estado = ESTADO_PENDIENTE;
    }

    public String getCodigo() { return codigo; }
    public Cliente getCliente() { return cliente; }
    public Vehiculo getVehiculo() { return vehiculo; }
    public Mecanicos getMecanico() { return mecanico; }                          // NUEVO
    public LocalDate getFecha() { return fecha; }
    public String getDescripcion() { return descripcion; }
    public String getEstado() { return estado; }

    public void setCliente(Cliente cliente) { this.cliente = cliente; }
    public void setVehiculo(Vehiculo vehiculo) { this.vehiculo = vehiculo; }
    public void setMecanico(Mecanicos mecanico) { this.mecanico = mecanico; }    // NUEVO
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public void setEstado(String estado) { this.estado = estado; }

    public boolean estaPendiente() {
        return ESTADO_PENDIENTE.equals(estado);
    }

    @Override
    public String toString() {
        return codigo;
    }
}