/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.mavenproject1.Formularios;
 
/**
 *
 * @author cxoy
 */
public class FrmMenuPrincipal extends javax.swing.JFrame {
 
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(FrmMenuPrincipal.class.getName());
 
    /**
     * Creates new form FrmMenuPrincipal
     */
    public FrmMenuPrincipal() {
        initComponents();
 
        this.setSize(900, 700); // Tamaño unificado
        this.setLocationRelativeTo(null); // Centrar en pantalla
        this.setResizable(false); // Evita que se redimensione
        cargarImagen1();
        cargarImagenlogo();
        aplicarPermisos();
        crearBarraSesion();
    }
 
    /** Habilita solo los botones que el rol de la sesión puede usar. */
    private void aplicarPermisos() {
        btnClientes.setEnabled(com.mycompany.mavenproject1.Servicios.Sesion.puedeUsar(com.mycompany.mavenproject1.Servicios.Sesion.MOD_CLIENTES));
        btnVehiculos.setEnabled(com.mycompany.mavenproject1.Servicios.Sesion.puedeUsar(com.mycompany.mavenproject1.Servicios.Sesion.MOD_VEHICULOS));
        btnMecanicos.setEnabled(com.mycompany.mavenproject1.Servicios.Sesion.puedeUsar(com.mycompany.mavenproject1.Servicios.Sesion.MOD_MECANICOS));
        btnRepuestos.setEnabled(com.mycompany.mavenproject1.Servicios.Sesion.puedeUsar(com.mycompany.mavenproject1.Servicios.Sesion.MOD_REPUESTOS));
        btnCategoriaRepuesto.setEnabled(com.mycompany.mavenproject1.Servicios.Sesion.puedeUsar(com.mycompany.mavenproject1.Servicios.Sesion.MOD_CATEGORIAS));
        btnProveedores.setEnabled(com.mycompany.mavenproject1.Servicios.Sesion.puedeUsar(com.mycompany.mavenproject1.Servicios.Sesion.MOD_PROVEEDORES));
        btnOrdenServicio.setEnabled(com.mycompany.mavenproject1.Servicios.Sesion.puedeUsar(com.mycompany.mavenproject1.Servicios.Sesion.MOD_ORDENES));
        btnFactura.setEnabled(com.mycompany.mavenproject1.Servicios.Sesion.puedeUsar(com.mycompany.mavenproject1.Servicios.Sesion.MOD_FACTURACION));
        btnUsuarios.setEnabled(com.mycompany.mavenproject1.Servicios.Sesion.puedeUsar(com.mycompany.mavenproject1.Servicios.Sesion.MOD_USUARIOS));
    }
 
