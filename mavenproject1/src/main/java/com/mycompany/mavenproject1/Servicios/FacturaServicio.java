package com.mycompany.mavenproject1.Servicios;

import com.mycompany.mavenproject1.Conexion.ConexionBD;
import com.mycompany.mavenproject1.Entidades.DetalleFactura;
import com.mycompany.mavenproject1.Entidades.Factura;
import com.mycompany.mavenproject1.Entidades.OrdenDeTrabajo;
import com.mycompany.mavenproject1.Entidades.Repuesto;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servicio de facturas con almacenamiento en MySQL (tablas factura y detalle_factura).
 * Guardar y anular se hacen en una transaccion: o se aplican todos los cambios
 * (factura, detalle, stock y orden) o ninguno.
 */
public class FacturaServicio {

    private static final Logger LOG = Logger.getLogger(FacturaServicio.class.getName());

    // Instancia unica compartida por todos los formularios
    private static final FacturaServicio INSTANCIA = new FacturaServicio();

    public static FacturaServicio getInstancia() {
        return INSTANCIA;
    }

    private static final String COLUMNAS_FACTURA =
            "id_factura, tipo_factura, estado, fecha_emision, codigo_orden, codigo_cliente, "
            + "nombre_manual, nit_manual, direccion_manual";

    private final RepuestoServicio repuestos = new RepuestoServicio();
    private final ClienteServicio clientes = ClienteServicio.getInstancia();

