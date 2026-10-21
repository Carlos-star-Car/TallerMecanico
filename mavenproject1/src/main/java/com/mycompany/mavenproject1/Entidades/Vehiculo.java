/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject1.Entidades;

/**
 *
 * @author cxoy
 */
public class Vehiculo {
    

    private final String placa;
    private String marca;
    private String modelo;
    private String color;
    private String tipo;
    private String codigoCliente;   // viene del jCBCodigoCliente

    public Vehiculo(String placa, String marca, String modelo,
                    String color, String tipo, String codigoCliente) {
        this.placa = placa;
        this.marca = marca;
        this.modelo = modelo;
        this.color = color;
        this.tipo = tipo;
        this.codigoCliente = codigoCliente;
    }

    public String getPlaca() { return placa; }
    public String getMarca() { return marca; }
    public String getModelo() { return modelo; }
    public String getColor() { return color; }
    public String getTipo() { return tipo; }
    public String getCodigoCliente() { return codigoCliente; }

    public void setMarca(String marca) { this.marca = marca; }
    public void setModelo(String modelo) { this.modelo = modelo; }
    public void setColor(String color) { this.color = color; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public void setCodigoCliente(String codigoCliente) { this.codigoCliente = codigoCliente; }
}