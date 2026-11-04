package com.mycompany.mavenproject1.Formularios;

import com.mycompany.mavenproject1.Entidades.Usuario;
import com.mycompany.mavenproject1.Servicios.Sesion;
import com.mycompany.mavenproject1.Servicios.UsuarioServicio;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Arrays;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.Timer;

/**
 * Pantalla de inicio de sesion (860 x 520). Se construye por codigo, no con el diseñador.
 */
public class FrmLogin extends JFrame {

    private static final Color OSCURO = new Color(0x1B1E23);
    private static final Color ROJO = new Color(0xC8102E);
    private static final Color FONDO = new Color(0xF3F4F6);
    private static final Color TEXTO = new Color(0x1F2328);
    private static final Color GRIS = new Color(0x4B5563);
    private static final int MAX_INTENTOS = 5;
    private static final int SEGUNDOS_BLOQUEO = 15;

    private final UsuarioServicio servicio = UsuarioServicio.getInstancia();
    private final JTextField txtUsuario = new JTextField();
    private final JPasswordField txtClave = new JPasswordField();
    private final JButton btnIngresar = new JButton("Ingresar");
    private final JLabel lblMensaje = new JLabel(" ");
    private int intentos = 0;
    private Timer bloqueo;

    public FrmLogin() {
        setTitle("Talleres Emanuel - Inicio de sesión");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(860, 520);
        setResizable(false);
        construir();
        getRootPane().setDefaultButton(btnIngresar);   // Enter = Ingresar
        setLocationRelativeTo(null);
        servicio.crearAdministradorInicial();           // solo actua si no hay usuarios
    }

    private void construir() {
        JPanel raiz = new JPanel(new BorderLayout());
        raiz.add(panelIzquierdo(), BorderLayout.WEST);
        raiz.add(panelDerecho(), BorderLayout.CENTER);
        setContentPane(raiz);
    }

    private JPanel panelIzquierdo() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(OSCURO);
        p.setPreferredSize(new Dimension(360, 520));
        p.setBorder(BorderFactory.createEmptyBorder(40, 36, 32, 36));

        JPanel arriba = new JPanel();
        arriba.setOpaque(false);
        arriba.setLayout(new BoxLayout(arriba, BoxLayout.Y_AXIS));

