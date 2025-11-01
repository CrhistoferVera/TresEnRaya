package org.tresenraya.model;

/**
 * Alfa-Beta con visualización de tableros como en el diagrama
 */
public class AlfaBeta {

    private static final int PROFUNDIDAD_MAX = 1;
    private static int contadorNodos = 0;
    private static int contadorPodas = 0;

    public static int alphabeta(Tablero estado, int profundidad, int alpha, int beta,
                                boolean esMax, char jugador, char oponente, int filaJugada, int colJugada,
                                boolean usarSimetria, boolean mostrarTableros) {

        contadorNodos++;
        String indent = "  ".repeat(profundidad);

        // Verificar estados terminales
        if (estado.hayGanador(jugador)) {
            int valor = 10 - profundidad;
            if (mostrarTableros && profundidad <= 1) {
                imprimirTableroMini(estado, indent);
                System.out.println(indent + "    Victoria " + jugador + " = " + valor + "\n");
            }
            return valor;
        }

        if (estado.hayGanador(oponente)) {
            int valor = profundidad - 10;
            if (mostrarTableros && profundidad <= 1) {
                imprimirTableroMini(estado, indent);
                System.out.println(indent + "    Victoria " + oponente + " = " + valor + "\n");
            }
            return valor;
        }

        if (estado.tableroLleno()) {
            if (mostrarTableros && profundidad <= 1) {
                imprimirTableroMini(estado, indent);
                System.out.println(indent + "    Empate = 0\n");
            }
            return 0;
        }

        // Límite de profundidad
        if (profundidad >= PROFUNDIDAD_MAX) {
            int valorHeuristico = Evaluador.evaluar(estado, jugador, oponente);
            if (mostrarTableros && profundidad <= 1) {
                imprimirTableroMini(estado, indent);
                System.out.println(indent + "    H = " + valorHeuristico + "\n");
            }
            return valorHeuristico;
        }

        // Obtener movimientos
        java.util.List<DetectorSimetria.Posicion> movimientos;
        if (usarSimetria) {
            movimientos = DetectorSimetria.obtenerMovimientosUnicos(estado);
        } else {
            movimientos = obtenerTodosMovimientos(estado);
        }

        if (esMax) {
            int mejor = Integer.MIN_VALUE;

            for (int i = 0; i < movimientos.size(); i++) {
                DetectorSimetria.Posicion pos = movimientos.get(i);
                Tablero nuevo = new Tablero(estado.getMatriz());
                nuevo.hacerMovimiento(pos.fila, pos.col, jugador);

                int valor = alphabeta(nuevo, profundidad + 1, alpha, beta, false,
                        jugador, oponente, pos.fila, pos.col, usarSimetria, mostrarTableros);

                if (valor > mejor) {
                    mejor = valor;
                }

                alpha = Math.max(alpha, mejor);

                if (beta <= alpha) {
                    contadorPodas++;
                    if (mostrarTableros && profundidad == 0) {
                        System.out.println("      ✂️ PODA (β≤α)\n");
                    }
                    break;
                }
            }

            return mejor;

        } else {
            int peor = Integer.MAX_VALUE;

            for (int i = 0; i < movimientos.size(); i++) {
                DetectorSimetria.Posicion pos = movimientos.get(i);
                Tablero nuevo = new Tablero(estado.getMatriz());
                nuevo.hacerMovimiento(pos.fila, pos.col, oponente);

                int valor = alphabeta(nuevo, profundidad + 1, alpha, beta, true,
                        jugador, oponente, pos.fila, pos.col, usarSimetria, mostrarTableros);

                if (valor < peor) {
                    peor = valor;
                }

                beta = Math.min(beta, peor);

                if (beta <= alpha) {
                    contadorPodas++;
                    if (mostrarTableros && profundidad == 0) {
                        System.out.println("      ✂️ PODA (β≤α)\n");
                    }
                    break;
                }
            }

            return peor;
        }
    }

