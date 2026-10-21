package com.mycompany.mavenproject1.Servicios;

import com.mycompany.mavenproject1.Conexion.ConexionBD;
import com.mycompany.mavenproject1.Entidades.CategoriaRepuesto;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servicio de categorias de repuesto con almacenamiento en MySQL
 * (tabla categoria_repuesto). Mantiene los mismos metodos que la version en memoria.
 */
public class CategoriaRepuestoServicio {

    private static final Logger LOG = Logger.getLogger(CategoriaRepuestoServicio.class.getName());

    private static final CategoriaRepuestoServicio INSTANCIA = new CategoriaRepuestoServicio();

    public static CategoriaRepuestoServicio getInstancia() {
        return INSTANCIA;
    }

    // Guarda una categoria nueva. Devuelve false si el codigo ya existe o hay un error.
    public boolean guardar(CategoriaRepuesto c) {
        if (buscarPorCodigo(c.getCodigo()) != null) {
            return false;
        }
        String sql = "INSERT INTO categoria_repuesto (codigo, nombre, descripcion) VALUES (?, ?, ?)";
        try (Connection cn = ConexionBD.obtener();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, c.getCodigo());
            ps.setString(2, c.getNombre());
            ps.setString(3, c.getDescripcion());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "No se pudo guardar la categoria", ex);
            return false;
        }
    }

    // Actualiza nombre y descripcion (el codigo no cambia).
    public boolean modificar(CategoriaRepuesto c) {
        String sql = "UPDATE categoria_repuesto SET nombre = ?, descripcion = ? WHERE codigo = ?";
        try (Connection cn = ConexionBD.obtener();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, c.getNombre());
            ps.setString(2, c.getDescripcion());
            ps.setInt(3, c.getCodigo());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "No se pudo modificar la categoria", ex);
            return false;
        }
    }

    // Elimina por codigo. Devuelve false si no existe o si la usa algun repuesto.
    public boolean eliminar(int codigo) {
        String sql = "DELETE FROM categoria_repuesto WHERE codigo = ?";
        try (Connection cn = ConexionBD.obtener();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, codigo);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "No se pudo eliminar la categoria", ex);
            return false;
        }
    }

    public CategoriaRepuesto buscarPorCodigo(int codigo) {
        String sql = "SELECT codigo, nombre, descripcion FROM categoria_repuesto WHERE codigo = ?";
        try (Connection cn = ConexionBD.obtener();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "No se pudo buscar la categoria", ex);
            return null;
        }
    }

    // Codigo exacto (si codigo >= 0) o parte del nombre.
    public List<CategoriaRepuesto> buscar(int codigo, String nombre) {
        List<CategoriaRepuesto> resultado = new ArrayList<>();
        String sql = "SELECT codigo, nombre, descripcion FROM categoria_repuesto "
                + "WHERE (? >= 0 AND codigo = ?) OR (? <> '' AND nombre LIKE ?) ORDER BY codigo";
        try (Connection cn = ConexionBD.obtener();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, codigo);
            ps.setInt(2, codigo);
            ps.setString(3, nombre);
            ps.setString(4, "%" + nombre + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    resultado.add(mapear(rs));
                }
            }
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "No se pudo buscar categorias", ex);
        }
        return resultado;
    }

    public List<CategoriaRepuesto> listar() {
        List<CategoriaRepuesto> resultado = new ArrayList<>();
        String sql = "SELECT codigo, nombre, descripcion FROM categoria_repuesto ORDER BY codigo";
        try (Connection cn = ConexionBD.obtener();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                resultado.add(mapear(rs));
            }
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "No se pudo listar las categorias", ex);
        }
        return resultado;
    }

    private CategoriaRepuesto mapear(ResultSet rs) throws SQLException {
        return new CategoriaRepuesto(rs.getInt("codigo"), rs.getString("nombre"),
                rs.getString("descripcion"));
    }
}
