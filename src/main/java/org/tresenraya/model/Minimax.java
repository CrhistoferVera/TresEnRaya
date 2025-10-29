package org.tresenraya.model;

import org.tresenraya.util.UtilidadesSimetria;
import java.util.List;
//comentario random
public class Minimax {

    public static int minimax(Tablero estado, int profundidad, boolean esMax,
                              char jugador, char oponente, int filaJugada, int colJugada, boolean usarSimetria) {

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
                Integer.MIN_VALUE,
                Integer.MAX_VALUE
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

                int valor = minimax(nuevo, profundidad + 1, false, jugador, oponente, i, j, usarSimetria);

                VisualizadorArbol.imprimirNodo(profundidad, "MAX", i, j, jugador, valor,
                        mejor < valor ? "⬆️ MEJOR" : "");

                mejor = Math.max(mejor, valor);
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

                int valor = minimax(nuevo, profundidad + 1, true, jugador, oponente, i, j, usarSimetria);

                VisualizadorArbol.imprimirNodo(profundidad, "MIN", i, j, oponente, valor,
                        peor > valor ? "⬇️ PEOR" : "");

                peor = Math.min(peor, valor);
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

    public static int[] mejorMovimiento(Tablero t, char jugador, char oponente, boolean visualizar, boolean capturarArbol) {
        return mejorMovimiento(t, jugador, oponente, visualizar, capturarArbol, false);
    }

    public static int[] mejorMovimiento(Tablero t, char jugador, char oponente, boolean visualizar,
                                        boolean capturarArbol, boolean usarSimetria) {
        if (usarSimetria) {
            UtilidadesSimetria.reiniciarCache();
        }

        if (visualizar) {
            VisualizadorArbol.reiniciar();
            VisualizadorArbol.setMostrarDetalles(true);
            VisualizadorArbol.setCapturandoArbol(capturarArbol);
            VisualizadorArbol.imprimirEncabezado("MINIMAX" + (usarSimetria ? " + SIMETRÍA" : ""));
        }

        int mejorValor = Integer.MIN_VALUE;
        int[] mejorMovimiento = {-1, -1};

        System.out.println("\n🔍 Evaluando movimientos posibles" +
                (usarSimetria ? " (con optimización de simetría):" : ":"));

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

            int valor = minimax(copia, 0, false, jugador, oponente, i, j, usarSimetria);

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
    public static int[] mejorMovimiento(Tablero t, char jugador, char oponente, boolean visualizar) {
        return mejorMovimiento(t, jugador, oponente, visualizar, false, false);
    }

    public static int[] mejorMovimiento(Tablero t, char jugador, char oponente) {
        return mejorMovimiento(t, jugador, oponente, false, false, false);
    }
}