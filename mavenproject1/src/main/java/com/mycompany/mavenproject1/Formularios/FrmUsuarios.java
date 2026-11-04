package com.mycompany.mavenproject1.Formularios;

import com.mycompany.mavenproject1.Entidades.Usuario;
import com.mycompany.mavenproject1.Servicios.Sesion;
import com.mycompany.mavenproject1.Servicios.UsuarioServicio;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * Gestion de usuarios (1000 x 640). Solo para ADMIN. Se construye por codigo.
 * Flujo igual al de los demas formularios: "Modificar" sube el usuario elegido
 * a los campos y "Guardar" aplica el cambio.
 */
public class FrmUsuarios extends JFrame {

    private static final Color OSCURO = new Color(0x1B1E23);
    private static final Color ROJO = new Color(0xC8102E);
    private static final Color FONDO = new Color(0xF3F4F6);
    private static final Color TEXTO = new Color(0x1F2328);
    private static final Color BORDE = new Color(0xE5E7EB);
    private static final Color SELECCION = new Color(0xFDECEF);

    private final UsuarioServicio servicio = UsuarioServicio.getInstancia();
    private final JTextField txtUsuario = new JTextField();
    private final JTextField txtNombre = new JTextField();
    private final JComboBox<String> cmbRol = new JComboBox<>(Usuario.ROLES);
    private final JPasswordField txtClave = new JPasswordField();
    private final JPasswordField txtClave2 = new JPasswordField();
    private final JCheckBox chkActivo = new JCheckBox("Usuario activo", true);
    private final JTextField txtBuscar = new JTextField();
    private final JTable tabla = new JTable();
    private final JButton btnGuardar = new JButton("Guardar");
    private final JButton btnModificar = new JButton("Modificar");
    private final JButton btnEstado = new JButton("Desactivar");
    private final JButton btnLimpiar = new JButton("Limpiar");
    private final JButton btnMenu = new JButton("Menú principal");

    private List<Usuario> mostrados = new ArrayList<>();
    private Usuario enEdicion = null;   // null = usuario nuevo

    /** Abre la ventana solo si quien inicio sesion es administrador. */
    public static void abrir() {
        if (!Sesion.esAdmin()) {
            JOptionPane.showMessageDialog(null, "Solo un administrador puede gestionar usuarios.");
            return;
        }
        FrmUsuarios f = new FrmUsuarios();
        f.setVisible(true);
    }

    public FrmUsuarios() {
        setTitle("Talleres Emanuel - Gestión de usuarios");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(1100, 640);
        setResizable(false);
        construir();
        configurarTabla();
        configurarAcciones();
        actualizarTabla(servicio.listar());
        limpiar();
        setLocationRelativeTo(null);
    }

    // ---------- Construccion de la pantalla ----------

