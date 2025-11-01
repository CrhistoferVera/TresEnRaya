package org.tresenraya.model;

/**
 * Alfa-Beta con soporte para simetría y visualización de tableros
 * ✅ CORREGIDO: Profundidad limitada a 2 niveles y heurística correcta
 */
public class AlfaBeta {

    // ✅ PROFUNDIDAD LIMITADA: Solo 2 movimientos (IA + oponente)
    // Profundidad 0 = IA juega
    // Profundidad 1 = Oponente responde → EVALUAR HEURÍSTICA
    private static final int PROFUNDIDAD_MAX = 1;
private static Minimax.LogCallback logCallback = null;

public static void setLogCallback(Minimax.LogCallback callback) {
    logCallback = callback;
}

private static void println(String mensaje) {
    System.out.println(mensaje);
    if (logCallback != null) {
        logCallback.log(mensaje + "\n");
    }
}

private static void print(String mensaje) {
    System.out.print(mensaje);
    if (logCallback != null) {
        logCallback.log(mensaje);
    }
}
    public static int alphabeta(Tablero estado, int profundidad, int alpha, int beta,
                                boolean esMax, char jugador, char oponente, int filaJugada, int colJugada,
                                boolean usarSimetria, boolean mostrarTableros) {

        VisualizadorArbolMejorado.incrementarNodos();

        // Verificar estados terminales
        if (estado.hayGanador(jugador)) {
            int valor = 10 - profundidad;
            VisualizadorArbolMejorado.imprimirEstadoTerminal(profundidad, "Victoria de " + jugador, valor);
            if (mostrarTableros && profundidad <= 1) {
                String indentacion = "  ".repeat(profundidad);
                imprimirTableroCompacto(estado, indentacion);
            }
            return valor;
        }

        if (estado.hayGanador(oponente)) {
            int valor = profundidad - 10;
            VisualizadorArbolMejorado.imprimirEstadoTerminal(profundidad, "Victoria de " + oponente, valor);
            if (mostrarTableros && profundidad <= 1) {
                String indentacion = "  ".repeat(profundidad);
                imprimirTableroCompacto(estado, indentacion);
            }
            return valor;
        }

        if (estado.tableroLleno()) {
            VisualizadorArbolMejorado.imprimirEstadoTerminal(profundidad, "Empate", 0);
            if (mostrarTableros && profundidad <= 1) {
                String indentacion = "  ".repeat(profundidad);
                imprimirTableroCompacto(estado, indentacion);
            }
            return 0;
        }

        // ✅ LÍMITE DE PROFUNDIDAD: Detener en profundidad 2 y usar heurística
        if (profundidad >= PROFUNDIDAD_MAX) {
            int valorHeuristico = Evaluador.evaluar(estado, jugador, oponente);
            if (mostrarTableros && profundidad <= 1) {
                String indentacion = "  ".repeat(profundidad);
                System.out.println(indentacion + "🔍 Heurística: " + valorHeuristico);
            }
            return valorHeuristico;
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

                // Calcular heurística ANTES de mostrar
                int heuristica = Evaluador.evaluar(nuevo, jugador, oponente);

                // MOSTRAR TABLERO ANTES de la recursión si profundidad <= 1
                if (mostrarTableros && profundidad <= 1) {
                    String extra = "α=" + alpha + " β=" + beta + " H=" + heuristica;
                    if (usarSimetria && profundidad == 0) {
                        String tipo = DetectorSimetria.clasificarMovimiento(i, j);
                        extra += " [" + tipo + "]";
                    }
                    
                    String indentacion = "  ".repeat(profundidad);
                    String simbolo = "▲";
                    System.out.println(indentacion + simbolo + " Prof:" + profundidad +
                            " Mov:(" + i + "," + j + ") " +
                            "Jugador:" + jugador + " " + extra);
                    imprimirTableroCompacto(nuevo, indentacion);
                }

                int valor = alphabeta(nuevo, profundidad + 1, alpha, beta, false,
                        jugador, oponente, i, j, usarSimetria, mostrarTableros);

                String extra = "α=" + alpha + " β=" + beta + " H=" + heuristica;
                if (mejor < valor) extra += " ⬆️ MEJOR";
                if (usarSimetria && profundidad == 0) {
                    String tipo = DetectorSimetria.clasificarMovimiento(i, j);
                    extra += " [" + tipo + "]";
                }

                // Solo imprimir info del nodo (sin tablero) si profundidad > 1 o sin mostrarTableros
                if (!mostrarTableros || profundidad > 1) {
                    VisualizadorArbolMejorado.imprimirNodo(profundidad, "MAX", i, j, jugador, valor, extra);
                } else {
                    // Ya mostramos el tablero, solo mostrar el valor
                    String indentacion = "  ".repeat(profundidad);
                    System.out.println(indentacion + "   → H=" + heuristica + " (Valor: " + valor + ")" + 
                                     (mejor < valor ? " ⬆️ MEJOR" : ""));
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

                // Calcular heurística ANTES de mostrar
                int heuristica = Evaluador.evaluar(nuevo, jugador, oponente);

                // MOSTRAR TABLERO ANTES de la recursión si profundidad <= 1
                if (mostrarTableros && profundidad <= 1) {
                    String extra = "α=" + alpha + " β=" + beta + " H=" + heuristica;
                    if (usarSimetria && profundidad == 0) {
                        String tipo = DetectorSimetria.clasificarMovimiento(i, j);
                        extra += " [" + tipo + "]";
                    }
                    
                    String indentacion = "  ".repeat(profundidad);
                    String simbolo = "▼";
                    System.out.println(indentacion + simbolo + " Prof:" + profundidad +
                            " Mov:(" + i + "," + j + ") " +
                            "Jugador:" + oponente + " " + extra);
                    imprimirTableroCompacto(nuevo, indentacion);
                }

                int valor = alphabeta(nuevo, profundidad + 1, alpha, beta, true,
                        jugador, oponente, i, j, usarSimetria, mostrarTableros);

                String extra = "α=" + alpha + " β=" + beta + " H=" + heuristica;
                if (peor > valor) extra += " ⬇️ PEOR";
                if (usarSimetria && profundidad == 0) {
                    String tipo = DetectorSimetria.clasificarMovimiento(i, j);
                    extra += " [" + tipo + "]";
                }

                // Solo imprimir info del nodo (sin tablero) si profundidad > 1 o sin mostrarTableros
                if (!mostrarTableros || profundidad > 1) {
                    VisualizadorArbolMejorado.imprimirNodo(profundidad, "MIN", i, j, oponente, valor, extra);
                } else {
                    // Ya mostramos el tablero, solo mostrar el valor
                    String indentacion = "  ".repeat(profundidad);
                    System.out.println(indentacion + "   → H=" + heuristica + " (Valor: " + valor + ")" + 
                                     (peor > valor ? " ⬇️ PEOR" : ""));
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
            System.out.print(indentacion + "   ");
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
            String titulo = "ALFA-BETA (Profundidad " + PROFUNDIDAD_MAX + ")";
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
        if (mostrarTableros) {
            System.out.println("   📊 Profundidad 0: Opciones de IA (con heurística)");
            System.out.println("   📊 Profundidad 1: Respuestas del oponente");
            System.out.println("   📊 Profundidad 2: Evaluar con heurística");
        }
        System.out.println();

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
                System.out.println("╔════════════════════════════════════════╗");
                System.out.println("🎯 Explorando: (" + i + ", " + j + ")" + tipo);
                System.out.println("╚════════════════════════════════════════╝");
            }

            int valor = alphabeta(copia, 0, Integer.MIN_VALUE, Integer.MAX_VALUE,
                    false, jugador, oponente, i, j, usarSimetria, mostrarTableros);

            String tipo = usarSimetria ? " [" + DetectorSimetria.clasificarMovimiento(i, j) + "]" : "";
            int heuristica = Evaluador.evaluar(copia, jugador, oponente);
            System.out.println("\n📌 Resultado movimiento (" + i + ", " + j + ")" + tipo);
            System.out.println("   Heurística inmediata: " + heuristica);
            System.out.println("   Valor después de análisis: " + valor +
                    (valor > mejorValor ? " ⭐ NUEVO MEJOR" : ""));
            System.out.println();

            if (valor > mejorValor) {
                mejorValor = valor;
                mejorMovimiento[0] = i;
                mejorMovimiento[1] = j;
            }
        }

        if (visualizar) {
            VisualizadorArbolMejorado.imprimirResumen();
            System.out.println("\n✅ Mejor movimiento: (" + mejorMovimiento[0] + ", " + mejorMovimiento[1] + 
                             ") con valor " + mejorValor);
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