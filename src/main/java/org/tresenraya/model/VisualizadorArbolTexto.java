package org.tresenraya.model;

import java.util.ArrayList;
import java.util.List;

public class VisualizadorArbolTexto {
    
    public static String generarArbolCompacto(Nodo raiz) {
        StringBuilder sb = new StringBuilder();
        sb.append("╔════════════════════════════════════════════╗\n");
        sb.append("║        🌳 ÁRBOL DE DECISIÓN              ║\n");
        sb.append("╚════════════════════════════════════════════╝\n\n");
        
        List<Nodo> nodosRaiz = obtenerNodosProfundidad0(raiz);
        
        if (nodosRaiz.isEmpty()) {
            sb.append("No hay nodos para mostrar.\n");
            return sb.toString();
        }
        
        for (int idx = 0; idx < nodosRaiz.size(); idx++) {
            Nodo nodoRaiz = nodosRaiz.get(idx);
            boolean esUltimo = (idx == nodosRaiz.size() - 1);
            
            generarNodoConHijos(nodoRaiz, sb, "", esUltimo);
            
            if (!esUltimo) {
                sb.append("\n");
            }
        }
        
        return sb.toString();
    }
    
    private static List<Nodo> obtenerNodosProfundidad0(Nodo raiz) {
        List<Nodo> nodos = new ArrayList<>();
        recolectarNodosPorProfundidad(raiz, 0, nodos);
        return nodos;
    }
    
    private static void recolectarNodosPorProfundidad(Nodo nodo, int profundidadObjetivo, List<Nodo> resultado) {
        if (nodo == null) return;
        
        if (nodo.getProfundidad() == profundidadObjetivo) {
            resultado.add(nodo);
        }
        
        for (Nodo hijo : nodo.getHijos()) {
            recolectarNodosPorProfundidad(hijo, profundidadObjetivo, resultado);
        }
    }
    private static void generarNodoConHijos(Nodo nodo, StringBuilder sb, String prefijo, boolean esUltimo) {
        char[][] tablero = simularTablero(nodo);
        String movInfo = "";
        if (nodo.getFila() >= 0) {
            movInfo = "Pos(" + nodo.getFila() + "," + nodo.getColumna() + ") ";
        }
        
        sb.append(prefijo);
        if (prefijo.isEmpty()) {
            sb.append("🎯 ");
        } else {
            sb.append(esUltimo ? "└─ " : "├─ ");
        }
        sb.append(movInfo);
        sb.append("- Valor: ").append(nodo.getValor());
        
        if (nodo.isPodado()) {
            sb.append(" ✂️[PODADO]");
        }
        if (nodo.isTerminal()) {
            sb.append(" 🏁[TERMINAL]");
        }
        
        sb.append("\n");
        String prefijoTablero = prefijo + (prefijo.isEmpty() ? "   " : (esUltimo ? "    " : "│   "));
        dibujarTablero(tablero, sb, prefijoTablero, nodo.getValor());
        
        List<Nodo> hijos = nodo.getHijos();
        if (!hijos.isEmpty() && nodo.getProfundidad() == 0) {
            for (int i = 0; i < hijos.size(); i++) {
                Nodo hijo = hijos.get(i);
                boolean esUltimoHijo = (i == hijos.size() - 1);
                String nuevoPrefijo = prefijo + (prefijo.isEmpty() ? "" : (esUltimo ? "    " : "│   "));
                
                generarHijo(hijo, sb, nuevoPrefijo, esUltimoHijo);
            }
        }
    }
    private static void generarHijo(Nodo hijo, StringBuilder sb, String prefijo, boolean esUltimo) {
        char[][] tablero = simularTablero(hijo);
        
        String movInfo = "";
        if (hijo.getFila() >= 0) {
            movInfo = "Pos(" + hijo.getFila() + "," + hijo.getColumna() + ") ";
        }
        
        sb.append(prefijo);
        sb.append(esUltimo ? "└─ " : "├─ ");
        sb.append(movInfo);
        sb.append("- Valor: ").append(hijo.getValor());
        
        if (hijo.isPodado()) {
            sb.append(" ✂️");
        }
        
        sb.append("\n");
        
        String prefijoTablero = prefijo + (esUltimo ? "    " : "│   ");
        dibujarTablero(tablero, sb, prefijoTablero, hijo.getValor());
    }
    
    private static void dibujarTablero(char[][] tablero, StringBuilder sb, String prefijo, int heuristica) {
        for (int i = 0; i < 3; i++) {
            sb.append(prefijo);
            for (int j = 0; j < 3; j++) {
                char c = tablero[i][j];
                sb.append(c == '-' ? ' ' : Character.toLowerCase(c));
                if (j < 2) sb.append("|");
            }
            
            if (i == 0) {
                sb.append(" H=").append(heuristica);
            }
            
            sb.append("\n");
        }
    }
    
    private static char[][] simularTablero(Nodo nodo) {
        char[][] tablero = new char[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                tablero[i][j] = '-';
            }
        }
        List<Nodo> ruta = new ArrayList<>();
        construirRuta(nodo, ruta);
        
        for (Nodo n : ruta) {
            if (n.getFila() >= 0 && n.getColumna() >= 0) {
                tablero[n.getFila()][n.getColumna()] = n.getJugador();
            }
        }
        
        return tablero;
    }
    
    private static void construirRuta(Nodo objetivo, List<Nodo> ruta) {
        Nodo raizGlobal = VisualizadorArbol.getRaiz();
        if (raizGlobal == null) return;
        
        List<Nodo> rutaTemporal = new ArrayList<>();
        if (buscarRuta(raizGlobal, objetivo, rutaTemporal)) {
            ruta.addAll(rutaTemporal);
        }
    }
    private static boolean buscarRuta(Nodo actual, Nodo objetivo, List<Nodo> ruta) {
        if (actual == null) return false;
        
        ruta.add(actual);
        
        if (actual == objetivo) {
            return true;
        }
        
        for (Nodo hijo : actual.getHijos()) {
            if (buscarRuta(hijo, objetivo, ruta)) {
                return true;
            }
        }
        
        ruta.remove(ruta.size() - 1);
        return false;
    }
}