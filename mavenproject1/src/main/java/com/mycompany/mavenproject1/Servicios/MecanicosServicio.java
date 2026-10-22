package com.mycompany.mavenproject1.Servicios;

import com.mycompany.mavenproject1.Conexion.ConexionBD;
import com.mycompany.mavenproject1.Entidades.Mecanicos;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servicio de mecanicos con almacenamiento en MySQL (tabla mecanico).
 * Mantiene los mismos metodos que la version en memoria.
 */
public class MecanicosServicio {

    private static final Logger LOG = Logger.getLogger(MecanicosServicio.class.getName());

    private static final String COLUMNAS = "codigo, nombre, apellido, telefono, especialidad, tarifa";

    public boolean guardar(Mecanicos mecanico) {
        if (buscarPorCodigo(mecanico.getCodigo()) != null) {
            return false;
        }
        String sql = "INSERT INTO mecanico (" + COLUMNAS + ") VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection cn = ConexionBD.obtener();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, mecanico.getCodigo());
            ps.setString(2, mecanico.getNombre());
            ps.setString(3, mecanico.getApellido());
            ps.setString(4, mecanico.getTelefono());
            ps.setString(5, mecanico.getEspecialidad());
            ps.setDouble(6, mecanico.getTarifa());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "No se pudo guardar el mecanico", ex);
            return false;
        }
    }

    public boolean modificar(Mecanicos mecanico) {
        String sql = "UPDATE mecanico SET nombre = ?, apellido = ?, telefono = ?, "
                + "especialidad = ?, tarifa = ? WHERE codigo = ?";
        try (Connection cn = ConexionBD.obtener();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, mecanico.getNombre());
            ps.setString(2, mecanico.getApellido());
            ps.setString(3, mecanico.getTelefono());
            ps.setString(4, mecanico.getEspecialidad());
            ps.setDouble(5, mecanico.getTarifa());
            ps.setInt(6, mecanico.getCodigo());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "No se pudo modificar el mecanico", ex);
            return false;
        }
    }

    /** Devuelve false si no existe o si tiene ordenes de trabajo asociadas. */
    public boolean eliminar(int codigo) {
        String sql = "DELETE FROM mecanico WHERE codigo = ?";
        try (Connection cn = ConexionBD.obtener();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, codigo);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "No se pudo eliminar el mecanico", ex);
            return false;
        }
    }

    public Mecanicos buscarPorCodigo(int codigo) {
        String sql = "SELECT " + COLUMNAS + " FROM mecanico WHERE codigo = ?";
        try (Connection cn = ConexionBD.obtener();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "No se pudo buscar el mecanico", ex);
            return null;
        }
    }

    // Atajo usado por el formulario: devuelve un mecanico o null.
    public Mecanicos buscar(int codigo) {
        return buscarPorCodigo(codigo);
    }

    // Codigo exacto (si codigo >= 0) o parte del nombre.
    public List<Mecanicos> buscar(int codigo, String nombre) {
        List<Mecanicos> resultado = new ArrayList<>();
        String sql = "SELECT " + COLUMNAS + " FROM mecanico "
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
            LOG.log(Level.SEVERE, "No se pudo buscar mecanicos", ex);
        }
        return resultado;
    }

    public List<Mecanicos> listar() {
        List<Mecanicos> resultado = new ArrayList<>();
        String sql = "SELECT " + COLUMNAS + " FROM mecanico ORDER BY codigo";
        try (Connection cn = ConexionBD.obtener();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                resultado.add(mapear(rs));
            }
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "No se pudo listar los mecanicos", ex);
        }
        return resultado;
    }

    private Mecanicos mapear(ResultSet rs) throws SQLException {
        return new Mecanicos(rs.getInt("codigo"), rs.getString("nombre"),
                rs.getString("apellido"), rs.getString("telefono"),
                rs.getString("especialidad"), rs.getFloat("tarifa"));
    }
}