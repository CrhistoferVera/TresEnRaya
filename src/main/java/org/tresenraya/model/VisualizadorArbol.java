package org.tresenraya.model;

public class VisualizadorArbol {
    private static int nodosExplorados = 0;
    private static int nodosPoados = 0;
    private static boolean mostrarDetalles = false;

    public static void reiniciar() {
        nodosExplorados = 0;
        nodosPoados = 0;
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
