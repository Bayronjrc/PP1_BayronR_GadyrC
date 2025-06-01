package main.java.symbol;

import java.util.*;
import java.io.*;
import java.lang.foreign.MemorySegment.Scope;

/**
 * Tabla de simbolos para analisis semantico.
 * 
 * Esta clase maneja multiples alcances (scopes) organizados en una pila,
 * permitiendo verificaciones semanticas como tipos, declaraciones,
 * compatibilidad y visibilidad de simbolos.
 * 
 * @author Compilador
 * @version 1.0
 */
public class SemanticSymbolTable {
    /** Pila de alcances activos */
    private Stack<Scope> scopeStack;
    
    /** Contador para niveles de alcance */
    private int currentLevel;
    
    /** Lista de errores semanticos encontrados */
    private List<String> errors;
    
    /** Lista de advertencias (variables no usadas, etc.) */
    private List<String> warnings;
    
    /** Tabla original para mantener compatibilidad (opcional) */
    private SymbolTable originalTable;
    
    /** Contador para generar nombres unicos de alcances */
    private int scopeCounter;
    
    /**
     * Constructor que inicializa la tabla semantica
     */
    public SemanticSymbolTable() {
        scopeStack = new Stack<>();
        currentLevel = 0;
        errors = new ArrayList<>();
        warnings = new ArrayList<>();
        originalTable = new SymbolTable(); 
        scopeCounter = 0;
        
        // Crear alcance global
        enterScope("GLOBAL", "global");
    }
}
