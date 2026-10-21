package com.mycompany.mavenproject1.Conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Conexion a la base de datos DBTallerMecanico (XAMPP, MySQL en el puerto 3306).
 * Cada servicio pide una conexion con ConexionBD.obtener() y la cierra al terminar
 * (try-with-resources).
 */
public class ConexionBD {

    private static final String URL =
            "jdbc:mysql://localhost:3306/DBTallerMecanico?useSSL=false&allowPublicKeyRetrieval=true&characterEncoding=UTF-8";
    private static final String USUARIO = "root";
    private static final String CLAVE = "";   // XAMPP: root sin contrasena por defecto

    private ConexionBD() {
    }

    public static Connection obtener() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, CLAVE);
    }

    // Prueba rapida: ejecutar este archivo (Run File / Shift+F6) con MySQL encendido.
    public static void main(String[] args) {
        try (Connection cn = obtener();
             Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery("SHOW TABLES")) {
            System.out.println("Conexion exitosa a DBTallerMecanico. Tablas:");
            int total = 0;
            while (rs.next()) {
                System.out.println(" - " + rs.getString(1));
                total++;
            }
            System.out.println("Total: " + total + " tablas (se esperan 9).");
        } catch (SQLException ex) {
            System.out.println("No se pudo conectar: " + ex.getMessage());
        }
    }
}