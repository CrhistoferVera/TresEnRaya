package org.tresenraya.model;

public class Tablero {
    private char[][] tablero;

    public Tablero() {
        tablero = new char[3][3];
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                tablero[i][j] = '-';
    }

    public Tablero(char[][] copia) {
        tablero = new char[3][3];
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                tablero[i][j] = copia[i][j];
    }

    public boolean esMovimientoValido(int fila, int col) {
        return tablero[fila][col] == '-';
    }

    public void hacerMovimiento(int fila, int col, char jugador) {
        tablero[fila][col] = jugador;
    }

    public boolean hayGanador(char jugador) {
        for (int i = 0; i < 3; i++) {
            if ((tablero[i][0] == jugador && tablero[i][1] == jugador && tablero[i][2] == jugador) ||
                    (tablero[0][i] == jugador && tablero[1][i] == jugador && tablero[2][i] == jugador))
                return true;
        }
        return (tablero[0][0] == jugador && tablero[1][1] == jugador && tablero[2][2] == jugador) ||
                (tablero[0][2] == jugador && tablero[1][1] == jugador && tablero[2][0] == jugador);
    }

    public boolean tableroLleno() {
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                if (tablero[i][j] == '-')
                    return false;
        return true;
    }

    public char[][] getMatriz() {
        return tablero;
    }

    public void imprimir() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++)
                System.out.print(tablero[i][j] + " ");
            System.out.println();
        }
        System.out.println();
    }
}
