package main.java.symbol;

import java.util.*;
import java.io.*;

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