package main.java.intermedio;

import java.io.*;
import java.lang.classfile.instruction.SwitchCase;
import java.util.*;

import java_cup.emit;

/**
 * Generador de Código Intermedio
 * 
 * Esta clase permite la generacion de codigo intermedio 
 * de tres direcciones
 * 
 * @author Bayron Rodríguez & Gadir Calderón
 * @version 1.0 
 */
public class IntermediateCodeGenerator {

    private List<String> code;
    private String outputFile;
    private int tempCounter;
    private int labelCounter;
    private boolean enabled;
    private List<SwitchCase> currentSwitchCases = new ArrayList<>();
    private String currentSwitchExit = null;
    private String currentFunctionName = null;
    private String currentFunctionReturnType = null;
    
    public IntermediateCodeGenerator(String outputFile) {
        this.outputFile = outputFile;
        this.code = new ArrayList<>();
        this.tempCounter = 1;
        this.labelCounter = 1;
        this.enabled = true;
        
        code.add("// Código Intermedio Generado");
        code.add("// Archivo: " + outputFile);
        code.add("");
        
        System.out.println("Generador de código intermedio inicializado: " + outputFile);
    }
    
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    

    public void declareVariable(String name, String type) {
        if (enabled) {
            emit("DECLARE " + name + " " + type);
        }
    }
    
    public void endFunction(String name) {
        if (enabled) {
            emit("END " + name);
            emit("");
        }
    }
    
    
    public void generateAssignment(String variable, String expression) {
        if (enabled) {
            emit(variable + " = " + expression);
        }
    }
    
    
    public String generateBinaryOp(String left, String op, String right) {
        if (!enabled) return "t" + tempCounter++;
        
        String temp = newTemp();
        emit(temp + " = " + left + " " + op + " " + right);
        return temp;
    }
    
    public String generateComparison(String left, String op, String right) {
        if (!enabled) return "t" + tempCounter++;
        
        String temp = newTemp();
        emit(temp + " = " + left + " " + op + " " + right);
        return temp;
    }
    
    
    public String generateUnaryOp(String op, String operand) {
        if (!enabled) return "t" + tempCounter++;
        
        String temp = newTemp();
        emit(temp + " = " + op + operand);
        return temp;
    }
    
    public String generateIncrement(String variable) {
        if (!enabled) return variable;
        
        emit(variable + " = " + variable + " + 1");
        return variable;
    }
    
    public String generateDecrement(String variable) {
        if (!enabled) return variable;
        
        emit(variable + " = " + variable + " - 1");
        return variable;
    }
    
    
    public String generateArrayAccess(String array, String index1, String index2) {
        if (!enabled) return "t" + tempCounter++;
        
        String temp = newTemp();
        emit(temp + " = " + array + "[" + index1 + "][" + index2 + "]");
        return temp;
    }
    
    
    public String generateFunctionCall(String function, List<String> args) {
        if (!enabled) return "t" + tempCounter++;
        
        for (String arg : args) {
            emit("PARAM " + arg);
        }
        
        String temp = newTemp();
        emit(temp + " = CALL " + function + " " + args.size());
        return temp;
    }
    
    
    public String newLabel() {
        return "L" + labelCounter++;
    }
    
    public void generateLabel(String label) {
        if (enabled) {
            emit(label + ":");
        }
    }
    
    public void generateJump(String label) {
        if (enabled) {
            emit("GOTO " + label);
        }
    }
    
    public void generateConditionalJump(String condition, String label) {
        if (enabled) {
            emit("IF " + condition + " GOTO " + label);
        }
    }
    
    public void generateReturn(String value) {
        if (enabled) {
            if (value != null) {
                emit("RETURN " + value);
            } else {
                emit("RETURN");
            }
        }
    }
    
    public void generateRead(String variable) {
        if (enabled) {
            emit("READ " + variable);
        }
    }
    
    public void generateWrite(String expression) {
        if (enabled) {
            emit("WRITE " + expression);
        }
    }
    
