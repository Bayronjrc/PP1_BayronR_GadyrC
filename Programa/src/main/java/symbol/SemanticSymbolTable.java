package main.java.symbol;

import java.util.*;
import java.io.*;

public class SemanticSymbolTable {
    private Stack<Scope> scopeStack;
    private int currentLevel;
    private List<String> errors;
    private List<String> warnings;
    private SymbolTable originalTable;
    private int scopeCounter;
    
    public SemanticSymbolTable() {
        scopeStack = new Stack<>();
        currentLevel = 0;
        errors = new ArrayList<>();
        warnings = new ArrayList<>();
        originalTable = new SymbolTable();
        scopeCounter = 0;
        
        enterScope("GLOBAL", "global");
    }
    
    public void enterScope(String tipoAlcance, String nombreAlcance) {
        Scope parent = scopeStack.isEmpty() ? null : scopeStack.peek();
        Scope newScope = new Scope(currentLevel++, parent, tipoAlcance, nombreAlcance);
        scopeStack.push(newScope);
    }
    
    public void enterScope() {
        enterScope("BLOCK", "block_" + (++scopeCounter));
    }
    
    public void exitScope() {
        if (!scopeStack.isEmpty()) {
            Scope currentScope = scopeStack.pop();
            
            for (SymbolInfo symbol : currentScope.getVariablesNoUtilizadas()) {
                addWarning("Variable '" + symbol.getLexema() + "' declarada pero no utilizada en linea " + symbol.getLinea());
            }
        }
    }
    
    public Scope getCurrentScope() {
        return scopeStack.isEmpty() ? null : scopeStack.peek();
    }
    
    public Scope getGlobalScope() {
        return scopeStack.isEmpty() ? null : scopeStack.firstElement();
    }
    
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
        
        SymbolInfo symbol = new SymbolInfo(name, "ID", line, column, 
                                           currentScope.getNivel(),
                                           currentScope.getNombreAlcance(),
                                           currentScope.getTipoAlcance());
        symbol.setTipoVariable(type);
        symbol.setEsVariable(true);
        symbol.setInicializada(inicializada);
        
        currentScope.declare(name, symbol);
        
        originalTable.insertarSimbolo(name, "ID", line, column, name);
        originalTable.actualizarTipoVariable(name, type);
        
