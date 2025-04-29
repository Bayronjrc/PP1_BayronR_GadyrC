package main.java.symbol;

import java.util.*;

/**
 * Estructura para almacenar información de los símbolos
 */
public class SymbolInfo {
    private String lexema;
    private String tipo;  // Tipo de token (ID, INT_LITERAL, etc.)
    private String tipoVariable; // Para variables: INT, FLOAT, etc.
    private int linea;
    private int columna;
    private Object valor;  // Para constantes y valores inicializados
    private boolean esVariable;
    private boolean esFuncion;
    private boolean esConstante;
    private List<String> parametros; // Para funciones
    
    public SymbolInfo(String lexema, String tipo, int linea, int columna) {
        this.lexema = lexema;
        this.tipo = tipo;
        this.linea = linea;
        this.columna = columna;
        this.esVariable = false;
        this.esFuncion = false;
        this.esConstante = false;
        this.parametros = new ArrayList<>();
    }
    
    // Getters y setters
    public String getLexema() { return lexema; }
    public String getTipo() { return tipo; }
    public String getTipoVariable() { return tipoVariable; }
    public void setTipoVariable(String tipoVariable) { this.tipoVariable = tipoVariable; }
    public int getLinea() { return linea; }
    public int getColumna() { return columna; }
    public Object getValor() { return valor; }
    public void setValor(Object valor) { this.valor = valor; }
    public boolean esVariable() { return esVariable; }
    public void setEsVariable(boolean esVariable) { this.esVariable = esVariable; }
    public boolean esFuncion() { return esFuncion; }
    public void setEsFuncion(boolean esFuncion) { this.esFuncion = esFuncion; }
    public boolean esConstante() { return esConstante; }
    public void setEsConstante(boolean esConstante) { this.esConstante = esConstante; }
    public List<String> getParametros() { return parametros; }
    public void addParametro(String param) { this.parametros.add(param); }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Lexema: ").append(lexema);
        sb.append(", Tipo: ").append(tipo);
        if (tipoVariable != null) {
            sb.append(", Tipo Variable: ").append(tipoVariable);
        }
        sb.append(", Línea: ").append(linea);
        sb.append(", Columna: ").append(columna);
        if (valor != null) {
            sb.append(", Valor: ").append(valor);
        }
        if (esFuncion) {
            sb.append(", Función: Sí");
            sb.append(", Parámetros: ").append(parametros);
        }
        if (esVariable) {
            sb.append(", Variable: Sí");
        }
        if (esConstante) {
            sb.append(", Constante: Sí");
        }
        return sb.toString();
    }
}