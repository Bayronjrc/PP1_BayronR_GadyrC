package main.java.symbol;

import java.util.*;
import java.io.*;

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
        System.out.println("DEBUG: Entrando a scope " + tipoAlcance + ":" + nombreAlcance + " (nivel " + (currentLevel-1) + ")");
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
            System.out.println("DEBUG: Saliendo de scope " + currentScope.getTipoAlcance() + ":" + currentScope.getNombreAlcance());
            
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
    
    // ============= MÉTODO PARA COMPATIBILIDAD CON PARSER =============

    /**
     * Obtiene la tabla de símbolos original para mantener compatibilidad
     * con el sistema del Proyecto 1
     * 
     * @return La tabla de símbolos original
     */
    public SymbolTable getOriginalTable() {
        if (originalTable == null) {
            originalTable = new SymbolTable();
        }
        return originalTable;
    }
    /**
     * NUEVO: Método para mostrar alcances durante ejecución
     */
    public void printCurrentScopes() {
        System.out.println("=== ALCANCES ACTUALES ===");
        for (int i = 0; i < scopeStack.size(); i++) {
            Scope scope = scopeStack.get(i);
            System.out.println("Nivel " + i + ": " + scope.toString() + 
                              " (Símbolos: " + scope.getCantidadSimbolos() + ")");
        }
        System.out.println("========================");
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
        
        System.out.println("DEBUG: Variable '" + name + "' declarada en scope " + currentScope.getNombreAlcance() + " como " + type);
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
        
        System.out.println("DEBUG: Función '" + name + "' declarada como " + returnType + " con " + 
                          (paramTypes != null ? paramTypes.size() : 0) + " parámetros");
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
    
    /**
     * NUEVO: Verificación mejorada de tipos en expresiones
     */
    public String getExpressionType(String expr) {
        if (expr == null) return null;
        
        // Detectar literales directos
        if (expr.equals("INT") || expr.equals("FLOAT") || expr.equals("BOOL") || 
            expr.equals("CHAR") || expr.equals("STRING")) {
            return expr;
        }
        
        // Detectar operaciones numéricas
        if (expr.equals("NUMERIC")) {
            return "FLOAT"; // Por defecto, las operaciones numéricas son FLOAT
        }
        
        // NUEVO: Si expr es un nombre de variable, buscar su tipo
        if (expr.matches("[a-zA-Z][a-zA-Z0-9]*")) { // Es un identificador válido
            SymbolInfo symbol = getCurrentScope().lookup(expr);
            if (symbol != null) {
                if (symbol.esVariable()) {
                    System.out.println("DEBUG: Expresión '" + expr + "' es variable tipo " + symbol.getTipoVariable());
                    return symbol.getTipoVariable();
                } else if (symbol.esFuncion()) {
                    System.out.println("DEBUG: Expresión '" + expr + "' es función tipo " + symbol.getTipoVariable());
                    return symbol.getTipoVariable(); // Tipo de retorno
                }
            }
        }
        
        // Si es una llamada a función o error
        if (expr.equals("FUNCTION_CALL") || expr.equals("ERROR")) {
            return expr;
        }
        
        // Para expresiones complejas, intentar inferir tipo
        if (expr.contains("VARIABLE")) {
            return "VARIABLE"; // Se procesará más tarde
        }
        
        System.out.println("DEBUG: Expresión '" + expr + "' no reconocida, retornando UNKNOWN");
        return "UNKNOWN";
    }

    /**
     * NUEVO: Verificar compatibilidad de return MEJORADA
     */
    public void checkReturnStatement(String functionName, String returnType, int line) {
        Scope globalScope = getGlobalScope();
        if (globalScope == null) {
            addError("Error interno: no hay alcance global en línea " + line);
            return;
        }
        
        SymbolInfo function = globalScope.getLocal(functionName);
        if (function == null || !function.esFuncion()) {
            addError("Error interno: función '" + functionName + "' no encontrada en línea " + line);
            return;
        }
        
        String expectedReturnType = function.getTipoVariable();
        
        // Procesar tipo de expresión retornada
        String actualReturnType = getExpressionType(returnType);
        
        System.out.println("DEBUG Return: función=" + functionName + 
                          ", esperado=" + expectedReturnType + 
                          ", actual=" + returnType + 
                          ", procesado=" + actualReturnType);
        
        // Verificar compatibilidad
        if (expectedReturnType.equals("VOID")) {
            if (actualReturnType != null && !actualReturnType.equals("UNKNOWN")) {
                addError("Función '" + functionName + "' de tipo VOID no puede retornar un valor en línea " + line);
            }
        } else {
            if (actualReturnType == null || actualReturnType.equals("UNKNOWN")) {
                addError("Función '" + functionName + "' de tipo '" + expectedReturnType + "' debe retornar un valor en línea " + line);
            } else {
                // Verificar compatibilidad de tipos específicos
                if (expectedReturnType.equals("BOOL")) {
                    if (!actualReturnType.equals("BOOL")) {
                        addError("Función '" + functionName + "' de tipo BOOL no puede retornar " + actualReturnType + " en línea " + line);
                    }
                } else if (expectedReturnType.equals("FLOAT")) {
                    if (actualReturnType.equals("INT")) {
                        // INT compatible con FLOAT
                        System.out.println("Conversión implícita: INT a FLOAT en return de función " + functionName);
                    } else if (!actualReturnType.equals("FLOAT") && !actualReturnType.equals("NUMERIC")) {
                        addError("Función '" + functionName + "' esperaba retornar FLOAT pero retorna " + actualReturnType + " en línea " + line);
                    }
                } else if (expectedReturnType.equals("INT")) {
                    if (!actualReturnType.equals("INT") && !actualReturnType.equals("NUMERIC")) {
                        addError("Función '" + functionName + "' esperaba retornar INT pero retorna " + actualReturnType + " en línea " + line);
                    }
                } else if (!expectedReturnType.equals(actualReturnType)) {
                    addError("Función '" + functionName + "' esperaba retornar " + expectedReturnType + " pero retorna " + actualReturnType + " en línea " + line);
                }
            }
        }
    }
    
    // ============= MÉTODOS WRAPPER PARA COMPATIBILIDAD CON PARSER =============
    
    /**
     * Wrapper para compatibilidad con el parser actualizado
     * Acepta Object en lugar de String para el tipo
     */
    public boolean declareVariable(String name, Object type, int line, int column, boolean initialized) {
        return declareVariable(name, type.toString(), line, column, initialized);
    }
    
    /**
     * Wrapper para checkAssignment que acepta Object en lugar de String
     */
    public boolean checkAssignment(String varName, Object expType, int line) {
        if (expType == null) {
            addError("Tipo de expresión nulo en asignación a '" + varName + "' en línea " + line);
            return false;
        }
        return checkAssignment(varName, expType.toString(), line);
    }
    
    /**
     * Wrapper para checkTypeCompatibility que acepta Object
     */
    public boolean checkTypeCompatibility(Object expected, Object actual, int line) {
        if (expected == null || actual == null) {
            addError("Tipo nulo en verificación de compatibilidad en línea " + line);
            return false;
        }
        return checkTypeCompatibility(expected.toString(), actual.toString(), line);
    }
    
    /**
     * Wrapper para checkArrayAccess que acepta Object
     */
    public String checkArrayAccess(String arrayName, Object index1, Object index2, int line) {
        String idx1Type = (index1 != null) ? index1.toString() : "NULL";
        String idx2Type = (index2 != null) ? index2.toString() : "NULL";
        return checkArrayAccess(arrayName, idx1Type, idx2Type, line);
    }
    
    /**
     * Método adicional para usar variables (compatibilidad)
     */
    public void useVariable(String varName, int line, int column) {
        checkVariableUsage(varName, line);
    }
    
    /**
     * NUEVO: Wrapper para checkReturnStatement que acepta Object
     */
    public void checkReturnStatement(String functionName, Object returnType, int line) {
        String returnTypeStr = (returnType != null) ? returnType.toString() : null;
        checkReturnStatement(functionName, returnTypeStr, line);
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
        System.out.println("Alcances procesados total: " + currentLevel);
        System.out.println("Alcances activos: " + scopeStack.size());
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
    
    // ============= COMPATIBILIDAD Y ESCRITURA MEJORADA =============
    
    /**
     * MEJORADO: Escribe las tablas con información detallada de alcances
     */
    public void escribirTablas(String archivo) throws IOException {
        PrintWriter writer = new PrintWriter(new FileWriter(archivo));
        
        // Escribir información de alcances
        writer.println("===== ANÁLISIS SEMÁNTICO =====");
        writer.println("Alcances procesados total: " + currentLevel); // Total creados
        writer.println("Alcances activos: " + scopeStack.size()); // Solo los activos
        writer.println("Errores semánticos: " + errors.size());
        writer.println("Advertencias: " + warnings.size());
        writer.println();
        
        // Escribir errores si existen
        if (!errors.isEmpty()) {
            writer.println("===== ERRORES SEMÁNTICOS =====");
            for (String error : errors) {
                writer.println(error);
            }
            writer.println();
        }
        
        // Escribir advertencias si existen
        if (!warnings.isEmpty()) {
            writer.println("===== ADVERTENCIAS =====");
            for (String warning : warnings) {
                writer.println(warning);
            }
            writer.println();
        }
        
        // Escribir información de scopes activos
        writer.println("===== INFORMACIÓN DE ALCANCES =====");
        for (int i = 0; i < scopeStack.size(); i++) {
            Scope scope = scopeStack.get(i);
            writer.println("Alcance " + (i+1) + ": " + scope.toString());
            
            Map<String, SymbolInfo> symbols = scope.getAllLocalSymbols();
            writer.println("  - Símbolos locales: " + symbols.size());
            
            int variables = 0, funciones = 0;
            for (SymbolInfo symbol : symbols.values()) {
                if (symbol.esVariable()) variables++;
                if (symbol.esFuncion()) funciones++;
            }
            writer.println("  - Variables: " + variables + ", Funciones: " + funciones);
            writer.println();
        }
        
        // Escribir símbolos por alcance
        for (int i = 0; i < scopeStack.size(); i++) {
            Scope scope = scopeStack.get(i);
            writer.println("===== ALCANCE: " + scope.toString() + " =====");
            Map<String, SymbolInfo> symbols = scope.getAllLocalSymbols();
            
            if (symbols.isEmpty()) {
                writer.println("(Sin símbolos locales)");
            } else {
                for (SymbolInfo symbol : symbols.values()) {
                    writer.println(symbol.toString());
                }
            }
            writer.println();
        }
        
        writer.close();
    }
    
    /**
     * Obtiene estadisticas del analisis MEJORADAS
     */
    public Map<String, Integer> getStatistics() {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("alcances_procesados", currentLevel);
        stats.put("alcances_activos", scopeStack.size());
        stats.put("errores", errors.size());
        stats.put("advertencias", warnings.size());
        
        int totalSymbols = 0;
        int variables = 0;
        int functions = 0;
        
        for (Scope scope : scopeStack) {
            Map<String, SymbolInfo> symbols = scope.getAllLocalSymbols();
            totalSymbols += symbols.size();
            for (SymbolInfo symbol : symbols.values()) {
                if (symbol.esVariable()) variables++;
                if (symbol.esFuncion()) functions++;
            }
        }
        
        stats.put("simbolos_totales", totalSymbols);
        stats.put("variables", variables);
        stats.put("funciones", functions);
        
        return stats;
    }
}
    