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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.tresenraya.model.Nodo;
import org.tresenraya.model.VisualizadorArbol;

/**
 * Visualizador de árbol MEJORADO con tableros visuales y heurísticas destacadas
 */
public class VisualizadorArbolGrafico extends JFrame {
    private PanelArbol panelArbol;

    public VisualizadorArbolGrafico(String algoritmo) {
        setTitle("🌳 Árbol de Decisión Minimax - " + algoritmo);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1400, 900);
        setLocationRelativeTo(null);

        panelArbol = new PanelArbol(VisualizadorArbol.getRaiz());
        
        JScrollPane scrollPane = new JScrollPane(panelArbol);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(16);
        
        add(scrollPane);

        // Panel de información
        JPanel panelInfo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelInfo.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        panelInfo.setBackground(new Color(240, 240, 255));
        
        JLabel lblInfo = new JLabel(String.format(
            "📊 Nodos explorados: %d | ✂️ Nodos podados: %d | Algoritmo: %s",
            VisualizadorArbol.getNodosExplorados(),
            VisualizadorArbol.getNodosPodados(),
            algoritmo.toUpperCase()
        ));
        lblInfo.setFont(new Font("Arial", Font.BOLD, 14));
        panelInfo.add(lblInfo);

        // Leyenda
        JPanel panelLeyenda = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelLeyenda.setBackground(new Color(240, 240, 255));
        panelLeyenda.add(new JLabel("🔵 MAX (IA)  "));
        panelLeyenda.add(new JLabel("🟢 MIN (Oponente)  "));
        panelLeyenda.add(new JLabel("🟡 Terminal  "));
        panelLeyenda.add(new JLabel("🔴 Podado"));
        panelInfo.add(panelLeyenda);

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
    private Map<Nodo, char[][]> tablerosNodos; // NUEVO: tableros de cada nodo
    
    private static final int ANCHO_NODO = 180;
    private static final int ALTO_NODO = 160;
    private static final int ESPACIO_VERTICAL = 140;
    private static final int ESPACIO_HORIZONTAL = 40;
    private static final int TAMAÑO_CELDA = 25;

    public PanelArbol(Nodo raiz) {
        this.raiz = raiz;
        this.posiciones = new HashMap<>();
        this.tablerosNodos = new HashMap<>();
        setBackground(Color.WHITE);
        
        if (raiz != null) {
            extraerTableros(raiz);
            calcularPosiciones();
        }
    }

    /**
     * Extrae los tableros simulando los movimientos del árbol
     */
    private void extraerTableros(Nodo nodo) {
        if (nodo == null) return;
        
        // Simular tablero para este nodo
        char[][] tablero = simularTableroDesdeRaiz(nodo);
        tablerosNodos.put(nodo, tablero);
        
        // Recursivo para hijos
        for (Nodo hijo : nodo.getHijos()) {
            extraerTableros(hijo);
        }
    }

    /**
     * Simula el tablero siguiendo la ruta desde la raíz hasta este nodo
     */
    private char[][] simularTableroDesdeRaiz(Nodo nodo) {
        // Crear tablero vacío
        char[][] tablero = new char[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                tablero[i][j] = '-';
            }
        }
        
        // Obtener ruta desde raíz
        List<Nodo> ruta = new ArrayList<>();
        Nodo actual = nodo;
        while (actual != null) {
            ruta.add(0, actual);
            actual = encontrarPadre(raiz, actual);
        }
        
        // Aplicar movimientos
        for (Nodo n : ruta) {
            if (n.getFila() >= 0 && n.getColumna() >= 0) {
                tablero[n.getFila()][n.getColumna()] = n.getJugador();
            }
        }
        
