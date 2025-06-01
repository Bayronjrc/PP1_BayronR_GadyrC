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

    // ============= MANEJO DE ALCANCES =============
    
    /**
     * Entra a un nuevo alcance
     * 
     * @param tipoAlcance Tipo del alcance ("GLOBAL", "FUNCTION", "BLOCK")
     * @param nombreAlcance Nombre identificativo del alcance
     */
    public void enterScope(String tipoAlcance, String nombreAlcance) {
        Scope parent = scopeStack.isEmpty() ? null : scopeStack.peek();
        Scope newScope = new Scope(currentLevel++, parent, tipoAlcance, nombreAlcance);
        scopeStack.push(newScope);
    }
    
    /**
     * Entra a un nuevo alcance de bloque con nombre automatico
     */
    public void enterScope() {
        enterScope("BLOCK", "block_" + (++scopeCounter));
    }
    
    /**
     * Sale del alcance actual y genera advertencias sobre variables no usadas
     */
    public void exitScope() {
        if (!scopeStack.isEmpty()) {
            Scope currentScope = scopeStack.pop();
            
            // Generar advertencias para variables no utilizadas
            for (SymbolInfo symbol : currentScope.getVariablesNoUtilizadas()) {
                addWarning("Variable '" + symbol.getLexema() + "' declarada pero no utilizada en línea " + symbol.getLinea());
            }
        }
    }
    
    /**
     * Obtiene el alcance actual
     */
    public Scope getCurrentScope() {
        return scopeStack.isEmpty() ? null : scopeStack.peek();
    }
    
    /**
     * Obtiene el alcance global (primer elemento de la pila)
     */
    public Scope getGlobalScope() {
        return scopeStack.isEmpty() ? null : scopeStack.firstElement();
    }
}
