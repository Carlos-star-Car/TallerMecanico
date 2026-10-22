package com.mycompany.mavenproject1.Servicios;

import com.mycompany.mavenproject1.Conexion.ConexionBD;
import com.mycompany.mavenproject1.Entidades.Cliente;
import com.mycompany.mavenproject1.Entidades.Mecanicos;
import com.mycompany.mavenproject1.Entidades.OrdenDeTrabajo;
import com.mycompany.mavenproject1.Entidades.Vehiculo;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servicio de ordenes de trabajo con almacenamiento en MySQL (tabla orden_trabajo).
 * Mantiene los mismos metodos que la version en memoria. El codigo (OT-001, OT-002...)
 * se calcula a partir del mayor codigo guardado en la base de datos.
 */
public class OrdenDeTrabajoServicio {

    private static final Logger LOG = Logger.getLogger(OrdenDeTrabajoServicio.class.getName());

    // Instancia unica compartida por todos los formularios
    private static final OrdenDeTrabajoServicio INSTANCIA = new OrdenDeTrabajoServicio();

    public static OrdenDeTrabajoServicio getInstancia() {
        return INSTANCIA;
    }

    private static final String COLUMNAS =
            "codigo, fecha, estado, descripcion, codigo_cliente, placa, codigo_mecanico";

    private final ClienteServicio clientes = ClienteServicio.getInstancia();
    private final VehiculoServicio vehiculos = new VehiculoServicio();
    private final MecanicosServicio mecanicos = new MecanicosServicio();

    // Codigo que tendra la proxima orden (para mostrarlo en el formulario).
    public String siguienteCodigo() {
        String sql = "SELECT COALESCE(MAX(CAST(SUBSTRING(codigo, 4) AS UNSIGNED)), 0) + 1 "
                + "FROM orden_trabajo WHERE codigo LIKE 'OT-%'";
        try (Connection cn = ConexionBD.obtener();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            int numero = rs.next() ? rs.getInt(1) : 1;
            return String.format("OT-%03d", numero);
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "No se pudo calcular el siguiente codigo de orden", ex);
            return "OT-001";
        }
    }

    // Entrega el codigo que corresponde a la orden nueva. Se llama solo al guardar.
    public String nuevoCodigo() {
        return siguienteCodigo();
    }

    // Guarda una orden nueva. Devuelve false si el codigo ya existe o hay un error.
    public boolean guardar(OrdenDeTrabajo orden) {
        if (buscarPorCodigo(orden.getCodigo()) != null) {
            return false;
        }
        String sql = "INSERT INTO orden_trabajo (" + COLUMNAS + ") VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection cn = ConexionBD.obtener();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, orden.getCodigo());
            ps.setDate(2, Date.valueOf(orden.getFecha()));
            ps.setString(3, orden.getEstado());
            ps.setString(4, orden.getDescripcion());
            ps.setString(5, orden.getCliente().getCodigo());
            if (orden.getVehiculo() != null) {
                ps.setString(6, orden.getVehiculo().getPlaca());
            } else {
                ps.setNull(6, Types.VARCHAR);
            }
            if (orden.getMecanico() != null) {
                ps.setInt(7, orden.getMecanico().getCodigo());
            } else {
                ps.setNull(7, Types.INTEGER);
            }
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "No se pudo guardar la orden", ex);
            return false;
        }
    }

    // Actualiza los datos de una orden existente (el codigo no cambia).
    public boolean modificar(OrdenDeTrabajo orden) {
        String sql = "UPDATE orden_trabajo SET fecha = ?, estado = ?, descripcion = ?, "
                + "codigo_cliente = ?, placa = ?, codigo_mecanico = ? WHERE codigo = ?";
        try (Connection cn = ConexionBD.obtener();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(orden.getFecha()));
            ps.setString(2, orden.getEstado());
            ps.setString(3, orden.getDescripcion());
            ps.setString(4, orden.getCliente().getCodigo());
            if (orden.getVehiculo() != null) {
                ps.setString(5, orden.getVehiculo().getPlaca());
            } else {
                ps.setNull(5, Types.VARCHAR);
            }
            if (orden.getMecanico() != null) {
                ps.setInt(6, orden.getMecanico().getCodigo());
            } else {
                ps.setNull(6, Types.INTEGER);
            }
            ps.setString(7, orden.getCodigo());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "No se pudo modificar la orden", ex);
            return false;
        }
    }

    // Elimina una orden por codigo. Devuelve false si no existe o si tiene una factura.
    public boolean eliminar(String codigo) {
        String sql = "DELETE FROM orden_trabajo WHERE codigo = ?";
        try (Connection cn = ConexionBD.obtener();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, codigo);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "No se pudo eliminar la orden", ex);
            return false;
        }
    }

    // Busca por codigo exacto. Devuelve null si no existe.
    public OrdenDeTrabajo buscarPorCodigo(String codigo) {
        String sql = "SELECT " + COLUMNAS + " FROM orden_trabajo WHERE codigo = ?";
        try (Connection cn = ConexionBD.obtener();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "No se pudo buscar la orden", ex);
            return null;
        }
    }

    // Devuelve todas las ordenes.
    public List<OrdenDeTrabajo> listar() {
        return consultar("SELECT " + COLUMNAS + " FROM orden_trabajo ORDER BY codigo");
    }

    // Devuelve solo las ordenes pendientes de facturar (para el combo de FrmFactura).
    public List<OrdenDeTrabajo> listarPendientes() {
        return consultar("SELECT " + COLUMNAS + " FROM orden_trabajo "
                + "WHERE estado = 'PENDIENTE' ORDER BY codigo");
    }

    // Cambia el estado a FACTURADA. La llama FacturaServicio al guardar una factura.
    public boolean marcarComoFacturada(String codigo) {
        return cambiarEstado(codigo, OrdenDeTrabajo.ESTADO_FACTURADA);
    }

    // Regresa la orden a PENDIENTE (al anular su factura).
    public boolean marcarComoPendiente(String codigo) {
        return cambiarEstado(codigo, OrdenDeTrabajo.ESTADO_PENDIENTE);
    }

    private boolean cambiarEstado(String codigo, String estado) {
        String sql = "UPDATE orden_trabajo SET estado = ? WHERE codigo = ?";
        try (Connection cn = ConexionBD.obtener();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, estado);
            ps.setString(2, codigo);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "No se pudo cambiar el estado de la orden", ex);
            return false;
        }
    }

    private List<OrdenDeTrabajo> consultar(String sql) {
        List<OrdenDeTrabajo> resultado = new ArrayList<>();
        try (Connection cn = ConexionBD.obtener();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                resultado.add(mapear(rs));
            }
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "No se pudo listar las ordenes", ex);
        }
        return resultado;
    }

    private OrdenDeTrabajo mapear(ResultSet rs) throws SQLException {
        Cliente cliente = clientes.buscarPorCodigo(rs.getString("codigo_cliente"));
        OrdenDeTrabajo orden = new OrdenDeTrabajo(rs.getString("codigo"), cliente,
                rs.getDate("fecha").toLocalDate(), rs.getString("descripcion"));
        orden.setEstado(rs.getString("estado"));

        String placa = rs.getString("placa");
        if (placa != null) {
            Vehiculo v = vehiculos.buscarPorPlaca(placa);
            orden.setVehiculo(v);
        }
        int codigoMecanico = rs.getInt("codigo_mecanico");
        if (!rs.wasNull()) {
            Mecanicos m = mecanicos.buscarPorCodigo(codigoMecanico);
            orden.setMecanico(m);
        }
        return orden;
    }
}