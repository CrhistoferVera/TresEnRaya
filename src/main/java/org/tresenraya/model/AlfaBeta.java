package org.tresenraya.model;

import org.tresenraya.util.UtilidadesSimetria;
import java.util.List;

public class AlfaBeta {

    public static int alphabeta(Tablero estado, int profundidad, int alpha, int beta,
                                boolean esMax, char jugador, char oponente, int filaJugada,
                                int colJugada, boolean usarSimetria) {

        VisualizadorArbol.incrementarNodos();

        // Verificar cache si usamos simetría
        if (usarSimetria) {
            Integer valorCache = UtilidadesSimetria.obtenerValorCache(estado);
            if (valorCache != null) {
                return valorCache;
            }
        }

        VisualizadorArbol.iniciarNodo(
                filaJugada >= 0 ? new int[]{filaJugada, colJugada} : null,
                esMax,
                alpha,
                beta
        );

        // Verificar estados terminales
        if (estado.hayGanador(jugador)) {
            int valor = 10 - profundidad;
            VisualizadorArbol.imprimirEstadoTerminal(profundidad, "Victoria de " + jugador, valor);
            VisualizadorArbol.finalizarNodo(valor);
            if (usarSimetria) UtilidadesSimetria.guardarEnCache(estado, valor);
            return valor;
        }

        if (estado.hayGanador(oponente)) {
            int valor = profundidad - 10;
            VisualizadorArbol.imprimirEstadoTerminal(profundidad, "Victoria de " + oponente, valor);
            VisualizadorArbol.finalizarNodo(valor);
            if (usarSimetria) UtilidadesSimetria.guardarEnCache(estado, valor);
            return valor;
        }

        if (estado.tableroLleno()) {
            VisualizadorArbol.imprimirEstadoTerminal(profundidad, "Empate", 0);
            VisualizadorArbol.finalizarNodo(0);
            if (usarSimetria) UtilidadesSimetria.guardarEnCache(estado, 0);
            return 0;
        }

        if (esMax) {
            int mejor = Integer.MIN_VALUE;

            // Obtener movimientos (únicos si usamos simetría)
            List<int[]> movimientos = usarSimetria ?
                    UtilidadesSimetria.obtenerMovimientosUnicos(estado) :
                    obtenerTodosMovimientos(estado);

            for (int[] mov : movimientos) {
                int i = mov[0];
                int j = mov[1];

                Tablero nuevo = new Tablero(estado.getMatriz());
                nuevo.hacerMovimiento(i, j, jugador);

                int valor = alphabeta(nuevo, profundidad + 1, alpha, beta, false,
                        jugador, oponente, i, j, usarSimetria);

                VisualizadorArbol.imprimirNodo(profundidad, "MAX", i, j, jugador, valor,
                        "α=" + alpha + " β=" + beta +
                                (mejor < valor ? " ⬆️ MEJOR" : ""));

                mejor = Math.max(mejor, valor);
                alpha = Math.max(alpha, mejor);

                if (beta <= alpha) {
                    VisualizadorArbol.imprimirPoda(profundidad, "BETA", alpha, beta);
                    break; // poda beta
                }
            }

            VisualizadorArbol.finalizarNodo(mejor);
            if (usarSimetria) UtilidadesSimetria.guardarEnCache(estado, mejor);
            return mejor;
        } else {
            int peor = Integer.MAX_VALUE;

            // Obtener movimientos (únicos si usamos simetría)
            List<int[]> movimientos = usarSimetria ?
                    UtilidadesSimetria.obtenerMovimientosUnicos(estado) :
                    obtenerTodosMovimientos(estado);

            for (int[] mov : movimientos) {
                int i = mov[0];
                int j = mov[1];

                Tablero nuevo = new Tablero(estado.getMatriz());
                nuevo.hacerMovimiento(i, j, oponente);

                int valor = alphabeta(nuevo, profundidad + 1, alpha, beta, true,
                        jugador, oponente, i, j, usarSimetria);

                VisualizadorArbol.imprimirNodo(profundidad, "MIN", i, j, oponente, valor,
                        "α=" + alpha + " β=" + beta +
                                (peor > valor ? " ⬇️ PEOR" : ""));

                peor = Math.min(peor, valor);
                beta = Math.min(beta, peor);

                if (beta <= alpha) {
                    VisualizadorArbol.imprimirPoda(profundidad, "ALPHA", alpha, beta);
                    break; // poda alfa
                }
            }

            VisualizadorArbol.finalizarNodo(peor);
            if (usarSimetria) UtilidadesSimetria.guardarEnCache(estado, peor);
            return peor;
        }
    }

