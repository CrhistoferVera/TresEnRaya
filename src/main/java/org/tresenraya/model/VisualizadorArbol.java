package org.tresenraya.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * VisualizadorArbol CORREGIDO
 * ✅ Sistema con Stack que mantiene contexto correcto de padres
 */
public class VisualizadorArbol {
    private static int nodosExplorados = 0;
    private static int nodosPoados = 0;
    private static boolean mostrarDetalles = false;
    
    private static Nodo raiz;
    private static Stack<Nodo> pilaContexto = new Stack<>();
    private static Map<Integer, Nodo> ultimoPorProfundidad = new HashMap<>();

    public static void reiniciar() {
        nodosExplorados = 0;
        nodosPoados = 0;
        raiz = null;
        pilaContexto.clear();
        ultimoPorProfundidad.clear();
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

    /**
     * ✅ CORREGIDO: Registra un nodo manteniendo el contexto de la pila
     */
    public static void registrarNodo(int profundidad, String tipo, int fila, int col,
                                     char jugador, int valor, int alpha, int beta) {
        Nodo nodo = new Nodo(fila, col, jugador, tipo, profundidad);
        nodo.setValor(valor);
        nodo.setAlpha(alpha);
        nodo.setBeta(beta);

        // Limpiar la pila hasta la profundidad correcta
        while (!pilaContexto.isEmpty() && pilaContexto.peek().getProfundidad() >= profundidad) {
            pilaContexto.pop();
        }

        if (profundidad == 0) {
            // Nodo raíz de una rama principal
            if (raiz == null) {
                raiz = nodo;
            }
            pilaContexto.push(nodo);
        } else {
            // Buscar el padre correcto desde la pila
            Nodo padre = null;
            
            if (!pilaContexto.isEmpty()) {
                // El padre es el último nodo en la pila (debería ser profundidad-1)
                padre = pilaContexto.peek();
                
                // Verificar que el padre tiene la profundidad correcta
                if (padre.getProfundidad() == profundidad - 1) {
                    padre.agregarHijo(nodo);
                }
            }
            
            pilaContexto.push(nodo);
        }
        
        // Actualizar el mapa de últimos nodos por profundidad
        ultimoPorProfundidad.put(profundidad, nodo);
    }

    public static void marcarPodado() {
        if (!pilaContexto.isEmpty()) {
            pilaContexto.peek().setPodado(true);
        }
    }

    public static void registrarTerminal(int profundidad, String resultado, int valor) {
        if (!pilaContexto.isEmpty()) {
            Nodo nodo = pilaContexto.peek();
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
        
        // Contar nodos totales en el árbol
        int totalNodos = contarNodos(raiz);
        System.out.println("   Nodos en árbol visual: " + totalNodos);
        System.out.println("   Nodos podados: " + nodosPoados);
        System.out.println("   Eficiencia: " + (nodosPoados > 0 ?
                String.format("%.1f%%", (nodosPoados * 100.0 / (nodosExplorados + nodosPoados))) :
                "0%"));
    }
    
    private static int contarNodos(Nodo nodo) {
        if (nodo == null) return 0;
        int count = 1;
        for (Nodo hijo : nodo.getHijos()) {
            count += contarNodos(hijo);
        }
        return count;
    }

    public static void imprimirEncabezado(String algoritmo) {
        System.out.println("\n" + "═".repeat(60));
        System.out.println("🌳 ÁRBOL DE DECISIÓN - " + algoritmo.toUpperCase());
        System.out.println("═".repeat(60));
    }
    
    /**
     * DEBUG: Imprime la estructura del árbol para verificar
     */
    public static void imprimirEstructuraArbol() {
        System.out.println("\n🔍 DEBUG - Estructura del árbol:");
        imprimirEstructuraRecursivo(raiz, 0);
    }
    
    private static void imprimirEstructuraRecursivo(Nodo nodo, int nivel) {
        if (nodo == null) return;
        
        String indent = "  ".repeat(nivel);
        System.out.println(indent + "- Nodo Prof:" + nodo.getProfundidad() + 
                         " Pos:(" + nodo.getFila() + "," + nodo.getColumna() + ")" +
                         " Tipo:" + nodo.getTipo() + 
                         " Hijos:" + nodo.getHijos().size());
        
        for (Nodo hijo : nodo.getHijos()) {
            imprimirEstructuraRecursivo(hijo, nivel + 1);
        }
    }
}