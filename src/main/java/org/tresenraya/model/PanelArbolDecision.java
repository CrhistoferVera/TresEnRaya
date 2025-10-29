package org.tresenraya.model;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel que dibuja el árbol de decisión gráficamente
 */
public class PanelArbolDecision extends JPanel {
    private NodoArbol raiz;
    private String algoritmo;
    private int nodosExplorados;
    private int nodosPodados;

    private static final int RADIO_NODO = 25;
    private static final int ESPACIO_HORIZONTAL = 80;
    private static final int ESPACIO_VERTICAL = 100;
    private static final Color COLOR_MAX = new Color(46, 204, 113);
    private static final Color COLOR_MIN = new Color(231, 76, 60);
    private static final Color COLOR_PODA = new Color(149, 165, 166);
    private static final Color COLOR_MEJOR = new Color(241, 196, 15);

    public PanelArbolDecision() {
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(800, 600));
    }

    public void setArbol(NodoArbol raiz, String algoritmo, int nodosExplorados, int nodosPodados) {
        this.raiz = raiz;
        this.algoritmo = algoritmo;
        this.nodosExplorados = nodosExplorados;
        this.nodosPodados = nodosPodados;
        repaint();
    }

    public void limpiar() {
        this.raiz = null;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Activar antialiasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        if (raiz == null) {
            dibujarMensajeVacio(g2d);
            return;
        }

        // Título
        g2d.setFont(new Font("Arial", Font.BOLD, 18));
        g2d.setColor(new Color(52, 73, 94));
        g2d.drawString("🌳 ÁRBOL DE DECISIÓN - " + algoritmo.toUpperCase(), 20, 30);

        // Estadísticas
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        g2d.drawString("Nodos explorados: " + nodosExplorados +
                (nodosPodados > 0 ? " | Podados: " + nodosPodados : ""), 20, 50);

        // Leyenda
        dibujarLeyenda(g2d);

        // Calcular posiciones y dibujar árbol
        int anchoDisponible = getWidth() - 40;
        calcularPosiciones(raiz, getWidth() / 2, 100, anchoDisponible, 0);
        dibujarArbol(g2d, raiz);
    }

    private void dibujarMensajeVacio(Graphics2D g2d) {
        g2d.setFont(new Font("Arial", Font.PLAIN, 16));
        g2d.setColor(Color.GRAY);
        String mensaje = "El árbol de decisión se mostrará aquí cuando la IA juegue";
        FontMetrics fm = g2d.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(mensaje)) / 2;
        int y = getHeight() / 2;
        g2d.drawString(mensaje, x, y);
    }

    private void dibujarLeyenda(Graphics2D g2d) {
        int x = getWidth() - 200;
        int y = 20;

        g2d.setFont(new Font("Arial", Font.BOLD, 11));
        g2d.setColor(Color.BLACK);
        g2d.drawString("LEYENDA:", x, y);

        y += 20;
        g2d.setFont(new Font("Arial", Font.PLAIN, 10));

        // MAX
        g2d.setColor(COLOR_MAX);
        g2d.fillOval(x, y - 8, 12, 12);
        g2d.setColor(Color.BLACK);
        g2d.drawString("MAX (▲)", x + 20, y);

        y += 18;
        // MIN
        g2d.setColor(COLOR_MIN);
        g2d.fillOval(x, y - 8, 12, 12);
        g2d.setColor(Color.BLACK);
        g2d.drawString("MIN (▼)", x + 20, y);

        y += 18;
        // Mejor
        g2d.setColor(COLOR_MEJOR);
        g2d.fillOval(x, y - 8, 12, 12);
        g2d.setColor(Color.BLACK);
        g2d.drawString("Mejor", x + 20, y);

        if (nodosPodados > 0) {
            y += 18;
            // Poda
            g2d.setColor(COLOR_PODA);
            g2d.fillOval(x, y - 8, 12, 12);
            g2d.setColor(Color.BLACK);
            g2d.drawString("Podado ✂", x + 20, y);
        }
    }

    private void calcularPosiciones(NodoArbol nodo, int x, int y, int ancho, int profundidad) {
        if (nodo == null) return;

        nodo.x = x;
        nodo.y = y;

        List<NodoArbol> hijos = nodo.hijos;
        if (hijos.isEmpty()) return;

        int numHijos = hijos.size();
        int espacioEntre = Math.min(ancho / Math.max(numHijos, 1), ESPACIO_HORIZONTAL);
        int anchoTotal = espacioEntre * (numHijos - 1);
        int inicioX = x - anchoTotal / 2;

        for (int i = 0; i < numHijos; i++) {
            NodoArbol hijo = hijos.get(i);
            int hijoX = inicioX + (i * espacioEntre);
            int hijoY = y + ESPACIO_VERTICAL;

            calcularPosiciones(hijo, hijoX, hijoY, espacioEntre, profundidad + 1);
        }
    }

    private void dibujarArbol(Graphics2D g2d, NodoArbol nodo) {
        if (nodo == null) return;

        // Dibujar líneas a los hijos primero
        g2d.setStroke(new BasicStroke(2));
        for (NodoArbol hijo : nodo.hijos) {
            if (hijo.esPoda) {
                g2d.setColor(COLOR_PODA);
                g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT,
                        BasicStroke.JOIN_BEVEL, 0, new float[]{5}, 0));
            } else {
                g2d.setColor(new Color(189, 195, 199));
                g2d.setStroke(new BasicStroke(2));
            }

            g2d.drawLine(nodo.x, nodo.y, hijo.x, hijo.y);

            // Dibujar recursivamente
            dibujarArbol(g2d, hijo);
        }

        // Dibujar el nodo
        dibujarNodo(g2d, nodo);
    }

    private void dibujarNodo(Graphics2D g2d, NodoArbol nodo) {
        // Color del nodo
        Color colorNodo;
        if (nodo.esPoda) {
            colorNodo = COLOR_PODA;
        } else if (nodo.esMejor) {
            colorNodo = COLOR_MEJOR;
        } else if (nodo.esMax) {
            colorNodo = COLOR_MAX;
        } else {
            colorNodo = COLOR_MIN;
        }

        // Dibujar círculo
        g2d.setColor(colorNodo);
        g2d.fillOval(nodo.x - RADIO_NODO, nodo.y - RADIO_NODO, RADIO_NODO * 2, RADIO_NODO * 2);

        // Borde
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(nodo.esMejor ? 3 : 2));
        g2d.drawOval(nodo.x - RADIO_NODO, nodo.y - RADIO_NODO, RADIO_NODO * 2, RADIO_NODO * 2);

        // Símbolo MAX/MIN
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.setColor(Color.WHITE);
        String simbolo = nodo.esMax ? "▲" : "▼";
        FontMetrics fm = g2d.getFontMetrics();
        int anchoTexto = fm.stringWidth(simbolo);
        g2d.drawString(simbolo, nodo.x - anchoTexto / 2, nodo.y + 6);

        // Información del nodo
        g2d.setFont(new Font("Arial", Font.PLAIN, 10));
        g2d.setColor(Color.BLACK);

        // Movimiento
        if (nodo.movimiento != null) {
            String mov = "(" + nodo.movimiento[0] + "," + nodo.movimiento[1] + ")";
            g2d.drawString(mov, nodo.x - 15, nodo.y - RADIO_NODO - 5);
        }

        // Valor
        if (nodo.valor != Integer.MIN_VALUE && nodo.valor != Integer.MAX_VALUE) {
            String val = nodo.esPoda ? "✂" : String.valueOf(nodo.valor);
            g2d.drawString(val, nodo.x - 8, nodo.y + RADIO_NODO + 15);
        }

        // Alpha-Beta (si aplica)
        if (nodo.alpha != Integer.MIN_VALUE || nodo.beta != Integer.MAX_VALUE) {
            String alphaBeta = "α:" + (nodo.alpha == Integer.MIN_VALUE ? "-∞" : nodo.alpha) +
                    " β:" + (nodo.beta == Integer.MAX_VALUE ? "∞" : nodo.beta);
            g2d.setFont(new Font("Arial", Font.PLAIN, 8));
            g2d.drawString(alphaBeta, nodo.x + RADIO_NODO + 5, nodo.y);
        }
    }

    /**
     * Clase interna para representar un nodo del árbol
     */
    public static class NodoArbol {
        public int x, y;
        public int[] movimiento;
        public int valor;
        public boolean esMax;
        public boolean esMejor;
        public boolean esPoda;
        public int alpha;
        public int beta;
        public List<NodoArbol> hijos;

        public NodoArbol(int[] movimiento, int valor, boolean esMax) {
            this.movimiento = movimiento;
            this.valor = valor;
            this.esMax = esMax;
            this.esMejor = false;
            this.esPoda = false;
            this.alpha = Integer.MIN_VALUE;
            this.beta = Integer.MAX_VALUE;
            this.hijos = new ArrayList<>();
        }

        public void agregarHijo(NodoArbol hijo) {
            this.hijos.add(hijo);
        }
    }
}