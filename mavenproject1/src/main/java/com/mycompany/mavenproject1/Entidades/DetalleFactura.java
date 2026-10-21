package com.mycompany.mavenproject1.Entidades;

public class DetalleFactura {

    private int idDetalleFactura;
    private int idFactura;
    private String concepto;
    private int cantidad;
    private double precioUnitario;
    private Integer codigoRepuesto;   // null si la fila es de servicio

    public DetalleFactura() {
    }

    public DetalleFactura(int idDetalleFactura, int idFactura, String concepto,
                          int cantidad, double precioUnitario) {
        this.idDetalleFactura = idDetalleFactura;
        this.idFactura = idFactura;
        this.concepto = concepto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    public int getIdDetalleFactura() { return idDetalleFactura; }
    public void setIdDetalleFactura(int idDetalleFactura) { this.idDetalleFactura = idDetalleFactura; }

    public int getIdFactura() { return idFactura; }
    public void setIdFactura(int idFactura) { this.idFactura = idFactura; }

    public String getConcepto() { return concepto; }
    public void setConcepto(String concepto) { this.concepto = concepto; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(double precioUnitario) { this.precioUnitario = precioUnitario; }

    public Integer getCodigoRepuesto() { return codigoRepuesto; }
    public void setCodigoRepuesto(Integer codigoRepuesto) { this.codigoRepuesto = codigoRepuesto; }

    public boolean esRepuesto() {
        return codigoRepuesto != null;
    }

    // El subtotal de la fila se calcula, no se guarda como campo
    public double getSubtotal() {
        return cantidad * precioUnitario;
    }
}