        return tablero;
    }

    /**
     * Encuentra el padre de un nodo en el árbol
     */
    private Nodo encontrarPadre(Nodo raiz, Nodo objetivo) {
        if (raiz == null || raiz == objetivo) return null;
        
        for (Nodo hijo : raiz.getHijos()) {
            if (hijo == objetivo) return raiz;
            Nodo resultado = encontrarPadre(hijo, objetivo);
            if (resultado != null) return resultado;
        }
        return null;
    }

    private void calcularPosiciones() {
        if (raiz == null) return;
        
        // Calcular anchos por nivel
        Map<Integer, Integer> anchosPorNivel = calcularAnchosPorNivel(raiz);
        
        int[] contadorX = {50};
        calcularPosicionesRecursivo(raiz, contadorX, 50, anchosPorNivel);
        
        int maxX = 0, maxY = 0;
        for (Point p : posiciones.values()) {
            maxX = Math.max(maxX, p.x + ANCHO_NODO + 50);
            maxY = Math.max(maxY, p.y + ALTO_NODO + 50);
        }
        setPreferredSize(new Dimension(maxX, maxY));
    }

    private Map<Integer, Integer> calcularAnchosPorNivel(Nodo nodo) {
        Map<Integer, Integer> anchos = new HashMap<>();
        calcularAnchosPorNivelRecursivo(nodo, 0, anchos);
        return anchos;
    }

    private void calcularAnchosPorNivelRecursivo(Nodo nodo, int nivel, Map<Integer, Integer> anchos) {
        if (nodo == null) return;
        
        anchos.put(nivel, anchos.getOrDefault(nivel, 0) + 1);
        
        for (Nodo hijo : nodo.getHijos()) {
            calcularAnchosPorNivelRecursivo(hijo, nivel + 1, anchos);
        }
    }

    private void calcularPosicionesRecursivo(Nodo nodo, int[] contadorX, int y, 
                                            Map<Integer, Integer> anchosPorNivel) {
        if (nodo == null) return;

        int x = contadorX[0];
        posiciones.put(nodo, new Point(x, y));
        contadorX[0] += ANCHO_NODO + ESPACIO_HORIZONTAL;

        int yHijo = y + ALTO_NODO + ESPACIO_VERTICAL;
        for (Nodo hijo : nodo.getHijos()) {
            calcularPosicionesRecursivo(hijo, contadorX, yHijo, anchosPorNivel);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

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
                    g2d.setStroke(new BasicStroke(3, BasicStroke.CAP_BUTT, 
                                BasicStroke.JOIN_BEVEL, 0, new float[]{8}, 0));
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
        Color colorBorde;
        
        if (nodo.isPodado()) {
            colorFondo = new Color(255, 220, 220);
            colorBorde = new Color(231, 76, 60);
        } else if (nodo.isTerminal()) {
            colorFondo = new Color(255, 250, 205);
            colorBorde = new Color(241, 196, 15);
        } else if (nodo.getTipo().equals("MAX")) {
            colorFondo = new Color(220, 237, 255);
            colorBorde = new Color(52, 152, 219);
        } else {
            colorFondo = new Color(220, 255, 237);
            colorBorde = new Color(46, 204, 113);
        }

        // Fondo del nodo
        g2d.setColor(colorFondo);
        g2d.fillRoundRect(x, y, ANCHO_NODO, ALTO_NODO, 15, 15);
        
        // Borde
        g2d.setColor(colorBorde);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRoundRect(x, y, ANCHO_NODO, ALTO_NODO, 15, 15);

        // DIBUJAR TABLERO en la parte superior del nodo
        char[][] tablero = tablerosNodos.get(nodo);
        if (tablero != null) {
            dibujarMiniTablero(g2d, tablero, x + 10, y + 10);
        }

        // Información del nodo (debajo del tablero)
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 11));
        
        int yTexto = y + 95;
        
        // Tipo de nodo
        String tipo = nodo.getTipo();
        if (nodo.isTerminal()) {
            tipo = "TERMINAL";
        }
        g2d.drawString(tipo, x + 10, yTexto);
        yTexto += 15;
        
        // Movimiento
        if (nodo.getFila() >= 0) {
            g2d.setFont(new Font("Arial", Font.PLAIN, 10));
            g2d.drawString("Mov: (" + nodo.getFila() + "," + nodo.getColumna() + ")", x + 10, yTexto);
            yTexto += 15;
        }
        
        // Valor / Heurística
        g2d.setFont(new Font("Arial", Font.BOLD, 13));
        g2d.setColor(new Color(0, 102, 204));
        g2d.drawString("H = " + nodo.getValor(), x + 10, yTexto);

        // Icono de podado
        if (nodo.isPodado()) {
            g2d.setColor(new Color(231, 76, 60));
            g2d.setFont(new Font("Arial", Font.BOLD, 20));
            g2d.drawString("✂", x + ANCHO_NODO - 30, y + 25);
        }
        
        // Estado terminal
        if (nodo.isTerminal() && nodo.getEstadoTerminal() != null) {
            g2d.setColor(new Color(200, 150, 0));
            g2d.setFont(new Font("Arial", Font.PLAIN, 9));
            String estado = nodo.getEstadoTerminal();
            if (estado.length() > 20) estado = estado.substring(0, 17) + "...";
            g2d.drawString(estado, x + 5, y + ALTO_NODO - 5);
        }
    }

    /**
     * Dibuja un tablero mini de 3x3 dentro del nodo
     */
    private void dibujarMiniTablero(Graphics2D g2d, char[][] tablero, int x, int y) {
        // Fondo del tablero
        g2d.setColor(new Color(240, 240, 240));
        g2d.fillRect(x, y, TAMAÑO_CELDA * 3 + 4, TAMAÑO_CELDA * 3 + 4);
        
        // Líneas de cuadrícula
        g2d.setColor(new Color(180, 180, 180));
        g2d.setStroke(new BasicStroke(2));
        
        for (int i = 0; i <= 3; i++) {
            // Líneas horizontales
            g2d.drawLine(x, y + i * TAMAÑO_CELDA, x + TAMAÑO_CELDA * 3, y + i * TAMAÑO_CELDA);
            // Líneas verticales
            g2d.drawLine(x + i * TAMAÑO_CELDA, y, x + i * TAMAÑO_CELDA, y + TAMAÑO_CELDA * 3);
        }
        
        // Dibujar símbolos
        g2d.setFont(new Font("Arial", Font.BOLD, 18));
        FontMetrics fm = g2d.getFontMetrics();
        
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                char simbolo = tablero[i][j];
                if (simbolo != '-') {
                    String str = String.valueOf(simbolo);
                    
                    // Color según jugador
                    if (simbolo == 'X') {
                        g2d.setColor(new Color(231, 76, 60)); // Rojo
                    } else if (simbolo == 'O') {
                        g2d.setColor(new Color(52, 152, 219)); // Azul
                    }
                    
                    // Centrar el símbolo en la celda
                    int xCentro = x + j * TAMAÑO_CELDA + (TAMAÑO_CELDA - fm.stringWidth(str)) / 2;
                    int yCentro = y + i * TAMAÑO_CELDA + (TAMAÑO_CELDA + fm.getAscent() - fm.getDescent()) / 2;
                    
                    g2d.drawString(str, xCentro, yCentro);
                }
            }
        }
    }
}