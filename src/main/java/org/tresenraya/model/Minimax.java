package org.tresenraya.model;

public class Minimax {

    public static int minimax(Tablero estado, int profundidad, boolean esMax,
                              char jugador, char oponente, int filaJugada, int colJugada,
                              boolean mostrarTableros) {

        VisualizadorArbolMejorado.incrementarNodos();

        if (estado.hayGanador(jugador)) {
            int valor = 10 - profundidad;
            VisualizadorArbolMejorado.imprimirEstadoTerminal(profundidad, "Victoria de " + jugador, valor);
            // ✅ CAMBIO: Solo profundidad 0
            if (mostrarTableros && profundidad == 0) {
                imprimirTableroCompacto(estado, "  ".repeat(profundidad));
            }
            return valor;
        }

        if (estado.hayGanador(oponente)) {
            int valor = profundidad - 10;
            VisualizadorArbolMejorado.imprimirEstadoTerminal(profundidad, "Victoria de " + oponente, valor);
            // ✅ CAMBIO: Solo profundidad 0
            if (mostrarTableros && profundidad == 0) {
                imprimirTableroCompacto(estado, "  ".repeat(profundidad));
            }
            return valor;
        }

        if (estado.tableroLleno()) {
            VisualizadorArbolMejorado.imprimirEstadoTerminal(profundidad, "Empate", 0);
            // ✅ CAMBIO: Solo profundidad 0
            if (mostrarTableros && profundidad == 0) {
                imprimirTableroCompacto(estado, "  ".repeat(profundidad));
            }
            return 0;
        }

        java.util.List<DetectorSimetria.Posicion> movimientos = 
            DetectorSimetria.obtenerMovimientosUnicos(estado);

        if (esMax) {
            int mejor = Integer.MIN_VALUE;
            for (DetectorSimetria.Posicion pos : movimientos) {
                int i = pos.fila;
                int j = pos.col;
                
                Tablero nuevo = new Tablero(estado.getMatriz());
                nuevo.hacerMovimiento(i, j, jugador);

                // ✅ CAMBIO: Solo profundidad 0
                if (mostrarTableros && profundidad == 0) {
                    String tipo = DetectorSimetria.clasificarMovimiento(i, j);
                    String extra = " [" + tipo + "]";
                    
                    String indentacion = "  ".repeat(profundidad);
                    String simbolo = "▲";
                    System.out.println(indentacion + simbolo + " Prof:" + profundidad +
                            " Mov:(" + i + "," + j + ") " +
                            "Jugador:" + jugador + extra);
                    imprimirTableroCompacto(nuevo, indentacion);
                }

                int valor = minimax(nuevo, profundidad + 1, false, jugador, oponente, i, j, mostrarTableros);

                String extra = mejor < valor ? "⬆️ MEJOR" : "";
                if (profundidad == 0) {
                    String tipo = DetectorSimetria.clasificarMovimiento(i, j);
                    extra += " [" + tipo + "]";
                }
                
                // ✅ CAMBIO: Solo profundidad 0
                if (!mostrarTableros || profundidad > 0) {
                    VisualizadorArbolMejorado.imprimirNodo(profundidad, "MAX", i, j, jugador, valor, extra);
                } else {
                    String indentacion = "  ".repeat(profundidad);
                    System.out.println(indentacion + "   → Valor: " + valor + (extra.isEmpty() ? "" : " " + extra));
                }
                
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

                // ✅ CAMBIO: Solo profundidad 0
                if (mostrarTableros && profundidad == 0) {
                    String tipo = DetectorSimetria.clasificarMovimiento(i, j);
                    String extra = " [" + tipo + "]";
                    
                    String indentacion = "  ".repeat(profundidad);
                    String simbolo = "▼";
                    System.out.println(indentacion + simbolo + " Prof:" + profundidad +
                            " Mov:(" + i + "," + j + ") " +
                            "Jugador:" + oponente + extra);
                    imprimirTableroCompacto(nuevo, indentacion);
                }

                int valor = minimax(nuevo, profundidad + 1, true, jugador, oponente, i, j, mostrarTableros);

                String extra = peor > valor ? "⬇️ PEOR" : "";
                if (profundidad == 0) {
                    String tipo = DetectorSimetria.clasificarMovimiento(i, j);
                    extra += " [" + tipo + "]";
                }
                
                // ✅ CAMBIO: Solo profundidad 0
                if (!mostrarTableros || profundidad > 0) {
                    VisualizadorArbolMejorado.imprimirNodo(profundidad, "MIN", i, j, oponente, valor, extra);
                } else {
                    String indentacion = "  ".repeat(profundidad);
                    System.out.println(indentacion + "   → Valor: " + valor + (extra.isEmpty() ? "" : " " + extra));
                }
                
                peor = Math.min(peor, valor);
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

    public static int[] mejorMovimiento(Tablero t, char jugador, char oponente, 
                                       boolean visualizar, boolean mostrarTableros) {
        if (visualizar) {
            VisualizadorArbolMejorado.reiniciar();
            VisualizadorArbolMejorado.setMostrarDetalles(true);
            String titulo = "MINIMAX CON SIMETRÍA";
            if (mostrarTableros) titulo += " + TABLEROS";
            VisualizadorArbolMejorado.imprimirEncabezado(titulo);
            DetectorSimetria.imprimirInfoSimetria(t);
        }

        int mejorValor = Integer.MIN_VALUE;
        int[] mejorMovimiento = {-1, -1};

        if (visualizar) {
            System.out.println("\n🔍 Evaluando movimientos posibles:");
            if (mostrarTableros) {
                System.out.println("   📊 Mostrando solo las opciones inmediatas de la IA");
            }
            System.out.println();
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
                System.out.println("═══════════════════════════════════════════");
                System.out.println("🎯 Explorando: (" + i + ", " + j + ") [" + tipo + "]");
                System.out.println("═══════════════════════════════════════════");
            }

            int valor = minimax(copia, 0, false, jugador, oponente, i, j, mostrarTableros);

            if (visualizar) {
                String tipo = DetectorSimetria.clasificarMovimiento(i, j);
                System.out.println("\n📌 Resultado movimiento (" + i + ", " + j + ") [" + tipo + "]: " + valor +
                        (valor > mejorValor ? " ⭐ NUEVO MEJOR" : ""));
                System.out.println();
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

    public static int[] mejorMovimiento(Tablero t, char jugador, char oponente, boolean visualizar) {
        return mejorMovimiento(t, jugador, oponente, visualizar, false);
    }

    public static int[] mejorMovimiento(Tablero t, char jugador, char oponente) {
        return mejorMovimiento(t, jugador, oponente, false, false);
    }

    public static int minimax(Tablero estado, int profundidad, boolean esMax,
                              char jugador, char oponente, int filaJugada, int colJugada) {
        return minimax(estado, profundidad, esMax, jugador, oponente, filaJugada, colJugada, false);
    }
}