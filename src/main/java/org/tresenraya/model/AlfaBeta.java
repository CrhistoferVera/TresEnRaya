package org.tresenraya.model;

/**
 * Alfa-Beta con visualización de tableros y captura de salida para UI
 */
public class AlfaBeta {

    private static final int PROFUNDIDAD_MAX = 1;
    private static int contadorNodos = 0;
    private static int contadorPodas = 0;
    private static StringBuilder capturaSalida = null; // Para capturar la salida

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
                imprimirLinea(indent + "    Victoria " + jugador + " = " + valor + "\n");
            }
            return valor;
        }

        if (estado.hayGanador(oponente)) {
            int valor = profundidad - 10;
            if (mostrarTableros && profundidad <= 1) {
                imprimirTableroMini(estado, indent);
                imprimirLinea(indent + "    Victoria " + oponente + " = " + valor + "\n");
            }
            return valor;
        }

        if (estado.tableroLleno()) {
            if (mostrarTableros && profundidad <= 1) {
                imprimirTableroMini(estado, indent);
                imprimirLinea(indent + "    Empate = 0\n");
            }
            return 0;
        }

        // Límite de profundidad
        if (profundidad >= PROFUNDIDAD_MAX) {
            int valorHeuristico = Evaluador.evaluar(estado, jugador, oponente);
            if (mostrarTableros && profundidad <= 1) {
                imprimirTableroMini(estado, indent);
                imprimirLinea(indent + "    H = " + valorHeuristico + "\n");
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
                        imprimirLinea("      ✂️ PODA (β≤α)\n");
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
                        imprimirLinea("      ✂️ PODA (β≤α)\n");
                    }
                    break;
                }
            }

            return peor;
        }
    }

    private static void imprimirTableroMini(Tablero tablero, String indent) {
        char[][] m = tablero.getMatriz();
        imprimirLinea(indent + "    ┌───┐");
        for (int i = 0; i < 3; i++) {
            StringBuilder linea = new StringBuilder(indent + "    │");
            for (int j = 0; j < 3; j++) {
                char c = m[i][j];
                linea.append(c == '-' ? ' ' : c);
            }
            linea.append("│");
            imprimirLinea(linea.toString());
        }
        imprimirLinea(indent + "    └───┘");
    }

    // Método auxiliar para imprimir (consola o captura)
    private static void imprimirLinea(String texto) {
        if (capturaSalida != null) {
            capturaSalida.append(texto).append("\n");
        } else {
            System.out.println(texto);
        }
    }

    private static void imprimir(String texto) {
        if (capturaSalida != null) {
            capturaSalida.append(texto);
        } else {
            System.out.print(texto);
        }
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
            imprimirLinea("\n═══════════════════════════════════════");
            imprimirLinea("    🌳 PODA ALFA-BETA");
            imprimirLinea("═══════════════════════════════════════");
            imprimirLinea("Jugador IA: " + jugador + " (MAX)");
            imprimirLinea("Oponente: " + oponente + " (MIN)");
            if (usarSimetria) {
                java.util.List<DetectorSimetria.Posicion> movs =
                        DetectorSimetria.obtenerMovimientosUnicos(t);
                imprimirLinea("Movimientos únicos: " + movs.size() + " (simetría)");
            }
            imprimirLinea("─────────────────────────────────────────\n");
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
            imprimirLinea("  Opción " + numOpcion + ": (" + i + "," + j + ")" + tipo);

            // Mostrar el tablero resultante
            imprimirTableroMini(copia, "");
            imprimirLinea("    α=-∞ β=+∞");
            imprimirLinea("");

            int valor = alphabeta(copia, 0, Integer.MIN_VALUE, Integer.MAX_VALUE,
                    false, jugador, oponente, i, j, usarSimetria, mostrarTableros);

            imprimirLinea("    → Valor final: " + valor);

            if (valor > mejorValor) {
                imprimirLinea("    ⭐ Mejor opción hasta ahora");
                mejorValor = valor;
                mejorMovimiento[0] = i;
                mejorMovimiento[1] = j;
            }
            imprimirLinea("");
            numOpcion++;
        }

        if (visualizar) {
            imprimirLinea("─────────────────────────────────────────");
            imprimirLinea("📊 Nodos: " + contadorNodos + " | Podas: " + contadorPodas);
            if (contadorPodas > 0) {
                imprimirLinea("⚡ Eficiencia: " +
                        String.format("%.1f%%", (contadorPodas * 100.0 / contadorNodos)));
            }
            imprimirLinea("✅ Mejor: (" + mejorMovimiento[0] + "," + mejorMovimiento[1] +
                    ") con valor " + mejorValor);
            imprimirLinea("═══════════════════════════════════════\n");
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

    // NUEVO: Método que captura la salida en un StringBuilder
    public static int[] mejorMovimientoAlfaBetaConCaptura(Tablero t, char jugador, char oponente,
                                                          boolean visualizar, boolean usarSimetria,
                                                          boolean mostrarTableros, StringBuilder salida) {
        capturaSalida = salida;
        try {
            return mejorMovimientoAlfaBeta(t, jugador, oponente, visualizar, usarSimetria, mostrarTableros);
        } finally {
            capturaSalida = null; // Restaurar a impresión por consola
        }
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