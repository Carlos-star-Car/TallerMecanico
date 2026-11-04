package com.mycompany.mavenproject1.Servicios;

import com.mycompany.mavenproject1.Entidades.Cliente;
import com.mycompany.mavenproject1.Entidades.DetalleFactura;
import com.mycompany.mavenproject1.Entidades.Factura;
import com.mycompany.mavenproject1.Entidades.OrdenDeTrabajo;
import java.awt.Component;
import java.awt.Desktop;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Genera la factura y la orden de trabajo en PDF (sin librerías externas),
 * y permite guardarlas o imprimirlas a elección del usuario.
 */
public final class DocumentoPdf {

    private static final String TALLER = "TALLERES EMANUEL";
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final Charset WIN = Charset.forName("windows-1252");

    private DocumentoPdf() {
    }

    // ------------------------------------------------------------------
    // Acciones para los formularios
    // ------------------------------------------------------------------

    public static void guardarFactura(Component padre, Factura f) {
        guardar(padre, "Factura_" + f.getIdFactura() + ".pdf", () -> crearFactura(f));
    }

    public static void imprimirFactura(Component padre, Factura f) {
        imprimir(padre, "Factura_" + f.getIdFactura(), () -> crearFactura(f));
    }

    public static void guardarOrden(Component padre, OrdenDeTrabajo o) {
        guardar(padre, "Orden_" + o.getCodigo() + ".pdf", () -> crearOrden(o));
    }

    public static void imprimirOrden(Component padre, OrdenDeTrabajo o) {
        imprimir(padre, "Orden_" + o.getCodigo(), () -> crearOrden(o));
    }

    private interface Generador {
        byte[] generar() throws IOException;
    }

