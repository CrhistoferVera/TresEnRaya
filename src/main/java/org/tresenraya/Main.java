package org.tresenraya;

import org.tresenraya.model.AlfaBeta;
import org.tresenraya.model.Minimax;
import org.tresenraya.model.Tablero;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        // Crear tablero vacío
        Tablero tablero = new Tablero();
        System.out.println("=== Estado inicial del tablero ===");
        tablero.imprimir();

        // Simulamos una jugada inicial del jugador X
        tablero.hacerMovimiento(0, 0, 'X');
        System.out.println("\n=== Tablero después de la jugada inicial de X ===");
        tablero.imprimir();

        // Ejecutar el algoritmo Minimax
        System.out.println("\n--- Ejecución de Minimax ---");
        int[] mejorJugadaMinimax = Minimax.mejorMovimiento(tablero, 'O', 'X');
        System.out.println("Mejor jugada para O (Minimax): (" + mejorJugadaMinimax[0] + ", " + mejorJugadaMinimax[1] + ")");
        tablero.hacerMovimiento(mejorJugadaMinimax[0], mejorJugadaMinimax[1], 'O');
        tablero.imprimir();

        // Reiniciamos el tablero para comparar con Alfa-Beta
        System.out.println("\n--- Ejecución de Alfa-Beta ---");
        Tablero tablero2 = new Tablero();
        tablero2.hacerMovimiento(0, 0, 'X');
        int[] mejorJugadaAlfaBeta = AlfaBeta.mejorMovimientoAlfaBeta(tablero2, 'O', 'X');
        System.out.println("Mejor jugada para O (Alfa-Beta): (" + mejorJugadaAlfaBeta[0] + ", " + mejorJugadaAlfaBeta[1] + ")");
        tablero2.hacerMovimiento(mejorJugadaAlfaBeta[0], mejorJugadaAlfaBeta[1], 'O');
        tablero2.imprimir();

        // Mostrar que ambos métodos encuentran la misma mejor jugada
        System.out.println("\nComparación final:");
        System.out.println("Minimax recomienda: (" + mejorJugadaMinimax[0] + ", " + mejorJugadaMinimax[1] + ")");
        System.out.println("Alfa-Beta recomienda: (" + mejorJugadaAlfaBeta[0] + ", " + mejorJugadaAlfaBeta[1] + ")");
    }
}

