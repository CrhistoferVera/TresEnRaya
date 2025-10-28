package org.tresenraya;

import org.tresenraya.model.AlfaBeta;
import org.tresenraya.model.Minimax;
import org.tresenraya.model.Tablero;

import java.util.Scanner;

public class ComparadorArboles {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("╔════════════════════════════════════════════════════╗");
        System.out.println("║  🌳 VISUALIZADOR DE ÁRBOLES DE DECISIÓN 🌳        ║");
        System.out.println("╚════════════════════════════════════════════════════╝");
        System.out.println();

        // Crear un tablero de ejemplo
        Tablero tablero = new Tablero();

        System.out.println("Vamos a crear un escenario de juego:");
        System.out.println("¿Quieres usar un tablero vacío o configurar movimientos? (v/c): ");
        String opcion = scanner.nextLine().trim().toLowerCase();

        if (opcion.equals("c")) {
            configurarTablero(tablero, scanner);
        }

        System.out.println("\n📋 Estado actual del tablero:");
        tablero.imprimir();

        // Menú principal
        while (true) {
            System.out.println("\n╔════════════════════════════════════════════════════╗");
            System.out.println("║                    OPCIONES                        ║");
            System.out.println("╠════════════════════════════════════════════════════╣");
            System.out.println("║ 1. Ver árbol de MINIMAX                            ║");
            System.out.println("║ 2. Ver árbol de ALFA-BETA                          ║");
            System.out.println("║ 3. Comparar ambos algoritmos                       ║");
            System.out.println("║ 4. Modificar tablero                               ║");
            System.out.println("║ 5. Reiniciar tablero                               ║");
            System.out.println("║ 6. Salir                                           ║");
            System.out.println("╚════════════════════════════════════════════════════╝");
            System.out.print("Elige una opción: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    ejecutarMinimax(tablero);
                    break;
                case "2":
                    ejecutarAlfaBeta(tablero);
                    break;
                case "3":
                    compararAlgoritmos(tablero);
                    break;
                case "4":
                    configurarTablero(tablero, scanner);
                    System.out.println("\n📋 Nuevo estado del tablero:");
                    tablero.imprimir();
                    break;
                case "5":
                    tablero = new Tablero();
                    System.out.println("\n✅ Tablero reiniciado");
                    tablero.imprimir();
                    break;
                case "6":
                    System.out.println("\n👋 ¡Hasta pronto!");
                    scanner.close();
                    return;
                default:
                    System.out.println("❌ Opción inválida");
            }

            System.out.print("\nPresiona ENTER para continuar...");
            scanner.nextLine();
        }
    }

    private static void configurarTablero(Tablero tablero, Scanner scanner) {
        System.out.println("\n📝 Configurar tablero:");
        System.out.println("Ingresa movimientos en formato: fila columna jugador");
        System.out.println("Ejemplo: 0 0 X (coloca X en la esquina superior izquierda)");
        System.out.println("Escribe 'fin' cuando termines\n");

        while (true) {
            System.out.print("Movimiento (o 'fin'): ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("fin")) {
                break;
            }

            String[] partes = input.split(" ");
            if (partes.length != 3) {
                System.out.println("❌ Formato incorrecto. Usa: fila columna jugador");
                continue;
            }

            try {
                int fila = Integer.parseInt(partes[0]);
                int col = Integer.parseInt(partes[1]);
                char jugador = partes[2].toUpperCase().charAt(0);

                if (fila < 0 || fila > 2 || col < 0 || col > 2) {
                    System.out.println("❌ Fila y columna deben estar entre 0 y 2");
                    continue;
                }

                if (jugador != 'X' && jugador != 'O') {
                    System.out.println("❌ El jugador debe ser X u O");
                    continue;
                }

                if (!tablero.esMovimientoValido(fila, col)) {
                    System.out.println("❌ Esa casilla ya está ocupada");
                    continue;
                }

                tablero.hacerMovimiento(fila, col, jugador);
                System.out.println("✅ Movimiento registrado");
                tablero.imprimir();

            } catch (Exception e) {
                System.out.println("❌ Error en el formato. Intenta de nuevo");
            }
        }
    }

    private static void ejecutarMinimax(Tablero tablero) {
        System.out.println("\n" + "═".repeat(60));
        System.out.println("🔴 EJECUTANDO MINIMAX");
        System.out.println("═".repeat(60));

        long inicio = System.currentTimeMillis();
        int[] mejorMovimiento = Minimax.mejorMovimiento(tablero, 'O', 'X', true);
        long fin = System.currentTimeMillis();

        System.out.println("\n🎯 RESULTADO:");
        System.out.println("   Mejor movimiento: (" + mejorMovimiento[0] + ", " + mejorMovimiento[1] + ")");
        System.out.println("   Tiempo de ejecución: " + (fin - inicio) + " ms");
    }

    private static void ejecutarAlfaBeta(Tablero tablero) {
        System.out.println("\n" + "═".repeat(60));
        System.out.println("🔵 EJECUTANDO ALFA-BETA");
        System.out.println("═".repeat(60));

        long inicio = System.currentTimeMillis();
        int[] mejorMovimiento = AlfaBeta.mejorMovimientoAlfaBeta(tablero, 'O', 'X', true);
        long fin = System.currentTimeMillis();

        System.out.println("\n🎯 RESULTADO:");
        System.out.println("   Mejor movimiento: (" + mejorMovimiento[0] + ", " + mejorMovimiento[1] + ")");
        System.out.println("   Tiempo de ejecución: " + (fin - inicio) + " ms");
    }

    private static void compararAlgoritmos(Tablero tablero) {
        System.out.println("\n" + "═".repeat(60));
        System.out.println("⚖️  COMPARACIÓN: MINIMAX vs ALFA-BETA");
        System.out.println("═".repeat(60));

        // Ejecutar Minimax
        System.out.println("\n🔴 MINIMAX:");
        long inicioMinimax = System.currentTimeMillis();
        int[] movMinimax = Minimax.mejorMovimiento(tablero, 'O', 'X', false);
        long finMinimax = System.currentTimeMillis();
        long tiempoMinimax = finMinimax - inicioMinimax;

        System.out.println("   Movimiento: (" + movMinimax[0] + ", " + movMinimax[1] + ")");
        System.out.println("   Tiempo: " + tiempoMinimax + " ms");

        // Ejecutar Alfa-Beta
        System.out.println("\n🔵 ALFA-BETA:");
        long inicioAlfaBeta = System.currentTimeMillis();
        int[] movAlfaBeta = AlfaBeta.mejorMovimientoAlfaBeta(tablero, 'O', 'X', false);
        long finAlfaBeta = System.currentTimeMillis();
        long tiempoAlfaBeta = finAlfaBeta - inicioAlfaBeta;

        System.out.println("   Movimiento: (" + movAlfaBeta[0] + ", " + movAlfaBeta[1] + ")");
        System.out.println("   Tiempo: " + tiempoAlfaBeta + " ms");

        // Comparación
        System.out.println("\n📊 ANÁLISIS:");
        System.out.println("   Ambos encuentran el mismo movimiento: " +
                (movMinimax[0] == movAlfaBeta[0] && movMinimax[1] == movAlfaBeta[1] ? "✅ SÍ" : "❌ NO"));

        if (tiempoMinimax > tiempoAlfaBeta) {
            double mejora = ((tiempoMinimax - tiempoAlfaBeta) * 100.0) / tiempoMinimax;
            System.out.println("   Alfa-Beta es " + String.format("%.1f%%", mejora) + " más rápido ⚡");
        } else {
            System.out.println("   Tiempos similares ⚖️");
        }
    }
}
