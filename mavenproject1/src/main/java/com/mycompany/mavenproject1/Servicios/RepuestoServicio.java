package com.mycompany.mavenproject1.Servicios;

import com.mycompany.mavenproject1.Conexion.ConexionBD;
import com.mycompany.mavenproject1.Entidades.Repuesto;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servicio de repuestos con almacenamiento en MySQL (tabla repuesto).
 * Mantiene los mismos metodos que la version en memoria.
 */
public class RepuestoServicio {

    private static final Logger LOG = Logger.getLogger(RepuestoServicio.class.getName());

    private static final String COLUMNAS =
            "codigo, nombre, existencia, unidad_medida, precio_unitario, codigo_categoria, codigo_proveedor";

    /** Agrega un repuesto. Devuelve false si el codigo ya existe o hay un error. */
    public boolean guardar(Repuesto r) {
        if (existeCodigo(r.getCodigoRepuesto())) {
            return false;
        }
        String sql = "INSERT INTO repuesto (" + COLUMNAS + ") VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection cn = ConexionBD.obtener();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, r.getCodigoRepuesto());
            ps.setString(2, r.getNombreRepuesto());
            ps.setInt(3, r.getExistencia());
            ps.setString(4, r.getUnidadMedida());
            ps.setDouble(5, r.getPrecioUnitario());
            ps.setInt(6, r.getCodigoCategoria());
            ps.setInt(7, r.getCodigoProveedor());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "No se pudo guardar el repuesto", ex);
            return false;
        }
    }

    /** Reemplaza los datos del repuesto con ese codigo. Devuelve false si no existe. */
    public boolean modificar(Repuesto r) {
        String sql = "UPDATE repuesto SET nombre = ?, existencia = ?, unidad_medida = ?, "
                + "precio_unitario = ?, codigo_categoria = ?, codigo_proveedor = ? WHERE codigo = ?";
        try (Connection cn = ConexionBD.obtener();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, r.getNombreRepuesto());
            ps.setInt(2, r.getExistencia());
            ps.setString(3, r.getUnidadMedida());
            ps.setDouble(4, r.getPrecioUnitario());
            ps.setInt(5, r.getCodigoCategoria());
            ps.setInt(6, r.getCodigoProveedor());
            ps.setInt(7, r.getCodigoRepuesto());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "No se pudo modificar el repuesto", ex);
            return false;
        }
    }

    /** Elimina por codigo. Devuelve false si no existe o si ya fue usado en una factura. */
    public boolean eliminar(int codigoRepuesto) {
        String sql = "DELETE FROM repuesto WHERE codigo = ?";
        try (Connection cn = ConexionBD.obtener();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, codigoRepuesto);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "No se pudo eliminar el repuesto", ex);
            return false;
        }
    }

    /** Devuelve la lista completa. */
    public List<Repuesto> listar() {
        List<Repuesto> resultado = new ArrayList<>();
        String sql = "SELECT " + COLUMNAS + " FROM repuesto ORDER BY codigo";
        try (Connection cn = ConexionBD.obtener();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                resultado.add(mapear(rs));
            }
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "No se pudo listar los repuestos", ex);
        }
        return resultado;
    }

    /** Devuelve el repuesto con ese codigo, o null si no existe. */
    public Repuesto obtener(int codigoRepuesto) {
        String sql = "SELECT " + COLUMNAS + " FROM repuesto WHERE codigo = ?";
        try (Connection cn = ConexionBD.obtener();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, codigoRepuesto);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "No se pudo buscar el repuesto", ex);
            return null;
        }
    }

    /**
     * Busca por codigo exacto (si el texto es numerico) o por nombre que contenga
     * el texto. Lista vacia si no hay resultados.
     */
    public List<Repuesto> buscar(String criterio) {
        List<Repuesto> resultado = new ArrayList<>();
        String texto = criterio.trim();

        int esCodigo = 0;
        int codigo = -1;
        try {
            codigo = Integer.parseInt(texto);
            esCodigo = 1;
        } catch (NumberFormatException ex) {
            // no es un codigo; se busca solo por nombre
        }

        String sql = "SELECT " + COLUMNAS + " FROM repuesto "
                + "WHERE (? = 1 AND codigo = ?) OR nombre LIKE ? ORDER BY codigo";
        try (Connection cn = ConexionBD.obtener();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, esCodigo);
            ps.setInt(2, codigo);
            ps.setString(3, "%" + texto + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    resultado.add(mapear(rs));
                }
            }
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "No se pudo buscar repuestos", ex);
        }
        return resultado;
    }

    /** Resta unidades a la existencia. Devuelve false si no existe o no alcanza. */
    public boolean descontarExistencia(int codigoRepuesto, int cantidad) {
        if (cantidad <= 0) {
            return false;
        }
        String sql = "UPDATE repuesto SET existencia = existencia - ? "
                + "WHERE codigo = ? AND existencia >= ?";
        try (Connection cn = ConexionBD.obtener();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, cantidad);
            ps.setInt(2, codigoRepuesto);
            ps.setInt(3, cantidad);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "No se pudo descontar la existencia", ex);
            return false;
        }
    }

    /** Suma unidades a la existencia (al anular una factura). Devuelve false si no existe. */
    public boolean devolverExistencia(int codigoRepuesto, int cantidad) {
        if (cantidad <= 0) {
            return false;
        }
        String sql = "UPDATE repuesto SET existencia = existencia + ? WHERE codigo = ?";
        try (Connection cn = ConexionBD.obtener();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, cantidad);
            ps.setInt(2, codigoRepuesto);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "No se pudo devolver la existencia", ex);
            return false;
        }
    }

    public boolean existeCodigo(int codigoRepuesto) {
        return obtener(codigoRepuesto) != null;
    }

    private Repuesto mapear(ResultSet rs) throws SQLException {
        return new Repuesto(rs.getInt("codigo"), rs.getString("nombre"),
                rs.getInt("existencia"), rs.getString("unidad_medida"),
                rs.getDouble("precio_unitario"), rs.getInt("codigo_categoria"),
                rs.getInt("codigo_proveedor"));
    }
}