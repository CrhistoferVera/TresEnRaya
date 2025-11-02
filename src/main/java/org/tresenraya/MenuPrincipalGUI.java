package org.tresenraya;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Menú principal para seleccionar el algoritmo de IA
 */
public class MenuPrincipalGUI extends JFrame {

    public MenuPrincipalGUI() {
        configurarVentana();
    }

    private void configurarVentana() {
        setTitle("Tres en Raya - Selección de Algoritmo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null);
        setResizable(false);

        // Panel principal con fondo
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBackground(new Color(236, 240, 241));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // Panel superior - Título
        JPanel panelTitulo = crearPanelTitulo();

        // Panel central - Botones
        JPanel panelBotones = crearPanelBotones();

        // Panel inferior - Información
        JPanel panelInfo = crearPanelInfo();

        panelPrincipal.add(panelTitulo, BorderLayout.NORTH);
        panelPrincipal.add(panelBotones, BorderLayout.CENTER);
        panelPrincipal.add(panelInfo, BorderLayout.SOUTH);

        add(panelPrincipal);
    }

    private JPanel crearPanelTitulo() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(236, 240, 241));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 30, 10));

        // Título principal
        JLabel lblTitulo = new JLabel("🎮 TRES EN RAYA");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 36));
        lblTitulo.setForeground(new Color(41, 128, 185));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Subtítulo
        JLabel lblSubtitulo = new JLabel("Inteligencia Artificial");
        lblSubtitulo.setFont(new Font("Arial", Font.PLAIN, 18));
        lblSubtitulo.setForeground(new Color(52, 73, 94));
        lblSubtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Instrucción
        JLabel lblInstruccion = new JLabel("Selecciona el algoritmo que deseas usar:");
        lblInstruccion.setFont(new Font("Arial", Font.ITALIC, 14));
        lblInstruccion.setForeground(new Color(127, 140, 141));
        lblInstruccion.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(lblTitulo);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(lblSubtitulo);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(lblInstruccion);

        return panel;
    }

    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(236, 240, 241));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // Botón Minimax
        JButton btnMinimax = crearBotonAlgoritmo(
                "🌳 MINIMAX",
                "Explora todo el árbol de decisión",
                "Más exhaustivo pero más lento",
                new Color(52, 152, 219),
                new Color(41, 128, 185)
        );
        btnMinimax.addActionListener(e -> abrirJuegoMinimax());
        btnMinimax.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Botón Alfa-Beta
        JButton btnAlfaBeta = crearBotonAlgoritmo(
                "✂️ ALFA-BETA",
                "Optimización con podas inteligentes",
                "Más eficiente y rápido",
                new Color(46, 204, 113),
                new Color(39, 174, 96)
        );
        btnAlfaBeta.addActionListener(e -> abrirJuegoAlfaBeta());
        btnAlfaBeta.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Botón Salir
        JButton btnSalir = crearBotonSalir();
        btnSalir.addActionListener(e -> System.exit(0));
        btnSalir.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(btnMinimax);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(btnAlfaBeta);
        panel.add(Box.createRigidArea(new Dimension(0, 25)));
        panel.add(btnSalir);

        return panel;
    }

    private JButton crearBotonAlgoritmo(String titulo, String descripcion, String ventaja,
                                        Color colorBase, Color colorHover) {
        JButton btn = new JButton();
        btn.setLayout(new BorderLayout(10, 5));
        btn.setPreferredSize(new Dimension(480, 85));
        btn.setMaximumSize(new Dimension(480, 85));
        btn.setMinimumSize(new Dimension(480, 85));
        btn.setBackground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(colorBase, 2, true),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        // Panel interno con la información
        JPanel panelInterno = new JPanel();
        panelInterno.setLayout(new BoxLayout(panelInterno, BoxLayout.Y_AXIS));
        panelInterno.setOpaque(false);

        // Título
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setForeground(colorBase);
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Descripción
        JLabel lblDescripcion = new JLabel(descripcion);
        lblDescripcion.setFont(new Font("Arial", Font.PLAIN, 13));
        lblDescripcion.setForeground(new Color(52, 73, 94));
        lblDescripcion.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Ventaja
        JLabel lblVentaja = new JLabel("✓ " + ventaja);
        lblVentaja.setFont(new Font("Arial", Font.ITALIC, 11));
        lblVentaja.setForeground(new Color(127, 140, 141));
        lblVentaja.setAlignmentX(Component.LEFT_ALIGNMENT);

        panelInterno.add(lblTitulo);
        panelInterno.add(Box.createRigidArea(new Dimension(0, 5)));
        panelInterno.add(lblDescripcion);
        panelInterno.add(Box.createRigidArea(new Dimension(0, 3)));
        panelInterno.add(lblVentaja);

        btn.add(panelInterno, BorderLayout.CENTER);

        // Efectos hover
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(245, 247, 250));
                btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(colorHover, 3, true),
                        BorderFactory.createEmptyBorder(12, 15, 12, 15)
                ));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(Color.WHITE);
                btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(colorBase, 2, true),
                        BorderFactory.createEmptyBorder(12, 15, 12, 15)
                ));
            }
        });

        return btn;
    }

    private JButton crearBotonSalir() {
        JButton btn = new JButton("❌ Salir");
        btn.setFont(new Font("Arial", Font.PLAIN, 14));
        btn.setPreferredSize(new Dimension(200, 40));
        btn.setMaximumSize(new Dimension(200, 40));
        btn.setMinimumSize(new Dimension(200, 40));
        btn.setBackground(new Color(231, 76, 60));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(192, 57, 43));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(231, 76, 60));
            }
        });

        return btn;
    }

    private JPanel crearPanelInfo() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(236, 240, 241));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 10, 5, 10));

        JLabel lblInfo = new JLabel("<html><center>💡 Ambos algoritmos utilizan detección de simetría<br>" +
                "para optimizar el análisis del tablero</center></html>");
        lblInfo.setFont(new Font("Arial", Font.PLAIN, 11));
        lblInfo.setForeground(new Color(127, 140, 141));
        lblInfo.setHorizontalAlignment(SwingConstants.CENTER);

        panel.add(lblInfo, BorderLayout.CENTER);

        return panel;
    }

    private void abrirJuegoMinimax() {
        System.out.println("\n🌳 Iniciando juego con algoritmo MINIMAX...");
        SwingUtilities.invokeLater(() -> {
            new JuegoGUI();
        });
        dispose();
    }

    private void abrirJuegoAlfaBeta() {
        System.out.println("\n✂️ Iniciando juego con algoritmo ALFA-BETA...");
        SwingUtilities.invokeLater(() -> {
            new JuegoGUIAlfaBeta();
        });
        dispose();
    }

    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════╗");
        System.out.println("║       🎮 TRES EN RAYA - MENÚ PRINCIPAL 🎮            ║");
        System.out.println("║          Con Minimax, Alfa-Beta y Simetría           ║");
        System.out.println("╚═══════════════════════════════════════════════════════╝\n");
        System.out.println("💡 Selecciona un algoritmo en la ventana que se abrirá...\n");

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            MenuPrincipalGUI menu = new MenuPrincipalGUI();
            menu.setVisible(true);
        });
    }
}