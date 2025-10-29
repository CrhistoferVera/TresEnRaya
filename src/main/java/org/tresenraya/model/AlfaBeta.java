package org.tresenraya.model;

/**
 * Alfa-Beta con soporte para simetría y visualización de tableros
 * VERSIÓN UNIFICADA - Compatible con todo el sistema
 */
public class AlfaBeta {

    public static int alphabeta(Tablero estado, int profundidad, int alpha, int beta,
                                boolean esMax, char jugador, char oponente, int filaJugada, int colJugada,
                                boolean usarSimetria, boolean mostrarTableros) {

        VisualizadorArbolMejorado.incrementarNodos();

        // Verificar estados terminales
        if (estado.hayGanador(jugador)) {
            int valor = 10 - profundidad;
            VisualizadorArbolMejorado.imprimirEstadoTerminal(profundidad, "Victoria de " + jugador, valor);
            if (mostrarTableros) {
                String indentacion = "  ".repeat(profundidad);
                imprimirTableroCompacto(estado, indentacion);
            }
            return valor;
        }

        if (estado.hayGanador(oponente)) {
            int valor = profundidad - 10;
            VisualizadorArbolMejorado.imprimirEstadoTerminal(profundidad, "Victoria de " + oponente, valor);
            if (mostrarTableros) {
                String indentacion = "  ".repeat(profundidad);
                imprimirTableroCompacto(estado, indentacion);
            }
            return valor;
        }

        if (estado.tableroLleno()) {
            VisualizadorArbolMejorado.imprimirEstadoTerminal(profundidad, "Empate", 0);
            if (mostrarTableros) {
                String indentacion = "  ".repeat(profundidad);
                imprimirTableroCompacto(estado, indentacion);
            }
            return 0;
        }

        // Obtener movimientos (con o sin simetría)
        java.util.List<DetectorSimetria.Posicion> movimientos;
        if (usarSimetria) {
            movimientos = DetectorSimetria.obtenerMovimientosUnicos(estado);
        } else {
            movimientos = obtenerTodosMovimientos(estado);
        }

        if (esMax) {
            int mejor = Integer.MIN_VALUE;
            for (DetectorSimetria.Posicion pos : movimientos) {
                int i = pos.fila;
                int j = pos.col;
                
                Tablero nuevo = new Tablero(estado.getMatriz());
                nuevo.hacerMovimiento(i, j, jugador);

                int valor = alphabeta(nuevo, profundidad + 1, alpha, beta, false,
                        jugador, oponente, i, j, usarSimetria, mostrarTableros);

                String extra = "α=" + alpha + " β=" + beta;
                if (mejor < valor) extra += " ⬆️ MEJOR";
                if (usarSimetria && profundidad == 0) {
                    String tipo = DetectorSimetria.clasificarMovimiento(i, j);
                    extra += " [" + tipo + "]";
                }

                if (mostrarTableros) {
                    VisualizadorArbolMejorado.imprimirNodoConTablero(nuevo, profundidad, "MAX",
                                                                     i, j, jugador, valor, extra);
                } else {
                    VisualizadorArbolMejorado.imprimirNodo(profundidad, "MAX", i, j, jugador, valor, extra);
                }

                // Registrar para visualización gráfica
                VisualizadorArbol.registrarNodo(profundidad, "MAX", i, j, jugador, valor, alpha, beta);

                mejor = Math.max(mejor, valor);
                alpha = Math.max(alpha, mejor);

                if (beta <= alpha) {
                    VisualizadorArbolMejorado.imprimirPoda(profundidad, "BETA", alpha, beta);
                    break; // poda beta
                }
            }
            return mejor;
        } else {
            int peor = Integer.MAX_VALUE;
            for (DetectorSimetria.Posicion pos : movimientos) {
                int i = pos.fila;
                int j = pos.col;
                
                Tablero nuevo = new Tablero(estado.getMatriz());
                nuevo.hacerMovimiento(i, j, oponente);

                int valor = alphabeta(nuevo, profundidad + 1, alpha, beta, true,
                        jugador, oponente, i, j, usarSimetria, mostrarTableros);

                String extra = "α=" + alpha + " β=" + beta;
                if (peor > valor) extra += " ⬇️ PEOR";
                if (usarSimetria && profundidad == 0) {
                    String tipo = DetectorSimetria.clasificarMovimiento(i, j);
                    extra += " [" + tipo + "]";
                }

                if (mostrarTableros) {
                    VisualizadorArbolMejorado.imprimirNodoConTablero(nuevo, profundidad, "MIN",
                                                                     i, j, oponente, valor, extra);
                } else {
                    VisualizadorArbolMejorado.imprimirNodo(profundidad, "MIN", i, j, oponente, valor, extra);
                }

                // Registrar para visualización gráfica
                VisualizadorArbol.registrarNodo(profundidad, "MIN", i, j, oponente, valor, alpha, beta);

                peor = Math.min(peor, valor);
                beta = Math.min(beta, peor);

                if (beta <= alpha) {
                    VisualizadorArbolMejorado.imprimirPoda(profundidad, "ALPHA", alpha, beta);
                    break; // poda alfa
                }
            }
            return peor;
        }
    }

