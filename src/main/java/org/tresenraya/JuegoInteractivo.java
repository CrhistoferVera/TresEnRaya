package org.tresenraya;

import org.tresenraya.model.AlfaBeta;
import org.tresenraya.model.Minimax;
import org.tresenraya.model.Tablero;
import org.tresenraya.model.VisualizadorArbol;

import java.util.Scanner;

public class JuegoInteractivo {
    private Tablero tablero;
    private Scanner scanner;
    private char jugadorHumano;
    private char jugadorIA;
    private String algoritmo; // "minimax" o "alfabeta"
    private boolean mostrarArbol; // Si se muestra el árbol de decisión
    private String nivelDetalle; // "completo", "resumen", "ninguno"

    public JuegoInteractivo() {
        this.tablero = new Tablero();
        this.scanner = new Scanner(System.in);
    }

    public void iniciar() {
        mostrarBienvenida();
        configurarJuego();
        jugar();
    }

    private void mostrarBienvenida() {
        System.out.println("╔═══════════════════════════════════════╗");
        System.out.println("║    🎮 TRES EN RAYA - IA vs HUMANO 🎮  ║");
        System.out.println("║          CON VISUALIZACIÓN 🌳         ║");
        System.out.println("╚═══════════════════════════════════════╝");
        System.out.println();
    }

    private void configurarJuego() {
        // Elegir símbolo
        System.out.println("¿Con qué símbolo quieres jugar?");
        System.out.println("1. X (juegas primero)");
        System.out.println("2. O (juega la IA primero)");
        System.out.print("Elige (1 o 2): ");

        int opcion = leerOpcion(1, 2);
        if (opcion == 1) {
            jugadorHumano = 'X';
            jugadorIA = 'O';
        } else {
            jugadorHumano = 'O';
            jugadorIA = 'X';
        }

        // Elegir algoritmo
        System.out.println("\n¿Qué algoritmo debe usar la IA?");
        System.out.println("1. Minimax (más lento, explora todo)");
        System.out.println("2. Alfa-Beta (más rápido, con podas)");
        System.out.print("Elige (1 o 2): ");

        opcion = leerOpcion(1, 2);
        algoritmo = (opcion == 1) ? "minimax" : "alfabeta";

        // Elegir nivel de visualización
        System.out.println("\n🌳 ¿Cómo quieres ver el árbol de decisión de la IA?");
        System.out.println("1. Árbol completo (muestra todos los nodos explorados)");
        System.out.println("2. Solo resumen (estadísticas y mejor movimiento)");
        System.out.println("3. Sin visualización (modo rápido)");
        System.out.print("Elige (1-3): ");

        opcion = leerOpcion(1, 3);
        switch (opcion) {
            case 1:
                nivelDetalle = "completo";
                mostrarArbol = true;
                break;
            case 2:
                nivelDetalle = "resumen";
                mostrarArbol = true;
                break;
            case 3:
                nivelDetalle = "ninguno";
                mostrarArbol = false;
                break;
        }

        System.out.println("\n✅ Configuración completa:");
        System.out.println("   Tú juegas con: " + jugadorHumano);
        System.out.println("   IA juega con: " + jugadorIA);
        System.out.println("   Algoritmo: " + algoritmo.toUpperCase());
        System.out.println("   Visualización: " + getNombreVisualizacion());
        System.out.println("\n📋 Instrucciones:");
        System.out.println("   El tablero tiene posiciones numeradas así:");
        System.out.println("   (0,0) (0,1) (0,2)");
        System.out.println("   (1,0) (1,1) (1,2)");
        System.out.println("   (2,0) (2,1) (2,2)");
        System.out.println("\n   Ingresa primero la FILA, luego la COLUMNA");
        System.out.println("   Ejemplo: para la esquina superior izquierda ingresa: 0 0");
        System.out.println("   Ejemplo: para el centro ingresa: 1 1\n");

        // Esperar que el usuario presione Enter para continuar
        System.out.print("Presiona ENTER para comenzar...");
        scanner.nextLine();
    }

    private String getNombreVisualizacion() {
        switch (nivelDetalle) {
            case "completo": return "Árbol completo 🌳";
            case "resumen": return "Solo resumen 📊";
            case "ninguno": return "Sin árbol ⚡";
            default: return "Desconocido";
        }
    }

