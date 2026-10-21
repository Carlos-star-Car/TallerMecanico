package com.mycompany.mavenproject1.Servicios;

import com.mycompany.mavenproject1.Conexion.ConexionBD;
import com.mycompany.mavenproject1.Entidades.Proveedores;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servicio de proveedores con almacenamiento en MySQL (tabla proveedor).
 * Mantiene los mismos metodos que la version en memoria.
 */
public class ProveedoresServicio {

    private static final Logger LOG = Logger.getLogger(ProveedoresServicio.class.getName());

    private static final String COLUMNAS =
            "codigo, nombre_empresa, nombre_contacto, telefono, correo, direccion";

    /** Agrega un proveedor. Devuelve false si el codigo ya existe o hay un error. */
    public boolean guardar(Proveedores p) {
        if (existeCodigo(p.getCodigoProveedor())) {
            return false;
        }
        String sql = "INSERT INTO proveedor (" + COLUMNAS + ") VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection cn = ConexionBD.obtener();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, p.getCodigoProveedor());
            ps.setString(2, p.getNombreEmpresa());
            ps.setString(3, p.getNombreContacto());
            ps.setString(4, p.getTelefono());
            ps.setString(5, p.getCorreo());
            ps.setString(6, p.getDireccion());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "No se pudo guardar el proveedor", ex);
            return false;
        }
    }

    /** Reemplaza los datos del proveedor con ese codigo. Devuelve false si no existe. */
    public boolean modificar(Proveedores p) {
        String sql = "UPDATE proveedor SET nombre_empresa = ?, nombre_contacto = ?, telefono = ?, "
                + "correo = ?, direccion = ? WHERE codigo = ?";
        try (Connection cn = ConexionBD.obtener();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, p.getNombreEmpresa());
            ps.setString(2, p.getNombreContacto());
            ps.setString(3, p.getTelefono());
            ps.setString(4, p.getCorreo());
            ps.setString(5, p.getDireccion());
            ps.setInt(6, p.getCodigoProveedor());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "No se pudo modificar el proveedor", ex);
            return false;
        }
    }

    /** Elimina por codigo. Devuelve false si no existe o si lo usa algun repuesto. */
    public boolean eliminar(int CodigoProveedor) {
        String sql = "DELETE FROM proveedor WHERE codigo = ?";
        try (Connection cn = ConexionBD.obtener();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, CodigoProveedor);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "No se pudo eliminar el proveedor", ex);
            return false;
        }
    }

    /** Devuelve la lista completa. */
    public List<Proveedores> listar() {
        List<Proveedores> resultado = new ArrayList<>();
        String sql = "SELECT " + COLUMNAS + " FROM proveedor ORDER BY codigo";
        try (Connection cn = ConexionBD.obtener();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                resultado.add(mapear(rs));
            }
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "No se pudo listar los proveedores", ex);
        }
        return resultado;
    }

    /** Devuelve el proveedor con ese codigo, o null si no existe. */
    public Proveedores obtener(int CodigoProveedor) {
        String sql = "SELECT " + COLUMNAS + " FROM proveedor WHERE codigo = ?";
        try (Connection cn = ConexionBD.obtener();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, CodigoProveedor);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "No se pudo buscar el proveedor", ex);
            return null;
        }
    }

    /**
     * Busca por codigo exacto (si el texto es numerico) o por nombre de empresa
     * que contenga el texto. Lista vacia si no hay resultados.
     */
    public List<Proveedores> buscar(String criterio) {
        List<Proveedores> resultado = new ArrayList<>();
        String texto = criterio.trim();

        int esCodigo = 0;
        int codigo = -1;
        try {
            codigo = Integer.parseInt(texto);
            esCodigo = 1;
        } catch (NumberFormatException ex) {
            // no es un codigo; se busca solo por nombre
        }

        String sql = "SELECT " + COLUMNAS + " FROM proveedor "
                + "WHERE (? = 1 AND codigo = ?) OR nombre_empresa LIKE ? ORDER BY codigo";
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
            LOG.log(Level.SEVERE, "No se pudo buscar proveedores", ex);
        }
        return resultado;
    }

    public boolean existeCodigo(int codigoProveedor) {
        return obtener(codigoProveedor) != null;
    }

    private Proveedores mapear(ResultSet rs) throws SQLException {
        return new Proveedores(rs.getInt("codigo"), rs.getString("nombre_empresa"),
                rs.getString("nombre_contacto"), rs.getString("telefono"),
                rs.getString("correo"), rs.getString("direccion"));
    }
}