    /** Barra superior con el usuario conectado, Cerrar sesión y Salir. */
    private void crearBarraSesion() {
        javax.swing.JMenuBar barra = new javax.swing.JMenuBar();
        com.mycompany.mavenproject1.Entidades.Usuario u = com.mycompany.mavenproject1.Servicios.Sesion.getUsuario();
        String texto = (u == null) ? "Sin sesión"
                : "Sesión: " + u.getNombreUsuario() + " · Rol: " + u.getRol();
        javax.swing.JLabel lblSesion = new javax.swing.JLabel("  " + texto);
        lblSesion.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 13));
        javax.swing.JButton btnCerrar = new javax.swing.JButton("Cerrar sesión");
        javax.swing.JButton btnSalirApp = new javax.swing.JButton("Salir");
        btnCerrar.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 13));
        btnSalirApp.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 13));
        btnCerrar.addActionListener(e -> {
            com.mycompany.mavenproject1.Servicios.Sesion.cerrar();
            FrmLogin login = new FrmLogin();
            login.setLocationRelativeTo(null);
            login.setVisible(true);
            dispose();
        });
        btnSalirApp.addActionListener(e -> {
            int r = javax.swing.JOptionPane.showConfirmDialog(this,
                    "¿Desea salir del sistema?", "Salir",
                    javax.swing.JOptionPane.YES_NO_OPTION);
            if (r == javax.swing.JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });
        barra.add(lblSesion);
        barra.add(javax.swing.Box.createHorizontalGlue());
        barra.add(btnCerrar);
        barra.add(javax.swing.Box.createHorizontalStrut(8));
        barra.add(btnSalirApp);
        barra.add(javax.swing.Box.createHorizontalStrut(8));
        setJMenuBar(barra);
    }
 
  private void cargarImagen1() {
    java.net.URL url = getClass().getResource("/tallermecanico.jpg");
    if (url == null) {
        System.out.println("No se encontró la imagen");
        return;
    }
    javax.swing.ImageIcon icono = new javax.swing.ImageIcon(url);
    java.awt.Image escalada = icono.getImage().getScaledInstance(
            420, 460, java.awt.Image.SCALE_SMOOTH);
    lblImagen.setIcon(new javax.swing.ImageIcon(escalada));
    lblImagen.setText("");
}
    private void cargarImagenlogo() {
    java.net.URL url = getClass().getResource("/logotaller.png");
    if (url == null) {
        System.out.println("No se encontró la imagen");
        return;
    }
    javax.swing.ImageIcon icono = new javax.swing.ImageIcon(url);
    java.awt.Image escalada = icono.getImage().getScaledInstance(
            260, 100, java.awt.Image.SCALE_SMOOTH);
    lbllogo.setIcon(new javax.swing.ImageIcon(escalada));
    lbllogo.setText("");
}
 
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
 
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        lblImagen = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        btnClientes = new javax.swing.JButton();
        btnVehiculos = new javax.swing.JButton();
        btnMecanicos = new javax.swing.JButton();
        btnRepuestos = new javax.swing.JButton();
        btnCategoriaRepuesto = new javax.swing.JButton();
        btnOrdenServicio = new javax.swing.JButton();
        btnFactura = new javax.swing.JButton();
        btnProveedores = new javax.swing.JButton();
        btnUsuarios = new javax.swing.JButton();
        lbllogo = new javax.swing.JLabel();
 
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(51, 153, 255));
        setForeground(java.awt.Color.white);
        setResizable(false);
 
        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 28)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Sistema de Gestión - Taller Mecánico");
        jLabel1.setMaximumSize(new java.awt.Dimension(500, 30));
        jLabel1.setMinimumSize(new java.awt.Dimension(500, 30));
        jLabel1.setPreferredSize(new java.awt.Dimension(500, 30));
 
        jLabel2.setMaximumSize(new java.awt.Dimension(150, 150));
        jLabel2.setMinimumSize(new java.awt.Dimension(150, 150));
 
        lblImagen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/tallermecanico.jpg"))); // NOI18N
        lblImagen.setMaximumSize(new java.awt.Dimension(420, 475));
        lblImagen.setMinimumSize(new java.awt.Dimension(420, 475));
        lblImagen.setPreferredSize(new java.awt.Dimension(420, 475));
 
        jPanel1.setMinimumSize(new java.awt.Dimension(260, 420));
        jPanel1.setPreferredSize(new java.awt.Dimension(260, 420));
 
        btnClientes.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnClientes.setText("Gestión de clientes");
        btnClientes.addActionListener(this::btnClientesActionPerformed);
 
        btnVehiculos.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnVehiculos.setText("Gestión de Vehículos");
        btnVehiculos.addActionListener(this::btnVehiculosActionPerformed);
 
        btnMecanicos.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnMecanicos.setText("Gestión de Mecánicos");
        btnMecanicos.addActionListener(this::btnMecanicosActionPerformed);
 
        btnRepuestos.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnRepuestos.setText("Gestión de Repuestos");
        btnRepuestos.addActionListener(this::btnRepuestosActionPerformed);
 
        btnCategoriaRepuesto.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnCategoriaRepuesto.setText("Categoría Repuestos");
        btnCategoriaRepuesto.addActionListener(this::btnCategoriaRepuestoActionPerformed);
 
        btnOrdenServicio.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnOrdenServicio.setText("Gestión de Servicio");
        btnOrdenServicio.addActionListener(this::btnOrdenServicioActionPerformed);
 
        btnFactura.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnFactura.setText("Facturación");
        btnFactura.addActionListener(this::btnFacturaActionPerformed);
 
        btnProveedores.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnProveedores.setText("Proveedores");
        btnProveedores.addActionListener(this::btnProveedoresActionPerformed);
 
        btnUsuarios.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnUsuarios.setText("Gestión de usuarios");
        btnUsuarios.addActionListener(this::btnUsuariosActionPerformed);
 
        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(43, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(btnFactura, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnProveedores, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnMecanicos, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnVehiculos, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnClientes, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnRepuestos, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnCategoriaRepuesto, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnOrdenServicio, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnUsuarios, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(38, 38, 38))
                    ))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnClientes)
                .addGap(18, 18, 18)
                .addComponent(btnVehiculos)
                .addGap(18, 18, 18)
                .addComponent(btnMecanicos)
                .addGap(19, 19, 19)
                .addComponent(btnRepuestos)
                .addGap(18, 18, 18)
                .addComponent(btnCategoriaRepuesto)
                .addGap(18, 18, 18)
                .addComponent(btnOrdenServicio)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnProveedores)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnFactura)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 14, Short.MAX_VALUE)
                .addComponent(btnUsuarios)
                .addContainerGap())
        );
 
        lbllogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/logotaller.png"))); // NOI18N
        lbllogo.setMaximumSize(new java.awt.Dimension(260, 100));
        lbllogo.setMinimumSize(new java.awt.Dimension(260, 100));
 
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbllogo, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 105, Short.MAX_VALUE)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(41, 41, 41)
                        .addComponent(lblImagen, javax.swing.GroupLayout.PREFERRED_SIZE, 420, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(130, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(52, 52, 52)
                        .addComponent(lblImagen, javax.swing.GroupLayout.PREFERRED_SIZE, 475, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lbllogo, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(63, 63, 63)
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 420, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(93, Short.MAX_VALUE))
        );
 
        pack();
    }// </editor-fold>//GEN-END:initComponents
 
    private void btnClientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClientesActionPerformed
      FrmCliente ventana = new FrmCliente();
    ventana.setVisible(true);
    ventana.setLocationRelativeTo(null);
    this.dispose();   // cierra el menú actual
    // TODO add your handling code here:
    }//GEN-LAST:event_btnClientesActionPerformed
 
    private void btnVehiculosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVehiculosActionPerformed
  FrmVehiculos ventana = new FrmVehiculos();
    ventana.setVisible(true);
    ventana.setLocationRelativeTo(null);
    this.dispose();   // cierra el menú actual      