    private void jugar() {
        boolean turnoHumano = (jugadorHumano == 'X'); // X siempre empieza
        int movimientos = 0;

        while (true) {
            System.out.println("\n═══════════════════════════════════════");
            System.out.println("Movimiento #" + (movimientos + 1));
            System.out.println("═══════════════════════════════════════");
            tablero.imprimir();

            if (turnoHumano) {
                System.out.println("🎮 Tu turno (" + jugadorHumano + ")");
                turnoJugador();
            } else {
                System.out.println("🤖 Turno de la IA (" + jugadorIA + ")");
                turnoIA();
            }

            movimientos++;

            // Verificar fin del juego
            if (tablero.hayGanador(jugadorHumano)) {
                System.out.println("\n═══════════════════════════════════════");
                tablero.imprimir();
                System.out.println("🎉🎉🎉 ¡FELICITACIONES! ¡HAS GANADO! 🎉🎉🎉");
                mostrarEstadisticas(movimientos);
                break;
            }

            if (tablero.hayGanador(jugadorIA)) {
                System.out.println("\n═══════════════════════════════════════");
                tablero.imprimir();
                System.out.println("😔 La IA ha ganado. ¡Inténtalo de nuevo!");
                mostrarEstadisticas(movimientos);
                break;
            }

            if (tablero.tableroLleno()) {
                System.out.println("\n═══════════════════════════════════════");
                tablero.imprimir();
                System.out.println("🤝 ¡EMPATE! Ambos jugaron muy bien.");
                mostrarEstadisticas(movimientos);
                break;
            }

            turnoHumano = !turnoHumano;

            // Pausar para que el usuario pueda leer el árbol
            if (!turnoHumano && nivelDetalle.equals("completo")) {
                System.out.print("\nPresiona ENTER para continuar...");
                scanner.nextLine();
            }
        }

        // Preguntar si quiere jugar de nuevo
        System.out.print("\n¿Quieres jugar otra partida? (s/n): ");
        scanner.nextLine(); // Limpiar buffer
        String respuesta = scanner.nextLine().trim();

        if (respuesta.equalsIgnoreCase("s")) {
            tablero = new Tablero();
            System.out.println("\n\n");
            iniciar();
        } else {
            System.out.println("\n👋 ¡Gracias por jugar! ¡Hasta pronto!");
            scanner.close();
        }
    }

    private void turnoJugador() {
        while (true) {
            System.out.print("\nIngresa FILA (0-2): ");
            String filaInput = scanner.nextLine().trim();

            System.out.print("Ingresa COLUMNA (0-2): ");
            String colInput = scanner.nextLine().trim();

            try {
                int fila = Integer.parseInt(filaInput);
                int col = Integer.parseInt(colInput);

                if (fila < 0 || fila > 2 || col < 0 || col > 2) {
                    System.out.println("❌ Error: Las posiciones deben estar entre 0 y 2");
                    continue;
                }

                if (!tablero.esMovimientoValido(fila, col)) {
                    System.out.println("❌ Error: Esa casilla ya está ocupada. Intenta otra posición.");
                    continue;
                }

                tablero.hacerMovimiento(fila, col, jugadorHumano);
                System.out.println("✅ Movimiento registrado en posición (" + fila + ", " + col + ")");
                break;

            } catch (NumberFormatException e) {
                System.out.println("❌ Error: Debes ingresar números entre 0 y 2");
            }
        }
    }

    private void turnoIA() {
        System.out.println("🤔 La IA está pensando...");

        if (mostrarArbol && nivelDetalle.equals("completo")) {
            System.out.println("\n" + "═".repeat(60));
            System.out.println("🌳 ÁRBOL DE DECISIÓN DE LA IA");
            System.out.println("═".repeat(60));
        }

        long tiempoInicio = System.currentTimeMillis();
        int[] movimiento;

        // Configurar visualización según el nivel de detalle
        boolean visualizarCompleto = nivelDetalle.equals("completo");

        if (algoritmo.equals("minimax")) {
            movimiento = Minimax.mejorMovimiento(tablero, jugadorIA, jugadorHumano, visualizarCompleto);
        } else {
            movimiento = AlfaBeta.mejorMovimientoAlfaBeta(tablero, jugadorIA, jugadorHumano, visualizarCompleto);
        }

        long tiempoFin = System.currentTimeMillis();
        long tiempoTranscurrido = tiempoFin - tiempoInicio;

        tablero.hacerMovimiento(movimiento[0], movimiento[1], jugadorIA);

        // Mostrar resultado
        System.out.println("\n" + "─".repeat(60));
        System.out.println("🎯 DECISIÓN DE LA IA:");
        System.out.println("   Posición elegida: (" + movimiento[0] + ", " + movimiento[1] + ")");
        System.out.println("   Tiempo de cálculo: " + tiempoTranscurrido + " ms");
        System.out.println("   Algoritmo usado: " + algoritmo.toUpperCase());

        // Si hay resumen, mostrarlo
        if (mostrarArbol && (nivelDetalle.equals("completo") || nivelDetalle.equals("resumen"))) {
            VisualizadorArbol.imprimirResumen();
        }

        System.out.println("─".repeat(60));
    }

    private void mostrarEstadisticas(int movimientos) {
        System.out.println("\n📊 ESTADÍSTICAS DE LA PARTIDA:");
        System.out.println("   Total de movimientos: " + movimientos);
        System.out.println("   Algoritmo de IA: " + algoritmo.toUpperCase());
        System.out.println("   Nivel de visualización: " + getNombreVisualizacion());
    }

    private int leerOpcion(int min, int max) {
        while (true) {
            try {
                if (!scanner.hasNextLine()) {
                    System.err.println("❌ Error: No hay entrada disponible");
                    System.exit(1);
                }
                String input = scanner.nextLine().trim();

                if (input.isEmpty()) {
                    System.out.print("❌ No ingresaste nada. Elige entre " + min + " y " + max + ": ");
                    continue;
                }

                int opcion = Integer.parseInt(input);
                if (opcion >= min && opcion <= max) {
                    return opcion;
                }
                System.out.print("❌ Opción inválida. Elige entre " + min + " y " + max + ": ");
            } catch (NumberFormatException e) {
                System.out.print("❌ Entrada inválida. Elige entre " + min + " y " + max + ": ");
            } catch (Exception e) {
                System.err.println("❌ Error inesperado: " + e.getMessage());
                e.printStackTrace();
                System.exit(1);
            }
        }
    }

    public static void main(String[] args) {
        JuegoInteractivo juego = new JuegoInteractivo();
        juego.iniciar();
    }
}