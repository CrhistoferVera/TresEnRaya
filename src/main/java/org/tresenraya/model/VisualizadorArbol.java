package org.tresenraya.model;
import org.tresenraya.model.PanelArbolDecision;
import java.util.Stack;

public class VisualizadorArbol {
    private static int nodosExplorados = 0;
    private static int nodosPoados = 0;
    private static boolean mostrarDetalles = false;
    private static PanelArbolDecision.NodoArbol raizArbol = null;
    private static Stack<PanelArbolDecision.NodoArbol> pilaActual = new Stack<>();
    private static boolean capturandoArbol = false;

    public static void reiniciar() {
        nodosExplorados = 0;
        nodosPoados = 0;
        raizArbol = null;
        pilaActual.clear();
    }

    public static void setMostrarDetalles(boolean mostrar) {
        mostrarDetalles = mostrar;
    }

    public static void setCapturandoArbol(boolean capturar) {
        capturandoArbol = capturar;
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

    public static PanelArbolDecision.NodoArbol getRaizArbol() {
        return raizArbol;
    }

    public static void iniciarNodo(int[] movimiento, boolean esMax, int alpha, int beta) {
        if (!capturandoArbol) return;

        PanelArbolDecision.NodoArbol nodo = new PanelArbolDecision.NodoArbol(
                movimiento != null ? movimiento.clone() : null,
                Integer.MIN_VALUE,
                esMax
        );
        nodo.alpha = alpha;
        nodo.beta = beta;

        if (raizArbol == null) {
            raizArbol = nodo;
        } else if (!pilaActual.isEmpty()) {
            pilaActual.peek().agregarHijo(nodo);
        }

        pilaActual.push(nodo);
    }

    public static void finalizarNodo(int valor) {
        if (!capturandoArbol || pilaActual.isEmpty()) return;

        PanelArbolDecision.NodoArbol nodo = pilaActual.pop();
        nodo.valor = valor;
    }

    public static void marcarMejorMovimiento(int[] movimiento) {
        if (!capturandoArbol || raizArbol == null) return;
        marcarMejorEnNodo(raizArbol, movimiento);
    }

    private static void marcarMejorEnNodo(PanelArbolDecision.NodoArbol nodo, int[] movimiento) {
        if (nodo.movimiento != null &&
                nodo.movimiento[0] == movimiento[0] &&
                nodo.movimiento[1] == movimiento[1]) {
            nodo.esMejor = true;
            return;
        }

        for (PanelArbolDecision.NodoArbol hijo : nodo.hijos) {
            marcarMejorEnNodo(hijo, movimiento);
        }
    }

    public static void marcarPoda() {
        if (!capturandoArbol || pilaActual.isEmpty()) return;

        PanelArbolDecision.NodoArbol nodo = pilaActual.peek();
        nodo.esPoda = true;
        incrementarPodas();
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
        marcarPoda();
    }

    public static void imprimirEstadoTerminal(int profundidad, String resultado, int valor) {
        if (!mostrarDetalles) return;

        String indentacion = "  ".repeat(profundidad);
        System.out.println(indentacion + "🏁 " + resultado + " | Valor: " + valor);
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