    // Codigo que tendra la proxima factura (para mostrarlo en txtCodigofactura).
    public int siguienteCodigo() {
        String sql = "SELECT COALESCE(MAX(id_factura), 0) + 1 FROM factura";
        try (Connection cn = ConexionBD.obtener();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 1;
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "No se pudo calcular el siguiente codigo de factura", ex);
            return 1;
        }
    }

    // Revisa la factura. Devuelve el mensaje de error, o null si todo esta bien.
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
                return "Ingrese nombre, NIT y direccion del cliente.";
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

    // Guarda una factura nueva, le asigna el codigo y descuenta el stock.
    // Devuelve false si no es valida o si algo falla (en ese caso no se guarda nada).
    public boolean guardar(Factura factura) {
        if (validar(factura) != null) {
            return false;
        }
        if (factura.getFechaEmision() == null) {
            factura.setFechaEmision(LocalDate.now());
        }
        factura.setEstado(Factura.ESTADO_VIGENTE);

        try (Connection cn = ConexionBD.obtener()) {
            cn.setAutoCommit(false);
            try {
                int id = insertarFactura(cn, factura);
                for (DetalleFactura d : factura.getDetalles()) {
                    insertarDetalle(cn, id, d);
                    if (d.esRepuesto() && !restarStock(cn, d.getCodigoRepuesto(), d.getCantidad())) {
                        cn.rollback();
                        return false;   // otra persona se llevo el stock
                    }
                }
                if (factura.getIdOrden() != null
                        && !cambiarEstadoOrden(cn, factura.getIdOrden(),
                                OrdenDeTrabajo.ESTADO_PENDIENTE, OrdenDeTrabajo.ESTADO_FACTURADA)) {
                    cn.rollback();
                    return false;       // la orden ya no estaba pendiente
                }
                cn.commit();
                factura.setIdFactura(id);
                return true;
            } catch (SQLException ex) {
                cn.rollback();
                throw ex;
            }
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "No se pudo guardar la factura", ex);
            return false;
        }
    }

    // Anula una factura: no se borra, queda ANULADA, los repuestos regresan al
    // stock y su orden de trabajo vuelve a quedar pendiente.
    // Devuelve el mensaje de error, o null si se anulo.
    public String anular(int idFactura) {
        Factura f = buscarPorCodigo(idFactura);
        if (f == null) {
            return "La factura no existe.";
        }
        if (f.estaAnulada()) {
            return "La factura ya esta anulada.";
        }
        try (Connection cn = ConexionBD.obtener()) {
            cn.setAutoCommit(false);
            try {
                try (PreparedStatement ps = cn.prepareStatement(
                        "UPDATE factura SET estado = 'ANULADA' WHERE id_factura = ? AND estado = 'VIGENTE'")) {
                    ps.setInt(1, idFactura);
                    if (ps.executeUpdate() == 0) {
                        cn.rollback();
                        return "La factura ya esta anulada.";
                    }
                }
                for (DetalleFactura d : f.getDetalles()) {
                    if (d.esRepuesto()) {
                        try (PreparedStatement ps = cn.prepareStatement(
                                "UPDATE repuesto SET existencia = existencia + ? WHERE codigo = ?")) {
                            ps.setInt(1, d.getCantidad());
                            ps.setInt(2, d.getCodigoRepuesto());
                            ps.executeUpdate();
                        }
                    }
                }
                if (f.getIdOrden() != null) {
                    cambiarEstadoOrden(cn, f.getIdOrden(),
                            OrdenDeTrabajo.ESTADO_FACTURADA, OrdenDeTrabajo.ESTADO_PENDIENTE);
                }
                cn.commit();
                return null;
            } catch (SQLException ex) {
                cn.rollback();
                throw ex;
            }
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "No se pudo anular la factura", ex);
            return "No se pudo anular la factura. Intente de nuevo.";
        }
    }

    public Factura buscarPorCodigo(int idFactura) {
        String sql = "SELECT " + COLUMNAS_FACTURA + " FROM factura WHERE id_factura = ?";
        try (Connection cn = ConexionBD.obtener();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idFactura);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                Factura f = mapear(rs);
                f.setDetalles(cargarDetalles(cn, f.getIdFactura()));
                return f;
            }
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "No se pudo buscar la factura", ex);
            return null;
        }
    }

    public List<Factura> listar() {
        List<Factura> resultado = new ArrayList<>();
        String sql = "SELECT " + COLUMNAS_FACTURA + " FROM factura ORDER BY id_factura";
        try (Connection cn = ConexionBD.obtener();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Factura f = mapear(rs);
                f.setDetalles(cargarDetalles(cn, f.getIdFactura()));
                resultado.add(f);
            }
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "No se pudo listar las facturas", ex);
        }
        return resultado;
    }

    // ---------- Apoyo ----------

    private int insertarFactura(Connection cn, Factura f) throws SQLException {
        String sql = "INSERT INTO factura (tipo_factura, estado, fecha_emision, codigo_orden, "
                + "codigo_cliente, nombre_manual, nit_manual, direccion_manual) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, f.getTipoFactura());
            ps.setString(2, f.getEstado());
            ps.setDate(3, Date.valueOf(f.getFechaEmision()));
            ps.setString(4, f.getIdOrden());
            if (f.getCliente() != null) {
                ps.setString(5, f.getCliente().getCodigo());
            } else {
                ps.setNull(5, Types.VARCHAR);
            }
            ps.setString(6, f.getNombreManual());
            ps.setString(7, f.getNitManual());
            ps.setString(8, f.getDireccionManual());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
            throw new SQLException("La base de datos no devolvio el numero de factura.");
        }
    }

    private void insertarDetalle(Connection cn, int idFactura, DetalleFactura d) throws SQLException {
        String sql = "INSERT INTO detalle_factura (id_factura, concepto, cantidad, "
                + "precio_unitario, codigo_repuesto) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idFactura);
            ps.setString(2, d.getConcepto());
            ps.setInt(3, d.getCantidad());
            ps.setDouble(4, d.getPrecioUnitario());
            if (d.esRepuesto()) {
                ps.setInt(5, d.getCodigoRepuesto());
            } else {
                ps.setNull(5, Types.INTEGER);
            }
            ps.executeUpdate();
        }
    }

    // Resta stock solo si alcanza.
    private boolean restarStock(Connection cn, int codigoRepuesto, int cantidad) throws SQLException {
        String sql = "UPDATE repuesto SET existencia = existencia - ? WHERE codigo = ? AND existencia >= ?";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, cantidad);
            ps.setInt(2, codigoRepuesto);
            ps.setInt(3, cantidad);
            return ps.executeUpdate() > 0;
        }
    }

    private boolean cambiarEstadoOrden(Connection cn, String codigo, String desde, String hacia)
            throws SQLException {
        String sql = "UPDATE orden_trabajo SET estado = ? WHERE codigo = ? AND estado = ?";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, hacia);
            ps.setString(2, codigo);
            ps.setString(3, desde);
            return ps.executeUpdate() > 0;
        }
    }

    private List<DetalleFactura> cargarDetalles(Connection cn, int idFactura) throws SQLException {
        List<DetalleFactura> detalles = new ArrayList<>();
        String sql = "SELECT id_detalle, id_factura, concepto, cantidad, precio_unitario, codigo_repuesto "
                + "FROM detalle_factura WHERE id_factura = ? ORDER BY id_detalle";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idFactura);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DetalleFactura d = new DetalleFactura(rs.getInt("id_detalle"),
                            rs.getInt("id_factura"), rs.getString("concepto"),
                            rs.getInt("cantidad"), rs.getDouble("precio_unitario"));
                    int codigoRepuesto = rs.getInt("codigo_repuesto");
                    if (!rs.wasNull()) {
                        d.setCodigoRepuesto(codigoRepuesto);
                    }
                    detalles.add(d);
                }
            }
        }
        return detalles;
    }

    private Factura mapear(ResultSet rs) throws SQLException {
        Factura f = new Factura();
        f.setIdFactura(rs.getInt("id_factura"));
        f.setTipoFactura(rs.getString("tipo_factura"));
        f.setEstado(rs.getString("estado"));
        f.setFechaEmision(rs.getDate("fecha_emision").toLocalDate());
        f.setIdOrden(rs.getString("codigo_orden"));
        String codigoCliente = rs.getString("codigo_cliente");
        if (codigoCliente != null) {
            f.setCliente(clientes.buscarPorCodigo(codigoCliente));
        }
        f.setNombreManual(rs.getString("nombre_manual"));
        f.setNitManual(rs.getString("nit_manual"));
        f.setDireccionManual(rs.getString("direccion_manual"));
        return f;
    }

    private boolean vacio(String texto) {
        return texto == null || texto.trim().isEmpty();
    }
}