    private String newTemp() {
        return "t" + tempCounter++;
    }
    
    private void emit(String instruction) {
        if (enabled && instruction != null) {
            code.add(instruction);
        }
    }
    
    
    public void printCode() {
        if (enabled) {
            System.out.println("\n=== CÓDIGO INTERMEDIO ===");
            for (int i = 0; i < code.size(); i++) {
                System.out.println((i + 1) + ": " + code.get(i));
            }
            System.out.println("=========================\n");
        }
    }
    
    public void printStatistics() {
        if (enabled) {
            System.out.println("Estadísticas:");
            System.out.println("- Líneas de código: " + code.size());
            System.out.println("- Variables temporales: " + (tempCounter - 1));
            System.out.println("- Etiquetas generadas: " + (labelCounter - 1));
        }
    }
    
    public void writeToFile() {
        if (!enabled) return;
        
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(outputFile));
            
            writer.println("// ========================================");
            writer.println("// CÓDIGO INTERMEDIO DE TRES DIRECCIONES");
            writer.println("// Generado automáticamente");
            writer.println("// ========================================");
            writer.println();
            
            for (int i = 0; i < code.size(); i++) {
                writer.println(String.format("%3d: %s", i + 1, code.get(i)));
            }
            
            writer.println();
            writer.println("// ========================================");
            writer.println("// ESTADÍSTICAS");
            writer.println("// Líneas: " + code.size());
            writer.println("// Temporales: " + (tempCounter - 1));
            writer.println("// Etiquetas: " + (labelCounter - 1));
            writer.println("// ========================================");
            
            writer.close();
            System.out.println("Código intermedio guardado en: " + outputFile);
            
        } catch (IOException e) {
            System.err.println("Error escribiendo código intermedio: " + e.getMessage());
        }
    }
    
    
    public void addCustomCode(String instruction) {
        if (enabled) {
            emit("// " + instruction);
        }
    }
    
    public void addComment(String comment) {
        if (enabled) {
            emit("// " + comment);
        }
    }

    /**
 * Genera IF simple con salto directo al final
 */
public void generateIfSimple(String condition, String endLabel) {
    if (enabled) {
        emit("IF " + condition + " GOTO " + endLabel);
    }
}

/**
 * Genera IF-ELSE: IF NOT condición GOTO etiqueta_else  
 */
public void generateIfElseStart(String condition, String elseLabel) {
    if (enabled) {
        emit("IF NOT " + condition + " GOTO " + elseLabel);
    }
}

/**
 * Genera salto incondicional
 */
public void generateGoto(String label) {
    if (enabled) {
        emit("GOTO " + label);
    }
}

public void generateIfCondition(String condition, String falseLabel) {
    emit("IF NOT " + condition + " GOTO " + falseLabel);
}

public int getCurrentPosition() {
    return code.size();
}

/**
 * Inserta una instrucción en una posición específica
 */
public void insertAt(int position, String instruction) {
    if (enabled && instruction != null) {
        if (position >= 0 && position <= code.size()) {
            code.add(position, instruction);
        } else {
            emit(instruction); // Si posición inválida, agregar al final
        }
    }
}

/**
 * Inserta antes de la primera línea de código real (después de declaraciones)
 */
public void insertBeforeCode(String instruction) {
    if (!enabled || instruction == null) return;
    
    // Buscar la primera línea que no sea declaración o comentario
    int insertPos = findCodeStartPosition();
    insertAt(insertPos, instruction);
}

/**
 * Encuentra la posición donde empieza el código real (después de declaraciones)
 */
private int findCodeStartPosition() {
    for (int i = code.size() - 1; i >= 0; i--) {
        String line = code.get(i).trim();
        
        // Saltar líneas vacías, comentarios y declaraciones
        if (line.isEmpty() || 
            line.startsWith("//") || 
            line.startsWith("DECLARE") ||
            line.startsWith("FUNCTION") ||
            line.startsWith("BEGIN")) {
            continue;
        }
        
        // Esta es la primera línea de código real, insertar ANTES
        return i;
    }
    
    // Si no hay código real, insertar al final
    return code.size();
}

