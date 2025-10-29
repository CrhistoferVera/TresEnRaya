// src/main/java/org/tresenraya/VisualizadorArbolGrafico.java
package org.tresenraya;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.tresenraya.model.Nodo;
import org.tresenraya.model.VisualizadorArbol;

public class VisualizadorArbolGrafico extends JFrame {
    private PanelArbol panelArbol;

    public VisualizadorArbolGrafico(String algoritmo) {
        setTitle("🌳 Árbol de Decisión - " + algoritmo);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        panelArbol = new PanelArbol(VisualizadorArbol.getRaiz());
        
        JScrollPane scrollPane = new JScrollPane(panelArbol);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        add(scrollPane);

        // Panel de información
        JPanel panelInfo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelInfo.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        JLabel lblInfo = new JLabel(String.format(
            "📊 Nodos: %d | ✂️ Podados: %d | Algoritmo: %s",
            VisualizadorArbol.getNodosExplorados(),
            VisualizadorArbol.getNodosPodados(),
            algoritmo.toUpperCase()
        ));
        lblInfo.setFont(new Font("Arial", Font.BOLD, 14));
        panelInfo.add(lblInfo);

        add(panelInfo, BorderLayout.NORTH);
    }

    public static void mostrar(String algoritmo) {
        SwingUtilities.invokeLater(() -> {
            VisualizadorArbolGrafico ventana = new VisualizadorArbolGrafico(algoritmo);
            ventana.setVisible(true);
        });
    }
}

class PanelArbol extends JPanel {
    private Nodo raiz;
    private Map<Nodo, Point> posiciones;
    private static final int ANCHO_NODO = 100;
    private static final int ALTO_NODO = 70;
    private static final int ESPACIO_VERTICAL = 100;
    private static final int ESPACIO_HORIZONTAL = 30;

    public PanelArbol(Nodo raiz) {
        this.raiz = raiz;
        this.posiciones = new HashMap<>();
        setBackground(Color.WHITE);
        
        if (raiz != null) {
            calcularPosiciones();
        }
    }

    private void calcularPosiciones() {
        if (raiz == null) return;
        
        int[] contadorX = {50};
        calcularPosicionesRecursivo(raiz, contadorX, 50);
        
        int maxX = 0, maxY = 0;
        for (Point p : posiciones.values()) {
            maxX = Math.max(maxX, p.x + ANCHO_NODO + 50);
            maxY = Math.max(maxY, p.y + ALTO_NODO + 50);
        }
        setPreferredSize(new Dimension(maxX, maxY));
    }

    private void calcularPosicionesRecursivo(Nodo nodo, int[] contadorX, int y) {
        if (nodo == null) return;

        int x = contadorX[0];
        posiciones.put(nodo, new Point(x, y));
        contadorX[0] += ANCHO_NODO + ESPACIO_HORIZONTAL;

        int yHijo = y + ALTO_NODO + ESPACIO_VERTICAL;
        for (Nodo hijo : nodo.getHijos()) {
            calcularPosicionesRecursivo(hijo, contadorX, yHijo);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (raiz != null) {
            dibujarArbol(g2d, raiz);
        }
    }

    private void dibujarArbol(Graphics2D g2d, Nodo nodo) {
        if (nodo == null || !posiciones.containsKey(nodo)) return;

        Point pos = posiciones.get(nodo);

        // Dibujar líneas a hijos
        for (Nodo hijo : nodo.getHijos()) {
            if (posiciones.containsKey(hijo)) {
                Point posHijo = posiciones.get(hijo);
                
                if (hijo.isPodado()) {
                    g2d.setColor(new Color(231, 76, 60));
                    g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{5}, 0));
                } else {
                    g2d.setColor(new Color(149, 165, 166));
                    g2d.setStroke(new BasicStroke(2));
                }
                
                g2d.drawLine(
                    pos.x + ANCHO_NODO / 2,
                    pos.y + ALTO_NODO,
                    posHijo.x + ANCHO_NODO / 2,
                    posHijo.y
                );
            }
        }

        dibujarNodo(g2d, nodo, pos.x, pos.y);

        for (Nodo hijo : nodo.getHijos()) {
            dibujarArbol(g2d, hijo);
        }
    }

    private void dibujarNodo(Graphics2D g2d, Nodo nodo, int x, int y) {
        Color colorFondo;
        Color colorTexto = Color.WHITE;
        
        if (nodo.isPodado()) {
            colorFondo = new Color(231, 76, 60);
        } else if (nodo.isTerminal()) {
            colorFondo = new Color(241, 196, 15);
            colorTexto = Color.BLACK;
        } else if (nodo.getTipo().equals("MAX")) {
            colorFondo = new Color(52, 152, 219);
        } else {
            colorFondo = new Color(46, 204, 113);
        }

        g2d.setColor(colorFondo);
        g2d.fillRoundRect(x, y, ANCHO_NODO, ALTO_NODO, 10, 10);
        
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(x, y, ANCHO_NODO, ALTO_NODO, 10, 10);

        g2d.setColor(colorTexto);
        g2d.setFont(new Font("Arial", Font.PLAIN, 11));
        FontMetrics fm = g2d.getFontMetrics();
        
        String[] lineas = nodo.toString().split("\n");
        int yTexto = y + 20;
        
        for (String linea : lineas) {
            Rectangle2D rect = fm.getStringBounds(linea, g2d);
            int xTexto = x + (ANCHO_NODO - (int) rect.getWidth()) / 2;
            g2d.drawString(linea, xTexto, yTexto);
            yTexto += fm.getHeight();
        }

        if (nodo.isPodado()) {
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 16));
            g2d.drawString("✂️", x + ANCHO_NODO - 20, y + 20);
        }
    }
}