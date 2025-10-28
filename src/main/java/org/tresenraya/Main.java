package org.tresenraya;

import org.tresenraya.model.AlfaBeta;
import org.tresenraya.model.Minimax;
import org.tresenraya.model.Tablero;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("╔═══════════════════════════════════════╗");
        System.out.println("║       TRES EN RAYA - MENÚ PRINCIPAL   ║");
        System.out.println("╚═══════════════════════════════════════╝");
        System.out.println();
        System.out.println("1. 🎮 Jugar contra la IA");
        System.out.println("2. 🧪 Ver demostración de algoritmos");
        System.out.println("3. ❌ Salir");
        System.out.print("\nElige una opción: ");

        try {
            int opcion = scanner.nextInt();

            switch (opcion) {
                case 1:
                    scanner.close(); // Cerrar este scanner
                    JuegoInteractivo juego = new JuegoInteractivo();
                    juego.iniciar();
                    return; // Salir después del juego

                case 2:
                    ejecutarDemostracion();
                    break;
                case 3:
                    System.out.println("👋 ¡Hasta pronto!");
                    break;
                default:
                    System.out.println("❌ Opción inválida");
            }
        } catch (Exception e) {
            System.out.println("❌ Entrada inválida");
        } finally {
            scanner.close();
        }
    }

    private static void ejecutarDemostracion() {
        // Crear tablero vacío
        Tablero tablero = new Tablero();
        System.out.println("\n=== Estado inicial del tablero ===");
        tablero.imprimir();

        // Simulamos una jugada inicial del jugador X
        tablero.hacerMovimiento(0, 0, 'X');
        System.out.println("=== Tablero después de la jugada inicial de X ===");
        tablero.imprimir();

        // Ejecutar el algoritmo Minimax
        System.out.println("--- Ejecución de Minimax ---");
        long inicioMinimax = System.currentTimeMillis();
        int[] mejorJugadaMinimax = Minimax.mejorMovimiento(tablero, 'O', 'X');
        long finMinimax = System.currentTimeMillis();
        System.out.println("Mejor jugada para O (Minimax): (" + mejorJugadaMinimax[0] + ", " + mejorJugadaMinimax[1] + ")");
        System.out.println("Tiempo: " + (finMinimax - inicioMinimax) + " ms");
        tablero.hacerMovimiento(mejorJugadaMinimax[0], mejorJugadaMinimax[1], 'O');
        tablero.imprimir();

        // Reiniciamos el tablero para comparar con Alfa-Beta
        System.out.println("--- Ejecución de Alfa-Beta ---");
        Tablero tablero2 = new Tablero();
        tablero2.hacerMovimiento(0, 0, 'X');
        long inicioAlfaBeta = System.currentTimeMillis();
        int[] mejorJugadaAlfaBeta = AlfaBeta.mejorMovimientoAlfaBeta(tablero2, 'O', 'X');
        long finAlfaBeta = System.currentTimeMillis();
        System.out.println("Mejor jugada para O (Alfa-Beta): (" + mejorJugadaAlfaBeta[0] + ", " + mejorJugadaAlfaBeta[1] + ")");
        System.out.println("Tiempo: " + (finAlfaBeta - inicioAlfaBeta) + " ms");
        tablero2.hacerMovimiento(mejorJugadaAlfaBeta[0], mejorJugadaAlfaBeta[1], 'O');
        tablero2.imprimir();

        // Mostrar que ambos métodos encuentran la misma mejor jugada
        System.out.println("📊 Comparación final:");
        System.out.println("Minimax recomienda: (" + mejorJugadaMinimax[0] + ", " + mejorJugadaMinimax[1] + ") - Tiempo: " + (finMinimax - inicioMinimax) + " ms");
        System.out.println("Alfa-Beta recomienda: (" + mejorJugadaAlfaBeta[0] + ", " + mejorJugadaAlfaBeta[1] + ") - Tiempo: " + (finAlfaBeta - inicioAlfaBeta) + " ms");

        long mejora = finMinimax - finAlfaBeta;
        if (mejora > 0) {
            System.out.println("⚡ Alfa-Beta fue " + mejora + " ms más rápido");
        }
    }
}