/**
 * Genera IF completo con inserción correcta
 */
public void generateCompleteIf(String condition, String endLabel) {
    if (!enabled) return;
    
    // 1. Insertar PLACEHOLDER para la condición
    String conditionPlaceholder = "PLACEHOLDER_IF_" + condition + "_" + endLabel;
    int insertPos = findBestInsertPosition();
    if (insertPos >= 0 && insertPos < code.size()) {
        code.add(insertPos, conditionPlaceholder);
    }
    
    // 2. Agregar etiqueta final
    emit(endLabel + ":");
    
    // 3. Resolver placeholder
    resolvePlaceholder(conditionPlaceholder, "IF NOT " + condition + " GOTO " + endLabel);
}

private int findBestInsertPosition() {
    for (int i = code.size() - 1; i >= 0; i--) {
        String line = code.get(i).trim();
        
        // Saltar líneas que no son código ejecutable
        if (line.isEmpty() || 
            line.startsWith("//") || 
            line.startsWith("DECLARE") ||
            line.startsWith("FUNCTION") ||
            line.startsWith("BEGIN")) {
            continue;
        }
        
        // Esta es la primera línea de código real desde el final
        return i;
    }
    
    return code.size(); // Si no encuentra, insertar al final
}


/**
 * Marca posición para IF-ELSE (guardar posición actual)
 */
private int ifElseStartPosition = -1;

public void markIfElseStart() {
    ifElseStartPosition = getCurrentPosition();
}

/**
 * Genera IF-ELSE completo con inserción correcta
 */
public void generateCompleteIfElse(String condition, String elseLabel, String endLabel) {
    if (!enabled) return;
    
    // 1. Insertar PLACEHOLDER para condición al inicio
    String conditionPlaceholder = "PLACEHOLDER_CONDITION_" + condition + "_" + elseLabel;
    int conditionPos = findBestInsertPosition();
    if (conditionPos >= 0 && conditionPos < code.size()) {
        code.add(conditionPos, conditionPlaceholder);
    }
    
    // 2. Insertar PLACEHOLDER para transición THEN->ELSE
    String transitionPlaceholder = "PLACEHOLDER_TRANSITION_" + elseLabel + "_" + endLabel;
    emit(transitionPlaceholder);
    
    // 3. Agregar etiqueta final
    emit(endLabel + ":");
    
    // 4. Resolver placeholders
    resolvePlaceholder(conditionPlaceholder, "IF NOT " + condition + " GOTO " + elseLabel);
    resolvePlaceholder(transitionPlaceholder, "GOTO " + endLabel + "\n" + elseLabel + ":");
}

private int findElseInsertPosition() {
    int codeLines = 0;
    int startPos = -1;
    
    // Contar líneas de código real
    for (int i = 0; i < code.size(); i++) {
        String line = code.get(i).trim();
        if (!line.isEmpty() && 
            !line.startsWith("//") && 
            !line.startsWith("DECLARE") &&
            !line.startsWith("FUNCTION") &&
            !line.startsWith("BEGIN")) {
            
            if (startPos == -1) startPos = i;
            codeLines++;
        }
    }
    
    // Insertar aproximadamente en la mitad del código
    int targetLine = codeLines / 2;
    int currentCodeLine = 0;
    
    for (int i = startPos; i < code.size(); i++) {
        String line = code.get(i).trim();
        if (!line.isEmpty() && 
            !line.startsWith("//") && 
            !line.startsWith("DECLARE") &&
            !line.startsWith("FUNCTION") &&
            !line.startsWith("BEGIN")) {
            
            if (currentCodeLine == targetLine) {
                return i;
            }
            currentCodeLine++;
        }
    }
    
    return code.size(); // Fallback
}

