package org.tresenraya.model;

/**
 * Minimax SIEMPRE con simetría y tableros visuales
 */
public class Minimax {

    public static int minimax(Tablero estado, int profundidad, boolean esMax,
                              char jugador, char oponente, int filaJugada, int colJugada) {

        VisualizadorArbolMejorado.incrementarNodos();

        // Verificar estados terminales
        if (estado.hayGanador(jugador)) {
            int valor = 10 - profundidad;
            VisualizadorArbolMejorado.imprimirEstadoTerminal(profundidad, "Victoria de " + jugador, valor);
            imprimirTableroCompacto(estado, "  ".repeat(profundidad));
            return valor;
        }

        if (estado.hayGanador(oponente)) {
            int valor = profundidad - 10;
            VisualizadorArbolMejorado.imprimirEstadoTerminal(profundidad, "Victoria de " + oponente, valor);
            imprimirTableroCompacto(estado, "  ".repeat(profundidad));
            return valor;
        }

        if (estado.tableroLleno()) {
            VisualizadorArbolMejorado.imprimirEstadoTerminal(profundidad, "Empate", 0);
            imprimirTableroCompacto(estado, "  ".repeat(profundidad));
            return 0;
        }

        // SIEMPRE usar simetría
        java.util.List<DetectorSimetria.Posicion> movimientos = 
            DetectorSimetria.obtenerMovimientosUnicos(estado);

        if (esMax) {
            int mejor = Integer.MIN_VALUE;
            for (DetectorSimetria.Posicion pos : movimientos) {
                int i = pos.fila;
                int j = pos.col;
                
                Tablero nuevo = new Tablero(estado.getMatriz());
                nuevo.hacerMovimiento(i, j, jugador);

                int valor = minimax(nuevo, profundidad + 1, false, jugador, oponente, i, j);

                String extra = mejor < valor ? "⬆️ MEJOR" : "";
                if (profundidad == 0) {
                    String tipo = DetectorSimetria.clasificarMovimiento(i, j);
                    extra += " [" + tipo + "]";
                }
                
                // SIEMPRE mostrar tableros
                VisualizadorArbolMejorado.imprimirNodoConTablero(nuevo, profundidad, "MAX", 
                                                                 i, j, jugador, valor, extra);
                
                mejor = Math.max(mejor, valor);
            }
            return mejor;
        } else {
            int peor = Integer.MAX_VALUE;
            for (DetectorSimetria.Posicion pos : movimientos) {
                int i = pos.fila;
                int j = pos.col;
                
                Tablero nuevo = new Tablero(estado.getMatriz());
                nuevo.hacerMovimiento(i, j, oponente);

                int valor = minimax(nuevo, profundidad + 1, true, jugador, oponente, i, j);

                String extra = peor > valor ? "⬇️ PEOR" : "";
                if (profundidad == 0) {
                    String tipo = DetectorSimetria.clasificarMovimiento(i, j);
                    extra += " [" + tipo + "]";
                }
                
                // SIEMPRE mostrar tableros
                VisualizadorArbolMejorado.imprimirNodoConTablero(nuevo, profundidad, "MIN", 
                                                                 i, j, oponente, valor, extra);
                
                peor = Math.min(peor, valor);
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

    // MÉTODO PRINCIPAL - SIEMPRE con simetría y tableros
    public static int[] mejorMovimiento(Tablero t, char jugador, char oponente, boolean visualizar) {
        if (visualizar) {
            VisualizadorArbolMejorado.reiniciar();
            VisualizadorArbolMejorado.setMostrarDetalles(true);
            VisualizadorArbolMejorado.imprimirEncabezado("MINIMAX CON SIMETRÍA + TABLEROS");
            DetectorSimetria.imprimirInfoSimetria(t);
        }

        int mejorValor = Integer.MIN_VALUE;
        int[] mejorMovimiento = {-1, -1};

        if (visualizar) {
            System.out.println("\n🔍 Evaluando movimientos posibles:");
        }

        java.util.List<DetectorSimetria.Posicion> movimientos = 
            DetectorSimetria.obtenerMovimientosUnicos(t);

        for (DetectorSimetria.Posicion pos : movimientos) {
            int i = pos.fila;
            int j = pos.col;
            
            Tablero copia = new Tablero(t.getMatriz());
            copia.hacerMovimiento(i, j, jugador);

            if (visualizar) {
                String tipo = DetectorSimetria.clasificarMovimiento(i, j);
                System.out.println("\n--- Explorando movimiento (" + i + ", " + j + ") [" + tipo + "] ---");
            }

            int valor = minimax(copia, 0, false, jugador, oponente, i, j);

            if (visualizar) {
                String tipo = DetectorSimetria.clasificarMovimiento(i, j);
                System.out.println("Movimiento (" + i + ", " + j + ") [" + tipo + "] → Valor: " + valor +
                        (valor > mejorValor ? " ⭐ NUEVO MEJOR" : ""));
            }

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

    // Versión sin visualización (para comparaciones rápidas)
    public static int[] mejorMovimiento(Tablero t, char jugador, char oponente) {
        return mejorMovimiento(t, jugador, oponente, false);
    }
}