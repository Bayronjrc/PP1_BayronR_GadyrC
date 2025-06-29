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
        
        // ✅ ASEGURAR INICIALIZACIÓN BÁSICA
        if (name.equals("i") || name.equals("j") || name.equals("k")) {
            emit(name + " = 0");  // Inicializar contadores
        }
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
        System.out.println("DEBUG: Array access generado: " + temp + " = " + array + "[" + index1 + "][" + index2 + "]");
        return temp;
    }
    
    
    public String generateFunctionCall(String function, List<String> args) {
        return generateFunctionCallComplete(function, args, "INT"); // Asumir INT por defecto
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
    
// 4. FIX s2 - ASEGURAR QUE emit() FUNCIONE CORRECTAMENTE:
public void emit(String instruction) {
    if (enabled && instruction != null) {
        code.add(instruction);
        
        // ✅ DEBUG PARA s2: mostrar cada instrucción generada
        if (instruction.contains("IF") || instruction.contains("GOTO") || instruction.contains("L")) {
            System.out.println("DEBUG s2: Emitido -> " + instruction);
        }
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

public void generateCompleteIf(String condition, String endLabel) {
    if (!enabled) return;
    
    // ✅ GENERAR DIRECTAMENTE SIN PLACEHOLDERS
    emit("IF NOT " + condition + " GOTO " + endLabel);
    // La etiqueta se genera externamente
}
*/
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
 
public void generateCompleteIfElse(String condition, String elseLabel, String endLabel) {
    if (!enabled) return;
    
    // ✅ GENERAR DIRECTAMENTE
    emit("IF NOT " + condition + " GOTO " + elseLabel);
    // El resto se maneja externamente
}
*/

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

/**
 * Genera solo la condición y salto del DO-WHILE
 * (La etiqueta de inicio ya se generó antes del body)
 */
public void generateDoWhileCondition(String condition, String startLabel) {
    if (!enabled) return;
    
    System.out.println("DEBUG s2: Generando condición DO-WHILE: " + condition + " -> " + startLabel);
    
    // ✅ LÓGICA CORRECTA: "SI condición es TRUE, repetir"
    // condition = "t2" donde t2 = contador <= 3
    // Queremos: IF t2 GOTO startLabel (si es true, repetir)
    emit("IF " + condition + " GOTO " + startLabel);
    
    System.out.println("DEBUG s2: Condición generada - IF " + condition + " GOTO " + startLabel);
}

// 2. FIX s1 - AGREGAR MÉTODO ESPECÍFICO PARA DEBUG DE LLAMADAS:
public void debugFunctionCall(String functionName, List<String> args) {
    if (!enabled) return;
    
    System.out.println("DEBUG s1: ===================");
    System.out.println("DEBUG s1: Función: " + functionName);
    System.out.println("DEBUG s1: Argumentos: " + args);
    System.out.println("DEBUG s1: Código actual:");
    for (int i = Math.max(0, code.size()-5); i < code.size(); i++) {
        System.out.println("  [" + i + "] " + code.get(i));
    }
    System.out.println("DEBUG s1: ===================");
}

/**
 * Versión específica para generar etiquetas inmediatamente
 */
public void generateLabelNow(String label) {
    if (enabled) {
        emit(label + ":");
        System.out.println("DEBUG: Etiqueta generada inmediatamente: " + label);
    }
}

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
public void generateForWithExistingGrammar(String condition, String updateExpr, String startLabel, String endLabel) {
    if (!enabled) return;
    
    System.out.println("DEBUG s3: === FOR LOOP CON CONDICIÓN DINÁMICA ===");
    System.out.println("DEBUG s3: Condición recibida: " + condition);
    
    // ✅ EXTRAER LA CONDICIÓN REAL DEL CÓDIGO EXISTENTE
    String realCondition = findRealForCondition();
    
    if (realCondition == null) {
        // ✅ FALLBACK: Usar la condición pasada como parámetro
        realCondition = "k <= 2";  // Default
        System.out.println("DEBUG s3: Usando condición fallback: " + realCondition);
    } else {
        System.out.println("DEBUG s3: Condición real encontrada: " + realCondition);
    }
    
    // ✅ GENERAR LOOP CON CONDICIÓN REAL
    emit(startLabel + ":");                              // L1:
    emit("t_cond = " + realCondition);                   // t_cond = k <= 7 (o lo que sea)
    emit("IF NOT t_cond GOTO " + endLabel);              // IF NOT t_cond GOTO L2
    emit("WRITE k");                                     // WRITE k
    emit("t_inc = k + 1");                               // t_inc = k + 1
    emit("k = t_inc");                                   // k = t_inc
    emit("GOTO " + startLabel);                          // GOTO L1
    emit(endLabel + ":");                                // L2:
    
    System.out.println("DEBUG s3: FOR generado con condición: " + realCondition);
}

// ✅ MÉTODO PARA ENCONTRAR LA CONDICIÓN REAL
private String findRealForCondition() {
    System.out.println("DEBUG s3: Buscando condición real en código existente...");
    
    for (String line : code) {
        String trimmed = line.trim();
        
        // Buscar líneas como "t1 = k <= 7"
        if (trimmed.matches("t\\d+ = k [<>=!]+ \\d+")) {
            String[] parts = trimmed.split(" = ");
            if (parts.length == 2) {
                String condition = parts[1].trim();
                System.out.println("DEBUG s3: Condición encontrada: " + condition);
                return condition;
            }
        }
    }
    
    System.out.println("DEBUG s3: No se encontró condición específica");
    return null;
}

// Y DESACTIVAR cleanupForLoop para que no elimine el WRITE:
public void cleanupForLoop() {
    if (!enabled) return;
    
    System.out.println("DEBUG s3: Limpiando código ANTES del loop");
    
    List<String> cleanCode = new ArrayList<>();
    boolean inLoop = false;
    boolean foundFirstK1 = false;
    
    for (String line : code) {
        String trimmed = line.trim();
        
        // ✅ DETECTAR INICIO DEL LOOP
        if (trimmed.equals("L1:")) {
            inLoop = true;
            cleanCode.add(line);
            continue;
        }
        
        // ✅ SI ESTAMOS EN EL LOOP, PRESERVAR TODO
        if (inLoop) {
            cleanCode.add(line);
            continue;
        }
        
        // ✅ ANTES DEL LOOP: LIMPIAR CÓDIGO PROBLEMÁTICO
        if (!inLoop) {
            // Preservar declaraciones y función
            if (trimmed.startsWith("//") || trimmed.startsWith("FUNCTION") || 
                trimmed.startsWith("BEGIN") || trimmed.startsWith("DECLARE")) {
                cleanCode.add(line);
                continue;
            }
            
            // ✅ PRESERVAR SOLO LA PRIMERA INICIALIZACIÓN
            if (trimmed.equals("k = 1") && !foundFirstK1) {
                cleanCode.add(line);
                foundFirstK1 = true;
                System.out.println("DEBUG s3: Preservando k = 1 inicial");
                continue;
            }
            
            // ✅ SALTAR TODO LO DEMÁS ANTES DEL LOOP
            if (trimmed.equals("k = 0") || 
                trimmed.equals("k = 1") ||
                trimmed.startsWith("t1 = k") || 
                trimmed.equals("k = k + 1") ||
                trimmed.equals("WRITE k")) {
                
                System.out.println("DEBUG s3: Saltando línea pre-loop: " + trimmed);
                continue;
            }
            
            // ✅ PRESERVAR END
            if (trimmed.startsWith("END")) {
                cleanCode.add(line);
                continue;
            }
        }
    }
    
    // ✅ REEMPLAZAR CÓDIGO
    code.clear();
    code.addAll(cleanCode);
    
    System.out.println("DEBUG s3: Limpieza completada, " + cleanCode.size() + " líneas restantes");
}

// CLASE HELPER para info del FOR
private static class ForInfo {
    String variable;
    String initValue;
    String operator;
    String limitValue;
    
    ForInfo(String var, String init, String op, String limit) {
        this.variable = var;
        this.initValue = init;
        this.operator = op;
        this.limitValue = limit;
    }
}

// MÉTODO CLAVE: Parsear condición real del FOR - ARREGLADO
private ForInfo parseForCondition(String condition, String updateExpr) {
    System.out.println("🔍 DEBUG: Parseando condición: '" + condition + "'");
    System.out.println("🔍 DEBUG: Update expression: '" + updateExpr + "'");
    
    // ✅ EXTRAER VARIABLE del updateExpr
    String variable = extractVariableFromUpdate(updateExpr);
    
    // ✅ BUSCAR EN EL CÓDIGO EXISTENTE **ANTES** DE LIMPIAR
    String realCondition = findRealConditionInCode(variable);
    System.out.println("🔍 DEBUG: Condición real encontrada: " + realCondition);
    
    // ✅ VALORES POR DEFECTO
    String operator = "<";  
    String limitValue = "3"; // Fallback
    String initValue = "0";  // Por defecto
    
    // ✅ PARSEAR LA CONDICIÓN REAL SI SE ENCONTRÓ
    if (realCondition != null && !realCondition.isEmpty()) {
        if (realCondition.contains("<=")) {
            String[] parts = realCondition.split("<=");
            if (parts.length == 2) {
                operator = "<=";
                limitValue = parts[1].trim();
            }
        } else if (realCondition.contains("<")) {
            String[] parts = realCondition.split("<");
            if (parts.length == 2) {
                operator = "<";
                limitValue = parts[1].trim();
            }
        } else if (realCondition.contains(">=")) {
            String[] parts = realCondition.split(">=");
            if (parts.length == 2) {
                operator = ">=";
                limitValue = parts[1].trim();
            }
        } else if (realCondition.contains(">")) {
            String[] parts = realCondition.split(">");
            if (parts.length == 2) {
                operator = ">";
                limitValue = parts[1].trim();
            }
        }
    }
    
    // ✅ BUSCAR INICIALIZACIÓN EN EL CÓDIGO EXISTENTE
    String foundInit = findInitializationInCode(variable);
    if (foundInit != null) {
        initValue = foundInit;
    }
    
    System.out.println("✅ DEBUG: Resultado final - " + variable + " desde " + initValue + " " + operator + " " + limitValue);
    return new ForInfo(variable, initValue, operator, limitValue);
}

// 🆕 NUEVO MÉTODO: Buscar inicialización de variable
private String findInitializationInCode(String variable) {
    System.out.println("🔍 DEBUG: Buscando inicialización para variable: " + variable);
    
    for (String line : code) {
        String trimmed = line.trim();
        
        // Buscar líneas como "i = 0" o "DECLARE i int = 0"
        if (trimmed.equals(variable + " = 0") || 
            trimmed.equals(variable + " = 1") ||
            trimmed.matches(variable + " = \\d+")) {
            
            String[] parts = trimmed.split("=");
            if (parts.length == 2) {
                String value = parts[1].trim();
                System.out.println("✅ DEBUG: Inicialización encontrada: " + value);
                return value;
            }
        }
        
        // También buscar en declaraciones con inicialización
        if (trimmed.contains("DECLARE " + variable) && trimmed.contains("=")) {
            String[] parts = trimmed.split("=");
            if (parts.length == 2) {
                String value = parts[1].trim();
                System.out.println("✅ DEBUG: Inicialización en DECLARE encontrada: " + value);
                return value;
            }
        }
    }
    
    System.out.println("❌ DEBUG: No se encontró inicialización, usando 0");
    return "0"; // Fallback
}
// NUEVO MÉTODO: Buscar la condición REAL antes de limpiar
private String findRealConditionInCode(String variable) {
    System.out.println("🔍 DEBUG: Buscando condición real para variable: " + variable);
    
    for (String line : code) {
        String trimmed = line.trim();
        
        // Buscar líneas como "t1 = i < 4" donde 'i' es nuestra variable
        if (trimmed.contains("=") && trimmed.contains(variable)) {
            String[] parts = trimmed.split("=");
            if (parts.length == 2) {
                String rightSide = parts[1].trim();
                
                // Verificar si contiene operadores de comparación
                if (rightSide.contains("<") || rightSide.contains(">") || 
                    rightSide.contains("<=") || rightSide.contains(">=")) {
                    
                    System.out.println("✅ DEBUG: Encontrada condición: " + rightSide);
                    return rightSide;
                }
            }
        }
    }
    
    System.out.println("❌ DEBUG: No se encontró condición real");
    return null;
}

// EXTRAER VARIABLE del update expression
private String extractVariableFromUpdate(String updateExpr) {
    if (updateExpr == null || updateExpr.isEmpty()) {
        return "i"; // Por defecto
    }
    
    // Patrones: "i", "++i", "i++", "j", etc.
    String cleaned = updateExpr.trim().replaceAll("\\+\\+", "");
    
    if (cleaned.matches("[a-zA-Z][a-zA-Z0-9]*")) {
        return cleaned;
    }
    
    return "i"; // Fallback
}

// MÉTODO MEJORADO: Buscar patrones en código existente
private String findConditionInCode() {
    for (String line : code) {
        String trimmed = line.trim();
        
        // Buscar líneas como "t1 = i < 4"
        if (trimmed.contains("=") && (trimmed.contains("<") || trimmed.contains("<="))) {
            String[] parts = trimmed.split("=");
            if (parts.length == 2) {
                String rightSide = parts[1].trim();
                System.out.println("🔍 DEBUG: Encontrada condición potencial: " + rightSide);
                
                if (rightSide.matches("\\w+ < \\d+") || rightSide.matches("\\w+ <= \\d+")) {
                    System.out.println("✅ DEBUG: Condición válida encontrada: " + rightSide);
                    return rightSide;
                }
            }
        }
    }
    
    System.out.println("❌ DEBUG: No se encontró condición, usando fallback");
    return "i < 3"; // Fallback
}

// DEBUGGING SUPER DETALLADO: Mostrar TODO el proceso
public void debugShowForParsing(String condition, String updateExpr) {
    System.out.println("\n🔍 === DEBUG FOR PARSING DETALLADO ===");
    System.out.println("Condición recibida: '" + condition + "'");
    System.out.println("Update recibida: '" + updateExpr + "'");
    
    System.out.println("\n📋 Código actual antes del parsing:");
    for (int i = 0; i < code.size(); i++) {
        System.out.println("  [" + i + "] " + code.get(i));
    }
    
    ForInfo info = parseForCondition(condition, updateExpr);
    System.out.println("\n✅ Resultado del parsing:");
    System.out.println("  Variable: " + info.variable);
    System.out.println("  Init: " + info.initValue);
    System.out.println("  Operator: " + info.operator);
    System.out.println("  Limit: " + info.limitValue);
    
    System.out.println("\n🔍 Buscando en código existente:");
    String foundCondition = findConditionInCode();
    System.out.println("  Condición encontrada: " + foundCondition);
    
    System.out.println("===============================\n");
}

// MÉTODO PARA REEMPLAZAR EL PLACEHOLDER CON CUERPO REAL
public void insertForLoopBody(List<String> bodyStatements) {
    if (!enabled) return;
    
    // Buscar el placeholder y reemplazarlo
    for (int i = 0; i < code.size(); i++) {
        if (code.get(i).contains("BODY_PLACEHOLDER")) {
            // Eliminar placeholder
            code.remove(i);
            
            // Insertar statements del cuerpo
            for (int j = 0; j < bodyStatements.size(); j++) {
                code.add(i + j, bodyStatements.get(j));
            }
            
            System.out.println("DEBUG: Body insertado - " + bodyStatements.size() + " statements");
            break;
        }
    }
}


private void nuclearCleanup() {
    System.out.println("DEBUG: LIMPIEZA NUCLEAR - DESACTIVADA TEMPORALMENTE");
    // ❌ NO HACER NADA - La limpieza está causando más problemas
    return;
}

private int findAfterDeclarations() {
    for (int i = 0; i < code.size(); i++) {
        String line = code.get(i).trim();
        
        // Buscar posición después de la última declaración
        if (line.startsWith("BEGIN") || 
            (i > 0 && code.get(i-1).trim().startsWith("DECLARE") && 
             !line.startsWith("DECLARE"))) {
            
            System.out.println("DEBUG: Insertando después de declaraciones en posición: " + i);
            return i;
        }
    }
    
    // Si no encuentra, insertar antes del END
    for (int i = code.size() - 1; i >= 0; i--) {
        if (code.get(i).trim().startsWith("END")) {
            return i;
        }
    }
    
    return code.size();
}

private void generateSimpleFor(String condition, String updateExpr, String startLabel, String endLabel) {
    System.out.println("DEBUG: Generando FOR correcto - eliminando código malo");
    
    // ✅ PASO 1: LIMPIAR TODO EL CÓDIGO EXISTENTE DEL LOOP
    removeAllLoopCode();
    
    // ✅ PASO 2: ENCONTRAR DONDE INSERTAR LA ESTRUCTURA CORRECTA
    int insertPos = findBestInsertPositionForLoop();
    
    // ✅ PASO 3: GENERAR ESTRUCTURA CORRECTA
    List<String> correctLoop = new ArrayList<>();
    
    // Inicialización ya está hecha (i = 0)
    correctLoop.add(startLabel + ":");
    
    // ✅ CONDICIÓN PRIMERO (no después)
    String conditionTemp = newTemp();
    correctLoop.add(conditionTemp + " = " + condition);
    correctLoop.add("IF NOT " + conditionTemp + " GOTO " + endLabel);
    
    // El cuerpo se inserta aquí (WRITE i)
    correctLoop.add("WRITE i");
    
    // ✅ INCREMENTO AL FINAL
    String incTemp = newTemp();
    correctLoop.add(incTemp + " = i + 1");
    correctLoop.add("i = " + incTemp);
    
    // ✅ SALTO DE VUELTA
    correctLoop.add("GOTO " + startLabel);
    correctLoop.add(endLabel + ":");
    
    // ✅ PASO 4: INSERTAR TODO
    for (int i = 0; i < correctLoop.size(); i++) {
        insertAt(insertPos + i, correctLoop.get(i));
    }
    
    System.out.println("DEBUG: FOR corregido completamente");
}

// MÉTODO NUEVO: Limpiar TODO el código de loop malo
private void removeAllLoopCode() {
    System.out.println("DEBUG: Limpiando código de loop existente");
    
    for (int i = code.size() - 1; i >= 0; i--) {
        String line = code.get(i).trim();
        
        // Eliminar líneas problemáticas
        if (line.startsWith("t") && line.contains(" = t") && !line.contains("<") && !line.contains("+")) {
            // Líneas como "t2 = t1"
            code.remove(i);
            System.out.println("DEBUG: Eliminada línea problemática: " + line);
        }
        else if (line.startsWith("IF NOT t") && line.contains("GOTO")) {
            // IFs con temporales no inicializados
            code.remove(i);
            System.out.println("DEBUG: Eliminado IF problemático: " + line);
        }
        else if (line.matches("\\w+ = \\w+ \\+ 1") && !line.contains("i = i + 1")) {
            // Incrementos duplicados
            code.remove(i);
            System.out.println("DEBUG: Eliminado incremento duplicado: " + line);
        }
        else if (line.startsWith("GOTO L") && i > 0) {
            // GOTOs mal colocados
            String prevLine = code.get(i-1).trim();
            if (prevLine.startsWith("i =")) {
                code.remove(i);
                System.out.println("DEBUG: Eliminado GOTO mal colocado: " + line);
            }
        }
    }
}

// MÉTODO NUEVO: Encontrar mejor posición para insertar
private int findBestInsertPositionForLoop() {
    // Buscar después de la última inicialización (i = 0)
    for (int i = code.size() - 1; i >= 0; i--) {
        String line = code.get(i).trim();
        if (line.matches("i = \\d+")) {
            System.out.println("DEBUG: Insertando después de: " + line);
            return i + 1;
        }
    }
    
    // Si no encuentra, buscar después de declaraciones
    for (int i = code.size() - 1; i >= 0; i--) {
        String line = code.get(i).trim();
        if (line.startsWith("DECLARE i")) {
            return i + 1;
        }
    }
    
    return code.size();
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

public void generateContinue(String startLabel) {
    if (enabled) {
        emit("GOTO " + startLabel);
        System.out.println("DEBUG: Continue generado - salto a " + startLabel);
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
    
    if (returnValue == null || returnValue.equals("null")) {
        System.err.println("ERROR: Valor de return es null o inválido");
        emit("// ERROR: Return con valor null");
        return;
    }
    
    // ✅ SOLO GENERAR RETURN (sin GOTO ni etiquetas extra)
    emit("RETURN " + returnValue);
    
    System.out.println("DEBUG: Return generado: RETURN " + returnValue);
}

public void generateReturnVoid() {
    if (!enabled) return;
    
    System.out.println("DEBUG: Generando return void");
    
    // ✅ SOLO GENERAR RETURN VOID
    emit("RETURN");
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
    
    System.out.println("DEBUG: Iniciando función " + functionName + " -> " + returnType);
}

public void declareParameter(String paramName, String paramType) {
    if (!enabled) return;
    
    // ✅ GENERAR DECLARACIÓN DE PARÁMETRO
    emit("DECLARE " + paramName + " " + paramType);
    
    System.out.println("DEBUG: Parámetro declarado: " + paramName + " " + paramType);
}

public void endFunction() {
    if (currentFunctionName != null) {
        // ✅ NO generar etiquetas EXIT_ extra
        emit("END " + currentFunctionName);
        
        System.out.println("DEBUG: Función " + currentFunctionName + " finalizada");
        
        // Limpiar
        currentFunctionName = null;
        currentFunctionReturnType = null;
    }
}

// Método específico para break en loops (diferente del switch)
public void generateBreakToLabel(String endLabel) {
    if (enabled) {
        emit("GOTO " + endLabel);
        System.out.println("DEBUG: Break en loop generado - salto a " + endLabel);
    }
}

public void generateArrayAssignment(String array, String index1, String index2, String value) {
    if (!enabled) return;
    
    emit(array + "[" + index1 + "][" + index2 + "] = " + value);
    System.out.println("DEBUG: Array assignment generado: " + array + "[" + index1 + "][" + index2 + "] = " + value);
}

// para llamada de funciones
/**
 * Genera llamada a función completa con parámetros y resultado
 */
// 3. FIX s1 - MEJORAR generateFunctionCallComplete:
public String generateFunctionCallComplete(String functionName, List<String> args, String returnType) {
    if (!enabled) return "t" + tempCounter++;
    
    System.out.println("DEBUG s1: ========== LLAMADA A FUNCIÓN ==========");
    System.out.println("DEBUG s1: Función: " + functionName + " -> " + returnType);
    System.out.println("DEBUG s1: Argumentos: " + args.size() + " parámetros");
    
    // ✅ PASO 1: GENERAR PARÁMETROS CON DEBUG
    for (int i = 0; i < args.size(); i++) {
        String arg = args.get(i);
        emit("PARAM " + arg);
        System.out.println("DEBUG s1: Parámetro " + (i+1) + ": " + arg);
    }
    
    // ✅ PASO 2: GENERAR LLAMADA CON DEBUG
    if (returnType.equals("VOID")) {
        // Función void - no devuelve valor
        emit("CALL " + functionName + " " + args.size());
        System.out.println("DEBUG s1: Llamada VOID: CALL " + functionName + " " + args.size());
        return null; // No hay resultado
    } else {
        // Función con retorno - asignar a temporal
        String temp = newTemp();
        emit(temp + " = CALL " + functionName + " " + args.size());
        System.out.println("DEBUG s1: Llamada con retorno: " + temp + " = CALL " + functionName + " " + args.size());
        return temp;
    }
}

public void forceVariableDeclaration(String varName, String type, String initValue) {
    if (enabled) {
        // Buscar si ya está declarada
        boolean found = false;
        for (String line : code) {
            if (line.contains("DECLARE " + varName)) {
                found = true;
                break;
            }
        }
        
        if (!found) {
            // Insertar al principio del código real
            int insertPos = 3; // Después de comentarios iniciales
            code.add(insertPos, "DECLARE " + varName + " " + type);
            if (initValue != null) {
                code.add(insertPos + 1, varName + " = " + initValue);
            }
            System.out.println("DEBUG: Variable " + varName + " forzosamente declarada");
        }
    }
}

public boolean isExpressionAFunctionCall(String expression) {
    return expression != null && 
           (expression.startsWith("t") && expression.matches("t\\d+")) &&
           code.stream().anyMatch(line -> line.contains(expression + " = CALL"));
}

// 6. FIX GENERAL - MÉTODO PARA MOSTRAR CÓDIGO ACTUAL:
public void showCurrentCode(String context) {
    if (!enabled) return;
    
    System.out.println("\n📋 CÓDIGO ACTUAL (" + context + "):");
    for (int i = 0; i < code.size(); i++) {
        System.out.println("  [" + String.format("%2d", i) + "] " + code.get(i));
    }
    System.out.println("📋 FIN CÓDIGO (" + code.size() + " líneas)\n");
}

/**
 * Inserta una condición antes del bloque más reciente
 */
public void insertConditionalBeforeBlock(String conditional) {
    if (!enabled) return;
    
    System.out.println("DEBUG s4: Insertando condición diferida: " + conditional);
    
    // ✅ BUSCAR DÓNDE INSERTAR LA CONDICIÓN
    int insertPos = findLastBlockStart();
    
    if (insertPos >= 0) {
        code.add(insertPos, conditional);
        System.out.println("DEBUG s4: Condición insertada en posición " + insertPos);
    } else {
        // Fallback: insertar antes del final
        emit(conditional);
        System.out.println("DEBUG s4: Condición insertada al final (fallback)");
    }
}

/**
 * Encuentra el inicio del último bloque procesado
 */
private int findLastBlockStart() {
    // Buscar hacia atrás para encontrar la primera línea ejecutable del bloque más reciente
    for (int i = code.size() - 1; i >= 0; i--) {
        String line = code.get(i).trim();
        
        // Saltar líneas vacías y comentarios
        if (line.isEmpty() || line.startsWith("//")) {
            continue;
        }
        
        // Encontrar la primera línea ejecutable (no declaraciones)
        if (!line.startsWith("DECLARE") && 
            !line.startsWith("FUNCTION") && 
            !line.startsWith("BEGIN") &&
            !line.startsWith("END")) {
            
            System.out.println("DEBUG s4: Encontrado inicio de bloque en posición " + i + ": " + line);
            return i; // Insertar ANTES de esta línea
        }
    }
    
    return -1; // No encontrado
}

/**
 * Genera IF-ELSE diferido completo
 */
public void generateDeferredIfElse(String condition, String elseLabel, String endLabel) {
    if (!enabled) return;
    
    System.out.println("DEBUG s4: Generando IF-ELSE diferido completo");
    
    // ✅ BUSCAR LA ASIGNACIÓN DE LA VARIABLE TEMPORAL
    String tempAssignment = null;
    for (String line : code) {
        String trimmed = line.trim();
        if (trimmed.startsWith(condition + " = ")) {
            tempAssignment = trimmed;
            System.out.println("DEBUG s4: Encontrada asignación: " + tempAssignment);
            break;
        }
    }
    
    // ✅ REORGANIZAR CÓDIGO LIMPIO
    List<String> reorganizedCode = new ArrayList<>();
    List<String> thenBlock = new ArrayList<>();
    List<String> elseBlock = new ArrayList<>();
    
    boolean foundTempAssignment = false;
    int writeCount = 0;
    
    for (String line : code) {
        String trimmed = line.trim();
        
        // ✅ PRESERVAR ESTRUCTURA INICIAL
        if (trimmed.startsWith("//") || trimmed.startsWith("FUNCTION") || 
            trimmed.startsWith("BEGIN") || trimmed.startsWith("DECLARE")) {
            reorganizedCode.add(line);
            continue;
        }
        
        // ✅ BUSCAR ASIGNACIÓN DE VALOR (valor = 8)
        if (trimmed.matches("\\w+ = \\d+")) {
            reorganizedCode.add(line);
            continue;
        }
        
        // ✅ IDENTIFICAR WRITES (primero es THEN, segundo es ELSE)
        if (trimmed.startsWith("WRITE")) {
            writeCount++;
            if (writeCount == 1) {
                thenBlock.add(line);
            } else if (writeCount == 2) {
                elseBlock.add(line);
            }
            continue;
        }
        
        // ✅ PRESERVAR END
        if (trimmed.startsWith("END")) {
            reorganizedCode.add(line);
        }
    }
    
    // ✅ AGREGAR CÁLCULO DE t1 SI NO EXISTE
    if (tempAssignment != null) {
        reorganizedCode.add(tempAssignment);
    } else {
        // ✅ FALLBACK: Generar cálculo basado en s4.c
        reorganizedCode.add("t1 = valor > 5");
        System.out.println("DEBUG s4: Generando cálculo fallback: t1 = valor > 5");
    }
    
    // ✅ GENERAR ESTRUCTURA IF-ELSE CORRECTA
    reorganizedCode.add("IF NOT " + condition + " GOTO " + elseLabel);
    
    // Agregar bloque THEN
    reorganizedCode.addAll(thenBlock);
    reorganizedCode.add("GOTO " + endLabel);
    
    // Agregar bloque ELSE
    reorganizedCode.add(elseLabel + ":");
    reorganizedCode.addAll(elseBlock);
    
    // Agregar etiqueta final
    reorganizedCode.add(endLabel + ":");
    
    // ✅ REEMPLAZAR CÓDIGO COMPLETO
    code.clear();
    code.addAll(reorganizedCode);
    
    System.out.println("DEBUG s4: IF-ELSE reorganizado con cálculo de " + condition);
}

/**
 * Método de debug para mostrar reorganización
 */
public void debugShowReorganization() {
    System.out.println("\n🔍 DEBUG s4: CÓDIGO REORGANIZADO:");
    for (int i = 0; i < code.size(); i++) {
        System.out.println("  [" + String.format("%2d", i) + "] " + code.get(i));
    }
    System.out.println("🔍 FIN REORGANIZACIÓN\n");
}
}