        return true;
    }
    
    public boolean declareVariable(String name, String type, int line, int column) {
        return declareVariable(name, type, line, column, false);
    }
    
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
        
        SymbolInfo symbol = new SymbolInfo(name, "ID", line, column, 0, "global", "GLOBAL");
        symbol.setTipoVariable(returnType);
        symbol.setEsFuncion(true);
        if (paramTypes != null) {
            for (String paramType : paramTypes) {
                symbol.addParametro(paramType);
            }
        }
        
        globalScope.declare(name, symbol);
        originalTable.marcarComoFuncion(name, returnType);
        
        return true;
    }
    
    public boolean declareArray(String name, String type, int dim1, int dim2, int line, int column) {
        boolean result = declareVariable(name, type, line, column, true);
        if (result) {
            SymbolInfo symbol = getCurrentScope().getLocal(name);
            symbol.addDimension(dim1);
            symbol.addDimension(dim2);
        }
        return result;
    }
    
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
    
    public boolean checkTypeCompatibility(String expected, String actual, int line) {
        if (expected == null || actual == null) {
            addError("Tipo nulo en verificacion de compatibilidad en linea " + line);
            return false;
        }
        
        if (expected.equals(actual)) {
            return true;
        }
        
        SymbolInfo tempSymbol = new SymbolInfo("temp", "ID", line, 0);
        tempSymbol.setTipoVariable(expected);
        
        if (!tempSymbol.esCompatibleCon(actual)) {
            addError("Tipos incompatibles: esperado '" + expected + "', encontrado '" + actual + "' en linea " + line);
            return false;
        }
        
        return true;
    }
    
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
                        "' tiene tipo incorrecto en linea " + line);
                return null;
            }
        }
        
        function.setUtilizada(true);
        return function.getTipoVariable();
    }
    
    public String checkArithmeticOperation(String leftType, String rightType, String operator, int line) {
        SymbolInfo leftSymbol = new SymbolInfo("temp_left", "ID", line, 0);
        leftSymbol.setTipoVariable(leftType);
        
        SymbolInfo rightSymbol = new SymbolInfo("temp_right", "ID", line, 0);
        rightSymbol.setTipoVariable(rightType);
        
        if (!leftSymbol.esNumerico() || !rightSymbol.esNumerico()) {
            addError("Operacion aritmetica '" + operator + "' requiere operandos numericos en linea " + line);
            return null;
        }
        
        String resultType = leftSymbol.tipoResultanteConOperacion(rightType);
        if (resultType == null) {
            addError("Operacion aritmetica invalida entre '" + leftType + "' y '" + rightType + "' en linea " + line);
        }
        
        return resultType;
    }
    
    public String checkRelationalOperation(String leftType, String rightType, String operator, int line) {
        if (operator.equals("==") || operator.equals("!=")) {
            if (leftType.equals("BOOL") && rightType.equals("BOOL")) {
                return "BOOL";
            }
        }
        
        SymbolInfo leftSymbol = new SymbolInfo("temp_left", "ID", line, 0);
        leftSymbol.setTipoVariable(leftType);
        
        SymbolInfo rightSymbol = new SymbolInfo("temp_right", "ID", line, 0);
        rightSymbol.setTipoVariable(rightType);
        
        if (!leftSymbol.esComparable() || !rightSymbol.esComparable()) {
            addError("Operacion relacional '" + operator + "' requiere operandos comparables en linea " + line);
            return null;
        }
        
        if (!checkTypeCompatibility(leftType, rightType, line)) {
            return null;
        }
        
        return "BOOL";
    }
    
    public String checkLogicalOperation(String leftType, String rightType, String operator, int line) {
        if (!leftType.equals("BOOL")) {
            addError("Operacion logica '" + operator + "' requiere operando booleano en linea " + line);
            return null;
        }
        
        if (rightType != null && !rightType.equals("BOOL")) {
            addError("Operacion logica '" + operator + "' requiere ambos operandos booleanos en linea " + line);
            return null;
        }
        
        return "BOOL";
    }
    
    public String checkArrayAccess(String arrayName, String index1, String index2, int line) {
        SymbolInfo array = checkVariableUsage(arrayName, line);
        if (array == null) {
            return null;
        }
        
        if (!array.esArray()) {
            addError("'" + arrayName + "' no es un array en linea " + line);
            return null;
        }
        
        if (!index1.equals("INT")) {
            addError("Indice de array debe ser entero en linea " + line);
            return null;
        }
        
        if (index2 != null && !index2.equals("INT")) {
            addError("Segundo indice de array debe ser entero en linea " + line);
            return null;
        }
        
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
    
    public boolean checkMainFunction() {
        Scope globalScope = getGlobalScope();
        if (globalScope == null) {
            addError("No hay alcance global definido");
            return false;
        }
        
        SymbolInfo main = globalScope.getLocal("main");
        if (main == null || !main.esFuncion()) {
            addError("Funcion 'main' no encontrada");
            return false;
        }
        
        if (!main.getTipoVariable().equals("VOID")) {
            addError("Funcion 'main' debe ser de tipo 'void'");
            return false;
        }
        
        if (!main.getParametros().isEmpty()) {
            addError("Funcion 'main' no debe tener parametros");
            return false;
        }
        
        return true;
    }
    
    public void checkFunctionReturn(String functionName, boolean hasReturn, int line) {
        Scope globalScope = getGlobalScope();
        if (globalScope != null) {
            SymbolInfo function = globalScope.getLocal(functionName);
            if (function != null && function.esFuncion()) {
                String returnType = function.getTipoVariable();
                if (!returnType.equals("VOID") && !hasReturn) {
                    addError("Funcion '" + functionName + "' debe retornar un valor de tipo '" + returnType + "' en linea " + line);
                }
            }
        }
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
    
    public SymbolTable getOriginalTable() {
        return originalTable;
    }
    
    public void escribirTablas(String archivo) throws IOException {
        PrintWriter writer = new PrintWriter(new FileWriter(archivo));
        
        writer.println("===== ANALISIS SEMANTICO =====");
        writer.println("Alcances encontrados: " + scopeStack.size());
        writer.println("Errores semanticos: " + errors.size());
        writer.println("Advertencias: " + warnings.size());
        writer.println();
        
        if (!errors.isEmpty()) {
            writer.println("===== ERRORES SEMANTICOS =====");
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
        
        for (int i = 0; i < scopeStack.size(); i++) {
            Scope scope = scopeStack.get(i);
            writer.println("===== ALCANCE: " + scope.toString() + " =====");
            Map<String, SymbolInfo> symbols = scope.getAllLocalSymbols();
            for (SymbolInfo symbol : symbols.values()) {
                writer.println(symbol.toString());
            }
            writer.println();
        }
        
        writer.close();
    }
    
    public Map<String, Integer> getStatistics() {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("alcances", scopeStack.size());
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
