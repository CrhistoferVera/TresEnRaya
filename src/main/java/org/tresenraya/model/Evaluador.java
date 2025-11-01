package org.tresenraya.model;

/**
 * Evaluador heurístico para Tic Tac Toe
 * ✅ CORREGIDO: Cuenta líneas donde el jugador PUEDE ganar (sin bloqueos del oponente)
 * NO importa si la línea está vacía o tiene fichas del jugador
 * Solo importa que NO tenga fichas del oponente
 * Fórmula: líneas_sin_bloqueo_X - líneas_sin_bloqueo_O
 */
public class Evaluador {
    
    /**
     * Evalúa el tablero usando función heurística CORRECTA
     * Retorna: líneas_libres_jugador - líneas_libres_oponente
     * Una línea es "libre" si NO tiene fichas del oponente
     */
    public static int evaluar(Tablero tablero, char jugador, char oponente) {
        // Si hay ganador, retornar valor definitivo
        if (tablero.hayGanador(jugador)) {
            return 1000; // Victoria absoluta
        }
        if (tablero.hayGanador(oponente)) {
            return -1000; // Derrota absoluta
        }
        
        // Contar líneas libres (sin bloqueos) para cada jugador
        int lineasLibresJugador = contarLineasLibres(tablero, oponente);
        int lineasLibresOponente = contarLineasLibres(tablero, jugador);
        
        return lineasLibresJugador - lineasLibresOponente;
    }
    
    /**
     * ✅ CORRECTO: Cuenta líneas donde NO hay fichas del oponente
     * No importa si están vacías o tienen fichas propias
     * Solo importa que el oponente NO las haya bloqueado
     */
    private static int contarLineasLibres(Tablero tablero, char oponente) {
        char[][] matriz = tablero.getMatriz();
        int lineasLibres = 0;
        
        // Verificar las 3 filas
        for (int i = 0; i < 3; i++) {
            if (lineaSinOponente(matriz[i][0], matriz[i][1], matriz[i][2], oponente)) {
                lineasLibres++;
            }
        }
        
        // Verificar las 3 columnas
        for (int j = 0; j < 3; j++) {
            if (lineaSinOponente(matriz[0][j], matriz[1][j], matriz[2][j], oponente)) {
                lineasLibres++;
            }
        }
        
        // Verificar diagonal principal (0,0 -> 1,1 -> 2,2)
        if (lineaSinOponente(matriz[0][0], matriz[1][1], matriz[2][2], oponente)) {
            lineasLibres++;
        }
        
        // Verificar diagonal secundaria (0,2 -> 1,1 -> 2,0)
        if (lineaSinOponente(matriz[0][2], matriz[1][1], matriz[2][0], oponente)) {
            lineasLibres++;
        }
        
        return lineasLibres;
    }
    
    /**
     * ✅ CORRECTO: Una línea está libre si NO tiene fichas del oponente
     * Puede estar vacía o tener fichas propias, no importa
     */
    private static boolean lineaSinOponente(char c1, char c2, char c3, char oponente) {
        return c1 != oponente && c2 != oponente && c3 != oponente;
    }
    