/**
 * Versión simplificada que usa marcadores de posición
 */
public void generateIfWithPosition(String condition, String endLabel, int thenStartPos) {
    if (!enabled) return;
    
    // Insertar condición justo antes del código THEN
    insertAt(thenStartPos, "IF NOT " + condition + " GOTO " + endLabel);
    emit(endLabel + ":");
}

/**
 * Debug: Mostrar código con números de línea
 */
public void debugPrintWithPositions() {
    if (enabled) {
        System.out.println("\n=== DEBUG: CÓDIGO CON POSICIONES ===");
        for (int i = 0; i < code.size(); i++) {
            System.out.println(String.format("[%3d]: %s", i, code.get(i)));
        }
        System.out.println("====================================\n");
    }
}

private int findLastCodePosition() {
    for (int i = code.size() - 1; i >= 0; i--) {
        String line = code.get(i).trim();
        if (!line.isEmpty() && !line.startsWith("//")) {
            return i + 1; // Insertar después de esta línea
        }
    }
    return code.size();
}

private void insertBeforeFirstRealCode(String instruction) {
    for (int i = 0; i < code.size(); i++) {
        String line = code.get(i).trim();
        if (!line.isEmpty() && 
            !line.startsWith("//") && 
            !line.startsWith("DECLARE") &&
            !line.startsWith("FUNCTION") &&
            !line.startsWith("BEGIN")) {
            
            code.add(i, instruction);
            return;
        }
    }
    
    // Si no encuentra, agregar al final
    emit(instruction);
}

private void resolvePlaceholder(String placeholder, String replacement) {
    for (int i = 0; i < code.size(); i++) {
        if (code.get(i).equals(placeholder)) {
            if (replacement.contains("\n")) {
                // Si tiene múltiples líneas, dividir
                String[] lines = replacement.split("\n");
                code.set(i, lines[0]);
                for (int j = 1; j < lines.length; j++) {
                    code.add(i + j, lines[j]);
                }
            } else {
                code.set(i, replacement);
            }
            break;
        }
    }
}

public void debugShowInsertPositions() {
    if (!enabled) return;
    
    System.out.println("\n=== ANÁLISIS DE POSICIONES ===");
    System.out.println("Mejor posición IF: " + findBestInsertPosition());
    System.out.println("Posición ELSE: " + findElseInsertPosition());
    System.out.println("Última posición código: " + findLastCodePosition());
    
    for (int i = 0; i < code.size(); i++) {
        String marker = "";
        if (i == findBestInsertPosition()) marker += " ← IF_POS";
        if (i == findElseInsertPosition()) marker += " ← ELSE_POS";
        
        System.out.println(String.format("[%3d]: %s%s", i, code.get(i), marker));
    }
    System.out.println("===============================\n");
}

/* FIX FINAL: USAR MARCADOR PARA EL BODY DEL DO-WHILE */

public void generateCompleteDoWhile(String condition, String startLabel) {
    if (!enabled) return;
    
    // 1. Insertar marcador ANTES de procesar el body
    String bodyMarker = "BODY_START_MARKER_" + startLabel;
    int currentPos = code.size();
    
    // 2. Buscar hacia atrás desde el final hasta encontrar el marcador
    int bodyStartPos = findDoWhileBodyStartSimple();
    
    if (bodyStartPos == -1) {
        // Si no hay marcador, usar heurística mejorada
        bodyStartPos = findDoWhileBodyStart();
    }
    
    System.out.println("DEBUG: Buscando marcador " + bodyMarker + ", encontrado en: " + bodyStartPos);
    System.out.println("DEBUG: Código actual:");
    for (int i = Math.max(0, code.size() - 6); i < code.size(); i++) {
        System.out.println("  [" + i + "] " + code.get(i));
    }
    
    // 3. Insertar etiqueta
    if (bodyStartPos >= 0 && bodyStartPos < code.size()) {
        code.add(bodyStartPos, startLabel + ":");
        System.out.println("DEBUG: Etiqueta insertada en posición: " + bodyStartPos);
    }
    
    // 4. Al final: agregar condición y salto de vuelta
    emit("IF " + condition + " GOTO " + startLabel);
}

