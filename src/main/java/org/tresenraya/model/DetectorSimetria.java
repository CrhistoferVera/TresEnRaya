package org.tresenraya.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class DetectorSimetria {
    
    public static class Posicion {
        public int fila;
        public int col;
        
        public Posicion(int fila, int col) {
            this.fila = fila;
            this.col = col;
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Posicion p = (Posicion) o;
            return fila == p.fila && col == p.col;
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(fila, col);
        }
        
        @Override
        public String toString() {
            return "(" + fila + "," + col + ")";
        }
    }
    
    public static List<Posicion> obtenerMovimientosUnicos(Tablero tablero) {
        List<Posicion> todosMovimientos = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (tablero.esMovimientoValido(i, j)) {
                    todosMovimientos.add(new Posicion(i, j));
                }
            }
        }
        if (esTableroVacio(tablero)) {
            List<Posicion> movimientosRepresentativos = new ArrayList<>();
            movimientosRepresentativos.add(new Posicion(0, 0));
            movimientosRepresentativos.add(new Posicion(1, 1));
            movimientosRepresentativos.add(new Posicion(0, 1));
            return movimientosRepresentativos;
        }
        return eliminarSimetricos(tablero, todosMovimientos);
    }
    
    private static boolean esTableroVacio(Tablero tablero) {
        char[][] matriz = tablero.getMatriz();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (matriz[i][j] != '-') {
                    return false;
                }
            }
        }
        return true;
    }
    
    private static List<Posicion> eliminarSimetricos(Tablero tablero, List<Posicion> movimientos) {
        List<Posicion> unicos = new ArrayList<>();
        Set<String> estadosVistos = new HashSet<>();
        for (Posicion mov : movimientos) {
            Tablero copia = new Tablero(tablero.getMatriz());
            copia.hacerMovimiento(mov.fila, mov.col, 'T');
            String estadoCanonigo = obtenerEstadoCanonigo(copia);
            if (!estadosVistos.contains(estadoCanonigo)) {
                estadosVistos.add(estadoCanonigo);
                unicos.add(mov);
            }
        }
        return unicos;
    }
    
    private static String obtenerEstadoCanonigo(Tablero tablero) {
        char[][] original = tablero.getMatriz();
        List<String> transformaciones = new ArrayList<>();
        transformaciones.add(matrizAString(original));
        transformaciones.add(matrizAString(rotar90(original)));
        transformaciones.add(matrizAString(rotar90(rotar90(original))));
        transformaciones.add(matrizAString(rotar90(rotar90(rotar90(original)))));
        char[][] reflejada = reflejarHorizontal(original);
        transformaciones.add(matrizAString(reflejada));
        transformaciones.add(matrizAString(rotar90(reflejada)));
        transformaciones.add(matrizAString(rotar90(rotar90(reflejada))));
        transformaciones.add(matrizAString(rotar90(rotar90(rotar90(reflejada)))));
        Collections.sort(transformaciones);
        return transformaciones.get(0);
    }
    
    private static char[][] rotar90(char[][] matriz) {
        char[][] rotada = new char[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                rotada[j][2 - i] = matriz[i][j];
            }
        }
        return rotada;
    }
    
    private static char[][] reflejarHorizontal(char[][] matriz) {
        char[][] reflejada = new char[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                reflejada[i][2 - j] = matriz[i][j];
            }
        }
        return reflejada;
    }
    
    private static String matrizAString(char[][] matriz) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                sb.append(matriz[i][j]);
            }
        }
        return sb.toString();
    }
    
    public static String clasificarMovimiento(int fila, int col) {
        if (fila == 1 && col == 1) {
            return "CENTRO";
        } else if ((fila == 0 || fila == 2) && (col == 0 || col == 2)) {
            return "ESQUINA";
        } else {
            return "LADO";
        }
    }
    
    public static List<Posicion> obtenerEquivalentes(int fila, int col) {
        List<Posicion> equivalentes = new ArrayList<>();
        String tipo = clasificarMovimiento(fila, col);
        switch (tipo) {
            case "CENTRO":
                equivalentes.add(new Posicion(1, 1));
                break;
            case "ESQUINA":
                equivalentes.add(new Posicion(0, 0));
                equivalentes.add(new Posicion(0, 2));
                equivalentes.add(new Posicion(2, 0));
                equivalentes.add(new Posicion(2, 2));
                break;
            case "LADO":
                equivalentes.add(new Posicion(0, 1));
                equivalentes.add(new Posicion(1, 0));
                equivalentes.add(new Posicion(1, 2));
                equivalentes.add(new Posicion(2, 1));
                break;
        }
        return equivalentes;
    }
    
    public static void imprimirInfoSimetria(Tablero tablero) {
        List<Posicion> todos = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (tablero.esMovimientoValido(i, j)) {
                    todos.add(new Posicion(i, j));
                }
            }
        }
        List<Posicion> unicos = obtenerMovimientosUnicos(tablero);
        System.out.println("\nAnalisis de simetria:");
        System.out.println("   Movimientos totales: " + todos.size());
        System.out.println("   Movimientos unicos: " + unicos.size());
        System.out.println("   Reduccion: " + (todos.size() - unicos.size()) + " movimientos eliminados");
        if (esTableroVacio(tablero)) {
            System.out.println("\nMovimientos representativos (tablero vacio):");
            for (Posicion p : unicos) {
                String tipo = clasificarMovimiento(p.fila, p.col);
                List<Posicion> equiv = obtenerEquivalentes(p.fila, p.col);
                System.out.println("   " + p + " - " + tipo + " (representa " + equiv.size() + " posiciones)");
            }
        }
    }
}
