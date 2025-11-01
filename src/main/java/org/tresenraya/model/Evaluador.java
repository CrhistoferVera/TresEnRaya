package org.tresenraya.model;

public class Evaluador {
    
    public static int evaluar(Tablero tablero, char jugador, char oponente) {
        if (tablero.hayGanador(jugador)) {
            return 1000;
        }
        if (tablero.hayGanador(oponente)) {
            return -1000;
        }
        
        int lineasLibresJugador = contarLineasLibres(tablero, oponente);
        int lineasLibresOponente = contarLineasLibres(tablero, jugador);
        
        return lineasLibresJugador - lineasLibresOponente;
    }
    
    private static int contarLineasLibres(Tablero tablero, char oponente) {
        char[][] matriz = tablero.getMatriz();
        int lineasLibres = 0;
        
        for (int i = 0; i < 3; i++) {
            if (lineaSinOponente(matriz[i][0], matriz[i][1], matriz[i][2], oponente)) {
                lineasLibres++;
            }
        }
        
        for (int j = 0; j < 3; j++) {
            if (lineaSinOponente(matriz[0][j], matriz[1][j], matriz[2][j], oponente)) {
                lineasLibres++;
            }
        }
        
        if (lineaSinOponente(matriz[0][0], matriz[1][1], matriz[2][2], oponente)) {
            lineasLibres++;
        }
        
        if (lineaSinOponente(matriz[0][2], matriz[1][1], matriz[2][0], oponente)) {
            lineasLibres++;
        }
        
        return lineasLibres;
    }
    
    private static boolean lineaSinOponente(char c1, char c2, char c3, char oponente) {
        return c1 != oponente && c2 != oponente && c3 != oponente;
    }
    
    public static void imprimirAnalisis(Tablero tablero, char jugador, char oponente) {
        System.out.println("\nANALISIS HEURISTICO DETALLADO:");
        System.out.println("Tablero actual:");
        tablero.imprimir();
        
        char[][] matriz = tablero.getMatriz();
        
        System.out.println("LINEAS LIBRES PARA " + jugador + " (sin " + oponente + "):");
        int lineasJugador = 0;
        
        for (int i = 0; i < 3; i++) {
            char[] linea = {matriz[i][0], matriz[i][1], matriz[i][2]};
            if (lineaSinOponente(linea[0], linea[1], linea[2], oponente)) {
                lineasJugador++;
                System.out.println("  Fila " + i + ": [" + 
                        mostrarCasilla(linea[0]) + "|" + 
                        mostrarCasilla(linea[1]) + "|" + 
                        mostrarCasilla(linea[2]) + "]");
            }
        }
        
        for (int j = 0; j < 3; j++) {
            char[] linea = {matriz[0][j], matriz[1][j], matriz[2][j]};
            if (lineaSinOponente(linea[0], linea[1], linea[2], oponente)) {
                lineasJugador++;
                System.out.println("  Col " + j + ": [" + 
                        mostrarCasilla(linea[0]) + "|" + 
                        mostrarCasilla(linea[1]) + "|" + 
                        mostrarCasilla(linea[2]) + "]");
            }
        }
        
        char[] diag1 = {matriz[0][0], matriz[1][1], matriz[2][2]};
        if (lineaSinOponente(diag1[0], diag1[1], diag1[2], oponente)) {
            lineasJugador++;
            System.out.println("  Diag \\: [" + 
                    mostrarCasilla(diag1[0]) + "|" + 
                    mostrarCasilla(diag1[1]) + "|" + 
                    mostrarCasilla(diag1[2]) + "]");
        }
        
        char[] diag2 = {matriz[0][2], matriz[1][1], matriz[2][0]};
        if (lineaSinOponente(diag2[0], diag2[1], diag2[2], oponente)) {
            lineasJugador++;
            System.out.println("  Diag /: [" + 
                    mostrarCasilla(diag2[0]) + "|" + 
                    mostrarCasilla(diag2[1]) + "|" + 
                    mostrarCasilla(diag2[2]) + "]");
        }
        
        System.out.println("Total: " + lineasJugador + " / 8 lineas");
        
        System.out.println("\nLINEAS LIBRES PARA " + oponente + " (sin " + jugador + "):");
        int lineasOponente = 0;
        
        for (int i = 0; i < 3; i++) {
            char[] linea = {matriz[i][0], matriz[i][1], matriz[i][2]};
            if (lineaSinOponente(linea[0], linea[1], linea[2], jugador)) {
                lineasOponente++;
                System.out.println("  Fila " + i + ": [" + 
                        mostrarCasilla(linea[0]) + "|" + 
                        mostrarCasilla(linea[1]) + "|" + 
                        mostrarCasilla(linea[2]) + "]");
            }
        }
        
        for (int j = 0; j < 3; j++) {
            char[] linea = {matriz[0][j], matriz[1][j], matriz[2][j]};
            if (lineaSinOponente(linea[0], linea[1], linea[2], jugador)) {
                lineasOponente++;
                System.out.println("  Col " + j + ": [" + 
                        mostrarCasilla(linea[0]) + "|" + 
                        mostrarCasilla(linea[1]) + "|" + 
                        mostrarCasilla(linea[2]) + "]");
            }
        }
        
        diag1 = new char[]{matriz[0][0], matriz[1][1], matriz[2][2]};
        if (lineaSinOponente(diag1[0], diag1[1], diag1[2], jugador)) {
            lineasOponente++;
            System.out.println("  Diag \\: [" + 
                    mostrarCasilla(diag1[0]) + "|" + 
                    mostrarCasilla(diag1[1]) + "|" + 
                    mostrarCasilla(diag1[2]) + "]");
        }
        
        diag2 = new char[]{matriz[0][2], matriz[1][1], matriz[2][0]};
        if (lineaSinOponente(diag2[0], diag2[1], diag2[2], jugador)) {
            lineasOponente++;
            System.out.println("  Diag /: [" + 
                    mostrarCasilla(diag2[0]) + "|" + 
                    mostrarCasilla(diag2[1]) + "|" + 
                    mostrarCasilla(diag2[2]) + "]");
        }
        
        System.out.println("Total: " + lineasOponente + " / 8 lineas");
        
        int valorTotal = lineasJugador - lineasOponente;
        System.out.println("\nRESULTADO:");
        System.out.println("   Heuristica = " + lineasJugador + " - " + lineasOponente + " = " + valorTotal);
    }
    
    private static String mostrarCasilla(char c) {
        return c == '-' ? " " : String.valueOf(c);
    }
}
