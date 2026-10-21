package com.mycompany.mavenproject1.Entidades;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Factura {

    public static final String TIPO_SERVICIO = "SERVICIO";
    public static final String TIPO_REPUESTO = "REPUESTO";
    public static final String ESTADO_VIGENTE = "VIGENTE";
    public static final String ESTADO_ANULADA = "ANULADA";
    public static final double TASA_IVA = 0.12;

    private int idFactura;               // lo asigna FacturaServicio al guardar
    private String tipoFactura;          // SERVICIO o REPUESTO
    private String estado = ESTADO_VIGENTE;
    private String idOrden;              // null si no hay orden de servicio
    private Cliente cliente;             // solo cuando hay orden; si no, null
    private LocalDate fechaEmision;

    // Datos escritos a mano (servicio sin orden). Con orden se toman del Cliente.
    private String nombreManual;
    private String nitManual;
    private String direccionManual;

    private List<DetalleFactura> detalles = new ArrayList<>();

    public Factura() {
    }

    public int getIdFactura() { return idFactura; }
    public void setIdFactura(int idFactura) { this.idFactura = idFactura; }

    public String getTipoFactura() { return tipoFactura; }
    public void setTipoFactura(String tipoFactura) { this.tipoFactura = tipoFactura; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public boolean estaAnulada() {
        return ESTADO_ANULADA.equals(estado);
    }

    public String getIdOrden() { return idOrden; }
    public void setIdOrden(String idOrden) { this.idOrden = idOrden; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public LocalDate getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(LocalDate fechaEmision) { this.fechaEmision = fechaEmision; }

    public String getNombreManual() { return nombreManual; }
    public void setNombreManual(String nombreManual) { this.nombreManual = nombreManual; }

    public String getNitManual() { return nitManual; }
    public void setNitManual(String nitManual) { this.nitManual = nitManual; }

    public String getDireccionManual() { return direccionManual; }
    public void setDireccionManual(String direccionManual) { this.direccionManual = direccionManual; }

    public List<DetalleFactura> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleFactura> detalles) { this.detalles = detalles; }

    public void agregarDetalle(DetalleFactura detalle) {
        detalles.add(detalle);
    }

    public double getSubtotal() {
        double suma = 0;
        for (DetalleFactura d : detalles) {
            suma += d.getSubtotal();
        }
        return redondear(suma);
    }

    public double getIva() {
        return redondear(getSubtotal() * TASA_IVA);
    }

    public double getTotal() {
        return redondear(getSubtotal() + getIva());
    }

    private double redondear(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }
}