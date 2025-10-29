package org.tresenraya.model;

public class AlfaBeta {

    public static int alphabeta(Tablero estado, int profundidad, int alpha, int beta,
                                boolean esMax, char jugador, char oponente, int filaJugada, int colJugada) {

        VisualizadorArbol.incrementarNodos();

        // Verificar estados terminales
        if (estado.hayGanador(jugador)) {
            int valor = 10 - profundidad;
            VisualizadorArbol.imprimirEstadoTerminal(profundidad, "Victoria de " + jugador, valor);
            return valor;
        }

        if (estado.hayGanador(oponente)) {
            int valor = profundidad - 10;
            VisualizadorArbol.imprimirEstadoTerminal(profundidad, "Victoria de " + oponente, valor);
            return valor;
        }

        if (estado.tableroLleno()) {
            VisualizadorArbol.imprimirEstadoTerminal(profundidad, "Empate", 0);
            return 0;
        }

        if (esMax) {
            int mejor = Integer.MIN_VALUE;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (estado.esMovimientoValido(i, j)) {
                        Tablero nuevo = new Tablero(estado.getMatriz());
                        nuevo.hacerMovimiento(i, j, jugador);

                        int valor = alphabeta(nuevo, profundidad + 1, alpha, beta, false,
                                jugador, oponente, i, j);

                        VisualizadorArbol.imprimirNodo(profundidad, "MAX", i, j, jugador, valor,
                                "α=" + alpha + " β=" + beta +
                                        (mejor < valor ? " ⬆️ MEJOR" : ""));

                        // ⭐ AGREGAR AQUÍ - Registrar nodo para visualización gráfica
                        VisualizadorArbol.registrarNodo(profundidad, "MAX", i, j, jugador, valor, alpha, beta);

                        mejor = Math.max(mejor, valor);
                        alpha = Math.max(alpha, mejor);

                        if (beta <= alpha) {
                            VisualizadorArbol.imprimirPoda(profundidad, "BETA", alpha, beta);
                            break; // poda beta
                        }
                    }
                }
                if (beta <= alpha) break; // salir del bucle exterior también
            }
            return mejor;
        } else {
            int peor = Integer.MAX_VALUE;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (estado.esMovimientoValido(i, j)) {
                        Tablero nuevo = new Tablero(estado.getMatriz());
                        nuevo.hacerMovimiento(i, j, oponente);

                        int valor = alphabeta(nuevo, profundidad + 1, alpha, beta, true,
                                jugador, oponente, i, j);

                        VisualizadorArbol.imprimirNodo(profundidad, "MIN", i, j, oponente, valor,
                                "α=" + alpha + " β=" + beta +
                                        (peor > valor ? " ⬇️ PEOR" : ""));

                        // ⭐ AGREGAR AQUÍ - Registrar nodo para visualización gráfica
                        VisualizadorArbol.registrarNodo(profundidad, "MIN", i, j, oponente, valor, alpha, beta);

                        peor = Math.min(peor, valor);
                        beta = Math.min(beta, peor);

                        if (beta <= alpha) {
                            VisualizadorArbol.imprimirPoda(profundidad, "ALPHA", alpha, beta);
                            break; // poda alfa
                        }
                    }
                }
                if (beta <= alpha) break; // salir del bucle exterior también
            }
            return peor;
        }
    }

    public static int[] mejorMovimientoAlfaBeta(Tablero t, char jugador, char oponente, boolean visualizar) {
        if (visualizar) {
            VisualizadorArbol.reiniciar();
            VisualizadorArbol.setMostrarDetalles(true);
            VisualizadorArbol.imprimirEncabezado("ALFA-BETA");
        }

        int mejorValor = Integer.MIN_VALUE;
        int[] mejorMovimiento = {-1, -1};

        System.out.println("\n🔍 Evaluando movimientos posibles con poda Alfa-Beta:");

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (t.esMovimientoValido(i, j)) {
                    Tablero copia = new Tablero(t.getMatriz());
                    copia.hacerMovimiento(i, j, jugador);

                    if (visualizar) {
                        System.out.println("\n--- Explorando movimiento (" + i + ", " + j + ") ---");
                    }

                    int valor = alphabeta(copia, 0, Integer.MIN_VALUE, Integer.MAX_VALUE,
                            false, jugador, oponente, i, j);

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
            VisualizadorArbol.imprimirResumen();
        }

        return mejorMovimiento;
    }

    // Método de compatibilidad con código anterior
    public static int[] mejorMovimientoAlfaBeta(Tablero t, char jugador, char oponente) {
        return mejorMovimientoAlfaBeta(t, jugador, oponente, false);
    }

    // Método antiguo para compatibilidad
    public static int alphabeta(Tablero estado, int profundidad, int alpha, int beta,
                                boolean esMax, char jugador, char oponente) {
        return alphabeta(estado, profundidad, alpha, beta, esMax, jugador, oponente, -1, -1);
    }
}