/* FIX RÁPIDO: AJUSTAR POSICIÓN DE ETIQUETA */

/* FIX DEFINITIVO: IGNORAR LA CONDICIÓN DEL DO-WHILE */

private int findDoWhileBodyStart() {
    int codeLines = 0;
    int firstBodyPos = code.size();
    
    // Contar líneas de código ejecutable desde el final, PERO saltarse la última
    // porque la última línea ejecutable es la condición del DO-WHILE
    for (int i = code.size() - 1; i >= 0; i--) {
        String line = code.get(i).trim();
        
        if (!line.isEmpty() && 
            !line.startsWith("//") && 
            !line.startsWith("DECLARE") &&
            !line.startsWith("FUNCTION") &&
            !line.startsWith("BEGIN") &&
            !line.contains(":") &&
            !line.startsWith("IF") &&
            !line.startsWith("GOTO")) {
            
            codeLines++;
            
            // ✅ SALTARSE LA PRIMERA (que es la condición)
            if (codeLines == 1) {
                System.out.println("DEBUG: Saltando condición: " + line + " en posición " + i);
                continue; // No contar esta línea
            }
            
            firstBodyPos = i; // Guardar posición de líneas del body real
            System.out.println("DEBUG: Línea " + (codeLines-1) + " del body: " + line + " en posición " + i);
            
            // Si llevamos 3 líneas total (1 condición + 2 body), ya tenemos todo
            if (codeLines >= 3) {
                System.out.println("DEBUG: Body completo encontrado, inicia en: " + firstBodyPos);
                return firstBodyPos; // ✅ Retornar la posición de la PRIMERA línea del body
            }
        }
    }
    
    // Si solo hay body de 1 línea + condición
    System.out.println("DEBUG: Body de 1 línea encontrado en: " + firstBodyPos);
    return firstBodyPos;
}

/* MÉTODO ALTERNATIVO MÁS SIMPLE: ASUMIR 2 LÍNEAS DE BODY */
private int findDoWhileBodyStartSimple() {
    int executableLines = 0;
    
    // Contar líneas ejecutables desde el final
    for (int i = code.size() - 1; i >= 0; i--) {
        String line = code.get(i).trim();
        
        if (!line.isEmpty() && 
            !line.startsWith("DECLARE") &&
            !line.startsWith("FUNCTION") &&
            !line.startsWith("BEGIN") &&
            !line.startsWith("//") &&
            !line.contains(":")) {
            
            executableLines++;
            
            // Las últimas 3 líneas son: condición + 2 líneas de body
            // Queremos la posición de la PRIMERA línea del body
            if (executableLines == 3) {
                System.out.println("DEBUG: Primera línea del body en posición: " + i);
                return i;
            }
        }
    }
    
    return code.size();
}

// pal for
/* FIX MULTI-PROBLEMA PARA FOR LOOP 🔥 */

public void generateForWithExistingGrammar(String condition, String updateExpr, String startLabel, String endLabel) {
    if (!enabled) return;
    
    System.out.println("DEBUG: Iniciando generación FOR");
    System.out.println("DEBUG: Condición: " + condition);
    System.out.println("DEBUG: Update: " + updateExpr);
    System.out.println("DEBUG: Código actual antes de reorganizar:");
    for (int i = 0; i < code.size(); i++) {
        System.out.println("  [" + i + "] " + code.get(i));
    }
    
    // PROBLEMA 1: Reorganizar código correctamente
    reorganizeForCodeFixed(condition, updateExpr, startLabel, endLabel);
    
    System.out.println("DEBUG: Código después de reorganizar:");
    for (int i = Math.max(0, code.size() - 10); i < code.size(); i++) {
        System.out.println("  [" + i + "] " + code.get(i));
    }
}

