package main.java.symbol;

import java.util.*;
import java.io.*;

/**
 * Tabla de símbolos para el compilador.
 * 
 * Esta clase implementa una estructura para almacenar y gestionar los diferentes
 * tipos de símbolos encontrados durante el análisis léxico y sintáctico.
 * Mantiene cuatro tablas separadas para variables, funciones, constantes y
 * palabras reservadas del lenguaje.
 * 
 * @author Compilador
 * @version 1.0
 */
public class SymbolTable {
    /**
     * Mapa para almacenar información de variables identificadas.
     * Clave: nombre de la variable (lexema)
     * Valor: objeto SymbolInfo con todos los detalles del símbolo
     */
    private Map<String, SymbolInfo> variables;
    
    /**
     * Mapa para almacenar información de funciones identificadas.
     * Clave: nombre de la función (lexema)
     * Valor: objeto SymbolInfo con todos los detalles del símbolo
     */
    private Map<String, SymbolInfo> funciones;
    
    /**
     * Mapa para almacenar información de constantes y literales.
     * Clave: representación textual del literal (lexema)
     * Valor: objeto SymbolInfo con todos los detalles del símbolo
     */
    private Map<String, SymbolInfo> constantes;
    
    /**
     * Mapa para almacenar información de palabras reservadas del lenguaje.
     * Clave: palabra reservada (lexema)
     * Valor: objeto SymbolInfo con todos los detalles del símbolo
     */
    private Map<String, SymbolInfo> palabrasReservadas;
    
    /**
     * Constructor que inicializa las cuatro tablas de símbolos y precarga
     * las palabras reservadas del lenguaje.
     */
    public SymbolTable() {
        variables = new HashMap<>();
        funciones = new HashMap<>();
        constantes = new HashMap<>();
        palabrasReservadas = new HashMap<>();
        
        // Inicializar palabras reservadas
        inicializarPalabrasReservadas();
    }
    
    /**
     * Inicializa la tabla de palabras reservadas con todas las palabras
     * clave del lenguaje.
     * 
     * Las palabras son almacenadas en minúsculas como clave, pero su tipo
     * se almacena en mayúsculas según la convención del compilador.
     */
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
     * Determina en qué tabla debe ser almacenado un token específico.
     * 
     * @param tipoToken Tipo del token (ID, LIT_INT, etc.)
     * @param lexema Texto del token
     * @return Nombre de la tabla donde debe almacenarse el símbolo 
     *         ("VARIABLES", "FUNCIONES", "CONSTANTES", "PALABRAS_RESERVADAS" o "NINGUNO")
     */
    public String determinarTabla(String tipoToken, String lexema) {
        // Verificar si es una palabra reservada (tienen prioridad)
        if (palabrasReservadas.containsKey(lexema.toLowerCase())) {
            return "PALABRAS_RESERVADAS";
        }
        
        // Los identificadores pueden ser variables o funciones
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
        
        // Los literales y valores booleanos van a constantes
        if (tipoToken.startsWith("LIT_") || tipoToken.equals("TRUE") || tipoToken.equals("FALSE")) {
            return "CONSTANTES";
        }
        
        // Otros tokens (operadores, delimitadores, etc.) no se almacenan en tablas
        return "NINGUNO";
    }
    
    /**
     * Inserta un símbolo en la tabla correspondiente según su tipo.
     * 
     * @param lexema Texto del token (identificador, literal, etc.)
     * @param tipoToken Tipo del token según el analizador léxico
     * @param linea Número de línea donde se encontró el token
     * @param columna Número de columna donde se encontró el token
     * @param valor Valor asociado al token (para constantes y literales)
     */
    public void insertarSimbolo(String lexema, String tipoToken, int linea, int columna, Object valor) {
        // Crear objeto con la información del símbolo
        SymbolInfo info = new SymbolInfo(lexema, tipoToken, linea, columna);
        
        // Establecer valor si existe
        if (valor != null) {
            info.setValor(valor);
        }
        
        // Determinar en qué tabla debe insertarse
        String tabla = determinarTabla(tipoToken, lexema);
        
        // Insertar en la tabla correspondiente
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
     * Marca un identificador como función, posiblemente moviéndolo
     * desde la tabla de variables a la de funciones si ya existía.
     * 
     * @param lexema Nombre de la función
     * @param tipoRetorno Tipo de retorno de la función
     */
    public void marcarComoFuncion(String lexema, String tipoRetorno) {
        // Si ya existía como variable, moverlo a funciones
        if (variables.containsKey(lexema)) {
            SymbolInfo info = variables.remove(lexema);
            info.setEsFuncion(true);
            info.setEsVariable(false);
            info.setTipoVariable(tipoRetorno);
            funciones.put(lexema, info);
        } 
        // Si no existe como función, crearlo
        else if (!funciones.containsKey(lexema)) {
            SymbolInfo info = new SymbolInfo(lexema, "ID", 0, 0);
            info.setEsFuncion(true);
            info.setTipoVariable(tipoRetorno);
            funciones.put(lexema, info);
        }
    }
    
    /**
     * Actualiza el tipo de datos de una variable ya registrada.
     * 
     * @param lexema Nombre de la variable
     * @param tipo Tipo de datos de la variable (INT, FLOAT, etc.)
     */
    public void actualizarTipoVariable(String lexema, String tipo) {
        if (variables.containsKey(lexema)) {
            SymbolInfo info = variables.get(lexema);
            info.setTipoVariable(tipo);
        }
    }
    
    /**
     * Escribe el contenido de todas las tablas de símbolos en un archivo.
     * 
     * @param archivo Ruta del archivo donde se escribirán las tablas
     * @throws IOException Si ocurre un error durante la escritura del archivo
     */
    public void escribirTablas(String archivo) throws IOException {
        PrintWriter writer = new PrintWriter(new FileWriter(archivo));
        
        // Escribir tabla de variables
        writer.println("===== TABLA DE VARIABLES =====");
        for (SymbolInfo info : variables.values()) {
            writer.println(info);
        }
        
        // Escribir tabla de funciones
        writer.println("\n===== TABLA DE FUNCIONES =====");
        for (SymbolInfo info : funciones.values()) {
            writer.println(info);
        }
        
        // Escribir tabla de constantes
        writer.println("\n===== TABLA DE CONSTANTES =====");
        for (SymbolInfo info : constantes.values()) {
            writer.println(info);
        }
        
        // Escribir tabla de palabras reservadas
        writer.println("\n===== TABLA DE PALABRAS RESERVADAS =====");
        for (SymbolInfo info : palabrasReservadas.values()) {
            writer.println(info);
        }
        
        writer.close();
    }
    
    /**
     * Obtiene la tabla de variables.
     * 
     * @return Mapa con todas las variables registradas
     */
    public Map<String, SymbolInfo> getVariables() { return variables; }
    
    /**
     * Obtiene la tabla de funciones.
     * 
     * @return Mapa con todas las funciones registradas
     */
    public Map<String, SymbolInfo> getFunciones() { return funciones; }
    
    /**
     * Obtiene la tabla de constantes.
     * 
     * @return Mapa con todas las constantes registradas
     */
    public Map<String, SymbolInfo> getConstantes() { return constantes; }
    
    /**
     * Obtiene la tabla de palabras reservadas.
     * 
     * @return Mapa con todas las palabras reservadas del lenguaje
     */
    public Map<String, SymbolInfo> getPalabrasReservadas() { return palabrasReservadas; }
}