    private static List<int[]> obtenerTodosMovimientos(Tablero estado) {
        List<int[]> movimientos = new java.util.ArrayList<>();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (estado.esMovimientoValido(i, j)) {
                    movimientos.add(new int[]{i, j});
                }
            }
        }
        return movimientos;
    }

    public static int[] mejorMovimientoAlfaBeta(Tablero t, char jugador, char oponente,
                                                boolean visualizar, boolean capturarArbol) {
        return mejorMovimientoAlfaBeta(t, jugador, oponente, visualizar, capturarArbol, false);
    }

    public static int[] mejorMovimientoAlfaBeta(Tablero t, char jugador, char oponente,
                                                boolean visualizar, boolean capturarArbol,
                                                boolean usarSimetria) {
        if (usarSimetria) {
            UtilidadesSimetria.reiniciarCache();
        }

        if (visualizar) {
            VisualizadorArbol.reiniciar();
            VisualizadorArbol.setMostrarDetalles(true);
            VisualizadorArbol.setCapturandoArbol(capturarArbol);
            VisualizadorArbol.imprimirEncabezado("ALFA-BETA" + (usarSimetria ? " + SIMETRÍA" : ""));
        }

        int mejorValor = Integer.MIN_VALUE;
        int[] mejorMovimiento = {-1, -1};

        System.out.println("\n🔍 Evaluando movimientos posibles con poda Alfa-Beta" +
                (usarSimetria ? " y optimización de simetría:" : ":"));

        // Obtener movimientos únicos si usamos simetría
        List<int[]> movimientos = usarSimetria ?
                UtilidadesSimetria.obtenerMovimientosUnicos(t) :
                obtenerTodosMovimientos(t);

        if (usarSimetria) {
            System.out.println("   Movimientos únicos por simetría: " + movimientos.size() +
                    " (de " + obtenerTodosMovimientos(t).size() + " posibles)");
        }

        for (int[] mov : movimientos) {
            int i = mov[0];
            int j = mov[1];

            Tablero copia = new Tablero(t.getMatriz());
            copia.hacerMovimiento(i, j, jugador);

            if (visualizar) {
                System.out.println("\n--- Explorando movimiento (" + i + ", " + j + ") ---");
            }

            int valor = alphabeta(copia, 0, Integer.MIN_VALUE, Integer.MAX_VALUE,
                    false, jugador, oponente, i, j, usarSimetria);

            System.out.println("Movimiento (" + i + ", " + j + ") → Valor: " + valor +
                    (valor > mejorValor ? " ⭐ NUEVO MEJOR" : ""));

            if (valor > mejorValor) {
                mejorValor = valor;
                mejorMovimiento[0] = i;
                mejorMovimiento[1] = j;
            }
        }

        if (visualizar) {
            VisualizadorArbol.marcarMejorMovimiento(mejorMovimiento);
            VisualizadorArbol.imprimirResumen();
            if (usarSimetria) {
                UtilidadesSimetria.imprimirEstadisticas();
            }
        }

        return mejorMovimiento;
    }

    // Métodos de compatibilidad
    public static int[] mejorMovimientoAlfaBeta(Tablero t, char jugador, char oponente, boolean visualizar) {
        return mejorMovimientoAlfaBeta(t, jugador, oponente, visualizar, false, false);
    }

    public static int[] mejorMovimientoAlfaBeta(Tablero t, char jugador, char oponente) {
        return mejorMovimientoAlfaBeta(t, jugador, oponente, false, false, false);
    }

    public static int alphabeta(Tablero estado, int profundidad, int alpha, int beta,
                                boolean esMax, char jugador, char oponente, int filaJugada, int colJugada) {
        return alphabeta(estado, profundidad, alpha, beta, esMax, jugador, oponente,
                filaJugada, colJugada, false);
    }

    public static int alphabeta(Tablero estado, int profundidad, int alpha, int beta,
                                boolean esMax, char jugador, char oponente) {
        return alphabeta(estado, profundidad, alpha, beta, esMax, jugador, oponente, -1, -1, false);
    }
}