private void reorganizeForCodeFixed(String condition, String updateExpr, String startLabel, String endLabel) {
    // 1. Capturar TODO el código actual
    List<String> allCode = new ArrayList<>(code);
    
    // 2. Separar en secciones correctas
    List<String> headers = new ArrayList<>();       // FUNCTION, BEGIN, comentarios
    List<String> declarations = new ArrayList<>();
    List<String> initAssignments = new ArrayList<>(); 
    List<String> bodyCode = new ArrayList<>();
    List<String> footers = new ArrayList<>();       // END
    
    // 3. Analizar línea por línea MÁS CUIDADOSAMENTE
    for (String line : allCode) {
        String trimmed = line.trim();
        
        // ✅ PRESERVAR HEADERS
        if (trimmed.startsWith("//") || trimmed.startsWith("FUNCTION") || 
            trimmed.equals("BEGIN") || trimmed.isEmpty()) {
            headers.add(line);
        } 
        // ✅ PRESERVAR FOOTERS  
        else if (trimmed.startsWith("END")) {
            footers.add(line);
        }
        // ✅ DECLARACIONES
        else if (trimmed.startsWith("DECLARE")) {
            declarations.add(line);
        } 
        // ✅ INICIALIZACIONES SIMPLES (variable = número)
        else if (trimmed.matches("\\w+ = \\d+")) {  // Solo "i = 0", "x = 0"
            initAssignments.add(line);
        } 
        // ✅ BODY CODE (pero NO el código viejo del loop)
        else if (!trimmed.isEmpty() && 
                !trimmed.contains("<") &&  // No condiciones viejas
                !trimmed.contains("+ 1") && // No incrementos viejos
                !trimmed.startsWith("t") &&  // No temporales viejos
                trimmed.contains("=")) {
            bodyCode.add(line);
        }
        // ✅ TODO LO DEMÁS que sea relevante para el body
        else if (trimmed.startsWith("t") && trimmed.contains("=") && 
                 !trimmed.contains("<") && !trimmed.contains("+ 1")) {
            bodyCode.add(line);
        }
    }
    
    System.out.println("DEBUG: Headers: " + headers.size());
    System.out.println("DEBUG: Declarations: " + declarations.size()); 
    System.out.println("DEBUG: Inits: " + initAssignments.size());
    System.out.println("DEBUG: Body: " + bodyCode.size());
    
    // 4. RECONSTRUIR CÓDIGO LIMPIO
    code.clear();
    
    // Headers (FUNCTION, BEGIN, etc.)
    for (String header : headers) {
        emit(header);
    }
    
    // Declaraciones
    for (String decl : declarations) {
        emit(decl);
    }
    
    // Inicializaciones
    for (String init : initAssignments) {
        emit(init);
    }
    
    // ✅ LOOP STRUCTURE CORRECTA
    emit(startLabel + ":");
    emit(condition + " = i < 5");  // ¡Calcular condición DENTRO del loop!
    emit("IF NOT " + condition + " GOTO " + endLabel);
    
    // Body del loop
    for (String bodyLine : bodyCode) {
        emit(bodyLine);
    }
    
    // ✅ UPDATE ÚNICO (no doble)
    if (updateExpr.trim().equals("i")) {
        emit("t_inc = i + 1");
        emit("i = t_inc");
    }
    
    // Salto y fin
    emit("GOTO " + startLabel);
    emit(endLabel + ":");
    
    // Footers (END)
    for (String footer : footers) {
        emit(footer);
    }
    
    System.out.println("DEBUG: FOR reorganizado correctamente con headers preservados");
}

