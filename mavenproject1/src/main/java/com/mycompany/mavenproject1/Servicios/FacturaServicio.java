package com.mycompany.mavenproject1.Servicios;

import com.mycompany.mavenproject1.Entidades.DetalleFactura;
import com.mycompany.mavenproject1.Entidades.Factura;
import com.mycompany.mavenproject1.Entidades.OrdenDeTrabajo;
import com.mycompany.mavenproject1.Entidades.Repuesto;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FacturaServicio {

    // Instancia única compartida por todos los formularios
    private static final FacturaServicio INSTANCIA = new FacturaServicio();

    public static FacturaServicio getInstancia() {
        return INSTANCIA;
    }

    private final List<Factura> facturas = new ArrayList<>();
    private final RepuestoServicio repuestos = new RepuestoServicio();   // su lista es estática
    private int contador = 1;   // código automático (luego será AUTO_INCREMENT)

    // Código que tendrá la próxima factura (para mostrarlo en txtCodigofactura).
    public int siguienteCodigo() {
        return contador;
    }

    // Revisa la factura. Devuelve el mensaje de error, o null si todo está bien.
    public String validar(Factura f) {
        if (f.getTipoFactura() == null) {
            return "Seleccione si factura por Servicio o por Repuesto.";
        }
        if (f.getDetalles().isEmpty()) {
            return "Agregue al menos un detalle a la factura.";
        }
        for (DetalleFactura d : f.getDetalles()) {
            if (d.getConcepto() == null || d.getConcepto().trim().isEmpty()
                    || d.getCantidad() <= 0 || d.getPrecioUnitario() <= 0) {
                return "Cada detalle necesita concepto, cantidad y precio mayores a cero.";
            }
        }
        if (Factura.TIPO_REPUESTO.equals(f.getTipoFactura())) {
            return validarRepuestos(f);
        }
        if (Factura.TIPO_SERVICIO.equals(f.getTipoFactura())) {
            if (f.getIdOrden() != null) {
                OrdenDeTrabajo orden = OrdenDeTrabajoServicio.getInstancia()
                        .buscarPorCodigo(f.getIdOrden());
                if (orden == null) {
                    return "La orden de trabajo no existe.";
                }
                if (f.getCliente() == null) {
                    return "La orden seleccionada no tiene cliente.";
                }
                if (!orden.estaPendiente()) {
                    return "La orden de trabajo ya fue facturada.";
                }
            } else if (vacio(f.getNombreManual()) || vacio(f.getNitManual())
                    || vacio(f.getDireccionManual())) {
                return "Ingrese nombre, NIT y dirección del cliente.";
            }
        }
        return null;
    }

    // Cada fila debe ser un repuesto existente y la existencia debe alcanzar
    // (si el mismo repuesto aparece en varias filas, se suman las cantidades).
    private String validarRepuestos(Factura f) {
        Map<Integer, Integer> pedido = new LinkedHashMap<>();
        for (DetalleFactura d : f.getDetalles()) {
            if (!d.esRepuesto()) {
                return "Todas las filas de una factura por repuesto deben tener un repuesto seleccionado.";
            }
            pedido.merge(d.getCodigoRepuesto(), d.getCantidad(), Integer::sum);
        }
        for (Map.Entry<Integer, Integer> e : pedido.entrySet()) {
            Repuesto r = repuestos.obtener(e.getKey());
            if (r == null) {
                return "El repuesto " + e.getKey() + " ya no existe.";
            }
            if (r.getExistencia() < e.getValue()) {
                return "Existencia insuficiente de " + r.getNombreRepuesto()
                        + ": disponible " + r.getExistencia()
                        + ", solicitado " + e.getValue() + ".";
            }
        }
        return null;
    }

    // Guarda una factura nueva, le asigna el código y descuenta el stock.
    // Devuelve false si no es válida.
    public boolean guardar(Factura factura) {
        if (validar(factura) != null) {
            return false;
        }
        for (DetalleFactura d : factura.getDetalles()) {
            if (d.esRepuesto()) {
                repuestos.descontarExistencia(d.getCodigoRepuesto(), d.getCantidad());
            }
        }
        factura.setIdFactura(contador++);
        factura.setEstado(Factura.ESTADO_VIGENTE);
        if (factura.getFechaEmision() == null) {
            factura.setFechaEmision(LocalDate.now());
        }
        facturas.add(factura);
        if (factura.getIdOrden() != null) {
            OrdenDeTrabajoServicio.getInstancia().marcarComoFacturada(factura.getIdOrden());
        }
        return true;
    }

    // Anula una factura: no se borra, queda ANULADA, los repuestos regresan al
    // stock y su orden de trabajo vuelve a quedar pendiente.
    // Devuelve el mensaje de error, o null si se anuló.
    public String anular(int idFactura) {
        Factura f = buscarPorCodigo(idFactura);
        if (f == null) {
            return "La factura no existe.";
        }
        if (f.estaAnulada()) {
            return "La factura ya está anulada.";
        }
        for (DetalleFactura d : f.getDetalles()) {
            if (d.esRepuesto()) {
                // Si el repuesto ya no existe en el inventario, no hay a quién devolverle.
                repuestos.devolverExistencia(d.getCodigoRepuesto(), d.getCantidad());
            }
        }
        f.setEstado(Factura.ESTADO_ANULADA);
        if (f.getIdOrden() != null) {
            OrdenDeTrabajoServicio.getInstancia().marcarComoPendiente(f.getIdOrden());
        }
        return null;
    }

    public Factura buscarPorCodigo(int idFactura) {
        for (Factura f : facturas) {
            if (f.getIdFactura() == idFactura) {
                return f;
            }
        }
        return null;
    }

    public List<Factura> listar() {
        return new ArrayList<>(facturas);
    }

    private boolean vacio(String texto) {
        return texto == null || texto.trim().isEmpty();
    }
}