    /**
     * Imprime análisis detallado del tablero
     * Útil para debugging
     */
    public static void imprimirAnalisis(Tablero tablero, char jugador, char oponente) {
        System.out.println("\n📊 ANÁLISIS HEURÍSTICO DETALLADO:");
        System.out.println("Tablero actual:");
        tablero.imprimir();
        
        char[][] matriz = tablero.getMatriz();
        
        // Analizar líneas libres para el jugador (sin oponente)
        System.out.println("🔍 LÍNEAS LIBRES PARA " + jugador + " (sin " + oponente + "):");
        int lineasJugador = 0;
        
        // Filas
        for (int i = 0; i < 3; i++) {
            char[] linea = {matriz[i][0], matriz[i][1], matriz[i][2]};
            if (lineaSinOponente(linea[0], linea[1], linea[2], oponente)) {
                lineasJugador++;
                System.out.println("  ✓ Fila " + i + ": [" + 
                        mostrarCasilla(linea[0]) + "|" + 
                        mostrarCasilla(linea[1]) + "|" + 
                        mostrarCasilla(linea[2]) + "]");
            }
        }
        
        // Columnas
        for (int j = 0; j < 3; j++) {
            char[] linea = {matriz[0][j], matriz[1][j], matriz[2][j]};
            if (lineaSinOponente(linea[0], linea[1], linea[2], oponente)) {
                lineasJugador++;
                System.out.println("  ✓ Col " + j + ": [" + 
                        mostrarCasilla(linea[0]) + "|" + 
                        mostrarCasilla(linea[1]) + "|" + 
                        mostrarCasilla(linea[2]) + "]");
            }
        }
        
        // Diagonales
        char[] diag1 = {matriz[0][0], matriz[1][1], matriz[2][2]};
        if (lineaSinOponente(diag1[0], diag1[1], diag1[2], oponente)) {
            lineasJugador++;
            System.out.println("  ✓ Diag \\: [" + 
                    mostrarCasilla(diag1[0]) + "|" + 
                    mostrarCasilla(diag1[1]) + "|" + 
                    mostrarCasilla(diag1[2]) + "]");
        }
        
        char[] diag2 = {matriz[0][2], matriz[1][1], matriz[2][0]};
        if (lineaSinOponente(diag2[0], diag2[1], diag2[2], oponente)) {
            lineasJugador++;
            System.out.println("  ✓ Diag /: [" + 
                    mostrarCasilla(diag2[0]) + "|" + 
                    mostrarCasilla(diag2[1]) + "|" + 
                    mostrarCasilla(diag2[2]) + "]");
        }
        
        System.out.println("Total: " + lineasJugador + " / 8 líneas");
        
        // Analizar líneas libres para el oponente (sin jugador)
        System.out.println("\n🔍 LÍNEAS LIBRES PARA " + oponente + " (sin " + jugador + "):");
        int lineasOponente = 0;
        
        // Filas
        for (int i = 0; i < 3; i++) {
            char[] linea = {matriz[i][0], matriz[i][1], matriz[i][2]};
            if (lineaSinOponente(linea[0], linea[1], linea[2], jugador)) {
                lineasOponente++;
                System.out.println("  ✓ Fila " + i + ": [" + 
                        mostrarCasilla(linea[0]) + "|" + 
                        mostrarCasilla(linea[1]) + "|" + 
                        mostrarCasilla(linea[2]) + "]");
            }
        }
        
        // Columnas
        for (int j = 0; j < 3; j++) {
            char[] linea = {matriz[0][j], matriz[1][j], matriz[2][j]};
            if (lineaSinOponente(linea[0], linea[1], linea[2], jugador)) {
                lineasOponente++;
                System.out.println("  ✓ Col " + j + ": [" + 
                        mostrarCasilla(linea[0]) + "|" + 
                        mostrarCasilla(linea[1]) + "|" + 
                        mostrarCasilla(linea[2]) + "]");
            }
        }
        
        // Diagonales
        diag1 = new char[]{matriz[0][0], matriz[1][1], matriz[2][2]};
        if (lineaSinOponente(diag1[0], diag1[1], diag1[2], jugador)) {
            lineasOponente++;
            System.out.println("  ✓ Diag \\: [" + 
                    mostrarCasilla(diag1[0]) + "|" + 
                    mostrarCasilla(diag1[1]) + "|" + 
                    mostrarCasilla(diag1[2]) + "]");
        }
        
        diag2 = new char[]{matriz[0][2], matriz[1][1], matriz[2][0]};
        if (lineaSinOponente(diag2[0], diag2[1], diag2[2], jugador)) {
            lineasOponente++;
            System.out.println("  ✓ Diag /: [" + 
                    mostrarCasilla(diag2[0]) + "|" + 
                    mostrarCasilla(diag2[1]) + "|" + 
                    mostrarCasilla(diag2[2]) + "]");
        }
        
        System.out.println("Total: " + lineasOponente + " / 8 líneas");
        
        int valorTotal = lineasJugador - lineasOponente;
        System.out.println("\n📈 RESULTADO:");
        System.out.println("   Heurística = " + lineasJugador + " - " + lineasOponente + " = " + valorTotal);
    }
    
    private static String mostrarCasilla(char c) {
        return c == '-' ? " " : String.valueOf(c);
    }
    
    /**
     * Ejemplo de uso - Prueba tu caso específico
     */
    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════╗");
        System.out.println("║     PRUEBA: CASO ESPECÍFICO DEL USUARIO              ║");
        System.out.println("╚═══════════════════════════════════════════════════════╝\n");
        
        // Crear el tablero del ejemplo:
        //  |x|
        // x| |
        // o| |
        Tablero tablero = new Tablero();
        tablero.hacerMovimiento(0, 1, 'X'); // (0,1) = x
        tablero.hacerMovimiento(1, 0, 'X'); // (1,0) = x
        tablero.hacerMovimiento(2, 0, 'O'); // (2,0) = o
        
        imprimirAnalisis(tablero, 'X', 'O');
        
