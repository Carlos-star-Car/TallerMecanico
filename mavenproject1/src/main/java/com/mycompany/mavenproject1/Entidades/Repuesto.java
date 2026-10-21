/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject1.Entidades;

/**
 *
 * @author cxoy
 */
public class Repuesto {
    

    private int codigoRepuesto;
    private String nombreRepuesto;
    private int existencia;
    private String unidadMedida;
    private double precioUnitario;
    private int codigoCategoria;
    private int codigoProveedor;



    public Repuesto(int codigoRepuesto, String nombreRepuesto, int existencia,
                    String unidadMedida, double precioUnitario,
                    int codigoCategoria, int codigoProveedor) {
        this.codigoRepuesto = codigoRepuesto;
        this.nombreRepuesto = nombreRepuesto;
        this.existencia = existencia;
        this.unidadMedida = unidadMedida;
        this.precioUnitario = precioUnitario;
        this.codigoCategoria = codigoCategoria;
        this.codigoProveedor = codigoProveedor;
    }

    public int getCodigoRepuesto() {
        return codigoRepuesto;
    }

    public void setCodigoRepuesto(int codigoRepuesto) {
        this.codigoRepuesto = codigoRepuesto;
    }

    public String getNombreRepuesto() {
        return nombreRepuesto;
    }

    public void setNombreRepuesto(String nombreRepuesto) {
        this.nombreRepuesto = nombreRepuesto;
    }

    public int getExistencia() {
        return existencia;
    }

    public void setExistencia(int existencia) {
        this.existencia = existencia;
    }

    public String getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public int getCodigoCategoria() {
        return codigoCategoria;
    }

    public void setCodigoCategoria(int codigoCategoria) {
        this.codigoCategoria = codigoCategoria;
    }

    public int getCodigoProveedor() {
        return codigoProveedor;
    }

    public void setCodigoProveedor(int codigoProveedor) {
        this.codigoProveedor = codigoProveedor;
    }     
}
