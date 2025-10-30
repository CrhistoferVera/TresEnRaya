package org.tresenraya;

import java.util.Scanner;

import org.tresenraya.model.AlfaBeta;
import org.tresenraya.model.Minimax;
import org.tresenraya.model.Tablero;
import org.tresenraya.model.VisualizadorArbolMejorado;

/**
 * Demo SIEMPRE con simetría y tableros visuales
 */
public class DemoCompleta {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("╔════════════════════════════════════════════════════════╗");
        System.out.println("║  🌳 VISUALIZADOR DE ÁRBOLES - CON SIMETRÍA 🌳         ║");
        System.out.println("╚════════════════════════════════════════════════════════╝");
        System.out.println();

        while (true) {
            System.out.println("\n╔════════════════════════════════════════════════════════╗");
            System.out.println("║                    MENÚ PRINCIPAL                       ║");
            System.out.println("╠════════════════════════════════════════════════════════╣");
            System.out.println("║ 1. Ver árbol MINIMAX con tableros 🎨                   ║");
            System.out.println("║ 2. Ver árbol ALFA-BETA con tableros 🎨                 ║");
            System.out.println("║ 3. Tablero personalizado                               ║");
            System.out.println("║ 4. Comparar Minimax vs Alfa-Beta                       ║");
            System.out.println("║ 5. Explicación de simetrías                            ║");
            System.out.println("║ 6. Salir                                               ║");
            System.out.println("╚════════════════════════════════════════════════════════╝");
            System.out.print("Elige una opción: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    verArbolMinimax(scanner);
                    break;
                case "2":
                    verArbolAlfaBeta(scanner);
                    break;
                case "3":
                    tableroPersonalizado(scanner);
                    break;
                case "4":
                    compararAlgoritmos();
                    break;
                case "5":
                    explicarSimetrias();
                    break;
                case "6":
                    System.out.println("\n👋 ¡Hasta pronto!");
                    scanner.close();
                    return;
                default:
                    System.out.println("❌ Opción inválida");
            }

            System.out.print("\nPresiona ENTER para continuar...");
            scanner.nextLine();
        }
    }

    private static void verArbolMinimax(Scanner scanner) {
        System.out.println("\n🌳 ÁRBOL MINIMAX CON SIMETRÍA + TABLEROS");
        System.out.println("\n¿Qué tablero usar?");
        System.out.println("1. Tablero vacío (3 ramas desde raíz) ⭐ Recomendado");
        System.out.println("2. Tablero con movimientos");
        System.out.print("Opción: ");
        
        String opt = scanner.nextLine().trim();
        Tablero tablero = new Tablero();
        
        if (opt.equals("2")) {
            tablero.hacerMovimiento(1, 1, 'X');
            tablero.hacerMovimiento(0, 0, 'O');
        }
        
        System.out.println("\n📋 Tablero:");
        tablero.imprimir();
        
        System.out.println("\n🟢 Ejecutando Minimax con simetría y tableros...\n");
        // ✅ CAMBIO: Activar mostrarTableros=true
        Minimax.mejorMovimiento(tablero, 'X', 'O', true, true);
    }

    private static void verArbolAlfaBeta(Scanner scanner) {
        System.out.println("\n🌳 ÁRBOL ALFA-BETA CON SIMETRÍA + TABLEROS");
        System.out.println("\n¿Qué tablero usar?");
        System.out.println("1. Tablero vacío (3 ramas desde raíz) ⭐ Recomendado");
        System.out.println("2. Tablero con movimientos");
        System.out.print("Opción: ");
        
        String opt = scanner.nextLine().trim();
        Tablero tablero = new Tablero();
        
        if (opt.equals("2")) {
            tablero.hacerMovimiento(1, 1, 'X');
            tablero.hacerMovimiento(0, 0, 'O');
        }
        
        System.out.println("\n📋 Tablero:");
        tablero.imprimir();
        
        System.out.println("\n🔵 Ejecutando Alfa-Beta con simetría y tableros...\n");
        // ✅ CAMBIO: Activar mostrarTableros=true
        AlfaBeta.mejorMovimientoAlfaBeta(tablero, 'X', 'O', true, true, true);
    }

    private static void tableroPersonalizado(Scanner scanner) {
        System.out.println("\n🎨 CONFIGURACIÓN DE TABLERO PERSONALIZADO");
        Tablero tablero = new Tablero();
        
        System.out.println("\nIngresa movimientos (formato: fila columna jugador)");
        System.out.println("Ejemplo: 0 0 X");
        System.out.println("Escribe 'fin' para terminar\n");

        while (true) {
            System.out.print("Movimiento: ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("fin")) break;

            String[] partes = input.split(" ");
            if (partes.length != 3) {
                System.out.println("❌ Formato incorrecto");
                continue;
            }

            try {
                int fila = Integer.parseInt(partes[0]);
                int col = Integer.parseInt(partes[1]);
                char jugador = partes[2].toUpperCase().charAt(0);

                if (fila < 0 || fila > 2 || col < 0 || col > 2) {
                    System.out.println("❌ Posición inválida");
                    continue;
                }

                if (!tablero.esMovimientoValido(fila, col)) {
                    System.out.println("❌ Casilla ocupada");
                    continue;
                }

                tablero.hacerMovimiento(fila, col, jugador);
                System.out.println("✅ Registrado");
                tablero.imprimir();

            } catch (Exception e) {
                System.out.println("❌ Error en formato");
            }
        }
        
        System.out.println("\n¿Qué algoritmo usar?");
        System.out.println("1. Minimax");
        System.out.println("2. Alfa-Beta");
        System.out.print("Opción: ");
        
        String alg = scanner.nextLine().trim();
        
        // ✅ CAMBIO: Activar mostrarTableros=true
        if (alg.equals("1")) {
            Minimax.mejorMovimiento(tablero, 'X', 'O', true, true);
        } else {
            AlfaBeta.mejorMovimientoAlfaBeta(tablero, 'X', 'O', true, true, true);
        }
    }

    private static void compararAlgoritmos() {
        System.out.println("\n" + "═".repeat(60));
        System.out.println("📊 COMPARACIÓN: MINIMAX vs ALFA-BETA (CON SIMETRÍA)");
        System.out.println("═".repeat(60));

        Tablero tablero = new Tablero();
        
        // Minimax (sin tableros para comparación rápida)
        System.out.println("\n🟢 Minimax...");
        long t1 = System.currentTimeMillis();
        int[] m1 = Minimax.mejorMovimiento(tablero, 'O', 'X', false, false);
        int n1 = VisualizadorArbolMejorado.getNodosExplorados();
        long d1 = System.currentTimeMillis() - t1;

        // Alfa-Beta (sin tableros para comparación rápida)
        System.out.println("🔵 Alfa-Beta...");
        long t2 = System.currentTimeMillis();
        int[] m2 = AlfaBeta.mejorMovimientoAlfaBeta(tablero, 'O', 'X', false, true, false);
        int n2 = VisualizadorArbolMejorado.getNodosExplorados();
        int p2 = VisualizadorArbolMejorado.getNodosPodados();
        long d2 = System.currentTimeMillis() - t2;

        System.out.println("\n" + "═".repeat(60));
        System.out.println("📊 RESULTADOS:");
        System.out.println("─".repeat(60));
        System.out.println("🟢 MINIMAX:");
        System.out.println("   Nodos explorados: " + n1);
        System.out.println("   Tiempo: " + d1 + " ms");
        System.out.println("   Movimiento: (" + m1[0] + ", " + m1[1] + ")");
        
        System.out.println("\n🔵 ALFA-BETA:");
        System.out.println("   Nodos explorados: " + n2);
        System.out.println("   Nodos podados: " + p2);
        System.out.println("   Tiempo: " + d2 + " ms");
        System.out.println("   Movimiento: (" + m2[0] + ", " + m2[1] + ")");
        
        System.out.println("\n✨ ANÁLISIS:");
        if (n1 > n2) {
            double reduccion = ((n1 - n2) * 100.0) / n1;
            System.out.println("   Reducción de nodos: " + String.format("%.1f%%", reduccion));
        }
        if (d1 > d2) {
            System.out.println("   Velocidad: " + String.format("%.1fx más rápido", (double)d1/d2));
        }
        System.out.println("   Mismo resultado: " + (m1[0] == m2[0] && m1[1] == m2[1] ? "✅ SÍ" : "❌ NO"));
        System.out.println("═".repeat(60));
    }

    private static void explicarSimetrias() {
        System.out.println("\n" + "═".repeat(60));
        System.out.println("📚 EXPLICACIÓN DE SIMETRÍAS EN TIC TAC TOE");
        System.out.println("═".repeat(60));

        System.out.println("\n🎲 En tablero vacío hay 9 posiciones, pero solo 3 únicas:");
        
        System.out.println("\n1️⃣  ESQUINA (4 equivalentes):");
        System.out.println("    x| |      |x|      | |x     | |  ");
        System.out.println("     | |      | |      | |      | | ");
        System.out.println("     | |      | |      | |     x| | ");
        System.out.println("   Todas equivalentes por rotación");

        System.out.println("\n2️⃣  CENTRO (1 única):");
        System.out.println("     | |  ");
        System.out.println("     |x| ");
        System.out.println("     | | ");

        System.out.println("\n3️⃣  LADO (4 equivalentes):");
        System.out.println("     |x|      x| |      | |      | | ");
        System.out.println("     | |       | |      | |     |x| ");
        System.out.println("     | |       | |     x| |      | | ");
        System.out.println("   Todas equivalentes por rotación");

        System.out.println("\n✨ Beneficio: 9 → 3 movimientos = 66.7% menos nodos");
        System.out.println("\n💡 Este sistema SIEMPRE usa simetría para máxima eficiencia");
    }
}