package org.tresenraya.model;

public class Minimax {

    public static int minimax(Tablero estado, int profundidad, boolean esMax,
                              char jugador, char oponente, int filaJugada, int colJugada) {

        VisualizadorArbol.incrementarNodos();
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
            return valor;
        }

        if (estado.hayGanador(oponente)) {
            int valor = profundidad - 10;
            VisualizadorArbol.imprimirEstadoTerminal(profundidad, "Victoria de " + oponente, valor);
            VisualizadorArbol.finalizarNodo(valor);
            return valor;
        }

        if (estado.tableroLleno()) {
            VisualizadorArbol.imprimirEstadoTerminal(profundidad, "Empate", 0);
            VisualizadorArbol.finalizarNodo(0);
            return 0;
        }

        if (esMax) {
            int mejor = Integer.MIN_VALUE;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (estado.esMovimientoValido(i, j)) {
                        Tablero nuevo = new Tablero(estado.getMatriz());
                        nuevo.hacerMovimiento(i, j, jugador);

                        int valor = minimax(nuevo, profundidad + 1, false, jugador, oponente, i, j);

                        VisualizadorArbol.imprimirNodo(profundidad, "MAX", i, j, jugador, valor,
                                mejor < valor ? "⬆️ MEJOR" : "");

                        mejor = Math.max(mejor, valor);
                    }
                }
            }
            VisualizadorArbol.finalizarNodo(mejor);
            return mejor;
        } else {
            int peor = Integer.MAX_VALUE;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (estado.esMovimientoValido(i, j)) {
                        Tablero nuevo = new Tablero(estado.getMatriz());
                        nuevo.hacerMovimiento(i, j, oponente);

                        int valor = minimax(nuevo, profundidad + 1, true, jugador, oponente, i, j);

                        VisualizadorArbol.imprimirNodo(profundidad, "MIN", i, j, oponente, valor,
                                peor > valor ? "⬇️ PEOR" : "");

                        peor = Math.min(peor, valor);
                    }
                }
            }
            VisualizadorArbol.finalizarNodo(peor);
            return peor;
        }
    }

    public static int[] mejorMovimiento(Tablero t, char jugador, char oponente, boolean visualizar) {
        return mejorMovimiento(t, jugador, oponente, visualizar, false);
    }

    public static int[] mejorMovimiento(Tablero t, char jugador, char oponente, boolean visualizar, boolean capturarArbol) {
        if (visualizar) {
            VisualizadorArbol.reiniciar();
            VisualizadorArbol.setMostrarDetalles(true);
            VisualizadorArbol.setCapturandoArbol(capturarArbol);
            VisualizadorArbol.imprimirEncabezado("MINIMAX");
        }

        int mejorValor = Integer.MIN_VALUE;
        int[] mejorMovimiento = {-1, -1};

        System.out.println("\n🔍 Evaluando movimientos posibles:");

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (t.esMovimientoValido(i, j)) {
                    Tablero copia = new Tablero(t.getMatriz());
                    copia.hacerMovimiento(i, j, jugador);

                    if (visualizar) {
                        System.out.println("\n--- Explorando movimiento (" + i + ", " + j + ") ---");
                    }

                    int valor = minimax(copia, 0, false, jugador, oponente, i, j);

                    System.out.println("Movimiento (" + i + ", " + j + ") → Valor: " + valor +
                            (valor > mejorValor ? " ⭐ NUEVO MEJOR" : ""));

                    if (valor > mejorValor) {
                        mejorValor = valor;
                        mejorMovimiento[0] = i;
                        mejorMovimiento[1] = j;
                    }
                }
            }
        }

        if (visualizar) {
            VisualizadorArbol.marcarMejorMovimiento(mejorMovimiento);
            VisualizadorArbol.imprimirResumen();
        }

        return mejorMovimiento;
    }

    // Método de compatibilidad con código anterior
    public static int[] mejorMovimiento(Tablero t, char jugador, char oponente) {
        return mejorMovimiento(t, jugador, oponente, false, false);
    }
}