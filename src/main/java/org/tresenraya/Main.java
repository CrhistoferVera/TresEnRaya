package org.tresenraya;

import java.util.Scanner;

/**
 * Menú principal simplificado - Solo interfaz gráfica
 */
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("╔═══════════════════════════════════════════════════════╗");
        System.out.println("║       🎮 TRES EN RAYA - MENÚ PRINCIPAL 🎮            ║");
        System.out.println("║          Con Minimax, Alfa-Beta y Simetría           ║");
        System.out.println("╚═══════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("1. 🖥️  Jugar contra la IA (Interfaz gráfica)");
        System.out.println("2. ❌ Salir");
        System.out.print("\nElige una opción: ");

        try {
            int opcion = scanner.nextInt();
            scanner.nextLine(); // Limpiar buffer

            switch (opcion) {
                case 1:
                    System.out.println("\n🖥️  Iniciando interfaz gráfica...\n");
                    javax.swing.SwingUtilities.invokeLater(() -> {
                        new JuegoGUI();
                    });
                    System.out.println("✅ Ventana de juego abierta");
                    System.out.println("🌳 El árbol se mostrará automáticamente después de cada jugada de la IA");
                    System.out.println("💡 Cierra esta consola cuando termines de jugar");
                    
                    // Mantener el programa corriendo
                    System.out.println("\nPresiona ENTER para salir...");
                    scanner.nextLine();
                    break;
                    
                case 2:
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