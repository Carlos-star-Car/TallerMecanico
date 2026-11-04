package com.mycompany.mavenproject1.Servicios;

import com.mycompany.mavenproject1.Conexion.ConexionBD;
import com.mycompany.mavenproject1.Entidades.Usuario;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Servicio de usuarios con MySQL (tabla usuario). Las contrasenas nunca se
 * guardan como texto: se guarda un hash PBKDF2 con sal propia de cada usuario.
 * Los metodos que modifican devuelven el mensaje de error, o null si todo salio bien.
 */
public class UsuarioServicio {

    private static final Logger LOG = Logger.getLogger(UsuarioServicio.class.getName());

    private static final UsuarioServicio INSTANCIA = new UsuarioServicio();

    public static UsuarioServicio getInstancia() {
        return INSTANCIA;
    }

    private static final String COLUMNAS = "id_usuario, nombre_usuario, nombre_completo, rol, activo";
    private static final int ITERACIONES = 120_000;
    private static final int BITS_CLAVE = 256;
    private static final int BYTES_SAL = 16;
    public static final int LARGO_MINIMO_CLAVE = 6;

    // ---------- Contrasenas ----------

    static String hashear(char[] clave) {
        try {
            byte[] sal = new byte[BYTES_SAL];
            new SecureRandom().nextBytes(sal);
            byte[] hash = derivar(clave, sal, ITERACIONES);
            return "pbkdf2$" + ITERACIONES + "$" + Base64.getEncoder().encodeToString(sal)
                    + "$" + Base64.getEncoder().encodeToString(hash);
        } catch (Exception ex) {
            throw new IllegalStateException("No se pudo cifrar la contrasena", ex);
        }
    }

    static boolean verificar(char[] clave, String almacenado) {
        try {
            String[] partes = almacenado.split("\\$");
            if (partes.length != 4 || !"pbkdf2".equals(partes[0])) {
                return false;
            }
            int iteraciones = Integer.parseInt(partes[1]);
            byte[] sal = Base64.getDecoder().decode(partes[2]);
            byte[] esperado = Base64.getDecoder().decode(partes[3]);
            byte[] calculado = derivar(clave, sal, iteraciones);
            return MessageDigest.isEqual(esperado, calculado);
        } catch (Exception ex) {
            return false;
        }
    }