    private static void imprimirTableroMini(Tablero tablero, String indent) {
        char[][] m = tablero.getMatriz();
        System.out.println(indent + "    ┌───┐");
        for (int i = 0; i < 3; i++) {
            System.out.print(indent + "    │");
            for (int j = 0; j < 3; j++) {
                char c = m[i][j];
                System.out.print(c == '-' ? ' ' : c);
            }
            System.out.println("│");
        }
        System.out.println(indent + "    └───┘");
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

    public static int[] mejorMovimientoAlfaBeta(Tablero t, char jugador, char oponente,
                                                boolean visualizar, boolean usarSimetria,
                                                boolean mostrarTableros) {
        contadorNodos = 0;
        contadorPodas = 0;

        if (visualizar) {
            System.out.println("\n═══════════════════════════════════════");
            System.out.println("    🌳 PODA ALFA-BETA");
            System.out.println("═══════════════════════════════════════");
            System.out.println("Jugador IA: " + jugador + " (MAX)");
            System.out.println("Oponente: " + oponente + " (MIN)");
            if (usarSimetria) {
                java.util.List<DetectorSimetria.Posicion> movs =
                        DetectorSimetria.obtenerMovimientosUnicos(t);
                System.out.println("Movimientos únicos: " + movs.size() + " (simetría)");
            }
            System.out.println("─────────────────────────────────────────\n");
        }

        int mejorValor = Integer.MIN_VALUE;
        int[] mejorMovimiento = {-1, -1};

        java.util.List<DetectorSimetria.Posicion> movimientos;
        if (usarSimetria) {
            movimientos = DetectorSimetria.obtenerMovimientosUnicos(t);
        } else {
            movimientos = obtenerTodosMovimientos(t);
        }

        int numOpcion = 1;
        for (DetectorSimetria.Posicion pos : movimientos) {
            int i = pos.fila;
            int j = pos.col;

            Tablero copia = new Tablero(t.getMatriz());
            copia.hacerMovimiento(i, j, jugador);

            // Mostrar el movimiento a evaluar
            String tipo = usarSimetria ? " [" + DetectorSimetria.clasificarMovimiento(i, j) + "]" : "";
            System.out.println("  Opción " + numOpcion + ": (" + i + "," + j + ")" + tipo);

            // Mostrar el tablero resultante
            imprimirTableroMini(copia, "");
            System.out.println("    α=-∞ β=+∞");
            System.out.println();

            int valor = alphabeta(copia, 0, Integer.MIN_VALUE, Integer.MAX_VALUE,
                    false, jugador, oponente, i, j, usarSimetria, mostrarTableros);

            System.out.println("    → Valor final: " + valor);

            if (valor > mejorValor) {
                System.out.println("    ⭐ Mejor opción hasta ahora");
                mejorValor = valor;
                mejorMovimiento[0] = i;
                mejorMovimiento[1] = j;
            }
            System.out.println();
            numOpcion++;
        }

        if (visualizar) {
            System.out.println("─────────────────────────────────────────");
            System.out.println("📊 Nodos: " + contadorNodos + " | Podas: " + contadorPodas);
            if (contadorPodas > 0) {
                System.out.println("⚡ Eficiencia: " +
                        String.format("%.1f%%", (contadorPodas * 100.0 / contadorNodos)));
            }
            System.out.println("✅ Mejor: (" + mejorMovimiento[0] + "," + mejorMovimiento[1] +
                    ") con valor " + mejorValor);
            System.out.println("═══════════════════════════════════════\n");
        }

        return mejorMovimiento;
    }

    // Métodos de compatibilidad
    public static int[] mejorMovimientoAlfaBeta(Tablero t, char jugador, char oponente, boolean visualizar) {
        return mejorMovimientoAlfaBeta(t, jugador, oponente, visualizar, true, true);
    }

    public static int[] mejorMovimientoAlfaBeta(Tablero t, char jugador, char oponente) {
        return mejorMovimientoAlfaBeta(t, jugador, oponente, false, true, false);
    }

    public static int alphabeta(Tablero estado, int profundidad, int alpha, int beta,
                                boolean esMax, char jugador, char oponente, int filaJugada, int colJugada) {
        return alphabeta(estado, profundidad, alpha, beta, esMax, jugador, oponente,
                filaJugada, colJugada, false, false);
    }

    public static int alphabeta(Tablero estado, int profundidad, int alpha, int beta,
                                boolean esMax, char jugador, char oponente) {
        return alphabeta(estado, profundidad, alpha, beta, esMax, jugador, oponente, -1, -1, false, false);
    }
}