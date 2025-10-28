package org.tresenraya.model;

public class AlfaBeta {

    public static int alphabeta(Tablero estado, int profundidad, int alpha, int beta, boolean esMax, char jugador, char oponente) {
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
                        int valor = alphabeta(nuevo, profundidad + 1, alpha, beta, false, jugador, oponente);
                        mejor = Math.max(mejor, valor);
                        alpha = Math.max(alpha, mejor);
                        if (beta <= alpha)
                            break; // poda
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
                        int valor = alphabeta(nuevo, profundidad + 1, alpha, beta, true, jugador, oponente);
                        peor = Math.min(peor, valor);
                        beta = Math.min(beta, peor);
                        if (beta <= alpha)
                            break; // poda
                    }
                }
            }
            return peor;
        }
    }
    public static int[] mejorMovimientoAlfaBeta(Tablero t, char jugador, char oponente) {
        int mejorValor = Integer.MIN_VALUE;
        int[] mejorMovimiento = {-1, -1};

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (t.esMovimientoValido(i, j)) {
                    Tablero copia = new Tablero(t.getMatriz());
                    copia.hacerMovimiento(i, j, jugador);
                    int valor = alphabeta(copia, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, false, jugador, oponente);
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