    private void construir() {
        JPanel raiz = new JPanel(new BorderLayout());
        raiz.setBackground(FONDO);
        raiz.add(encabezado(), BorderLayout.NORTH);

        JPanel cuerpo = new JPanel(new BorderLayout(24, 0));
        cuerpo.setOpaque(false);
        cuerpo.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));
        cuerpo.add(panelFormulario(), BorderLayout.WEST);
        cuerpo.add(panelListado(), BorderLayout.CENTER);
        raiz.add(cuerpo, BorderLayout.CENTER);
        setContentPane(raiz);
    }

    private JPanel encabezado() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(OSCURO);
        p.setPreferredSize(new Dimension(1000, 72));
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 4, 0, ROJO),
                BorderFactory.createEmptyBorder(0, 32, 0, 32)));
        JLabel titulo = new JLabel("Gestión de usuarios");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(Color.WHITE);
        Usuario u = Sesion.getUsuario();
        JLabel sesion = new JLabel(u == null ? "" : "Sesión: " + u.getNombreUsuario() + " · Rol: " + u.getRol());
        sesion.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        sesion.setForeground(new Color(0xD1D5DB));
        p.add(titulo, BorderLayout.WEST);
        p.add(sesion, BorderLayout.EAST);
        return p;
    }

    private JPanel panelFormulario() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(Color.WHITE);
        p.setPreferredSize(new Dimension(320, 480));
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        JLabel titulo = new JLabel("Datos del usuario");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(titulo);
        p.add(Box.createVerticalStrut(12));

        agregarCampo(p, "Usuario", txtUsuario);
        agregarCampo(p, "Nombre completo", txtNombre);
        cmbRol.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        agregarCampo(p, "Rol", cmbRol);
        agregarCampo(p, "Contraseña", txtClave);
        agregarCampo(p, "Confirmar contraseña", txtClave2);

        chkActivo.setOpaque(false);
        chkActivo.setEnabled(false);   // el estado se cambia con el boton Activar/Desactivar
        chkActivo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        chkActivo.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(chkActivo);
        return p;
    }

    private void agregarCampo(JPanel panel, String etiqueta, javax.swing.JComponent campo) {
        JLabel l = new JLabel(etiqueta);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(l);
        panel.add(Box.createVerticalStrut(4));
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        campo.setPreferredSize(new Dimension(280, 38));
        campo.setMaximumSize(new Dimension(280, 38));
        campo.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(campo);
        panel.add(Box.createVerticalStrut(12));
    }

    private JPanel panelListado() {
        JPanel p = new JPanel(new BorderLayout(0, 16));
        p.setOpaque(false);

        JPanel busqueda = new JPanel(new BorderLayout(12, 0));
        busqueda.setOpaque(false);
        JLabel lb = new JLabel("Buscar");
        lb.setFont(new Font("Segoe UI", Font.BOLD, 14));
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtBuscar.setPreferredSize(new Dimension(200, 38));
        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnBuscar.setPreferredSize(new Dimension(100, 38));
        btnBuscar.addActionListener(e -> buscar());
        txtBuscar.addActionListener(e -> buscar());
        busqueda.add(lb, BorderLayout.WEST);
        busqueda.add(txtBuscar, BorderLayout.CENTER);
        busqueda.add(btnBuscar, BorderLayout.EAST);
        p.add(busqueda, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createLineBorder(BORDE));
        scroll.getViewport().setBackground(Color.WHITE);
        p.add(scroll, BorderLayout.CENTER);

        JPanel botones = new JPanel(new java.awt.GridLayout(1, 5, 8, 0));
        botones.setOpaque(false);
        botones.setPreferredSize(new Dimension(100, 44));
        estilizarBoton(btnGuardar, true);
        estilizarBoton(btnModificar, false);
        estilizarBoton(btnEstado, false);
        estilizarBoton(btnLimpiar, false);
        btnMenu.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnMenu.setBackground(OSCURO);
        btnMenu.setForeground(Color.WHITE);
        btnMenu.setFocusPainted(false);
        botones.add(btnGuardar);
        botones.add(btnModificar);
        botones.add(btnEstado);
        botones.add(btnLimpiar);
        botones.add(btnMenu);
        p.add(botones, BorderLayout.SOUTH);
        return p;
    }

    private void estilizarBoton(JButton b, boolean principal) {
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setFocusPainted(false);
        b.setPreferredSize(new Dimension(110, 44));
        if (principal) {
            b.setBackground(ROJO);
            b.setForeground(Color.WHITE);
        }
    }

    private void configurarTabla() {
        tabla.setRowHeight(32);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setSelectionBackground(SELECCION);
        tabla.setSelectionForeground(TEXTO);
        tabla.setGridColor(BORDE);
        tabla.setFillsViewportHeight(true);
        DefaultTableCellRenderer cabecera = new DefaultTableCellRenderer();
        cabecera.setBackground(OSCURO);
        cabecera.setForeground(Color.WHITE);
        cabecera.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cabecera.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        tabla.getTableHeader().setDefaultRenderer(cabecera);
        tabla.getTableHeader().setPreferredSize(new Dimension(100, 38));
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                actualizarTextoEstado();
            }
        });
    }

    // ---------- Acciones ----------

    private void configurarAcciones() {
        btnGuardar.addActionListener(e -> guardar());
        btnModificar.addActionListener(e -> cargarSeleccionado());
        btnEstado.addActionListener(e -> cambiarEstado());
        btnLimpiar.addActionListener(e -> limpiar());
        btnMenu.addActionListener(e -> {
            FrmMenuPrincipal menu = new FrmMenuPrincipal();
            menu.setLocationRelativeTo(null);
            menu.setVisible(true);
            dispose();
        });
    }

    private void guardar() {
        char[] clave = txtClave.getPassword();
        char[] clave2 = txtClave2.getPassword();
        try {
            if (!Arrays.equals(clave, clave2)) {
                aviso("La contraseña y su confirmación no coinciden.");
                return;
            }
            String rol = (String) cmbRol.getSelectedItem();
            if (enEdicion == null) {
                Usuario nuevo = new Usuario(0, txtUsuario.getText().trim(), txtNombre.getText().trim(), rol, true);
                String error = servicio.guardar(nuevo, clave);
                if (error != null) {
                    aviso(error);
                    return;
                }
                aviso("Usuario guardado.");
            } else {
                Usuario cambio = new Usuario(enEdicion.getIdUsuario(), enEdicion.getNombreUsuario(),
                        txtNombre.getText().trim(), rol, enEdicion.isActivo());
                String error = servicio.modificar(cambio, clave);
                if (error != null) {
                    aviso(error);
                    return;
                }
                Usuario yo = Sesion.getUsuario();
                if (yo != null && yo.getIdUsuario() == cambio.getIdUsuario()) {
                    Sesion.iniciar(cambio);   // si se edita a si mismo, la sesion se actualiza
                }
                aviso("Usuario modificado.");
            }
            limpiar();
            actualizarTabla(servicio.listar());
        } finally {
            Arrays.fill(clave, '\0');
            Arrays.fill(clave2, '\0');
        }
    }

    private void cargarSeleccionado() {
        Usuario u = seleccionado();
        if (u == null) {
            aviso("Seleccione un usuario de la tabla.");
            return;
        }
        enEdicion = u;
        txtUsuario.setText(u.getNombreUsuario());
        txtUsuario.setEditable(false);   // el nombre de usuario no cambia
        txtNombre.setText(u.getNombreCompleto());
        cmbRol.setSelectedItem(u.getRol());
        txtClave.setText("");
        txtClave2.setText("");
        chkActivo.setSelected(u.isActivo());
        txtClave.setToolTipText("Déjela en blanco para conservar la contraseña actual.");
    }

    private void cambiarEstado() {
        Usuario u = seleccionado();
        if (u == null) {
            aviso("Seleccione un usuario de la tabla.");
            return;
        }
        boolean activar = !u.isActivo();
        Usuario yo = Sesion.getUsuario();
        if (!activar && yo != null && yo.getIdUsuario() == u.getIdUsuario()) {
            aviso("No puede desactivar su propio usuario.");
            return;
        }
        int respuesta = JOptionPane.showConfirmDialog(this,
                (activar ? "¿Activar" : "¿Desactivar") + " al usuario " + u.getNombreUsuario() + "?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }
        String error = servicio.cambiarEstado(u.getIdUsuario(), activar);
        if (error != null) {
            aviso(error);
            return;
        }
        limpiar();
        actualizarTabla(servicio.listar());
    }

    private void buscar() {
        String texto = txtBuscar.getText().trim();
        actualizarTabla(texto.isEmpty() ? servicio.listar() : servicio.buscar(texto));
    }

    private void limpiar() {
        enEdicion = null;
        txtUsuario.setText("");
        txtUsuario.setEditable(true);
        txtNombre.setText("");
        cmbRol.setSelectedIndex(0);
        txtClave.setText("");
        txtClave2.setText("");
        txtClave.setToolTipText(null);
        chkActivo.setSelected(true);
        tabla.clearSelection();
        actualizarTextoEstado();
    }

    // ---------- Tabla ----------

    private void actualizarTabla(List<Usuario> lista) {
        mostrados = new ArrayList<>(lista);
        DefaultTableModel modelo = new DefaultTableModel(
                new Object[]{"Usuario", "Nombre completo", "Rol", "Estado"}, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        for (Usuario u : mostrados) {
            modelo.addRow(new Object[]{u.getNombreUsuario(), u.getNombreCompleto(), u.getRol(),
                u.isActivo() ? "Activo" : "Inactivo"});
        }
        tabla.setModel(modelo);
        actualizarTextoEstado();
    }

    private Usuario seleccionado() {
        int fila = tabla.getSelectedRow();
        return (fila < 0 || fila >= mostrados.size()) ? null : mostrados.get(fila);
    }

    private void actualizarTextoEstado() {
        Usuario u = seleccionado();
        btnEstado.setText(u != null && !u.isActivo() ? "Activar" : "Desactivar");
    }

    private void aviso(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje);
    }
}