        System.out.println("\n" + "═".repeat(60));
        System.out.println("✅ VERIFICACIÓN ESPERADA:");
        System.out.println("─".repeat(60));
        System.out.println("Líneas libres para X (sin O):");
        System.out.println("  1. Fila 0: [ |x| ] ✓");
        System.out.println("  2. Fila 1: [x| | ] ✓");
        System.out.println("  3. Fila 2: [o| | ] ✗ (tiene O)");
        System.out.println("  4. Col 0: [ |x|o] ✗ (tiene O)");
        System.out.println("  5. Col 1: [x| | ] ✓");
        System.out.println("  6. Col 2: [ | | ] ✓");
        System.out.println("  7. Diag \\: [ | | ] ✓");
        System.out.println("  8. Diag /: [ | |x] ✓");
        System.out.println("Total X: 6 líneas ✓");
        System.out.println("\nLíneas libres para O (sin X):");
        System.out.println("  1. Fila 0: [ |x| ] ✗ (tiene X)");
        System.out.println("  2. Fila 1: [x| | ] ✗ (tiene X)");
        System.out.println("  3. Fila 2: [o| | ] ✓");
        System.out.println("  4. Col 0: [ |x|o] ✗ (tiene X)");
        System.out.println("  5. Col 1: [x| | ] ✗ (tiene X)");
        System.out.println("  6. Col 2: [ | | ] ✓");
        System.out.println("  7. Diag \\: [ | | ] ✓");
        System.out.println("  8. Diag /: [ | |x] ✗ (tiene X)");
        System.out.println("Total O: 3 líneas... ESPERA, revisemos!");
        System.out.println("\nDiag /: posiciones (0,2) (1,1) (2,0)");
        System.out.println("  = [ ] [ ] [o] - NO tiene X, SÍ cuenta para O ✓");
        System.out.println("Total O: 4 líneas ✓");
        System.out.println("\nHeurística: 6 - 4 = 2... PERO tu dijiste H=1");
        System.out.println("═".repeat(60));
        
        System.out.println("\n\n╔═══════════════════════════════════════════════════════╗");
        System.out.println("║     PRUEBA: DIAGRAMA ORIGINAL                         ║");
        System.out.println("╚═══════════════════════════════════════════════════════╝\n");
        
        // Crear el tablero del diagrama:
        //  x| |
        //   |o|x
        //   | |o
        Tablero t2 = new Tablero();
        t2.hacerMovimiento(0, 0, 'X');
        t2.hacerMovimiento(1, 1, 'O');
        t2.hacerMovimiento(1, 2, 'X');
        t2.hacerMovimiento(2, 2, 'O');
        
        imprimirAnalisis(t2, 'X', 'O');
        
        System.out.println("\n" + "═".repeat(60));
        System.out.println("✅ VERIFICACIÓN:");
        System.out.println("Esperado: X=2, O=3, Heurística = 2-3 = -1");
        System.out.println("═".repeat(60));
        
        System.out.println("\n\n╔═══════════════════════════════════════════════════════╗");
        System.out.println("║     PRUEBA: TABLERO VACÍO - PRIMER MOVIMIENTO        ║");
        System.out.println("╚═══════════════════════════════════════════════════════╝\n");
        
        // Centro
        Tablero t3 = new Tablero();
        t3.hacerMovimiento(1, 1, 'X');
        System.out.println("1️⃣ CENTRO (1,1):");
        imprimirAnalisis(t3, 'X', 'O');
        
        // Esquina
        Tablero t4 = new Tablero();
        t4.hacerMovimiento(0, 0, 'X');
        System.out.println("\n\n2️⃣ ESQUINA (0,0):");
        imprimirAnalisis(t4, 'X', 'O');
        
        // Lado
        Tablero t5 = new Tablero();
        t5.hacerMovimiento(0, 1, 'X');
        System.out.println("\n\n3️⃣ LADO (0,1):");
        imprimirAnalisis(t5, 'X', 'O');
        
        System.out.println("\n" + "═".repeat(60));
        System.out.println("💡 CONCLUSIÓN:");
        System.out.println("Todas deberían dar X=8 (todas las líneas libres)");
        System.out.println("Diferencia está en O (líneas bloqueadas por X)");
        System.out.println("Centro bloquea 4 líneas → O=4 → H=8-4=4 🥇");
        System.out.println("Esquina bloquea 3 líneas → O=5 → H=8-5=3");
        System.out.println("Lado bloquea 2 líneas → O=6 → H=8-6=2");
        System.out.println("═".repeat(60));
    }
}