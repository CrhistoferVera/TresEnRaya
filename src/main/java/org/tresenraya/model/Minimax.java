package org.tresenraya.model;

public class Minimax {

    private static final int PROFUNDIDAD_MAX = 1;

    public static int minimax(Tablero estado, int profundidad, boolean esMax, char jugador, char oponente, int filaJugada, int colJugada, boolean mostrarTableros) {

        if (estado.hayGanador(jugador)) {
            return 1000;
        }

        if (estado.hayGanador(oponente)) {
            return  -1000;
        }

        if (estado.tableroLleno()) {
            return 0;
        }

        if (profundidad >= PROFUNDIDAD_MAX) {
            return Evaluador.evaluar(estado, jugador, oponente);
        }

        java.util.List<DetectorSimetria.Posicion> movimientos = 
            DetectorSimetria.obtenerMovimientosUnicos(estado);

        if (esMax) {
            int mejor = Integer.MIN_VALUE;
            for (DetectorSimetria.Posicion pos : movimientos) {
                Tablero nuevo = new Tablero(estado.getMatriz());
                nuevo.hacerMovimiento(pos.fila, pos.col, jugador);
                int valor = minimax(nuevo, profundidad + 1, false, jugador, oponente, pos.fila, pos.col, mostrarTableros);
                mejor = Math.max(mejor, valor);
            }
            return mejor;
        } else {
            int peor = Integer.MAX_VALUE;
            for (DetectorSimetria.Posicion pos : movimientos) {
                Tablero nuevo = new Tablero(estado.getMatriz());
                nuevo.hacerMovimiento(pos.fila, pos.col, oponente);
                int heuristica = Evaluador.evaluar(nuevo, jugador, oponente);

                if (mostrarTableros && profundidad == 0) {
                    System.out.println("Mov:(" + pos.fila + "," + pos.col + ") Jugador:" + oponente);
                    imprimirTableroCompacto(nuevo, "");
                    System.out.println("Heuristica: " + heuristica);
                    System.out.println();
                }

                int valor = minimax(nuevo, profundidad + 1, true, jugador, oponente, pos.fila, pos.col, mostrarTableros);
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

    public static int[] mejorMovimiento(Tablero t, char jugador, char oponente, boolean visualizar, boolean mostrarTableros) {

        int mejorValor = Integer.MIN_VALUE;
        int[] mejorMovimiento = {-1, -1};

        java.util.List<DetectorSimetria.Posicion> movimientos = 
            DetectorSimetria.obtenerMovimientosUnicos(t);

        for (DetectorSimetria.Posicion pos : movimientos) {
            Tablero copia = new Tablero(t.getMatriz());
            copia.hacerMovimiento(pos.fila, pos.col, jugador);

            if (visualizar) {
                System.out.println("----------------------------------------");
                System.out.println("Explorando: (" + pos.fila + ", " + pos.col + ")");
                System.out.println("----------------------------------------");
            }

            int valor = minimax(copia, 0, false, jugador, oponente, pos.fila, pos.col, mostrarTableros);

            if (visualizar) {
                System.out.println("Resultado movimiento (" + pos.fila + ", " + pos.col + ")");
                System.out.println("   Valor despues de analisis: " + valor);
                System.out.println();
            }

            if (valor > mejorValor) {
                mejorValor = valor;
                mejorMovimiento[0] = pos.fila;
                mejorMovimiento[1] = pos.col;
            }
        }

        System.out.println("Opcion escogida: (" + mejorMovimiento[0] + ", " + mejorMovimiento[1] + ")");
        return mejorMovimiento;
    }

    public static int[] mejorMovimiento(Tablero t, char jugador, char oponente, boolean visualizar) {
        return mejorMovimiento(t, jugador, oponente, visualizar, true);
    }

    public static int[] mejorMovimiento(Tablero t, char jugador, char oponente) {
        return mejorMovimiento(t, jugador, oponente, false, true);
    }

    public static int minimax(Tablero estado, int profundidad, boolean esMax, char jugador, char oponente, int filaJugada, int colJugada) {
        return minimax(estado, profundidad, esMax, jugador, oponente, filaJugada, colJugada, true);
    }
}
