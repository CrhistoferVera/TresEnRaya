package org.tresenraya.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Detecta y maneja simetrías en el tablero de Tic Tac Toe
 * Para reducir el espacio de búsqueda
 */
public class DetectorSimetria {
    
    /**
     * Clase para representar una posición en el tablero
     */
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
    
    /**
     * Obtiene movimientos únicos considerando simetrías
     * En tablero vacío: solo 3 posiciones (esquina, centro, lado)
     * En tableros con movimientos: aplica todas las simetrías
     */
    public static List<Posicion> obtenerMovimientosUnicos(Tablero tablero) {
        List<Posicion> todosMovimientos = new ArrayList<>();
        
        // Obtener todos los movimientos válidos
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (tablero.esMovimientoValido(i, j)) {
                    todosMovimientos.add(new Posicion(i, j));
                }
            }
        }
        
        // Si el tablero está vacío, solo retornar posiciones representativas
        if (esTableroVacio(tablero)) {
            List<Posicion> movimientosRepresentativos = new ArrayList<>();
            movimientosRepresentativos.add(new Posicion(0, 0)); // Esquina
            movimientosRepresentativos.add(new Posicion(1, 1)); // Centro
            movimientosRepresentativos.add(new Posicion(0, 1)); // Lado
            return movimientosRepresentativos;
        }
        
        // Para tableros no vacíos, eliminar movimientos simétricos
        return eliminarSimetricos(tablero, todosMovimientos);
    }
    
    /**
     * Verifica si el tablero está completamente vacío
     */
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
    
    /**
     * Elimina movimientos que son equivalentes por simetría
     */
    private static List<Posicion> eliminarSimetricos(Tablero tablero, List<Posicion> movimientos) {
        List<Posicion> unicos = new ArrayList<>();
        Set<String> estadosVistos = new HashSet<>();
        
        for (Posicion mov : movimientos) {
            Tablero copia = new Tablero(tablero.getMatriz());
            copia.hacerMovimiento(mov.fila, mov.col, 'T'); // Marca temporal
            
            String estadoCanonigo = obtenerEstadoCanonigo(copia);
            
            if (!estadosVistos.contains(estadoCanonigo)) {
                estadosVistos.add(estadoCanonigo);
                unicos.add(mov);
            }
        }
        
        return unicos;
    }
    
    /**
     * Obtiene la representación canónica de un tablero
     * (la "menor" de todas sus rotaciones y reflexiones)
     */
    private static String obtenerEstadoCanonigo(Tablero tablero) {
        char[][] original = tablero.getMatriz();
        List<String> transformaciones = new ArrayList<>();
        
        // Generar todas las transformaciones (8 en total)
        transformaciones.add(matrizAString(original));
        transformaciones.add(matrizAString(rotar90(original)));
        transformaciones.add(matrizAString(rotar90(rotar90(original))));
        transformaciones.add(matrizAString(rotar90(rotar90(rotar90(original)))));
        
        char[][] reflejada = reflejarHorizontal(original);
        transformaciones.add(matrizAString(reflejada));
        transformaciones.add(matrizAString(rotar90(reflejada)));
        transformaciones.add(matrizAString(rotar90(rotar90(reflejada))));
        transformaciones.add(matrizAString(rotar90(rotar90(rotar90(reflejada)))));
        
        // Retornar la "menor" lexicográficamente
        Collections.sort(transformaciones);
        return transformaciones.get(0);
    }
    
    /**
     * Rota una matriz 90 grados en sentido horario
     */
    private static char[][] rotar90(char[][] matriz) {
        char[][] rotada = new char[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                rotada[j][2-i] = matriz[i][j];
            }
        }
        return rotada;
    }
    
    /**
     * Refleja una matriz horizontalmente
     */
    private static char[][] reflejarHorizontal(char[][] matriz) {
        char[][] reflejada = new char[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                reflejada[i][2-j] = matriz[i][j];
            }
        }
        return reflejada;
    }
    
    /**
     * Convierte una matriz a String para comparación
     */
    private static String matrizAString(char[][] matriz) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                sb.append(matriz[i][j]);
            }
        }
        return sb.toString();
    }
    
    /**
     * Clasifica un movimiento en un tablero vacío
     */
    public static String clasificarMovimiento(int fila, int col) {
        if (fila == 1 && col == 1) {
            return "CENTRO";
        } else if ((fila == 0 || fila == 2) && (col == 0 || col == 2)) {
            return "ESQUINA";
        } else {
            return "LADO";
        }
    }
    
    /**
     * Obtiene todas las posiciones equivalentes por simetría
     */
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
    
    /**
     * Imprime información sobre la reducción por simetría
     */
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
        
        System.out.println("\n🔄 ANÁLISIS DE SIMETRÍA:");
        System.out.println("   Movimientos totales: " + todos.size());
        System.out.println("   Movimientos únicos: " + unicos.size());
        System.out.println("   Reducción: " + (todos.size() - unicos.size()) + " movimientos eliminados");
        
        if (esTableroVacio(tablero)) {
            System.out.println("\n📍 Movimientos representativos (tablero vacío):");
            for (Posicion p : unicos) {
                String tipo = clasificarMovimiento(p.fila, p.col);
                List<Posicion> equiv = obtenerEquivalentes(p.fila, p.col);
                System.out.println("   " + p + " - " + tipo + " (representa " + equiv.size() + " posiciones)");
            }
        }
    }
}