package org.tresenraya.model;

public class Minimax {

    public static int minimax(Tablero estado, int profundidad, boolean esMax, char jugador, char oponente) {
        if (estado.hayGanador(jugador)) return 10 - profundidad;
        if (estado.hayGanador(oponente)) return profundidad - 10;
        if (estado.tableroLleno()) return 0;

        if (esMax) {
            int mejor = Integer.MIN_VALUE;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (estado.esMovimientoValido(i, j)) {
                        Tablero nuevo = new Tablero(estado.getMatriz());
                        nuevo.hacerMovimiento(i, j, jugador);
                        int valor = minimax(nuevo, profundidad + 1, false, jugador, oponente);
                        mejor = Math.max(mejor, valor);
                    }
                }
            }
            return mejor;
        } else {
            int peor = Integer.MAX_VALUE;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (estado.esMovimientoValido(i, j)) {
                        Tablero nuevo = new Tablero(estado.getMatriz());
                        nuevo.hacerMovimiento(i, j, oponente);
                        int valor = minimax(nuevo, profundidad + 1, true, jugador, oponente);
                        peor = Math.min(peor, valor);
                    }
                }
            }
            return peor;
        }
    }

    public static int[] mejorMovimiento(Tablero t, char jugador, char oponente) {
        int mejorValor = Integer.MIN_VALUE;
        int[] mejorMovimiento = {-1, -1};

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (t.esMovimientoValido(i, j)) {
                    Tablero copia = new Tablero(t.getMatriz());
                    copia.hacerMovimiento(i, j, jugador);
                    int valor = minimax(copia, 0, false, jugador, oponente);
                    if (valor > mejorValor) {
                        mejorValor = valor;
                        mejorMovimiento[0] = i;
                        mejorMovimiento[1] = j;
                    }
                }
            }
        }
        return mejorMovimiento;
    }
}
