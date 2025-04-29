package symbol;

import java.util.*;
import java.io.*;

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

/**
 * Tabla de símbolos para el compilador
 */
public class SymbolTable {
    // Tablas separadas para diferentes tipos de símbolos
    private Map<String, SymbolInfo> variables;
    private Map<String, SymbolInfo> funciones;
    private Map<String, SymbolInfo> constantes;
    private Map<String, SymbolInfo> palabrasReservadas;
    
    // Constructor
    public SymbolTable() {
        variables = new HashMap<>();
        funciones = new HashMap<>();
        constantes = new HashMap<>();
        palabrasReservadas = new HashMap<>();
        
        // Inicializar palabras reservadas
        inicializarPalabrasReservadas();
    }
    
    private void inicializarPalabrasReservadas() {
        // Palabras reservadas del lenguaje
        String[] palabras = {"if", "elif", "else", "do", "while", "for", "break", 
                           "return", "int", "float", "bool", "char", "string", 
                           "void", "read", "write", "main", "true", "false", "switch", "case", "default"};
        
        for (String palabra : palabras) {
            SymbolInfo info = new SymbolInfo(palabra, palabra.toUpperCase(), 0, 0);
            palabrasReservadas.put(palabra, info);
        }
    }
    
    /**
     * Determina que tabla debe usarse para un token específico
     */
    public String determinarTabla(String tipoToken, String lexema) {
        // Palabras reservadas
        if (palabrasReservadas.containsKey(lexema.toLowerCase())) {
            return "PALABRAS_RESERVADAS";
        }
        
        // Identificadores
        if (tipoToken.equals("ID")) {
            // Verificar si ya está en alguna tabla
            if (variables.containsKey(lexema)) {
                return "VARIABLES";
            } else if (funciones.containsKey(lexema)) {
                return "FUNCIONES";
            } else {
                // Por defecto los IDs nuevos van a variables
                return "VARIABLES";
            }
        }
        
        // Literales
        if (tipoToken.startsWith("LIT_") || tipoToken.equals("TRUE") || tipoToken.equals("FALSE")) {
            return "CONSTANTES";
        }
        
        // Para otros tipos de token (operadores, delimitadores, etc.)
        return "NINGUNO";
    }
    
    /**
     * Inserta un símbolo en la tabla correspondiente
     */
    public void insertarSimbolo(String lexema, String tipoToken, int linea, int columna, Object valor) {
        SymbolInfo info = new SymbolInfo(lexema, tipoToken, linea, columna);
        
        if (valor != null) {
            info.setValor(valor);
        }
        
        String tabla = determinarTabla(tipoToken, lexema);
        
        switch (tabla) {
            case "VARIABLES":
                info.setEsVariable(true);
                variables.put(lexema, info);
                break;
            case "FUNCIONES":
                info.setEsFuncion(true);
                funciones.put(lexema, info);
                break;
            case "CONSTANTES":
                info.setEsConstante(true);
                constantes.put(lexema, info);
                break;
            case "PALABRAS_RESERVADAS":
                // No insertar nuevamente, ya están precargadas
                break;
        }
    }
    
    /**
     * Marcar un identificador como función
     */
    public void marcarComoFuncion(String lexema, String tipoRetorno) {
        if (variables.containsKey(lexema)) {
            SymbolInfo info = variables.remove(lexema);
            info.setEsFuncion(true);
            info.setEsVariable(false);
            info.setTipoVariable(tipoRetorno);
            funciones.put(lexema, info);
        } else if (!funciones.containsKey(lexema)) {
            SymbolInfo info = new SymbolInfo(lexema, "ID", 0, 0);
            info.setEsFuncion(true);
            info.setTipoVariable(tipoRetorno);
            funciones.put(lexema, info);
        }
    }
    
    /**
     * Actualizar el tipo de una variable
     */
    public void actualizarTipoVariable(String lexema, String tipo) {
        if (variables.containsKey(lexema)) {
            SymbolInfo info = variables.get(lexema);
            info.setTipoVariable(tipo);
        }
    }
    
    /**
     * Escribir todas las tablas de símbolos en un archivo
     */
    public void escribirTablas(String archivo) throws IOException {
        PrintWriter writer = new PrintWriter(new FileWriter(archivo));
        
        writer.println("===== TABLA DE VARIABLES =====");
        for (SymbolInfo info : variables.values()) {
            writer.println(info);
        }
        
        writer.println("\n===== TABLA DE FUNCIONES =====");
        for (SymbolInfo info : funciones.values()) {
            writer.println(info);
        }
        
        writer.println("\n===== TABLA DE CONSTANTES =====");
        for (SymbolInfo info : constantes.values()) {
            writer.println(info);
        }
        
        writer.println("\n===== TABLA DE PALABRAS RESERVADAS =====");
        for (SymbolInfo info : palabrasReservadas.values()) {
            writer.println(info);
        }
        
        writer.close();
    }
    
    // Getters para las tablas
    public Map<String, SymbolInfo> getVariables() { return variables; }
    public Map<String, SymbolInfo> getFunciones() { return funciones; }
    public Map<String, SymbolInfo> getConstantes() { return constantes; }
    public Map<String, SymbolInfo> getPalabrasReservadas() { return palabrasReservadas; }
}