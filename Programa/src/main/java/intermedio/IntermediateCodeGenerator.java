package main.java.intermedio;

import java.io.*;
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
    
    public void startFunction(String name, String returnType) {
        if (enabled) {
            emit("FUNCTION " + name + " RETURNS " + returnType);
            emit("BEGIN");
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
    
    public void generateBreak() {
        if (enabled) {
            emit("BREAK");
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



}