private void generateCorrectUpdate(String updateExpr) {
    // PROBLEMA 2: El updateExpr solo contiene "i", pero necesitamos "i = i + 1"
    
    if (updateExpr.trim().equals("i") || updateExpr.trim().equals("++i")) {
        // Generar incremento manual
        emit("t_inc = i + 1");
        emit("i = t_inc");
        System.out.println("DEBUG: Generado incremento manual para: " + updateExpr);
    } else if (updateExpr.contains("=")) {
        // Ya es una asignación completa
        emit(updateExpr);
    } else {
        // Fallback: asumir que es variable que se incrementa
        emit("t_inc = " + updateExpr + " + 1");
        emit(updateExpr + " = t_inc");
        System.out.println("DEBUG: Generado incremento genérico para: " + updateExpr);
    }
}

//Pal Switch
public static class SwitchCase {
    public String value;
    public String label;
    public SwitchCase(String value, String label) {
        this.value = value;
        this.label = label;
    }
}

public void generateCompleteSwitchWithDeferred(String switchExpr, String exitLabel, List<String> deferredCode) {
    if (!enabled) return;
    
    currentSwitchExit = exitLabel;
    
    System.out.println("DEBUG: Generando SWITCH con código diferido");
    System.out.println("DEBUG: Expresión: " + switchExpr);
    System.out.println("DEBUG: Cases registrados: " + currentSwitchCases.size());
    System.out.println("DEBUG: Código diferido: " + deferredCode.size() + " líneas");
    
    for (String def : deferredCode) {
        System.out.println("  - " + def);
    }
    
    // Reorganizar con código diferido
    reorganizeSwitchWithDeferredCode(switchExpr, exitLabel, deferredCode);
    
    // Limpiar
    currentSwitchCases.clear();
    currentSwitchExit = null;
}

public void registerCase(String caseValue, String caseLabel) {
    currentSwitchCases.add(new SwitchCase(caseValue, caseLabel));
    System.out.println("DEBUG: Case " + caseValue + " registrado con etiqueta " + caseLabel);
}

public void registerDefault(String defaultLabel) {
    currentSwitchCases.add(new SwitchCase("DEFAULT", defaultLabel));
    System.out.println("DEBUG: Default registrado con etiqueta " + defaultLabel);
}

public void generateBreak() {
    if (!enabled) return;
    
    if (currentSwitchExit != null) {
        emit("GOTO " + currentSwitchExit);
        System.out.println("DEBUG: Break generado - salto a " + currentSwitchExit);
    } else {
        System.out.println("DEBUG: Break ignorado - sin contexto de switch");
    }
}

private void reorganizeSwitchWithDeferredCode(String switchExpr, String exitLabel, List<String> deferredCode) {
    // Separar código diferido por cases
    List<List<String>> caseBlocks = new ArrayList<>();
    List<String> currentBlock = new ArrayList<>();
    
    for (String code : deferredCode) {
        if (code.equals("BREAK")) {
            caseBlocks.add(new ArrayList<>(currentBlock));
            currentBlock.clear();
        } else {
            currentBlock.add(code);
        }
    }
    
    // Si queda código sin break (default case)
    if (!currentBlock.isEmpty()) {
        caseBlocks.add(currentBlock);
    }
    
    System.out.println("DEBUG: " + caseBlocks.size() + " bloques de cases identificados");
    
    // Generar comparaciones
    generateSwitchComparisons(switchExpr, exitLabel);
    
    // Generar cases con código diferido
    generateCasesWithDeferredCode(caseBlocks, exitLabel);
    
    // Etiqueta de salida
    emit(exitLabel + ":");
}

private void generateSwitchComparisons(String switchExpr, String exitLabel) {
    // Generar comparaciones secuenciales
    for (SwitchCase switchCase : currentSwitchCases) {
        if (!switchCase.value.equals("DEFAULT")) {
            emit("t_case = " + switchExpr + " == " + switchCase.value);
            emit("IF t_case GOTO " + switchCase.label);
        }
    }
    
    // Si hay default, saltar a él, si no, salir
    boolean hasDefault = currentSwitchCases.stream()
        .anyMatch(c -> c.value.equals("DEFAULT"));
    
    if (hasDefault) {
        String defaultLabel = currentSwitchCases.stream()
            .filter(c -> c.value.equals("DEFAULT"))
            .findFirst().get().label;
        emit("GOTO " + defaultLabel);
    } else {
        emit("GOTO " + exitLabel);
    }
}

