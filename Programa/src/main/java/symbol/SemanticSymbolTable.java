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

    
    /**
     * Verifica una asignacon de variable
     * 
     * @param name Nombre de la variable
     * @param valueType Tipo del valor a asignar
     * @param line Linea de la asignacion
     * @return true si la asignacion es valida
     */
    public boolean checkAssignment(String name, String valueType, int line) {
        SymbolInfo variable = checkVariableUsage(name, line);
        if (variable == null) {
            return false;
        }
        
        if (!variable.esCompatibleCon(valueType)) {
            addError("Tipos incompatibles en asignacion: '" + variable.getTipoVariable() + 
                    "' no puede recibir '" + valueType + "' en linea " + line);
            return false;
        }
        
        // Marcar como inicializada
        variable.setInicializada(true);
        return true;
    }
    
    /**
     * Verifica compatibilidad de tipos
     * 
     * @param expected Tipo esperado
     * @param actual Tipo actual
     * @param line Linea donde ocurre
     * @return true si son compatibles
     */
    public boolean checkTypeCompatibility(String expected, String actual, int line) {
        if (expected == null || actual == null) {
            addError("Tipo nulo en verificacion de compatibilidad en linea " + line);
            return false;
        }
        
        if (expected.equals(actual)) {
            return true;
        }
        
        // Crear un simbolo temporal para usar el metodo de compatibilidad
        SymbolInfo tempSymbol = new SymbolInfo("temp", "ID", line, 0);
        tempSymbol.setTipoVariable(expected);
        
        if (!tempSymbol.esCompatibleCon(actual)) {
            addError("Tipos incompatibles: esperado '" + expected + "', encontrado '" + actual + "' en linea " + line);
            return false;
        }
        
        return true;
    }

    
    /**
     * Verifica una llamada a funcion
     * 
     * @param name Nombre de la funcion
     * @param argTypes Lista de tipos de argumentos
     * @param line inea de la llamada
     * @return Tipo de retorno de la función o null si hay error
     */
    public String checkFunctionCall(String name, List<String> argTypes, int line) {
        Scope currentScope = getCurrentScope();
        if (currentScope == null) {
            addError("Error interno: no hay alcance activo para llamar funcion '" + name + "' en linea " + line);
            return null;
        }
        
        SymbolInfo function = currentScope.lookupFunction(name);
        if (function == null) {
            addError("Funcion '" + name + "' no declarada en linea " + line);
            return null;
        }
        
        // Verificar número de argumentos
        List<String> expectedParams = function.getParametros();
        if (expectedParams.size() != argTypes.size()) {
            addError("Funcion '" + name + "' espera " + expectedParams.size() + 
                    " argumentos, pero se proporcionaron " + argTypes.size() + " en linea " + line);
            return null;
        }
        
        // Verificar tipos de argumentos
        for (int i = 0; i < expectedParams.size(); i++) {
            if (!checkTypeCompatibility(expectedParams.get(i), argTypes.get(i), line)) {
                addError("Argumento " + (i+1) + " de funcion '" + name + 
                        "' tiene tipo incorrecto en lonea " + line);
                return null;
            }
        }
        
        // Marcar funcion como utilizada
        function.setUtilizada(true);
        
        return function.getTipoVariable(); 
    }
    
    /**
     * Verifica una operacion aritmetica entre dos tipos
     * 
     * @param leftType Tipo del operando izquierdo
     * @param rightType Tipo del operando derecho
     * @param operator Operador (+, -, *, /, etc.)
     * @param line Linea de la operacion
     * @return Tipo resultante de la operación o null si es invalida
     */
    public String checkArithmeticOperation(String leftType, String rightType, String operator, int line) {
        // Crear simbolos temporales para verificacion
        SymbolInfo leftSymbol = new SymbolInfo("temp_left", "ID", line, 0);
        leftSymbol.setTipoVariable(leftType);
        
        SymbolInfo rightSymbol = new SymbolInfo("temp_right", "ID", line, 0);
        rightSymbol.setTipoVariable(rightType);
        
        // Verificar que ambos sean numericos
        if (!leftSymbol.esNumerico() || !rightSymbol.esNumerico()) {
            addError("Operacion aritmetica '" + operator + "' requiere operandos numericos en linea " + line);
            return null;
        }
        
        // Obtener tipo resultante
        String resultType = leftSymbol.tipoResultanteConOperacion(rightType);
        if (resultType == null) {
            addError("Operacion aritmetica invalida entre '" + leftType + "' y '" + rightType + "' en linea " + line);
        }
        
        return resultType;
    }

    
    /**
     * Verifica una operacion relacional
     * 
     * @param leftType Tipo del operando izquierdo
     * @param rightType Tipo del operando derecho
     * @param operator Operador (<, >, <=, >=, ==, !=)
     * @param line Linea de la operación
     * @return "BOOL" si es valida, null si es invalida
     */
    public String checkRelationalOperation(String leftType, String rightType, String operator, int line) {
        // Operadores de igualdad permiten booleanos
        if (operator.equals("==") || operator.equals("!=")) {
            if (leftType.equals("BOOL") && rightType.equals("BOOL")) {
                return "BOOL";
            }
        }
        
        // Crear simbolos temporales
        SymbolInfo leftSymbol = new SymbolInfo("temp_left", "ID", line, 0);
        leftSymbol.setTipoVariable(leftType);
        
        SymbolInfo rightSymbol = new SymbolInfo("temp_right", "ID", line, 0);
        rightSymbol.setTipoVariable(rightType);
        
        // Verificar que sean comparables
        if (!leftSymbol.esComparable() || !rightSymbol.esComparable()) {
            addError("Operación relacional '" + operator + "' requiere operandos comparables en línea " + line);
            return null;
        }
        
        // Verificar compatibilidad de tipos
        if (!checkTypeCompatibility(leftType, rightType, line)) {
            return null;
        }
        
        return "BOOL";
    }
    
    /**
     * Verifica una operacion logica
     * 
     * @param leftType Tipo del operando izquierdo
     * @param rightType Tipo del operando derecho (null para operaciones unarias)
     * @param operator Operador (^, #, !)
     * @param line Linea de la operación
     * @return "BOOL" si es valida, null si es invalida
     */
    public String checkLogicalOperation(String leftType, String rightType, String operator, int line) {
        // Verificar que el operando izquierdo sea booleano
        if (!leftType.equals("BOOL")) {
            addError("Operación lógica '" + operator + "' requiere operando booleano en línea " + line);
            return null;
        }
        
        // Para operaciones binarias, verificar operando derecho
        if (rightType != null && !rightType.equals("BOOL")) {
            addError("Operación lógica '" + operator + "' requiere ambos operandos booleanos en línea " + line);
            return null;
        }
        
        return "BOOL";
    }

    
    /**
     * Verifica acceso a array
     * 
     * @param arrayName Nombre del array
     * @param index1 Tipo del primer índice
     * @param index2 Tipo del segundo índice (null para array unidimensional)
     * @param line Linea del acceso
     * @return Tipo del elemento del array o null si hay error
     */
    public String checkArrayAccess(String arrayName, String index1, String index2, int line) {
        SymbolInfo array = checkVariableUsage(arrayName, line);
        if (array == null) {
            return null;
        }
        
        if (!array.esArray()) {
            addError("'" + arrayName + "' no es un array en linea " + line);
            return null;
        }
        
        // Verificar que los índices sean enteros
        if (!index1.equals("INT")) {
            addError("Indice de array debe ser entero en linea " + line);
            return null;
        }
        
        if (index2 != null && !index2.equals("INT")) {
            addError("Segundo indice de array debe ser entero en linea " + line);
            return null;
        }
        
        // Verificar dimensiones
        List<Integer> dimensions = array.getDimensiones();
        if (index2 != null && dimensions.size() < 2) {
            addError("Array '" + arrayName + "' no es bidimensional en linea " + line);
            return null;
        }
        
        if (index2 == null && dimensions.size() != 1) {
            addError("Array '" + arrayName + "' requiere dos indices en linea " + line);
            return null;
        }
        
        return array.getTipoVariable();
    }

    
    // ============= VERIFICACIONES ESPECIALES =============
    
    /**
     * Verifica que exista la función main
     */
    public boolean checkMainFunction() {
        Scope globalScope = getGlobalScope();
        if (globalScope == null) {
            addError("No hay alcance global definido");
            return false;
        }
        
        SymbolInfo main = globalScope.getLocal("main");
        if (main == null || !main.esFuncion()) {
            addError("Función 'main' no encontrada");
            return false;
        }
        
        if (!main.getTipoVariable().equals("VOID")) {
            addError("Función 'main' debe ser de tipo 'void'");
            return false;
        }
        
        if (!main.getParametros().isEmpty()) {
            addError("Funcion 'main' no debe tener parametros");
            return false;
        }
        
        return true;
    }
    
    /**
     * Verifica que una funcion tenga return si es necesario
     * 
     * @param functionName Nombre de la función
     * @param hasReturn Si la función tiene return
     * @param line Línea donde termina la función
     */
    public void checkFunctionReturn(String functionName, boolean hasReturn, int line) {
        Scope globalScope = getGlobalScope();
        if (globalScope != null) {
            SymbolInfo function = globalScope.getLocal(functionName);
            if (function != null && function.esFuncion()) {
                String returnType = function.getTipoVariable();
                if (!returnType.equals("VOID") && !hasReturn) {
                    addError("Funcion '" + functionName + "' debe retornar un valor de tipo '" + returnType + "' en lonea " + line);
                }
            }
        }
    }
    
    
    // ============= MANEJO DE ERRORES Y ADVERTENCIAS =============
    
    private void addError(String error) {
        errors.add(error);
        System.err.println("Error semantico: " + error);
    }
    
    private void addWarning(String warning) {
        warnings.add(warning);
        System.out.println("Advertencia: " + warning);
    }
    
    public List<String> getErrors() { return new ArrayList<>(errors); }
    public List<String> getWarnings() { return new ArrayList<>(warnings); }
    public boolean hasErrors() { return !errors.isEmpty(); }
    public boolean hasWarnings() { return !warnings.isEmpty(); }
    public int getErrorCount() { return errors.size(); }
    public int getWarningCount() { return warnings.size(); }
    
    /**
     * Imprime un resumen de errores y advertencias
     */
    public void printSummary() {
        System.out.println("\n=== RESUMEN DEL ANALISIS SEMANTICO ===");
        System.out.println("Errores encontrados: " + errors.size());
        System.out.println("Advertencias encontradas: " + warnings.size());
        
        if (!errors.isEmpty()) {
            System.out.println("\nERRORES:");
            for (int i = 0; i < errors.size(); i++) {
                System.out.println((i+1) + ". " + errors.get(i));
            }
        }
        
        if (!warnings.isEmpty()) {
            System.out.println("\nADVERTENCIAS:");
            for (int i = 0; i < warnings.size(); i++) {
                System.out.println((i+1) + ". " + warnings.get(i));
            }
        }
    }
}