        JLabel logo = new JLabel();
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);
        java.net.URL url = getClass().getResource("/logotaller.png");
        if (url != null) {
            java.awt.Image img = new ImageIcon(url).getImage()
                    .getScaledInstance(288, 111, java.awt.Image.SCALE_SMOOTH);
            logo.setIcon(new ImageIcon(img));
        } else {
            logo.setText("Talleres Emanuel");
            logo.setForeground(Color.WHITE);
        }
        arriba.add(logo);
        arriba.add(Box.createVerticalStrut(20));

        JPanel barra = new JPanel();
        barra.setBackground(ROJO);
        barra.setMaximumSize(new Dimension(56, 4));
        barra.setPreferredSize(new Dimension(56, 4));
        barra.setAlignmentX(Component.LEFT_ALIGNMENT);
        arriba.add(barra);
        arriba.add(Box.createVerticalStrut(20));

        JLabel titulo = new JLabel("Talleres Emanuel");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 30));
        titulo.setForeground(Color.WHITE);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        arriba.add(titulo);
        arriba.add(Box.createVerticalStrut(12));

        JLabel descripcion = new JLabel("<html><div style='width:280px'>Sistema de gestión del taller "
                + "mecánico: clientes, órdenes, repuestos y facturación.</div></html>");
        descripcion.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        descripcion.setForeground(new Color(0xD1D5DB));
        descripcion.setAlignmentX(Component.LEFT_ALIGNMENT);
        arriba.add(descripcion);

        JLabel version = new JLabel("Versión 1.0 · Acceso solo para personal autorizado");
        version.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        version.setForeground(new Color(0x9CA3AF));

        p.add(arriba, BorderLayout.NORTH);
        p.add(version, BorderLayout.SOUTH);
        return p;
    }

    private JPanel panelDerecho() {
        JPanel p = new JPanel(new java.awt.GridBagLayout());
        p.setBackground(FONDO);

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setPreferredSize(new Dimension(340, 360));

        JLabel titulo = new JLabel("Iniciar sesión");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titulo.setForeground(TEXTO);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(titulo);
        form.add(Box.createVerticalStrut(4));

        JLabel sub = new JLabel("Ingrese sus credenciales para continuar.");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        sub.setForeground(GRIS);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(sub);
        form.add(Box.createVerticalStrut(22));

        form.add(etiqueta("Usuario"));
        form.add(Box.createVerticalStrut(6));
        estilizarCampo(txtUsuario);
        form.add(txtUsuario);
        form.add(Box.createVerticalStrut(16));

        form.add(etiqueta("Contraseña"));
        form.add(Box.createVerticalStrut(6));
        estilizarCampo(txtClave);
        form.add(txtClave);
        form.add(Box.createVerticalStrut(8));

        lblMensaje.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblMensaje.setForeground(ROJO);
        lblMensaje.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(lblMensaje);
        form.add(Box.createVerticalStrut(8));

        btnIngresar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnIngresar.setBackground(ROJO);
        btnIngresar.setForeground(Color.WHITE);
        btnIngresar.setFocusPainted(false);
        btnIngresar.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnIngresar.setPreferredSize(new Dimension(340, 48));
        btnIngresar.setMaximumSize(new Dimension(340, 48));
        btnIngresar.addActionListener(e -> ingresar());
        form.add(btnIngresar);
        form.add(Box.createVerticalStrut(14));

        JLabel ayuda = new JLabel("¿Olvidó su contraseña? Solicite el cambio a un administrador.");
        ayuda.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        ayuda.setForeground(GRIS);
        ayuda.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(ayuda);

        p.add(form);
        return p;
    }

    private JLabel etiqueta(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("Segoe UI", Font.BOLD, 14));
        l.setForeground(TEXTO);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private void estilizarCampo(JTextField campo) {
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        campo.setPreferredSize(new Dimension(340, 44));
        campo.setMaximumSize(new Dimension(340, 44));
        campo.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private void ingresar() {
        String usuario = txtUsuario.getText().trim();
        char[] clave = txtClave.getPassword();
        try {
            if (usuario.isEmpty() || clave.length == 0) {
                mensaje("Ingrese usuario y contraseña.");
                return;
            }
            Usuario u;
            try {
                u = servicio.autenticar(usuario, clave);
            } catch (IllegalStateException ex) {
                mensaje("No hay conexión con la base de datos. Encienda MySQL en XAMPP.");
                return;
            }
            if (u == null) {
                intentos++;
                txtClave.setText("");
                if (intentos >= MAX_INTENTOS) {
                    bloquearTemporalmente();
                } else {
                    mensaje("Usuario o contraseña incorrectos (" + intentos + " de " + MAX_INTENTOS + ").");
                }
                return;
            }
            Sesion.iniciar(u);
            FrmMenuPrincipal menu = new FrmMenuPrincipal();
            menu.setLocationRelativeTo(null);
            menu.setVisible(true);
            dispose();
        } finally {
            Arrays.fill(clave, '\0');
        }
    }

    // Tras varios intentos fallidos, espera unos segundos antes de permitir otro.
    private void bloquearTemporalmente() {
        btnIngresar.setEnabled(false);
        txtUsuario.setEnabled(false);
        txtClave.setEnabled(false);
        final int[] restante = {SEGUNDOS_BLOQUEO};
        mensaje("Demasiados intentos. Espere " + restante[0] + " segundos.");
        bloqueo = new Timer(1000, e -> {
            restante[0]--;
            if (restante[0] <= 0) {
                bloqueo.stop();
                intentos = 0;
                btnIngresar.setEnabled(true);
                txtUsuario.setEnabled(true);
                txtClave.setEnabled(true);
                mensaje(" ");
                txtClave.requestFocusInWindow();
            } else {
                mensaje("Demasiados intentos. Espere " + restante[0] + " segundos.");
            }
        });
        bloqueo.start();
    }

    private void mensaje(String texto) {
        lblMensaje.setText(texto);
    }

    public static void main(String[] args) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FrmLogin.class.getName())
                    .log(java.util.logging.Level.SEVERE, null, ex);
        }
        java.awt.EventQueue.invokeLater(() -> new FrmLogin().setVisible(true));
    }
}