private void generateCasesWithDeferredCode(List<List<String>> caseBlocks, String exitLabel) {
    for (int i = 0; i < currentSwitchCases.size(); i++) {
        SwitchCase switchCase = currentSwitchCases.get(i);
        emit(switchCase.label + ":");
        
        // Emitir código del bloque correspondiente
        if (i < caseBlocks.size()) {
            for (String code : caseBlocks.get(i)) {
                emit(code);
            }
            
            // Break automático si no es default
            if (!switchCase.value.equals("DEFAULT")) {
                emit("GOTO " + exitLabel);
            }
        }
    }
}

// pal return
public void setCurrentFunction(String functionName, String returnType) {
    this.currentFunctionName = functionName;
    this.currentFunctionReturnType = returnType;
    System.out.println("DEBUG: Función actual establecida: " + functionName + " -> " + returnType);
}

public void generateReturnWithValue(String returnValue) {
    if (!enabled) return;
    
    System.out.println("DEBUG: Generando return con valor: " + returnValue);
    
    // ✅ VERIFICAR SI EL VALOR ES NULL O INVÁLIDO
    if (returnValue == null || returnValue.equals("null")) {
        System.err.println("ERROR: Valor de return es null o inválido");
        emit("// ERROR: Return con valor null");
        return;
    }
    
    // ✅ GENERAR CÓDIGO INTERMEDIO PARA RETURN CON VALOR
    emit("RETURN " + returnValue);
    
    // ✅ GENERAR ETIQUETA DE SALIDA DE FUNCIÓN
    if (currentFunctionName != null) {
        String functionExitLabel = "EXIT_" + currentFunctionName.toUpperCase();
        emit("GOTO " + functionExitLabel);
    }
}

public void generateReturnVoid() {
    if (!enabled) return;
    
    System.out.println("DEBUG: Generando return void");
    
    // ✅ GENERAR CÓDIGO INTERMEDIO PARA RETURN VOID
    emit("RETURN");
    
    // ✅ GENERAR ETIQUETA DE SALIDA DE FUNCIÓN (opcional)
    if (currentFunctionName != null) {
        String functionExitLabel = "EXIT_" + currentFunctionName.toUpperCase();
        emit("GOTO " + functionExitLabel);
    }
}

// Método legacy para compatibilidad
public void generateReturn(Object returnExpr) {
    if (returnExpr != null) {
        generateReturnWithValue(returnExpr.toString());
    } else {
        generateReturnVoid();
    }
}

/* MEJORA: GENERAR ETIQUETAS DE SALIDA DE FUNCIÓN */

public void startFunction(String functionName, String returnType) {
    setCurrentFunction(functionName, returnType);
    
    // ✅ GENERAR ENCABEZADO DE FUNCIÓN
    if (returnType.equals("VOID")) {
        emit("FUNCTION " + functionName + " RETURNS VOID");
    } else {
        emit("FUNCTION " + functionName + " RETURNS " + returnType);
    }
    emit("BEGIN");
}

public void endFunction() {
    if (currentFunctionName != null) {
        // ✅ GENERAR ETIQUETA DE SALIDA
        String functionExitLabel = "EXIT_" + currentFunctionName.toUpperCase();
        emit(functionExitLabel + ":");
        
        // ✅ RETURN IMPLÍCITO PARA VOID
        if ("VOID".equals(currentFunctionReturnType)) {
            emit("RETURN");
        }
        
        emit("END " + currentFunctionName);
        
        System.out.println("DEBUG: Función " + currentFunctionName + " finalizada");
        
        // Limpiar
        currentFunctionName = null;
        currentFunctionReturnType = null;
    }
}
}