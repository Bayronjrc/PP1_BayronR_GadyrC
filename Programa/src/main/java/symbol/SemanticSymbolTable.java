package main.java.symbol;

import java.util.*;
import java.io.*;
import main.java.symbol.SymbolInfo;

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
    private Stack<Scope> scopeStack;
    
    private int currentLevel;
    
    private List<String> errors;
    
    private List<String> warnings;
    
    private SymbolTable originalTable;
    
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
        
        enterScope("GLOBAL", "global");
    }
    
    
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
    
    public void enterScope() {
        enterScope("BLOCK", "block_" + (++scopeCounter));
    }
    
    public void exitScope() {
        if (!scopeStack.isEmpty()) {
            Scope currentScope = scopeStack.pop();
            System.out.println("DEBUG: Saliendo de scope " + currentScope.getTipoAlcance() + ":" + currentScope.getNombreAlcance());
            
            for (SymbolInfo symbol : currentScope.getVariablesNoUtilizadas()) {
                addWarning("Variable '" + symbol.getLexema() + "' declarada pero no utilizada en línea " + symbol.getLinea());
            }
        }
    }
    
    public Scope getCurrentScope() {
        return scopeStack.isEmpty() ? null : scopeStack.peek();
    }
    
    public Scope getGlobalScope() {
        return scopeStack.isEmpty() ? null : scopeStack.firstElement();
    }
    

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
        
        if (currentScope.existsLocal(name)) {
            addError("Variable '" + name + "' ya declarada en este alcance en linea " + line);
            return false;
        }
        
        SymbolInfo symbol = new SymbolInfo(name, "ID", line, column, currentScope.getNivel());
        symbol.setTipoVariable(type);
        symbol.setEsVariable(true);
        symbol.setInicializada(inicializada);
        
        currentScope.declare(name, symbol);
        
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
        
        if (globalScope.existsLocal(name)) {
            addError("Funcion '" + name + "' ya declarada en linea " + line);
            return false;
        }
        
        SymbolInfo symbol = new SymbolInfo(name, "ID", line, column, 0);
        symbol.setTipoVariable(returnType);
        symbol.setEsFuncion(true);
        if (paramTypes != null) {
            for (String paramType : paramTypes) {
                symbol.addParametro(paramType);
            }
        }
        
        globalScope.declare(name, symbol);
        
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
        addError("Tipo nulo en verificación de compatibilidad en línea " + line);
        return false;
    }
    
    // ✅ COMPATIBILIDAD EXACTA
    if (expected.equals(actual)) {
        return true;
    }
    
    // ✅ CONVERSIONES IMPLÍCITAS PERMITIDAS
    // INT puede convertirse a FLOAT
    if (expected.equals("FLOAT") && actual.equals("INT")) {
        return true;
    }
    
    // FLOAT puede usarse donde se espera un número en operaciones
    if (expected.equals("INT") && actual.equals("FLOAT")) {
        addWarning("Conversión implícita de FLOAT a INT en línea " + line + " (posible pérdida de precisión)");
        return true; // Permitir pero advertir
    }
    
    // ✅ TIPOS NUMÉRICOS SON COMPATIBLES ENTRE SÍ
    if (isNumericType(expected) && isNumericType(actual)) {
        return true;
    }
    
    // ❌ INCOMPATIBLES
    addError("Tipos incompatibles: esperado '" + expected + "', encontrado '" + actual + "' en línea " + line);
    return false;
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
        
        List<String> expectedParams = function.getParametros();
        if (expectedParams.size() != argTypes.size()) {
            addError("Funcion '" + name + "' espera " + expectedParams.size() + 
                    " argumentos, pero se proporcionaron " + argTypes.size() + " en linea " + line);
            return null;
        }
        
        for (int i = 0; i < expectedParams.size(); i++) {
            if (!checkTypeCompatibility(expectedParams.get(i), argTypes.get(i), line)) {
                addError("Argumento " + (i+1) + " de funcion '" + name + 
                        "' tiene tipo incorrecto en lonea " + line);
                return null;
            }
        }
        
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
    // ✅ VERIFICAR QUE AMBOS SEAN TIPOS VÁLIDOS
    if (leftType == null || rightType == null) {
        addError("Tipo nulo en operación aritmética '" + operator + "' en línea " + line);
        return null;
    }
    
    // ✅ PROCESAR TIPOS DE EXPRESIONES
    String processedLeft = processExpressionType(leftType);
    String processedRight = processExpressionType(rightType);
    
    // ✅ VERIFICAR QUE SEAN NUMÉRICOS
    if (!isNumericType(processedLeft)) {
        addError("Operando izquierdo de '" + operator + "' debe ser numérico, encontrado: " + leftType + " en línea " + line);
        return null;
    }
    
    if (!isNumericType(processedRight)) {
        addError("Operando derecho de '" + operator + "' debe ser numérico, encontrado: " + rightType + " en línea " + line);
        return null;
    }
    
    // ✅ DETERMINAR TIPO RESULTANTE
    if (processedLeft.equals("FLOAT") || processedRight.equals("FLOAT")) {
        return "FLOAT";
    } else {
        return "INT";
    }
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
    // ✅ PROCESAR TIPOS
    String processedLeft = processExpressionType(leftType);
    String processedRight = processExpressionType(rightType);
    
    // ✅ OPERADORES DE IGUALDAD (== y !=) son más flexibles
    if (operator.equals("==") || operator.equals("!=")) {
        // Pueden comparar cualquier tipo compatible
        if (areComparableTypes(processedLeft, processedRight)) {
            return "BOOL";
        } else {
            addError("Tipos no comparables en operación '" + operator + "': " + leftType + " y " + rightType + " en línea " + line);
            return null;
        }
    }
    
    // ✅ OPERADORES DE ORDEN (<, >, <=, >=) requieren tipos numéricos o char
    if (!isOrderComparableType(processedLeft) || !isOrderComparableType(processedRight)) {
        addError("Operación relacional '" + operator + "' requiere operandos numéricos o char en línea " + line);
        return null;
    }
    
    // ✅ VERIFICAR COMPATIBILIDAD
    if (!areCompatibleForComparison(processedLeft, processedRight)) {
        addError("Tipos incompatibles en comparación '" + operator + "': " + leftType + " y " + rightType + " en línea " + line);
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
    String processedLeft = processExpressionType(leftType);
    
    // ✅ VERIFICAR OPERANDO IZQUIERDO
    if (!processedLeft.equals("BOOL")) {
        addError("Operación lógica '" + operator + "' requiere operando booleano, encontrado: " + leftType + " en línea " + line);
        return null;
    }
    
    // ✅ VERIFICAR OPERANDO DERECHO (si existe - para operaciones binarias)
    if (rightType != null) {
        String processedRight = processExpressionType(rightType);
        if (!processedRight.equals("BOOL")) {
            addError("Operación lógica '" + operator + "' requiere ambos operandos booleanos, encontrado: " + rightType + " en línea " + line);
            return null;
        }
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
        SymbolInfo array = getCurrentScope().lookup(arrayName);  // ✅ Usar lookup en lugar de checkVariableUsage
        
        if (array == null) {
            addError("Array '" + arrayName + "' no declarado en línea " + line);
            return null;
        }
        
        if (!array.getTipoVariable().contains("[]")) {
            addError("'" + arrayName + "' no es un array en línea " + line);
            return null;
        }
        
        // ✅ NO VERIFICAR INICIALIZACIÓN para arrays - se consideran válidos por declaración
        
        if (!index1.equals("INT")) {
            addError("Índice de array debe ser entero en línea " + line);
            return null;
        }
        
        if (index2 != null && !index2.equals("INT")) {
            addError("Segundo índice de array debe ser entero en línea " + line);
            return null;
        }
        
        // ✅ MARCAR COMO USADO
        array.setUtilizada(true);
        
        // ✅ RETORNAR TIPO BASE (sin [])
        String baseType = array.getTipoVariable().replace("[][]", "");
        return baseType;
    }
    
    
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
    
    public String getExpressionType(String expr) {
        if (expr == null) return null;
        
        if (expr.equals("INT") || expr.equals("FLOAT") || expr.equals("BOOL") || 
            expr.equals("CHAR") || expr.equals("STRING")) {
            return expr;
        }
        
        if (expr.equals("NUMERIC")) {
            return "FLOAT"; // Por defecto, las operaciones numéricas son FLOAT
        }
        
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
        
        if (expr.equals("FUNCTION_CALL") || expr.equals("ERROR")) {
            return expr;
        }
        
        if (expr.contains("VARIABLE")) {
            return "VARIABLE"; 
        }
        
        System.out.println("DEBUG: Expresión '" + expr + "' no reconocida, retornando UNKNOWN");
        return "UNKNOWN";
    }

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
        
        String actualReturnType = getExpressionType(returnType);
        
        System.out.println("DEBUG Return: función=" + functionName + 
                          ", esperado=" + expectedReturnType + 
                          ", actual=" + returnType + 
                          ", procesado=" + actualReturnType);
        
        if (expectedReturnType.equals("VOID")) {
            if (actualReturnType != null && !actualReturnType.equals("UNKNOWN")) {
                addError("Función '" + functionName + "' de tipo VOID no puede retornar un valor en línea " + line);
            }
        } else {
            if (actualReturnType == null || actualReturnType.equals("UNKNOWN")) {
                addError("Función '" + functionName + "' de tipo '" + expectedReturnType + "' debe retornar un valor en línea " + line);
            } else {
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
    
    
    public void escribirTablas(String archivo) throws IOException {
        PrintWriter writer = new PrintWriter(new FileWriter(archivo));
        
        writer.println("===== ANÁLISIS SEMÁNTICO =====");
        writer.println("Alcances procesados total: " + currentLevel); // Total creados
        writer.println("Alcances activos: " + scopeStack.size()); // Solo los activos
        writer.println("Errores semánticos: " + errors.size());
        writer.println("Advertencias: " + warnings.size());
        writer.println();
        
        if (!errors.isEmpty()) {
            writer.println("===== ERRORES SEMÁNTICOS =====");
            for (String error : errors) {
                writer.println(error);
            }
            writer.println();
        }
        
        if (!warnings.isEmpty()) {
            writer.println("===== ADVERTENCIAS =====");
            for (String warning : warnings) {
                writer.println(warning);
            }
            writer.println();
        }
        
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
// =================== MÉTODOS AUXILIARES ===================
/**
 * Procesa tipos de expresiones para obtener el tipo real
 */
private String processExpressionType(String type) {
    if (type == null) return "UNKNOWN";
    
    // ✅ TIPOS DIRECTOS
    if (type.equals("INT") || type.equals("FLOAT") || type.equals("BOOL") || 
        type.equals("CHAR") || type.equals("STRING")) {
        return type;
    }
    
    // ✅ TIPOS ESPECIALES
    if (type.equals("NUMERIC")) {
        return "FLOAT"; // Por defecto
    }
    
    if (type.equals("VARIABLE")) {
        return "UNKNOWN"; // Necesitaría más contexto
    }
    
    // ✅ LITERALES ESPECÍFICOS
    if (type.matches("\\d+")) { // Es un número entero literal
        return "INT";
    }
    
    if (type.matches("\\d+\\.\\d*")) { // Es un número decimal literal
        return "FLOAT";
    }
    
    // ✅ VARIABLES - buscar en tabla
    SymbolInfo symbol = getCurrentScope().lookup(type);
    if (symbol != null && symbol.esVariable()) {
        return symbol.getTipoVariable();
    }
    
    return type; // Devolver tal como está si no se puede procesar
}

/**
 * Verifica si un tipo es numérico
 */
private boolean isNumericType(String type) {
    return type != null && (type.equals("INT") || type.equals("FLOAT"));
}

/**
 * Verifica si un tipo puede usarse en comparaciones de orden
 */
private boolean isOrderComparableType(String type) {
    return type != null && (type.equals("INT") || type.equals("FLOAT") || type.equals("CHAR"));
}

/**
 * Verifica si dos tipos son comparables entre sí
 */
private boolean areComparableTypes(String type1, String type2) {
    if (type1 == null || type2 == null) return false;
    
    // ✅ TIPOS IGUALES
    if (type1.equals(type2)) return true;
    
    // ✅ TIPOS NUMÉRICOS ENTRE SÍ
    if (isNumericType(type1) && isNumericType(type2)) return true;
    
    // ✅ OTROS CASOS ESPECÍFICOS
    return false;
}

/**
 * Verifica compatibilidad para comparaciones
 */
private boolean areCompatibleForComparison(String type1, String type2) {
    return areComparableTypes(type1, type2);
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

    public boolean declareParameter(String paramName, String paramType, int line) {
    Scope currentScope = getCurrentScope();
    if (currentScope == null) {
        addError("Error interno: no hay alcance activo para declarar parámetro '" + paramName + "' en línea " + line);
        return false;
    }
    
    // ✅ VERIFICAR QUE ESTEMOS EN UNA FUNCIÓN
    if (!currentScope.getTipoAlcance().equals("FUNCTION")) {
        addError("Parámetro '" + paramName + "' solo puede declararse dentro de una función en línea " + line);
        return false;
    }
    
    // ✅ VERIFICAR QUE NO EXISTA YA
    if (currentScope.existsLocal(paramName)) {
        addError("Parámetro '" + paramName + "' ya declarado en esta función en línea " + line);
        return false;
    }
    
    // ✅ CREAR SÍMBOLO DEL PARÁMETRO
    SymbolInfo symbol = new SymbolInfo(paramName, "ID", line, 0, currentScope.getNivel());
    symbol.setTipoVariable(paramType);
    symbol.setEsVariable(true);
    symbol.setInicializada(true); // ✅ Los parámetros están inicializados por definición
    
    // ✅ DECLARAR EN EL SCOPE ACTUAL
    currentScope.declare(paramName, symbol);
    
    // ✅ MANTENER COMPATIBILIDAD CON TABLA ORIGINAL
    originalTable.insertarSimbolo(paramName, "ID", line, 0, paramName);
    originalTable.actualizarTipoVariable(paramName, paramType);
    
    System.out.println("DEBUG: Parámetro '" + paramName + "' declarado como " + paramType + " en función");
    return true;
}
}
    