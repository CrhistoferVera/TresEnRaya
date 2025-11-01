package org.tresenraya;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;

import org.tresenraya.model.AlfaBeta;
import org.tresenraya.model.Minimax;
import org.tresenraya.model.Tablero;

/**
 * Interfaz gráfica con visualización del árbol de decisión en la UI
 */
public class JuegoGUI extends JFrame {
    private Tablero tablero;
    private char jugadorHumano;
    private char jugadorIA;
    private String algoritmo;
    private boolean turnoHumano;

    private JButton[][] botones;
    private JLabel lblEstado;
    private JLabel lblEstadisticas;
    private JTextArea txtLog;
    private JTextArea txtArbol; // NUEVO: Área para mostrar el árbol
    private JPanel panelTablero;

    private int nodosExplorados = 0;
    private int nodosPoados = 0;
    private long tiempoTotal = 0;
    private int movimientos = 0;

    public JuegoGUI() {
        configurarVentana();
        mostrarConfiguracionInicial();
    }

    private void configurarVentana() {
        setTitle("Tres en Raya - IA con Minimax/Alfa-Beta + Simetria");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800); // Ventana más grande
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Panel superior - Estado
        JPanel panelSuperior = new JPanel(new GridLayout(2, 1));
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));

        lblEstado = new JLabel("Bienvenido al Tres en Raya", SwingConstants.CENTER);
        lblEstado.setFont(new Font("Arial", Font.BOLD, 18));
        lblEstado.setForeground(new Color(41, 128, 185));

        lblEstadisticas = new JLabel("Movimientos: 0 | Tiempo IA: 0 ms", SwingConstants.CENTER);
        lblEstadisticas.setFont(new Font("Arial", Font.PLAIN, 12));

        panelSuperior.add(lblEstado);
        panelSuperior.add(lblEstadisticas);

        // Panel central - Tablero
        panelTablero = new JPanel(new GridLayout(3, 3, 5, 5));
        panelTablero.setBorder(BorderFactory.createEmptyBorder(10, 50, 10, 50));
        panelTablero.setBackground(new Color(236, 240, 241));
        panelTablero.setPreferredSize(new Dimension(400, 400));

        botones = new JButton[3][3];
        inicializarBotones();

        // Panel derecho - Log y árbol
        JPanel panelDerecho = new JPanel(new BorderLayout(5, 5));
        panelDerecho.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 10));
        panelDerecho.setPreferredSize(new Dimension(700, 0));

        // Dividir el panel derecho en dos secciones
        JPanel panelSuperiorDerecho = new JPanel(new BorderLayout(5, 5));
        JPanel panelInferiorDerecho = new JPanel(new BorderLayout(5, 5));

        // Sección de Log
        JLabel lblLog = new JLabel("Registro del Juego");
        lblLog.setFont(new Font("Arial", Font.BOLD, 14));

        txtLog = new JTextArea();
        txtLog.setEditable(false);
        txtLog.setFont(new Font("Monospaced", Font.PLAIN, 11));
        txtLog.setLineWrap(true);
        txtLog.setWrapStyleWord(true);
        JScrollPane scrollLog = new JScrollPane(txtLog);
        scrollLog.setPreferredSize(new Dimension(0, 150));

        panelSuperiorDerecho.add(lblLog, BorderLayout.NORTH);
        panelSuperiorDerecho.add(scrollLog, BorderLayout.CENTER);

        // NUEVO: Sección del Árbol de Decisión
        JLabel lblArbol = new JLabel("🌳 Árbol de Decisión (Última jugada IA)");
        lblArbol.setFont(new Font("Arial", Font.BOLD, 14));
        lblArbol.setForeground(new Color(41, 128, 185));

        txtArbol = new JTextArea();
        txtArbol.setEditable(false);
        txtArbol.setFont(new Font("Monospaced", Font.PLAIN, 10));
        txtArbol.setLineWrap(false);
        txtArbol.setBackground(new Color(250, 250, 250));
        JScrollPane scrollArbol = new JScrollPane(txtArbol);

        panelInferiorDerecho.add(lblArbol, BorderLayout.NORTH);
        panelInferiorDerecho.add(scrollArbol, BorderLayout.CENTER);

        // Botones de control
        JPanel panelBotones = new JPanel(new GridLayout(2, 1, 5, 5));

        JButton btnNuevoJuego = new JButton("Nuevo Juego");
        btnNuevoJuego.addActionListener(e -> nuevoJuego());

        JButton btnSalir = new JButton("Salir");
        btnSalir.addActionListener(e -> System.exit(0));

        panelBotones.add(btnNuevoJuego);
        panelBotones.add(btnSalir);

        panelInferiorDerecho.add(panelBotones, BorderLayout.SOUTH);

        // Combinar paneles derechos
        JPanel panelDerechoCompleto = new JPanel(new GridLayout(2, 1, 5, 5));
        panelDerechoCompleto.add(panelSuperiorDerecho);
        panelDerechoCompleto.add(panelInferiorDerecho);

        // Agregar todo
        add(panelSuperior, BorderLayout.NORTH);
        add(panelTablero, BorderLayout.CENTER);
        add(panelDerechoCompleto, BorderLayout.EAST);
    }

    private void inicializarBotones() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                botones[i][j] = new JButton("");
                botones[i][j].setFont(new Font("Arial", Font.BOLD, 60));
                botones[i][j].setFocusPainted(false);
                botones[i][j].setBackground(Color.WHITE);

                final int fila = i;
                final int col = j;

                botones[i][j].addActionListener(e -> {
                    if (turnoHumano) {
                        jugarHumano(fila, col);
                    }
                });

                panelTablero.add(botones[i][j]);
            }
        }
    }

    private void mostrarConfiguracionInicial() {
        // Elegir símbolo
        String[] opciones = {"X (juego primero)", "O (IA juega primero)"};
        int eleccion = JOptionPane.showOptionDialog(this,
                "¿Con que simbolo quieres jugar?",
                "Configuracion - Simbolo",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                opciones,
                opciones[0]);

        if (eleccion == -1) System.exit(0);

        jugadorHumano = (eleccion == 0) ? 'X' : 'O';
        jugadorIA = (eleccion == 0) ? 'O' : 'X';

        // Elegir algoritmo
        String[] algoritmos = {"Minimax (explora todo)", "Alfa-Beta (con podas)"};
        eleccion = JOptionPane.showOptionDialog(this,
                "¿Que algoritmo debe usar la IA?",
                "Configuracion - Algoritmo",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                algoritmos,
                algoritmos[1]);

        if (eleccion == -1) System.exit(0);

        algoritmo = (eleccion == 0) ? "minimax" : "alfabeta";

        // Inicializar juego
        iniciarJuego();

        agregarLog("Configuracion completa:");
        agregarLog("   Tu simbolo: " + jugadorHumano);
        agregarLog("   IA simbolo: " + jugadorIA);
        agregarLog("   Algoritmo: " + algoritmo.toUpperCase() + " + SIMETRIA");
        agregarLog("━━━━━━━━━━━━━━━━━━━━━━━━━━");

        setVisible(true);
    }

    private void iniciarJuego() {
        tablero = new Tablero();
        turnoHumano = (jugadorHumano == 'X');
        movimientos = 0;
        nodosExplorados = 0;
        nodosPoados = 0;
        tiempoTotal = 0;

        actualizarTablero();
        actualizarEstado();

        // Limpiar el área del árbol
        txtArbol.setText("El árbol de decisión se mostrará aquí cuando la IA juegue");

        if (!turnoHumano) {
            Timer timer = new Timer(500, e -> {
                jugarIA();
                ((Timer)e.getSource()).stop();
            });
            timer.setRepeats(false);
            timer.start();
        }
    }

    private void jugarHumano(int fila, int col) {
        if (!turnoHumano) return;

        if (!tablero.esMovimientoValido(fila, col)) {
            JOptionPane.showMessageDialog(this,
                    "Esa casilla ya esta ocupada",
                    "Movimiento invalido",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        tablero.hacerMovimiento(fila, col, jugadorHumano);
        movimientos++;
        agregarLog("Jugaste en (" + fila + ", " + col + ")");

        actualizarTablero();

        if (verificarFinJuego()) return;

        turnoHumano = false;
        actualizarEstado();

        Timer timer = new Timer(800, e -> {
            jugarIA();
            ((Timer)e.getSource()).stop();
        });
        timer.setRepeats(false);
        timer.start();
    }

    private void jugarIA() {
        agregarLog("\nTurno de la IA...");
        lblEstado.setText("La IA esta pensando...");

        // NUEVO: Capturar la salida del árbol
        StringBuilder arbolTexto = new StringBuilder();

        long inicio = System.currentTimeMillis();
        int[] mov;

        if (algoritmo.equals("minimax")) {
            mov = Minimax.mejorMovimiento(tablero, jugadorIA, jugadorHumano,
                    true, true);
            arbolTexto.append("MINIMAX - Sin información de árbol disponible\n");
            arbolTexto.append("(El árbol detallado solo está disponible con Alfa-Beta)");
        } else {
            // Capturar la salida de Alfa-Beta
            mov = AlfaBeta.mejorMovimientoAlfaBetaConCaptura(
                    tablero, jugadorIA, jugadorHumano,
                    true, true, true, arbolTexto);
        }

        long tiempo = System.currentTimeMillis() - inicio;
        tiempoTotal += tiempo;

        tablero.hacerMovimiento(mov[0], mov[1], jugadorIA);
        movimientos++;

        agregarLog("IA jugo en (" + mov[0] + ", " + mov[1] + ")");
        agregarLog("   Tiempo: " + tiempo + " ms");

        // NUEVO: Mostrar el árbol en la UI
        txtArbol.setText(arbolTexto.toString());
        txtArbol.setCaretPosition(0); // Scroll al inicio

        actualizarTablero();

        if (verificarFinJuego()) return;

        turnoHumano = true;
        actualizarEstado();
    }

    private void actualizarTablero() {
        char[][] matriz = tablero.getMatriz();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                char c = matriz[i][j];

                if (c == 'X') {
                    botones[i][j].setText("X");
                    botones[i][j].setForeground(new Color(231, 76, 60));
                    botones[i][j].setEnabled(false);
                } else if (c == 'O') {
                    botones[i][j].setText("O");
                    botones[i][j].setForeground(new Color(52, 152, 219));
                    botones[i][j].setEnabled(false);
                } else {
                    botones[i][j].setText("");
                    botones[i][j].setEnabled(true);
                    botones[i][j].setBackground(Color.WHITE);
                }
            }
        }

        actualizarEstadisticas();
    }

    private void actualizarEstado() {
        if (turnoHumano) {
            lblEstado.setText("Tu turno (" + jugadorHumano + ") - Haz clic en una casilla");
            lblEstado.setForeground(new Color(46, 204, 113));
        } else {
            lblEstado.setText("Turno de la IA (" + jugadorIA + ")");
            lblEstado.setForeground(new Color(52, 152, 219));
        }
    }

    private void actualizarEstadisticas() {
        lblEstadisticas.setText(String.format(
                "Movimientos: %d | Tiempo total IA: %d ms",
                movimientos, tiempoTotal
        ));
    }

    private boolean verificarFinJuego() {
        if (tablero.hayGanador(jugadorHumano)) {
            mostrarFinJuego("FELICITACIONES! HAS GANADO!", "Victoria", true);
            return true;
        }

        if (tablero.hayGanador(jugadorIA)) {
            mostrarFinJuego("La IA ha ganado. Intentalo de nuevo!", "Derrota", false);
            return true;
        }

        if (tablero.tableroLleno()) {
            mostrarFinJuego("EMPATE! Ambos jugaron muy bien.", "Empate", false);
            return true;
        }

        return false;
    }

    private void mostrarFinJuego(String mensaje, String titulo, boolean ganaste) {
        deshabilitarBotones();
        lblEstado.setText(mensaje);

        if (ganaste) {
            lblEstado.setForeground(new Color(39, 174, 96));
        } else {
            lblEstado.setForeground(new Color(192, 57, 43));
        }

        agregarLog("\n" + "━".repeat(30));
        agregarLog(mensaje);
        agregarLog("━".repeat(30));

        String estadisticas = String.format(
                "%s\n\nEstadisticas finales:\n" +
                        "   Movimientos totales: %d\n" +
                        "   Tiempo total IA: %d ms\n" +
                        "   Algoritmo: %s + SIMETRIA\n\n" +
                        "¿Quieres jugar otra partida?",
                mensaje, movimientos, tiempoTotal, algoritmo.toUpperCase()
        );

        int opcion = JOptionPane.showConfirmDialog(this,
                estadisticas,
                titulo,
                JOptionPane.YES_NO_OPTION,
                ganaste ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.PLAIN_MESSAGE);

        if (opcion == JOptionPane.YES_OPTION) {
            nuevoJuego();
        }
    }

    private void deshabilitarBotones() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                botones[i][j].setEnabled(false);
            }
        }
    }

    private void nuevoJuego() {
        txtLog.setText("");
        txtArbol.setText("");
        limpiarBotones();
        mostrarConfiguracionInicial();
    }

    private void limpiarBotones() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                botones[i][j].setText("");
                botones[i][j].setEnabled(true);
                botones[i][j].setBackground(Color.WHITE);
            }
        }
    }

    private void agregarLog(String mensaje) {
        txtLog.append(mensaje + "\n");
        txtLog.setCaretPosition(txtLog.getDocument().getLength());
    }

    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════╗");
        System.out.println("║  TRES EN RAYA - INTERFAZ GUI         ║");
        System.out.println("║   IA con Minimax/Alfa-Beta           ║");
        System.out.println("║   + SIMETRIA                         ║");
        System.out.println("╚═══════════════════════════════════════╝\n");

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new JuegoGUI();
        });
    }
}