// TODO add your handling code here:
    }//GEN-LAST:event_btnVehiculosActionPerformed
 
    private void btnMecanicosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMecanicosActionPerformed
    FrmMecanicos ventana = new FrmMecanicos();
    ventana.setVisible(true);
    ventana.setLocationRelativeTo(null);
    this.dispose();   // cierra el menú actual        
// TODO add your handling code here:
    }//GEN-LAST:event_btnMecanicosActionPerformed
 
    private void btnCategoriaRepuestoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCategoriaRepuestoActionPerformed
    FrmCategoriaRepuesto ventana = new FrmCategoriaRepuesto();
    ventana.setVisible(true);
    ventana.setLocationRelativeTo(null);
    this.dispose();   // cierra el menú actual
// TODO add your handling code here:
    }//GEN-LAST:event_btnCategoriaRepuestoActionPerformed
 
    private void btnOrdenServicioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOrdenServicioActionPerformed
        FrmOrdenDeTrabajo ventanaOrdenServicio = new FrmOrdenDeTrabajo();
        ventanaOrdenServicio.setVisible(true);
        ventanaOrdenServicio.setLocationRelativeTo(null);        // TODO add your handling code here:
    }//GEN-LAST:event_btnOrdenServicioActionPerformed
 
    private void btnFacturaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFacturaActionPerformed
    FrmFactura ventana = new FrmFactura();
    ventana.setVisible(true);
    ventana.setLocationRelativeTo(null);
    this.dispose();   // cierra el menú actual        
// TODO add your handling code here:
    }//GEN-LAST:event_btnFacturaActionPerformed
 
    private void btnRepuestosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRepuestosActionPerformed
      FrmRepuestos ventana = new FrmRepuestos();
    ventana.setVisible(true);
    ventana.setLocationRelativeTo(null);
    this.dispose();   // cierra el menú actual;   
// cierra el menú actual     
        // TODO add your handling code here:
    }//GEN-LAST:event_btnRepuestosActionPerformed
 
    private void btnProveedoresActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProveedoresActionPerformed
FrmProveedores ventana = new FrmProveedores();
    ventana.setVisible(true);
    ventana.setLocationRelativeTo(null);
    this.dispose();
        // TODO add your handling code here:
    }//GEN-LAST:event_btnProveedoresActionPerformed
 
    private void btnUsuariosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUsuariosActionPerformed
       FrmUsuarios.abrir();
        // TODO add your handling code here:
    }//GEN-LAST:event_btnUsuariosActionPerformed
 
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
 
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new FrmMenuPrincipal().setVisible(true));
    }
 
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCategoriaRepuesto;
    private javax.swing.JButton btnClientes;
    private javax.swing.JButton btnFactura;
    private javax.swing.JButton btnMecanicos;
    private javax.swing.JButton btnOrdenServicio;
    private javax.swing.JButton btnProveedores;
    private javax.swing.JButton btnRepuestos;
    private javax.swing.JButton btnUsuarios;
    private javax.swing.JButton btnVehiculos;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblImagen;
    private javax.swing.JLabel lbllogo;
    // End of variables declaration//GEN-END:variables
}