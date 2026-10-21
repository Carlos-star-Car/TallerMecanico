package com.mycompany.mavenproject1.Servicios;

import com.mycompany.mavenproject1.Conexion.ConexionBD;
import com.mycompany.mavenproject1.Entidades.Cliente;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servicio de clientes con almacenamiento en MySQL (tabla cliente).
 * Mantiene los mismos metodos que la version en memoria.
 */
public class ClienteServicio {

    private static final Logger LOG = Logger.getLogger(ClienteServicio.class.getName());

    // Instancia unica compartida por todos los formularios
    private static final ClienteServicio INSTANCIA = new ClienteServicio();

    public static ClienteServicio getInstancia() {
        return INSTANCIA;
    }

    private static final String COLUMNAS = "codigo, nombre, apellido, nit, telefono, direccion, correo";

    // Guarda un cliente nuevo. Devuelve false si el codigo ya existe o hay un error.
    public boolean guardar(Cliente cliente) {
        if (buscarPorCodigo(cliente.getCodigo()) != null) {
            return false;
        }
        String sql = "INSERT INTO cliente (" + COLUMNAS + ") VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection cn = ConexionBD.obtener();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, cliente.getCodigo());
            ps.setString(2, cliente.getNombre());
            ps.setString(3, cliente.getApellido());
            ps.setString(4, cliente.getNit());
            ps.setString(5, cliente.getTelefono());
            ps.setString(6, cliente.getDireccion());
            ps.setString(7, cliente.getCorreo());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "No se pudo guardar el cliente", ex);
            return false;
        }
    }

    // Actualiza los datos de un cliente existente (el codigo no cambia).
    public boolean modificar(Cliente cliente) {
        String sql = "UPDATE cliente SET nombre = ?, apellido = ?, nit = ?, telefono = ?, "
                + "direccion = ?, correo = ? WHERE codigo = ?";
        try (Connection cn = ConexionBD.obtener();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getApellido());
            ps.setString(3, cliente.getNit());
            ps.setString(4, cliente.getTelefono());
            ps.setString(5, cliente.getDireccion());
            ps.setString(6, cliente.getCorreo());
            ps.setString(7, cliente.getCodigo());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "No se pudo modificar el cliente", ex);
            return false;
        }
    }

    // Elimina un cliente por codigo. Devuelve false si no existe o si tiene
    // vehiculos, ordenes o facturas asociadas (la base de datos lo impide).
    public boolean eliminar(String codigo) {
        String sql = "DELETE FROM cliente WHERE codigo = ?";
        try (Connection cn = ConexionBD.obtener();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, codigo);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "No se pudo eliminar el cliente", ex);
            return false;
        }
    }

    // Busca por codigo exacto (ignora mayusculas). Devuelve null si no existe.
    public Cliente buscarPorCodigo(String codigo) {
        String sql = "SELECT " + COLUMNAS + " FROM cliente WHERE codigo = ?";
        try (Connection cn = ConexionBD.obtener();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "No se pudo buscar el cliente", ex);
            return null;
        }
    }

    // Busca clientes cuyo codigo sea exacto o cuyo nombre contenga el texto.
    public List<Cliente> buscar(String codigo, String nombre) {
        List<Cliente> resultado = new ArrayList<>();
        String sql = "SELECT " + COLUMNAS + " FROM cliente "
                + "WHERE (? <> '' AND codigo = ?) OR (? <> '' AND nombre LIKE ?) "
                + "ORDER BY codigo";
        try (Connection cn = ConexionBD.obtener();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, codigo);
            ps.setString(2, codigo);
            ps.setString(3, nombre);
            ps.setString(4, "%" + nombre + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    resultado.add(mapear(rs));
                }
            }
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "No se pudo buscar clientes", ex);
        }
        return resultado;
    }

    // Devuelve todos los clientes.
    public List<Cliente> listar() {
        List<Cliente> resultado = new ArrayList<>();
        String sql = "SELECT " + COLUMNAS + " FROM cliente ORDER BY codigo";
        try (Connection cn = ConexionBD.obtener();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                resultado.add(mapear(rs));
            }
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "No se pudo listar los clientes", ex);
        }
        return resultado;
    }

    private Cliente mapear(ResultSet rs) throws SQLException {
        return new Cliente(
                rs.getString("codigo"),
                rs.getString("nombre"),
                rs.getString("apellido"),
                rs.getString("nit"),
                rs.getString("telefono"),
                rs.getString("direccion"),
                rs.getString("correo"));
    }
}