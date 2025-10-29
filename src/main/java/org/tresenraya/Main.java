package org.tresenraya;

import java.util.Scanner;

/**
 * Menú principal unificado con todas las opciones
 */
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("╔═══════════════════════════════════════════════════════╗");
        System.out.println("║       🎮 TRES EN RAYA - MENÚ PRINCIPAL 🎮            ║");
        System.out.println("║          Con Minimax, Alfa-Beta y Simetría           ║");
        System.out.println("╚═══════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("1. 🎮 Jugar contra la IA (Interfaz de texto)");
        System.out.println("2. 🖥️  Jugar contra la IA (Interfaz gráfica)");
        System.out.println("3. 🌳 Visualizador de árboles (Demo completa)");
        System.out.println("4. 📊 Comparador de algoritmos");
        System.out.println("5. ❌ Salir");
        System.out.print("\nElige una opción: ");

        try {
            int opcion = scanner.nextInt();
            scanner.nextLine(); // Limpiar buffer

            switch (opcion) {
                case 1:
                    System.out.println("\n🎮 Iniciando juego en modo texto...\n");
                    JuegoInteractivo juego = new JuegoInteractivo();
                    juego.iniciar();
                    break;
                    
                case 2:
                    System.out.println("\n🖥️  Iniciando interfaz gráfica...\n");
                    javax.swing.SwingUtilities.invokeLater(() -> {
                        new JuegoGUI();
                    });
                    System.out.println("✅ Ventana de juego abierta");
                    System.out.println("💡 Cierra esta consola cuando termines de jugar");
                    break;
                    
                case 3:
                    System.out.println("\n🌳 Abriendo visualizador de árboles...\n");
                    DemoCompleta.main(new String[]{});
                    break;
                    
                case 4:
                    System.out.println("\n📊 Abriendo comparador...\n");
                    ComparadorArboles.main(new String[]{});
                    break;
                    
                case 5:
                    System.out.println("\n👋 ¡Hasta pronto!");
                    break;
                    
                default:
                    System.out.println("❌ Opción inválida");
            }
        } catch (Exception e) {
            System.out.println("❌ Entrada inválida");
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
}