    private static void guardar(Component padre, String nombreSugerido, Generador g) {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Guardar como PDF");
        fc.setFileFilter(new FileNameExtensionFilter("Documento PDF (*.pdf)", "pdf"));
        fc.setSelectedFile(new File(nombreSugerido));
        if (fc.showSaveDialog(padre) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File destino = fc.getSelectedFile();
        if (!destino.getName().toLowerCase().endsWith(".pdf")) {
            destino = new File(destino.getParentFile(), destino.getName() + ".pdf");
        }
        if (destino.exists() && JOptionPane.showConfirmDialog(padre,
                "El archivo ya existe. ¿Desea reemplazarlo?", "Guardar PDF",
                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            Files.write(destino.toPath(), g.generar());
            JOptionPane.showMessageDialog(padre, "PDF guardado en:\n" + destino.getAbsolutePath());
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(padre, "No se pudo guardar el PDF:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Crea un PDF temporal y lo envía a la impresora; si no se puede, lo abre para imprimirlo desde el visor. */
    private static void imprimir(Component padre, String prefijo, Generador g) {
        try {
            File tmp = File.createTempFile(prefijo + "_", ".pdf");
            tmp.deleteOnExit();
            Files.write(tmp.toPath(), g.generar());
            Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
            if (desktop == null) {
                throw new IOException("Este equipo no permite abrir documentos desde el programa.");
            }
            try {
                if (desktop.isSupported(Desktop.Action.PRINT)) {
                    desktop.print(tmp);
                    return;
                }
            } catch (IOException | UnsupportedOperationException ex) {
                // Se intenta abrir el PDF para imprimir manualmente.
            }
            desktop.open(tmp);
            JOptionPane.showMessageDialog(padre,
                    "Se abrió el PDF en su visor.\nUse la opción Imprimir (Ctrl+P) del visor.");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(padre, "No se pudo imprimir:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ------------------------------------------------------------------
    // Documentos
    // ------------------------------------------------------------------

    public static byte[] crearFactura(Factura f) throws IOException {
        Pdf pdf = new Pdf();
        pdf.nuevaPagina();
        encabezado(pdf, "FACTURA No. " + f.getIdFactura(),
                "Fecha: " + f.getFechaEmision().format(FECHA));
        if (f.estaAnulada()) {
            pdf.texto(Pdf.ANCHO - Pdf.MARGEN, 98, "ANULADA", true, 14, Pdf.DER);
        }

        String nombre;
        String nit;
        String direccion;
        Cliente c = f.getCliente();
        if (c != null) {
            nombre = c.getNombreCompleto();
            nit = c.getNit();
            direccion = c.getDireccion();
        } else {
            nombre = f.getNombreManual();
            nit = f.getNitManual();
            direccion = f.getDireccionManual();
        }
        double y = 125;
        y = campo(pdf, y, "Cliente:", vacio(nombre, "Consumidor final"));
        y = campo(pdf, y, "NIT:", vacio(nit, "CF"));
        y = campo(pdf, y, "Dirección:", vacio(direccion, "Ciudad"));
        y = campo(pdf, y, "Tipo:", Factura.TIPO_REPUESTO.equals(f.getTipoFactura())
                ? "Venta de repuestos" : "Servicio");
        if (f.getIdOrden() != null) {
            y = campo(pdf, y, "Orden de trabajo:", f.getIdOrden());
        }
        y += 14;

        double[] col = {Pdf.MARGEN, Pdf.MARGEN + 60, Pdf.MARGEN + 360, Pdf.ANCHO - Pdf.MARGEN};
        y = encabezadoTabla(pdf, y, col);
        List<DetalleFactura> detalles = f.getDetalles();
        for (DetalleFactura d : detalles) {
            if (y > Pdf.ALTO - 150) {
                pdf.nuevaPagina();
                encabezado(pdf, "FACTURA No. " + f.getIdFactura(), "(continuación)");
                y = encabezadoTabla(pdf, 100, col);
            }
            pdf.texto(col[0] + 8, y + 14, String.valueOf(d.getCantidad()), false, 10, Pdf.IZQ);
            pdf.texto(col[1] + 8, y + 14, recortar(d.getConcepto(), false, 10, col[2] - col[1] - 16), false, 10, Pdf.IZQ);
            pdf.texto(col[2] + 62, y + 14, money(d.getPrecioUnitario()), false, 10, Pdf.DER);
            pdf.texto(col[3] - 8, y + 14, money(d.getSubtotal()), false, 10, Pdf.DER);
            y += 20;
            pdf.linea(Pdf.MARGEN, y, Pdf.ANCHO - Pdf.MARGEN, y, 0.5, 0.8);
        }
        if (y > Pdf.ALTO - 120) {
            pdf.nuevaPagina();
            encabezado(pdf, "FACTURA No. " + f.getIdFactura(), "(continuación)");
            y = 100;
        }
        y += 18;
        double xe = Pdf.ANCHO - Pdf.MARGEN - 150;
        double xv = Pdf.ANCHO - Pdf.MARGEN - 8;
        pdf.texto(xe, y, "Subtotal:", false, 11, Pdf.IZQ);
        pdf.texto(xv, y, money(f.getSubtotal()), false, 11, Pdf.DER);
        y += 18;
        pdf.texto(xe, y, "IVA (12%):", false, 11, Pdf.IZQ);
        pdf.texto(xv, y, money(f.getIva()), false, 11, Pdf.DER);
        y += 8;
        pdf.linea(xe, y, Pdf.ANCHO - Pdf.MARGEN, y, 1, 0);
        y += 18;
        pdf.texto(xe, y, "TOTAL:", true, 13, Pdf.IZQ);
        pdf.texto(xv, y, money(f.getTotal()), true, 13, Pdf.DER);

        pdf.texto(Pdf.ANCHO / 2, Pdf.ALTO - 40, "Gracias por su preferencia", false, 10, Pdf.CENTRO);
        return pdf.bytes();
    }

    public static byte[] crearOrden(OrdenDeTrabajo o) throws IOException {
        Pdf pdf = new Pdf();
        pdf.nuevaPagina();
        encabezado(pdf, "ORDEN DE TRABAJO " + o.getCodigo(), "Fecha: " + o.getFecha().format(FECHA));
        pdf.texto(Pdf.ANCHO - Pdf.MARGEN, 98, "Estado: " + o.getEstado(), true, 11, Pdf.DER);

        double y = 125;
        y = seccion(pdf, y, "Cliente");
        Cliente c = o.getCliente();
        if (c != null) {
            y = campo(pdf, y, "Nombre:", c.getNombreCompleto());
            y = campo(pdf, y, "Teléfono:", vacio(c.getTelefono(), "-"));
            y = campo(pdf, y, "NIT:", vacio(c.getNit(), "CF"));
        }
        y += 8;
        y = seccion(pdf, y, "Vehículo");
        if (o.getVehiculo() != null) {
            y = campo(pdf, y, "Placa:", o.getVehiculo().getPlaca());
            y = campo(pdf, y, "Marca / modelo:", o.getVehiculo().getMarca() + " " + o.getVehiculo().getModelo());
            y = campo(pdf, y, "Color:", vacio(o.getVehiculo().getColor(), "-"));
        } else {
            y = campo(pdf, y, "Vehículo:", "No indicado");
        }
        y += 8;
        y = seccion(pdf, y, "Mecánico asignado");
        if (o.getMecanico() != null) {
            y = campo(pdf, y, "Nombre:", o.getMecanico().getNombre() + " " + o.getMecanico().getApellido());
            y = campo(pdf, y, "Especialidad:", vacio(o.getMecanico().getEspecialidad(), "-"));
        } else {
            y = campo(pdf, y, "Mecánico:", "No asignado");
        }
        y += 8;
        y = seccion(pdf, y, "Descripción / diagnóstico");
        for (String linea : pdf.dividir(vacio(o.getDescripcion(), "-"), false, 11, Pdf.ANCHO - 2 * Pdf.MARGEN - 8)) {
            if (y > Pdf.ALTO - 140) {
                pdf.nuevaPagina();
                encabezado(pdf, "ORDEN DE TRABAJO " + o.getCodigo(), "(continuación)");
                y = 100;
            }
            pdf.texto(Pdf.MARGEN + 8, y + 12, linea, false, 11, Pdf.IZQ);
            y += 16;
        }

        double yf = Pdf.ALTO - 90;
        pdf.linea(Pdf.MARGEN, yf, Pdf.MARGEN + 200, yf, 0.8, 0);
        pdf.texto(Pdf.MARGEN, yf + 14, "Firma del cliente", false, 10, Pdf.IZQ);
        pdf.linea(Pdf.ANCHO - Pdf.MARGEN - 200, yf, Pdf.ANCHO - Pdf.MARGEN, yf, 0.8, 0);
        pdf.texto(Pdf.ANCHO - Pdf.MARGEN - 200, yf + 14, "Firma del mecánico", false, 10, Pdf.IZQ);
        return pdf.bytes();
    }

    // ------------------------------------------------------------------
    // Piezas de diseño
    // ------------------------------------------------------------------

    private static void encabezado(Pdf pdf, String titulo, String subtitulo) {
        pdf.rect(0, 0, Pdf.ANCHO, 70, 0.11);
        pdf.rect(0, 70, Pdf.ANCHO, 4, -1);   // franja roja
        pdf.textoColor(Pdf.MARGEN, 38, TALLER, true, 20, Pdf.IZQ, 1);
        pdf.textoColor(Pdf.MARGEN, 56, "Taller mecánico", false, 10, Pdf.IZQ, 0.82);
        pdf.textoColor(Pdf.ANCHO - Pdf.MARGEN, 34, titulo, true, 15, Pdf.DER, 1);
        pdf.textoColor(Pdf.ANCHO - Pdf.MARGEN, 54, subtitulo, false, 10, Pdf.DER, 0.82);
    }

    private static double campo(Pdf pdf, double y, String etiqueta, String valor) {
        pdf.texto(Pdf.MARGEN, y + 12, etiqueta, true, 11, Pdf.IZQ);
        pdf.texto(Pdf.MARGEN + 110, y + 12,
                recortar(valor, false, 11, Pdf.ANCHO - 2 * Pdf.MARGEN - 110), false, 11, Pdf.IZQ);
        return y + 17;
    }

    private static double seccion(Pdf pdf, double y, String titulo) {
        pdf.rect(Pdf.MARGEN, y, Pdf.ANCHO - 2 * Pdf.MARGEN, 20, 0.93);
        pdf.texto(Pdf.MARGEN + 8, y + 14, titulo, true, 11, Pdf.IZQ);
        return y + 26;
    }

    private static double encabezadoTabla(Pdf pdf, double y, double[] col) {
        pdf.rect(col[0], y, col[3] - col[0], 22, 0.11);
        pdf.textoColor(col[0] + 8, y + 15, "Cant.", true, 10, Pdf.IZQ, 1);
        pdf.textoColor(col[1] + 8, y + 15, "Concepto", true, 10, Pdf.IZQ, 1);
        pdf.textoColor(col[2] + 62, y + 15, "Precio unit.", true, 10, Pdf.DER, 1);
        pdf.textoColor(col[3] - 8, y + 15, "Subtotal", true, 10, Pdf.DER, 1);
        return y + 22;
    }

    private static String money(double v) {
        return String.format(java.util.Locale.US, "Q %,.2f", v);
    }

    private static String vacio(String s, String porDefecto) {
        return (s == null || s.trim().isEmpty()) ? porDefecto : s.trim();
    }

    private static String recortar(String s, boolean negrita, double tam, double ancho) {
        if (s == null) {
            return "";
        }
        if (Pdf.ancho(s, negrita, tam) <= ancho) {
            return s;
        }
        String t = s;
        while (t.length() > 1 && Pdf.ancho(t + "...", negrita, tam) > ancho) {
            t = t.substring(0, t.length() - 1);
        }
        return t + "...";
    }

    // ------------------------------------------------------------------
    // Escritor de PDF mínimo (Helvetica, tamaño Carta)
    // ------------------------------------------------------------------

    private static final class Pdf {

        static final double ANCHO = 612;
        static final double ALTO = 792;
        static final double MARGEN = 40;
        static final int IZQ = 0;
        static final int CENTRO = 1;
        static final int DER = 2;

        private static final int[] W_REG = {
        761, 761, 761, 761, 761, 761, 761, 761, 761, 761, 761, 761, 761, 761, 761, 761,
        761, 761, 761, 761, 761, 761, 761, 761, 761, 761, 761, 761, 761, 761, 761, 761,
        278, 278, 355, 556, 556, 889, 667, 191, 333, 333, 389, 584, 278, 333, 278, 278,
        556, 556, 556, 556, 556, 556, 556, 556, 556, 556, 278, 278, 584, 584, 584, 556,
        1015, 667, 667, 722, 722, 667, 611, 778, 722, 278, 500, 667, 556, 833, 722, 778,
        667, 778, 722, 667, 611, 722, 667, 944, 667, 667, 611, 278, 278, 278, 469, 556,
        333, 556, 556, 500, 556, 556, 278, 556, 556, 222, 222, 500, 222, 833, 556, 556,
        556, 556, 333, 500, 278, 556, 500, 722, 500, 500, 500, 334, 260, 334, 584, 761,
        556, 500, 222, 556, 333, 1000, 556, 556, 333, 1000, 667, 333, 1000, 500, 611, 500,
        500, 222, 222, 333, 333, 350, 556, 1000, 333, 1000, 500, 333, 944, 500, 500, 667,
        278, 333, 556, 556, 556, 556, 260, 556, 333, 737, 370, 556, 584, 333, 737, 333,
        400, 584, 333, 333, 333, 556, 537, 278, 333, 333, 365, 556, 834, 834, 834, 611,
        667, 667, 667, 667, 667, 667, 1000, 722, 667, 667, 667, 667, 278, 278, 278, 278,
        722, 722, 778, 778, 778, 778, 778, 584, 778, 722, 722, 722, 722, 667, 667, 611,
        556, 556, 556, 556, 556, 556, 889, 500, 556, 556, 556, 556, 278, 278, 278, 278,
        556, 556, 556, 556, 556, 556, 556, 584, 611, 556, 556, 556, 556, 500, 556, 500
        };
        private static final int[] W_BOLD = {
        761, 761, 761, 761, 761, 761, 761, 761, 761, 761, 761, 761, 761, 761, 761, 761,
        761, 761, 761, 761, 761, 761, 761, 761, 761, 761, 761, 761, 761, 761, 761, 761,
        278, 333, 474, 556, 556, 889, 722, 238, 333, 333, 389, 584, 278, 333, 278, 278,
        556, 556, 556, 556, 556, 556, 556, 556, 556, 556, 333, 333, 584, 584, 584, 611,
        975, 722, 722, 722, 722, 667, 611, 778, 722, 278, 556, 722, 611, 833, 722, 778,
        667, 778, 722, 667, 611, 722, 667, 944, 667, 667, 611, 333, 278, 333, 584, 556,
        333, 556, 611, 556, 611, 556, 333, 611, 611, 278, 278, 556, 278, 889, 611, 611,
        611, 611, 389, 556, 333, 611, 556, 778, 556, 556, 500, 389, 280, 389, 584, 761,
        556, 500, 278, 556, 500, 1000, 556, 556, 333, 1000, 667, 333, 1000, 500, 611, 500,
        500, 278, 278, 500, 500, 350, 556, 1000, 333, 1000, 556, 333, 944, 500, 500, 667,
        278, 333, 556, 556, 556, 556, 280, 556, 333, 737, 370, 556, 584, 333, 737, 333,
        400, 584, 333, 333, 333, 611, 556, 278, 333, 333, 365, 556, 834, 834, 834, 611,
        722, 722, 722, 722, 722, 722, 1000, 722, 667, 667, 667, 667, 278, 278, 278, 278,
        722, 722, 778, 778, 778, 778, 778, 584, 778, 722, 722, 722, 722, 667, 667, 611,
        556, 556, 556, 556, 556, 556, 889, 556, 556, 556, 556, 556, 278, 278, 278, 278,
        611, 611, 611, 611, 611, 611, 611, 584, 611, 611, 611, 611, 611, 556, 611, 556
        };

        private final List<StringBuilder> paginas = new ArrayList<>();
        private StringBuilder actual;

        void nuevaPagina() {
            actual = new StringBuilder();
            paginas.add(actual);
        }

        static double ancho(String s, boolean negrita, double tam) {
            int[] w = negrita ? W_BOLD : W_REG;
            byte[] b = s.getBytes(WIN);
            double total = 0;
            for (byte x : b) {
                total += w[x & 0xFF];
            }
            return total * tam / 1000.0;
        }

        List<String> dividir(String texto, boolean negrita, double tam, double anchoMax) {
            List<String> lineas = new ArrayList<>();
            for (String parrafo : texto.split("\\r?\\n")) {
                StringBuilder linea = new StringBuilder();
                for (String palabra : parrafo.split(" ")) {
                    String prueba = linea.length() == 0 ? palabra : linea + " " + palabra;
                    if (ancho(prueba, negrita, tam) <= anchoMax || linea.length() == 0) {
                        linea = new StringBuilder(prueba);
                    } else {
                        lineas.add(linea.toString());
                        linea = new StringBuilder(palabra);
                    }
                }
                lineas.add(linea.toString());
            }
            return lineas;
        }

        private static String n(double v) {
            return String.format(java.util.Locale.US, "%.2f", v);
        }

        /** y se mide desde el borde superior de la página. */
        void rect(double x, double y, double w, double h, double gris) {
            if (gris < 0) {
                actual.append("0.78 0.06 0.18 rg\n");
            } else {
                actual.append(n(gris)).append(" g\n");
            }
            actual.append(n(x)).append(' ').append(n(ALTO - y - h)).append(' ')
                    .append(n(w)).append(' ').append(n(h)).append(" re f\n");
        }

        void linea(double x1, double y1, double x2, double y2, double grosor, double gris) {
            actual.append(n(gris)).append(" G ").append(n(grosor)).append(" w ")
                    .append(n(x1)).append(' ').append(n(ALTO - y1)).append(" m ")
                    .append(n(x2)).append(' ').append(n(ALTO - y2)).append(" l S\n");
        }

        void texto(double x, double y, String s, boolean negrita, double tam, int alin) {
            textoColor(x, y, s, negrita, tam, alin, 0);
        }

        void textoColor(double x, double y, String s, boolean negrita, double tam, int alin, double gris) {
            double w = ancho(s, negrita, tam);
            double px = alin == DER ? x - w : (alin == CENTRO ? x - w / 2 : x);
            actual.append(n(gris)).append(" g BT /").append(negrita ? "F2" : "F1").append(' ')
                    .append(n(tam)).append(" Tf ").append(n(px)).append(' ').append(n(ALTO - y))
                    .append(" Td (").append(escapar(s)).append(") Tj ET\n");
        }

        private static String escapar(String s) {
            StringBuilder sb = new StringBuilder();
            for (byte b : s.getBytes(WIN)) {
                int c = b & 0xFF;
                if (c == '(' || c == ')' || c == '\\') {
                    sb.append('\\').append((char) c);
                } else if (c < 32 || c > 126) {
                    sb.append(String.format("\\%03o", c));
                } else {
                    sb.append((char) c);
                }
            }
            return sb.toString();
        }

        byte[] bytes() throws IOException {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            List<Integer> offsets = new ArrayList<>();
            int np = paginas.size();
            // Objetos: 1 catálogo, 2 páginas, 3 y 4 fuentes, luego (página, contenido) por cada hoja.
            escribir(out, "%PDF-1.4\n");
            objeto(out, offsets, 1, "<< /Type /Catalog /Pages 2 0 R >>");
            StringBuilder kids = new StringBuilder();
            for (int i = 0; i < np; i++) {
                kids.append(5 + 2 * i).append(" 0 R ");
            }
            objeto(out, offsets, 2, "<< /Type /Pages /Kids [" + kids + "] /Count " + np + " >>");
            objeto(out, offsets, 3, "<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica /Encoding /WinAnsiEncoding >>");
            objeto(out, offsets, 4, "<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica-Bold /Encoding /WinAnsiEncoding >>");
            for (int i = 0; i < np; i++) {
                int idPag = 5 + 2 * i;
                objeto(out, offsets, idPag, "<< /Type /Page /Parent 2 0 R /MediaBox [0 0 612 792] "
                        + "/Resources << /Font << /F1 3 0 R /F2 4 0 R >> >> /Contents " + (idPag + 1) + " 0 R >>");
                byte[] contenido = paginas.get(i).toString().getBytes(WIN);
                offsets.add(out.size());
                escribir(out, (idPag + 1) + " 0 obj\n<< /Length " + contenido.length + " >>\nstream\n");
                out.write(contenido);
                escribir(out, "\nendstream\nendobj\n");
            }
            int xref = out.size();
            int total = offsets.size() + 1;
            escribir(out, "xref\n0 " + total + "\n0000000000 65535 f \n");
            for (int off : offsets) {
                escribir(out, String.format("%010d 00000 n \n", off));
            }
            escribir(out, "trailer\n<< /Size " + total + " /Root 1 0 R >>\nstartxref\n" + xref + "\n%%EOF\n");
            return out.toByteArray();
        }

        private static void objeto(ByteArrayOutputStream out, List<Integer> offsets, int id, String cuerpo)
                throws IOException {
            offsets.add(out.size());
            escribir(out, id + " 0 obj\n" + cuerpo + "\nendobj\n");
        }

        private static void escribir(ByteArrayOutputStream out, String s) throws IOException {
            out.write(s.getBytes(WIN));
        }
    }
}
