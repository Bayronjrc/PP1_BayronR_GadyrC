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

    
    // ============= DECLARACIONES =============
    
    /**
     * Declara una variable en el alcance actual
     * 
     * @param name Nombre de la variable
     * @param type Tipo de la variable
     * @param line Línea de declaración
     * @param column Columna de declaración
     * @param inicializada Si la variable se inicializa en la declaracion
     * @return true si se declara exitosamente
     */
    public boolean declareVariable(String name, String type, int line, int column, boolean inicializada) {
        Scope currentScope = getCurrentScope();
        if (currentScope == null) {
            addError("Error interno: no hay alcance activo para declarar variable '" + name + "' en linea " + line);
            return false;
        }
        
        // Verificar si ya existe en el alcance actual
        if (currentScope.existsLocal(name)) {
            addError("Variable '" + name + "' ya declarada en este alcance en linea " + line);
            return false;
        }
        
        // Crear el simbolo
        SymbolInfo symbol = new SymbolInfo(name, "ID", line, column, currentScope.getNivel());
        symbol.setTipoVariable(type);
        symbol.setEsVariable(true);
        symbol.setInicializada(inicializada);
        
        // Declarar en el alcance
        currentScope.declare(name, symbol);
        
        // Mantener compatibilidad con tabla original
        originalTable.insertarSimbolo(name, "ID", line, column, name);
        originalTable.actualizarTipoVariable(name, type);
        
        return true;
    }
    
    /**
     * Sobrecarga para variables sin inicializacion
     */
    public boolean declareVariable(String name, String type, int line, int column) {
        return declareVariable(name, type, line, column, false);
    }
    
    /**
     * Declara una funcion en el alcance global
     * 
     * @param name Nombre de la funcion
     * @param returnType Tipo de retorno
     * @param paramTypes Lista de tipos de parametros
     * @param line Linea de declaracion
     * @param column Columna de declaracion
     * @return true si se declara exitosamente
     */
    public boolean declareFunction(String name, String returnType, List<String> paramTypes, int line, int column) {
        Scope globalScope = getGlobalScope();
        if (globalScope == null) {
            addError("Error interno: no hay alcance global para declarar funcion '" + name + "' en linea " + line);
            return false;
        }
        
        // Verificar si ya existe
        if (globalScope.existsLocal(name)) {
            addError("Funcion '" + name + "' ya declarada en linea " + line);
            return false;
        }
        
        // Crear el simbolo de funcion
        SymbolInfo symbol = new SymbolInfo(name, "ID", line, column, 0);
        symbol.setTipoVariable(returnType);
        symbol.setEsFuncion(true);
        if (paramTypes != null) {
            for (String paramType : paramTypes) {
                symbol.addParametro(paramType);
            }
        }
        
        // Declarar en alcance global
        globalScope.declare(name, symbol);
        
        // Mantener compatibilidad
        originalTable.marcarComoFuncion(name, returnType);
        
        return true;
    }

    
    /**
     * Declara un array bidimensional
     */
    public boolean declareArray(String name, String type, int dim1, int dim2, int line, int column) {
        boolean result = declareVariable(name, type, line, column, true);
        if (result) {
            SymbolInfo symbol = getCurrentScope().getLocal(name);
            symbol.addDimension(dim1);
            symbol.addDimension(dim2);
        }
        return result;
    }
    
    
    // ============= VERIFICACIONES SEMANTICAS =============
    
    /**
     * Verifica el uso de una variable
     * 
     * @param name Nombre de la variable
     * @param line Linea donde se usa
     * @return SymbolInfo de la variable o null si hay error
     */
    public SymbolInfo checkVariableUsage(String name, int line) {
        Scope currentScope = getCurrentScope();
        if (currentScope == null) {
            addError("Error interno: no hay alcance activo para usar variable '" + name + "' en linea " + line);
            return null;
        }
        
        SymbolInfo symbol = currentScope.lookup(name);
        if (symbol == null) {
            addError("Variable '" + name + "' no declarada en linea " + line);
            return null;
        }
        
        if (symbol.esVariable() && !symbol.estaInicializada()) {
            addError("Variable '" + name + "' usada sin inicializar en linea " + line);
            return null;
        }
        
        return symbol;
    }
}