    private static byte[] derivar(char[] clave, byte[] sal, int iteraciones) throws Exception {
        PBEKeySpec spec = new PBEKeySpec(clave, sal, iteraciones, BITS_CLAVE);
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        } finally {
            spec.clearPassword();
        }
    }

    // ---------- Inicio de sesion ----------

    /**
     * Crea el usuario "admin" (clave admin123) solo si la tabla esta vacia.
     * Cambie esa clave en cuanto entre por primera vez.
     */
    public void crearAdministradorInicial() {
        try (Connection cn = ConexionBD.obtener();
             PreparedStatement ps = cn.prepareStatement("SELECT COUNT(*) FROM usuario");
             ResultSet rs = ps.executeQuery()) {
            if (rs.next() && rs.getInt(1) == 0) {
                insertar(cn, "admin", "Administrador", Usuario.ROL_ADMIN, "admin123".toCharArray());
            }
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "No se pudo crear el administrador inicial", ex);
        }
    }

    /**
     * Devuelve el usuario si el nombre y la clave son correctos y esta activo; si no, null.
     * Lanza IllegalStateException si no hay conexion con la base de datos.
     */
    public Usuario autenticar(String nombreUsuario, char[] clave) {
        String sql = "SELECT " + COLUMNAS + ", clave_hash FROM usuario "
                + "WHERE nombre_usuario = ? AND activo = 1";
        try (Connection cn = ConexionBD.obtener();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, nombreUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && verificar(clave, rs.getString("clave_hash"))) {
                    return mapear(rs);
                }
                return null;
            }
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "No se pudo consultar el usuario", ex);
            throw new IllegalStateException("Sin conexion con la base de datos", ex);
        }
    }

    // ---------- Altas y cambios ----------

    public String guardar(Usuario u, char[] clave) {
        String error = validarDatos(u);
        if (error != null) {
            return error;
        }
        if (clave == null || clave.length < LARGO_MINIMO_CLAVE) {
            return "La contraseña debe tener al menos " + LARGO_MINIMO_CLAVE + " caracteres.";
        }
        if (buscarPorNombre(u.getNombreUsuario()) != null) {
            return "Ya existe un usuario con ese nombre.";
        }
        try (Connection cn = ConexionBD.obtener()) {
            insertar(cn, u.getNombreUsuario(), u.getNombreCompleto(), u.getRol(), clave);
            return null;
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "No se pudo guardar el usuario", ex);
            return "No se pudo guardar el usuario.";
        }
    }

    /** Si claveNueva es null o vacia, conserva la contraseña actual. */
    public String modificar(Usuario u, char[] claveNueva) {
        String error = validarDatos(u);
        if (error != null) {
            return error;
        }
        boolean cambiaClave = claveNueva != null && claveNueva.length > 0;
        if (cambiaClave && claveNueva.length < LARGO_MINIMO_CLAVE) {
            return "La contraseña debe tener al menos " + LARGO_MINIMO_CLAVE + " caracteres.";
        }
        Usuario actual = buscarPorId(u.getIdUsuario());
        if (actual == null) {
            return "El usuario ya no existe.";
        }
        if (actual.esAdmin() && actual.isActivo() && !u.esAdmin() && !quedaOtroAdminActivo(actual.getIdUsuario())) {
            return "Debe quedar al menos un administrador activo.";
        }
        String sql = cambiaClave
                ? "UPDATE usuario SET nombre_completo = ?, rol = ?, clave_hash = ? WHERE id_usuario = ?"
                : "UPDATE usuario SET nombre_completo = ?, rol = ? WHERE id_usuario = ?";
        try (Connection cn = ConexionBD.obtener();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, u.getNombreCompleto());
            ps.setString(2, u.getRol());
            if (cambiaClave) {
                ps.setString(3, hashear(claveNueva));
                ps.setInt(4, u.getIdUsuario());
            } else {
                ps.setInt(3, u.getIdUsuario());
            }
            return ps.executeUpdate() > 0 ? null : "No se pudo modificar el usuario.";
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "No se pudo modificar el usuario", ex);
            return "No se pudo modificar el usuario.";
        }
    }

    /** Activa o desactiva un usuario (no se borran: su historial debe conservarse). */
    public String cambiarEstado(int idUsuario, boolean activo) {
        Usuario u = buscarPorId(idUsuario);
        if (u == null) {
            return "El usuario ya no existe.";
        }
        if (!activo && u.esAdmin() && u.isActivo() && !quedaOtroAdminActivo(idUsuario)) {
            return "Debe quedar al menos un administrador activo.";
        }
        String sql = "UPDATE usuario SET activo = ? WHERE id_usuario = ?";
        try (Connection cn = ConexionBD.obtener();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setBoolean(1, activo);
            ps.setInt(2, idUsuario);
            return ps.executeUpdate() > 0 ? null : "No se pudo cambiar el estado.";
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "No se pudo cambiar el estado del usuario", ex);
            return "No se pudo cambiar el estado.";
        }
    }

    // ---------- Consultas ----------

    public Usuario buscarPorId(int idUsuario) {
        return uno("SELECT " + COLUMNAS + " FROM usuario WHERE id_usuario = ?", String.valueOf(idUsuario), true);
    }

    public Usuario buscarPorNombre(String nombreUsuario) {
        return uno("SELECT " + COLUMNAS + " FROM usuario WHERE nombre_usuario = ?", nombreUsuario, false);
    }

    public List<Usuario> listar() {
        return varios("SELECT " + COLUMNAS + " FROM usuario ORDER BY nombre_usuario", null);
    }

    /** Por parte del nombre de usuario o del nombre completo. */
    public List<Usuario> buscar(String texto) {
        return varios("SELECT " + COLUMNAS + " FROM usuario "
                + "WHERE nombre_usuario LIKE ? OR nombre_completo LIKE ? ORDER BY nombre_usuario",
                "%" + texto + "%");
    }

    // ---------- Apoyo ----------

    private String validarDatos(Usuario u) {
        String nombre = u.getNombreUsuario() == null ? "" : u.getNombreUsuario();
        if (!nombre.matches("[A-Za-z0-9._-]{3,30}")) {
            return "El usuario debe tener de 3 a 30 caracteres: letras, números, punto, guion o guion bajo.";
        }
        if (u.getNombreCompleto() == null || u.getNombreCompleto().trim().isEmpty()) {
            return "Ingrese el nombre completo.";
        }
        if (!Usuario.ROL_ADMIN.equals(u.getRol()) && !Usuario.ROL_RECEPCION.equals(u.getRol())
                && !Usuario.ROL_MECANICO.equals(u.getRol())) {
            return "Seleccione un rol válido.";
        }
        return null;
    }

    private boolean quedaOtroAdminActivo(int idExcluido) {
        String sql = "SELECT COUNT(*) FROM usuario WHERE rol = 'ADMIN' AND activo = 1 AND id_usuario <> ?";
        try (Connection cn = ConexionBD.obtener();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idExcluido);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "No se pudo contar administradores", ex);
            return false;
        }
    }

    private void insertar(Connection cn, String nombreUsuario, String nombreCompleto, String rol,
                          char[] clave) throws SQLException {
        String sql = "INSERT INTO usuario (nombre_usuario, nombre_completo, clave_hash, rol, activo) "
                + "VALUES (?, ?, ?, ?, 1)";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, nombreUsuario);
            ps.setString(2, nombreCompleto.trim());
            ps.setString(3, hashear(clave));
            ps.setString(4, rol);
            ps.executeUpdate();
        }
    }

    private Usuario uno(String sql, String parametro, boolean numerico) {
        try (Connection cn = ConexionBD.obtener();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            if (numerico) {
                ps.setInt(1, Integer.parseInt(parametro));
            } else {
                ps.setString(1, parametro);
            }
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "No se pudo buscar el usuario", ex);
            return null;
        }
    }

    private List<Usuario> varios(String sql, String patron) {
        List<Usuario> resultado = new ArrayList<>();
        try (Connection cn = ConexionBD.obtener();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            if (patron != null) {
                ps.setString(1, patron);
                ps.setString(2, patron);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    resultado.add(mapear(rs));
                }
            }
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "No se pudo listar los usuarios", ex);
        }
        return resultado;
    }

    private Usuario mapear(ResultSet rs) throws SQLException {
        return new Usuario(rs.getInt("id_usuario"), rs.getString("nombre_usuario"),
                rs.getString("nombre_completo"), rs.getString("rol"), rs.getBoolean("activo"));
    }
}
