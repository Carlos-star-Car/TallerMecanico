package com.mycompany.mavenproject1.Servicios;

import com.mycompany.mavenproject1.Conexion.ConexionBD;
import com.mycompany.mavenproject1.Entidades.Vehiculo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servicio de vehiculos con almacenamiento en MySQL (tabla vehiculo).
 * Mantiene los mismos metodos que la version en memoria.
 */
public class VehiculoServicio {

    private static final Logger LOG = Logger.getLogger(VehiculoServicio.class.getName());

    private static final String COLUMNAS = "placa, marca, modelo, color, tipo, codigo_cliente";

    /** Devuelve false si la placa ya existe, el cliente no existe o hay un error. */
    public boolean guardar(Vehiculo v) {
        if (buscarPorPlaca(v.getPlaca()) != null) {
            return false;
        }
        String sql = "INSERT INTO vehiculo (" + COLUMNAS + ") VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection cn = ConexionBD.obtener();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, v.getPlaca());
            ps.setString(2, v.getMarca());
            ps.setString(3, v.getModelo());
            ps.setString(4, v.getColor());
            ps.setString(5, v.getTipo());
            ps.setString(6, v.getCodigoCliente());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "No se pudo guardar el vehiculo", ex);
            return false;
        }
    }

    public boolean modificar(Vehiculo v) {
        String sql = "UPDATE vehiculo SET marca = ?, modelo = ?, color = ?, tipo = ?, "
                + "codigo_cliente = ? WHERE placa = ?";
        try (Connection cn = ConexionBD.obtener();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, v.getMarca());
            ps.setString(2, v.getModelo());
            ps.setString(3, v.getColor());
            ps.setString(4, v.getTipo());
            ps.setString(5, v.getCodigoCliente());
            ps.setString(6, v.getPlaca());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "No se pudo modificar el vehiculo", ex);
            return false;
        }
    }

    /** Devuelve false si no existe o si tiene ordenes de trabajo asociadas. */
    public boolean eliminar(String placa) {
        String sql = "DELETE FROM vehiculo WHERE placa = ?";
        try (Connection cn = ConexionBD.obtener();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, placa);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "No se pudo eliminar el vehiculo", ex);
            return false;
        }
    }

    public Vehiculo buscarPorPlaca(String placa) {
        String sql = "SELECT " + COLUMNAS + " FROM vehiculo WHERE placa = ?";
        try (Connection cn = ConexionBD.obtener();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, placa);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "No se pudo buscar el vehiculo", ex);
            return null;
        }
    }

    // Placa exacta o parte del modelo.
    public List<Vehiculo> buscar(String placa, String modelo) {
        List<Vehiculo> resultado = new ArrayList<>();
        String sql = "SELECT " + COLUMNAS + " FROM vehiculo "
                + "WHERE (? <> '' AND placa = ?) OR (? <> '' AND modelo LIKE ?) ORDER BY placa";
        try (Connection cn = ConexionBD.obtener();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, placa);
            ps.setString(2, placa);
            ps.setString(3, modelo);
            ps.setString(4, "%" + modelo + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    resultado.add(mapear(rs));
                }
            }
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "No se pudo buscar vehiculos", ex);
        }
        return resultado;
    }

    public List<Vehiculo> listar() {
        List<Vehiculo> resultado = new ArrayList<>();
        String sql = "SELECT " + COLUMNAS + " FROM vehiculo ORDER BY placa";
        try (Connection cn = ConexionBD.obtener();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                resultado.add(mapear(rs));
            }
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "No se pudo listar los vehiculos", ex);
        }
        return resultado;
    }

    private Vehiculo mapear(ResultSet rs) throws SQLException {
        return new Vehiculo(rs.getString("placa"), rs.getString("marca"),
                rs.getString("modelo"), rs.getString("color"), rs.getString("tipo"),
                rs.getString("codigo_cliente"));
    }
}