// src/main/java/org/tresenraya/model/VisualizadorArbol.java
package org.tresenraya.model;

import java.util.Stack;

public class VisualizadorArbol {
    private static int nodosExplorados = 0;
    private static int nodosPoados = 0;
    private static boolean mostrarDetalles = false;
    
    private static Nodo raiz;
    private static Stack<Nodo> pilaActual = new Stack<>();

    public static void reiniciar() {
        nodosExplorados = 0;
        nodosPoados = 0;
        raiz = null;
        pilaActual.clear();
    }

    public static void setMostrarDetalles(boolean mostrar) {
        mostrarDetalles = mostrar;
    }

    public static void incrementarNodos() {
        nodosExplorados++;
    }

    public static void incrementarPodas() {
        nodosPoados++;
    }

    public static int getNodosExplorados() {
        return nodosExplorados;
    }

    public static int getNodosPodados() {
        return nodosPoados;
    }

    public static Nodo getRaiz() {
        return raiz;
    }

    public static void registrarNodo(int profundidad, String tipo, int fila, int col,
                                     char jugador, int valor, int alpha, int beta) {
        Nodo nodo = new Nodo(fila, col, jugador, tipo, profundidad);
        nodo.setValor(valor);
        nodo.setAlpha(alpha);
        nodo.setBeta(beta);

        if (raiz == null) {
            raiz = nodo;
            pilaActual.push(nodo);
        } else {
            while (!pilaActual.isEmpty() && pilaActual.peek().getProfundidad() >= profundidad) {
                pilaActual.pop();
            }
            
            if (!pilaActual.isEmpty()) {
                pilaActual.peek().agregarHijo(nodo);
            }
            pilaActual.push(nodo);
        }
    }

    public static void marcarPodado() {
        if (!pilaActual.isEmpty()) {
            pilaActual.peek().setPodado(true);
        }
    }

    public static void registrarTerminal(int profundidad, String resultado, int valor) {
        if (!pilaActual.isEmpty()) {
            Nodo nodo = pilaActual.peek();
            nodo.setTerminal(true);
            nodo.setEstadoTerminal(resultado);
            nodo.setValor(valor);
        }
    }

    public static void imprimirNodo(int profundidad, String tipo, int fila, int col,
                                    char jugador, int valor, String extra) {
        if (!mostrarDetalles) return;

        String indentacion = "  ".repeat(profundidad);
        String simbolo = tipo.equals("MAX") ? "▲" : "▼";

        System.out.println(indentacion + simbolo + " Prof:" + profundidad +
                " Mov:(" + fila + "," + col + ") " +
                "Jugador:" + jugador +
                " Valor:" + valor +
                (extra.isEmpty() ? "" : " " + extra));
    }

    public static void imprimirPoda(int profundidad, String tipo, int alpha, int beta) {
        if (!mostrarDetalles) return;

        String indentacion = "  ".repeat(profundidad);
        System.out.println(indentacion + "✂️ PODA " + tipo + " (α=" + alpha + ", β=" + beta + ")");
        incrementarPodas();
        marcarPodado();
    }

    public static void imprimirEstadoTerminal(int profundidad, String resultado, int valor) {
        if (!mostrarDetalles) return;

        String indentacion = "  ".repeat(profundidad);
        System.out.println(indentacion + "🏁 " + resultado + " | Valor: " + valor);
        registrarTerminal(profundidad, resultado, valor);
    }

    public static void imprimirResumen() {
        System.out.println("\n📊 ESTADÍSTICAS DEL ÁRBOL:");
        System.out.println("   Nodos explorados: " + nodosExplorados);
        System.out.println("   Nodos podados: " + nodosPoados);
        System.out.println("   Eficiencia: " + (nodosPoados > 0 ?
                String.format("%.1f%%", (nodosPoados * 100.0 / (nodosExplorados + nodosPoados))) :
                "0%"));
    }

    public static void imprimirEncabezado(String algoritmo) {
        System.out.println("\n" + "═".repeat(60));
        System.out.println("🌳 ÁRBOL DE DECISIÓN - " + algoritmo.toUpperCase());
        System.out.println("═".repeat(60));
    }
}