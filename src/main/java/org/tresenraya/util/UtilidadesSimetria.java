package org.tresenraya.util;

import org.tresenraya.model.Tablero;
import java.util.*;

/**
 * Utilidades para detectar y aprovechar simetrías en el tablero
 */
public class UtilidadesSimetria {

    // Cache de estados ya evaluados (tablero normalizado -> valor)
    private static Map<String, Integer> cacheEstados = new HashMap<>();
    private static int ahorros = 0;

    public static void reiniciarCache() {
        cacheEstados.clear();
        ahorros = 0;
    }

    public static int getAhorros() {
        return ahorros;
    }

    public static int getTamañoCache() {
        return cacheEstados.size();
    }

    /**
     * Obtiene el valor cacheado si existe, o null si no
     */
    public static Integer obtenerValorCache(Tablero tablero) {
        String clave = normalizarTablero(tablero);
        Integer valor = cacheEstados.get(clave);
        if (valor != null) {
            ahorros++;
        }
        return valor;
    }

    /**
     * Guarda un valor en el cache
     */
    public static void guardarEnCache(Tablero tablero, int valor) {
        String clave = normalizarTablero(tablero);
        cacheEstados.put(clave, valor);
    }

    /**
     * Normaliza el tablero a su forma canónica considerando todas las simetrías
     */
    private static String normalizarTablero(Tablero tablero) {
        char[][] matriz = tablero.getMatriz();

        // Generar todas las transformaciones simétricas
        List<String> transformaciones = new ArrayList<>();

        transformaciones.add(matrizAString(matriz));
        transformaciones.add(matrizAString(rotar90(matriz)));
        transformaciones.add(matrizAString(rotar180(matriz)));
        transformaciones.add(matrizAString(rotar270(matriz)));
        transformaciones.add(matrizAString(reflejarHorizontal(matriz)));
        transformaciones.add(matrizAString(reflejarVertical(matriz)));
        transformaciones.add(matrizAString(reflejarDiagonalPrincipal(matriz)));
        transformaciones.add(matrizAString(reflejarDiagonalSecundaria(matriz)));

        // Retornar la representación lexicográficamente menor (forma canónica)
        Collections.sort(transformaciones);
        return transformaciones.get(0);
    }

    /**
     * Obtiene movimientos únicos considerando simetría
     * En un tablero vacío, solo hay 3 movimientos únicos: esquina, borde, centro
     */
    public static List<int[]> obtenerMovimientosUnicos(Tablero tablero) {
        List<int[]> todosMovimientos = new ArrayList<>();
        Set<String> movimientosVistos = new HashSet<>();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (tablero.esMovimientoValido(i, j)) {
                    // Crear tablero temporal con este movimiento
                    char[][] matrizTemp = copiarMatriz(tablero.getMatriz());
                    matrizTemp[i][j] = 'T'; // Temporal

                    // Normalizar
                    String forma = normalizarMatriz(matrizTemp);

                    if (!movimientosVistos.contains(forma)) {
                        movimientosVistos.add(forma);
                        todosMovimientos.add(new int[]{i, j});
                    }
                }
            }
        }

        return todosMovimientos;
    }

    /**
     * Normaliza una matriz considerando simetrías
     */
    private static String normalizarMatriz(char[][] matriz) {
        List<String> transformaciones = new ArrayList<>();

        transformaciones.add(matrizAString(matriz));
        transformaciones.add(matrizAString(rotar90(matriz)));
        transformaciones.add(matrizAString(rotar180(matriz)));
        transformaciones.add(matrizAString(rotar270(matriz)));
        transformaciones.add(matrizAString(reflejarHorizontal(matriz)));
        transformaciones.add(matrizAString(reflejarVertical(matriz)));
        transformaciones.add(matrizAString(reflejarDiagonalPrincipal(matriz)));
        transformaciones.add(matrizAString(reflejarDiagonalSecundaria(matriz)));

        Collections.sort(transformaciones);
        return transformaciones.get(0);
    }

    // ========== TRANSFORMACIONES ==========

    private static char[][] rotar90(char[][] matriz) {
        char[][] resultado = new char[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                resultado[j][2 - i] = matriz[i][j];
            }
        }
        return resultado;
    }

    private static char[][] rotar180(char[][] matriz) {
        char[][] resultado = new char[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                resultado[2 - i][2 - j] = matriz[i][j];
            }
        }
        return resultado;
    }

    private static char[][] rotar270(char[][] matriz) {
        char[][] resultado = new char[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                resultado[2 - j][i] = matriz[i][j];
            }
        }
        return resultado;
    }

    private static char[][] reflejarHorizontal(char[][] matriz) {
        char[][] resultado = new char[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                resultado[i][2 - j] = matriz[i][j];
            }
        }
        return resultado;
    }

    private static char[][] reflejarVertical(char[][] matriz) {
        char[][] resultado = new char[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                resultado[2 - i][j] = matriz[i][j];
            }
        }
        return resultado;
    }

    private static char[][] reflejarDiagonalPrincipal(char[][] matriz) {
        char[][] resultado = new char[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                resultado[j][i] = matriz[i][j];
            }
        }
        return resultado;
    }

    private static char[][] reflejarDiagonalSecundaria(char[][] matriz) {
        char[][] resultado = new char[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                resultado[2 - j][2 - i] = matriz[i][j];
            }
        }
        return resultado;
    }

    // ========== UTILIDADES ==========

    private static String matrizAString(char[][] matriz) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                sb.append(matriz[i][j]);
            }
        }
        return sb.toString();
    }

    private static char[][] copiarMatriz(char[][] matriz) {
        char[][] copia = new char[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                copia[i][j] = matriz[i][j];
            }
        }
        return copia;
    }

    /**
     * Imprime información sobre el uso del cache
     */
    public static void imprimirEstadisticas() {
        System.out.println("\n🔄 ESTADÍSTICAS DE SIMETRÍA:");
        System.out.println("   Estados únicos en cache: " + cacheEstados.size());
        System.out.println("   Ahorros por simetría: " + ahorros);
        if (ahorros > 0) {
            System.out.println("   ⚡ Se evitaron " + ahorros + " evaluaciones redundantes");
        }
    }
}