    private static void imprimirTableroCompacto(Tablero tablero, String indentacion) {
        char[][] matriz = tablero.getMatriz();
        for (int i = 0; i < 3; i++) {
            System.out.print(indentacion + "                ");
            for (int j = 0; j < 3; j++) {
                char c = matriz[i][j];
                System.out.print(c == '-' ? ' ' : Character.toLowerCase(c));
                if (j < 2) System.out.print("|");
            }
            System.out.println();
        }
        System.out.println();
    }

    private static java.util.List<DetectorSimetria.Posicion> obtenerTodosMovimientos(Tablero estado) {
        java.util.List<DetectorSimetria.Posicion> movimientos = new java.util.ArrayList<>();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (estado.esMovimientoValido(i, j)) {
                    movimientos.add(new DetectorSimetria.Posicion(i, j));
                }
            }
        }
        return movimientos;
    }

    // MÉTODO PRINCIPAL - Con todas las opciones
    public static int[] mejorMovimientoAlfaBeta(Tablero t, char jugador, char oponente, 
                                                 boolean visualizar, boolean usarSimetria,
                                                 boolean mostrarTableros) {
        if (visualizar) {
            VisualizadorArbol.reiniciar();
            VisualizadorArbolMejorado.reiniciar();
            VisualizadorArbolMejorado.setMostrarDetalles(true);
            String titulo = "ALFA-BETA";
            if (usarSimetria) titulo += " CON SIMETRÍA";
            if (mostrarTableros) titulo += " + TABLEROS";
            VisualizadorArbolMejorado.imprimirEncabezado(titulo);
            
            if (usarSimetria) {
                DetectorSimetria.imprimirInfoSimetria(t);
            }
        }

        int mejorValor = Integer.MIN_VALUE;
        int[] mejorMovimiento = {-1, -1};

        System.out.println("\n🔍 Evaluando movimientos posibles con poda Alfa-Beta:");

        java.util.List<DetectorSimetria.Posicion> movimientos;
        if (usarSimetria) {
            movimientos = DetectorSimetria.obtenerMovimientosUnicos(t);
        } else {
            movimientos = obtenerTodosMovimientos(t);
        }

        for (DetectorSimetria.Posicion pos : movimientos) {
            int i = pos.fila;
            int j = pos.col;
            
            Tablero copia = new Tablero(t.getMatriz());
            copia.hacerMovimiento(i, j, jugador);

            if (visualizar) {
                String tipo = usarSimetria ? " [" + DetectorSimetria.clasificarMovimiento(i, j) + "]" : "";
                System.out.println("\n--- Explorando movimiento (" + i + ", " + j + ")" + tipo + " ---");
            }

            int valor = alphabeta(copia, 0, Integer.MIN_VALUE, Integer.MAX_VALUE,
                    false, jugador, oponente, i, j, usarSimetria, mostrarTableros);

            String tipo = usarSimetria ? " [" + DetectorSimetria.clasificarMovimiento(i, j) + "]" : "";
            System.out.println("Movimiento (" + i + ", " + j + ")" + tipo + " → Valor: " + valor +
                    (valor > mejorValor ? " ⭐ NUEVO MEJOR" : ""));

            if (valor > mejorValor) {
                mejorValor = valor;
                mejorMovimiento[0] = i;
                mejorMovimiento[1] = j;
            }
        }

        if (visualizar) {
            VisualizadorArbolMejorado.imprimirResumen();
        }

        return mejorMovimiento;
    }

    // MÉTODOS DE COMPATIBILIDAD - Para código existente

    // Versión con visualización, usa simetría por defecto
    public static int[] mejorMovimientoAlfaBeta(Tablero t, char jugador, char oponente, boolean visualizar) {
        return mejorMovimientoAlfaBeta(t, jugador, oponente, visualizar, true, false);
    }

    // Versión sin visualización
    public static int[] mejorMovimientoAlfaBeta(Tablero t, char jugador, char oponente) {
        return mejorMovimientoAlfaBeta(t, jugador, oponente, false, true, false);
    }

    // Versión recursiva simple (para compatibilidad)
    public static int alphabeta(Tablero estado, int profundidad, int alpha, int beta,
                                boolean esMax, char jugador, char oponente, int filaJugada, int colJugada) {
        return alphabeta(estado, profundidad, alpha, beta, esMax, jugador, oponente, 
                        filaJugada, colJugada, false, false);
    }

    // Versión sin parámetros de jugada
    public static int alphabeta(Tablero estado, int profundidad, int alpha, int beta,
                                boolean esMax, char jugador, char oponente) {
        return alphabeta(estado, profundidad, alpha, beta, esMax, jugador, oponente, -1, -1, false, false);
    }
}