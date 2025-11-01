package org.tresenraya.model;

import java.util.ArrayList;
import java.util.List;

public class Nodo {
    private int fila;
    private int columna;
    private char jugador;
    private int valor;
    private String tipo;
    private int profundidad;
    private List<Nodo> hijos;
    private boolean podado;
    private int alpha;
    private int beta;
    private boolean esTerminal;
    private String estadoTerminal; 

    public Nodo(int fila, int columna, char jugador, String tipo, int profundidad) {
        this.fila = fila;
        this.columna = columna;
        this.jugador = jugador;
        this.tipo = tipo;
        this.profundidad = profundidad;
        this.hijos = new ArrayList<>();
        this.podado = false;
        this.esTerminal = false;
    }

    // Getters y Setters
    public int getFila() { return fila; }
    public int getColumna() { return columna; }
    public char getJugador() { return jugador; }
    public int getValor() { return valor; }
    public void setValor(int valor) { this.valor = valor; }
    public String getTipo() { return tipo; }
    public int getProfundidad() { return profundidad; }
    public List<Nodo> getHijos() { return hijos; }
    public void agregarHijo(Nodo hijo) { this.hijos.add(hijo); }
    public boolean isPodado() { return podado; }
    public void setPodado(boolean podado) { this.podado = podado; }
    public int getAlpha() { return alpha; }
    public void setAlpha(int alpha) { this.alpha = alpha; }
    public int getBeta() { return beta; }
    public void setBeta(int beta) { this.beta = beta; }
    public boolean isTerminal() { return esTerminal; }
    public void setTerminal(boolean terminal) { this.esTerminal = terminal; }
    public String getEstadoTerminal() { return estadoTerminal; }
    public void setEstadoTerminal(String estadoTerminal) { this.estadoTerminal = estadoTerminal; }

    @Override
    public String toString() {
        if (esTerminal) {
            return estadoTerminal + "\nValor: " + valor;
        }
        String mov = (fila >= 0) ? "(" + fila + "," + columna + ")" : "Inicio";
        return mov + "\n" + jugador + " | " + tipo + "\nV:" + valor;
    }
}