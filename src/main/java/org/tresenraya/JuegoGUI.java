package org.tresenraya;

import org.tresenraya.model.AlfaBeta;
import org.tresenraya.model.Minimax;
import org.tresenraya.model.Tablero;
import org.tresenraya.model.VisualizadorArbol;
import org.tresenraya.VisualizadorArbolGrafico; // ← AGREGAR ESTE

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Interfaz gráfica completa para Tres en Raya con IA
 */
public class JuegoGUI extends JFrame {
    private Tablero tablero;
    private char jugadorHumano;
    private char jugadorIA;
    private String algoritmo;
    private boolean mostrarArbol;
    private boolean turnoHumano;

    private JButton[][] botones;
    private JLabel lblEstado;
    private JLabel lblEstadisticas;
    private JTextArea txtLog;
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
        setTitle("🎮 Tres en Raya - IA con Minimax/Alfa-Beta");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
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

        botones = new JButton[3][3];
        inicializarBotones();

        // Panel derecho - Log y controles
        JPanel panelDerecho = new JPanel(new BorderLayout(5, 5));
        panelDerecho.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 10));
        panelDerecho.setPreferredSize(new Dimension(300, 0));

        JLabel lblLog = new JLabel("📊 Registro del Juego");
        lblLog.setFont(new Font("Arial", Font.BOLD, 14));

        txtLog = new JTextArea();
        txtLog.setEditable(false);
        txtLog.setFont(new Font("Monospaced", Font.PLAIN, 11));
        txtLog.setLineWrap(true);
        txtLog.setWrapStyleWord(true);
        JScrollPane scrollLog = new JScrollPane(txtLog);

        JPanel panelBotones = new JPanel(new GridLayout(3, 1, 5, 5));

        JButton btnNuevoJuego = new JButton("🔄 Nuevo Juego");
        btnNuevoJuego.addActionListener(e -> nuevoJuego());

        JButton btnVerArbol = new JButton("🌳 Ver Árbol");
        btnVerArbol.addActionListener(e -> toggleArbol());

        JButton btnSalir = new JButton("❌ Salir");
        btnSalir.addActionListener(e -> System.exit(0));

        panelBotones.add(btnNuevoJuego);
        panelBotones.add(btnVerArbol);
        panelBotones.add(btnSalir);

        panelDerecho.add(lblLog, BorderLayout.NORTH);
        panelDerecho.add(scrollLog, BorderLayout.CENTER);
        panelDerecho.add(panelBotones, BorderLayout.SOUTH);

        // Agregar todo
        add(panelSuperior, BorderLayout.NORTH);
        add(panelTablero, BorderLayout.CENTER);
        add(panelDerecho, BorderLayout.EAST);
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

                botones[i][j].addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (turnoHumano) {
                            jugarHumano(fila, col);
                        }
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
                "¿Con qué símbolo quieres jugar?",
                "Configuración - Símbolo",
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
                "¿Qué algoritmo debe usar la IA?",
                "Configuración - Algoritmo",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                algoritmos,
                algoritmos[1]);

        if (eleccion == -1) System.exit(0);

        algoritmo = (eleccion == 0) ? "minimax" : "alfabeta";

        // Mostrar árbol
        eleccion = JOptionPane.showConfirmDialog(this,
                "¿Mostrar árbol de decisión en consola?",
                "Configuración - Visualización",
                JOptionPane.YES_NO_OPTION);

        mostrarArbol = (eleccion == JOptionPane.YES_OPTION);

        // Inicializar juego
        iniciarJuego();

        agregarLog("✅ Configuración completa:");
        agregarLog("   Tu símbolo: " + jugadorHumano);
        agregarLog("   IA símbolo: " + jugadorIA);
        agregarLog("   Algoritmo: " + algoritmo.toUpperCase());
        agregarLog("   Visualización: " + (mostrarArbol ? "Activada" : "Desactivada"));
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

        if (!turnoHumano) {
            // IA juega primero
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
                    "❌ Esa casilla ya está ocupada",
                    "Movimiento inválido",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        tablero.hacerMovimiento(fila, col, jugadorHumano);
        movimientos++;
        agregarLog("🎮 Jugaste en (" + fila + ", " + col + ")");

        actualizarTablero();

        if (verificarFinJuego()) return;

        turnoHumano = false;
        actualizarEstado();

        // IA juega después de un delay
        Timer timer = new Timer(800, e -> {
            jugarIA();
            ((Timer)e.getSource()).stop();
        });
        timer.setRepeats(false);
        timer.start();
    }

    private void jugarIA() {
    agregarLog("\n🤖 Turno de la IA...");
    lblEstado.setText("🤔 La IA está pensando...");

    VisualizadorArbol.reiniciar();

    long inicio = System.currentTimeMillis();
    int[] mov;

    // ⭐ CAMBIO: Siempre pasar TRUE para construir el árbol
    // El parámetro controla si se IMPRIME en consola, pero necesitamos construirlo siempre
    if (algoritmo.equals("minimax")) {
        mov = Minimax.mejorMovimiento(tablero, jugadorIA, jugadorHumano, true); // ← Cambiar aquí
    } else {
        mov = AlfaBeta.mejorMovimientoAlfaBeta(tablero, jugadorIA, jugadorHumano, true); // ← Cambiar aquí
    }

    long tiempo = System.currentTimeMillis() - inicio;
    tiempoTotal += tiempo;

    tablero.hacerMovimiento(mov[0], mov[1], jugadorIA);
    movimientos++;

    nodosExplorados = VisualizadorArbol.getNodosExplorados();
    nodosPoados = VisualizadorArbol.getNodosPodados();

    agregarLog("🤖 IA jugó en (" + mov[0] + ", " + mov[1] + ")");
    agregarLog("   ⏱️ Tiempo: " + tiempo + " ms");
    agregarLog("   📊 Nodos explorados: " + nodosExplorados);
    if (nodosPoados > 0) {
        agregarLog("   ✂️ Nodos podados: " + nodosPoados);
    }

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
                    botones[i][j].setForeground(new Color(231, 76, 60)); // Rojo
                    botones[i][j].setEnabled(false);
                } else if (c == 'O') {
                    botones[i][j].setText("O");
                    botones[i][j].setForeground(new Color(52, 152, 219)); // Azul
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
            lblEstado.setText("🎮 Tu turno (" + jugadorHumano + ") - Haz clic en una casilla");
            lblEstado.setForeground(new Color(46, 204, 113));
        } else {
            lblEstado.setText("🤖 Turno de la IA (" + jugadorIA + ")");
            lblEstado.setForeground(new Color(52, 152, 219));
        }
    }

    private void actualizarEstadisticas() {
        lblEstadisticas.setText(String.format(
                "Movimientos: %d | Tiempo total IA: %d ms | Nodos: %d | Podas: %d",
                movimientos, tiempoTotal, nodosExplorados, nodosPoados
        ));
    }

    private boolean verificarFinJuego() {
        if (tablero.hayGanador(jugadorHumano)) {
            mostrarFinJuego("🎉 ¡FELICITACIONES! ¡HAS GANADO! 🎉", "Victoria", true);
            return true;
        }

        if (tablero.hayGanador(jugadorIA)) {
            mostrarFinJuego("😔 La IA ha ganado. ¡Inténtalo de nuevo!", "Derrota", false);
            return true;
        }

        if (tablero.tableroLleno()) {
            mostrarFinJuego("🤝 ¡EMPATE! Ambos jugaron muy bien.", "Empate", false);
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
                "%s\n\n📊 Estadísticas finales:\n" +
                        "   Movimientos totales: %d\n" +
                        "   Tiempo total IA: %d ms\n" +
                        "   Nodos explorados: %d\n" +
                        "   Nodos podados: %d\n" +
                        "   Algoritmo: %s\n\n" +
                        "¿Quieres jugar otra partida?",
                mensaje, movimientos, tiempoTotal, nodosExplorados, nodosPoados, algoritmo.toUpperCase()
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

private void toggleArbol() {
    if (VisualizadorArbol.getRaiz() != null) {
        VisualizadorArbolGrafico.mostrar(algoritmo);
    } else {
        JOptionPane.showMessageDialog(this,
                "Aún no hay árbol generado.\nLa IA debe jugar al menos una vez.",
                "Sin datos",
                JOptionPane.INFORMATION_MESSAGE);
    }
}

    private void agregarLog(String mensaje) {
        txtLog.append(mensaje + "\n");
        txtLog.setCaretPosition(txtLog.getDocument().getLength());
    }

    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════╗");
        System.out.println("║    🎮 TRES EN RAYA - INTERFAZ GUI    ║");
        System.out.println("║        IA con Minimax/Alfa-Beta       ║");
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
