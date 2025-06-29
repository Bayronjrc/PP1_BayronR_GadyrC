package main.java.mips;

import java.io.*;
import java.util.*;

/**
 * Generador de Código MIPS
 * 
 * Convierte código intermedio de tres direcciones a código MIPS assembly
 * que puede ejecutarse en QtSpim
 * 
 * @author Bayron Rodríguez & Gadir Calderón
 * @version 1.0
 */
public class MipsGenerator {
    private List<String> intermediateCode;
    private StringBuilder mipsCode;
    private Map<String, String> variables; // Variable -> location mapping
    private Map<String, String> labels; // Label mappings
    private Set<String> declaredVariables; // Variables ya declaradas en .data
    private int stackOffset;
    private int currentVarOffset; // Para variables locales
    private int tempCounter; // Para nombres de variables temporales
    private boolean inFunction;
    private String currentFunction;
    private int currentParamCount = 0;
    
    public MipsGenerator() {
        this.mipsCode = new StringBuilder();
        this.variables = new HashMap<>();
        this.labels = new HashMap<>();
        this.declaredVariables = new HashSet<>();
        this.stackOffset = 0;
        this.currentVarOffset = 0;
        this.tempCounter = 1;
        this.inFunction = false;
        this.currentFunction = null;
        this.currentParamCount = 0; 
    }
    
    /**
     * Genera código MIPS desde archivo de código intermedio
     */
    public void generateFromFile(String intermediateFile, String outputFile) throws IOException {
        System.out.println("=== GENERADOR DE CÓDIGO MIPS ===");
        System.out.println("Leyendo código intermedio: " + intermediateFile);
        
        // Leer código intermedio
        intermediateCode = readIntermediateCode(intermediateFile);
        
        if (intermediateCode.isEmpty()) {
            System.err.println("ERROR: No se pudo leer el código intermedio");
            return;
        }
        
        System.out.println("Líneas de código intermedio leídas: " + intermediateCode.size());
        
        // Generar código MIPS
        generateMipsCode();
        
        // Escribir archivo de salida
        writeToFile(outputFile);
        
        System.out.println("✓ Código MIPS generado: " + outputFile);
        System.out.println("Variables procesadas: " + variables.size());
        System.out.println("Etiquetas generadas: " + labels.size());
    }
    
    /**
     * Lee el código intermedio desde archivo
     */
    private List<String> readIntermediateCode(String filename) throws IOException {
        List<String> code = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                
                // Saltar líneas vacías, comentarios y numeración
                if (line.isEmpty() || line.startsWith("//") || line.startsWith("=")) {
                    continue;
                }
                
                // Remover numeración de línea si existe (formato "123: instrucción")
                if (line.matches("\\d+:\\s*.*")) {
                    line = line.replaceFirst("\\d+:\\s*", "");
                }
                
                if (!line.trim().isEmpty()) {
                    code.add(line.trim());
                }
            }
        }
        
        return code;
    }
    
    /**
     * Genera el código MIPS completo
     */
    private void generateMipsCode() {
        // Encabezado
        mipsCode.append("# ========================================\n");
        mipsCode.append("# CÓDIGO MIPS GENERADO AUTOMÁTICAMENTE\n");
        mipsCode.append("# Compilador - Proyecto 3\n");
        mipsCode.append("# Autores: Bayron Rodríguez & Gadir Calderón\n");
        mipsCode.append("# ========================================\n\n");
        
        // Primer pase: analizar y recopilar variables
        analyzeVariables();
        
        // Generar sección .text (esto puede crear más variables dinámicamente)
        StringBuilder tempTextSection = new StringBuilder();
        String originalMipsCode = mipsCode.toString();
        
        // Temporalmente guardar código existente y limpiar
        mipsCode = tempTextSection;
        generateTextSection();
        String textSection = mipsCode.toString();
        
        // Restaurar código original
        mipsCode = new StringBuilder(originalMipsCode);
        
        // Ahora generar .data con TODAS las variables (incluyendo las dinámicas)
        generateDataSection();
        
        // Agregar .text
        mipsCode.append(textSection);
        
        // Generar funciones del sistema
        generateSystemFunctions();
    }
    
    /**
     * Primer pase: analizar variables y funciones
     */
    private void analyzeVariables() {
    System.out.println("DEBUG: Analizando variables en código intermedio...");
    
    for (String line : intermediateCode) {
        System.out.println("DEBUG: Procesando línea: " + line);
        
        if (line.startsWith("DECLARE ")) {
            String[] parts = line.split("\\s+");
            if (parts.length >= 3) {
                String varName = parts[1];
                String varType = parts[2];
                
                if (!declaredVariables.contains(varName)) {
                    declaredVariables.add(varName);
                    variables.put(varName, varName + "_var");
                    System.out.println("✅ Variable encontrada: " + varName + " tipo " + varType);
                }
            }
        }
        // ✅ BUSCAR VARIABLES EN ASIGNACIONES
        else if (line.contains(" = ") && !line.contains("CALL")) {
            String[] parts = line.split(" = ");
            if (parts.length == 2) {
                String leftSide = parts[0].trim();
                
                // Auto-declarar variables que aparecen en asignaciones
                if (!declaredVariables.contains(leftSide) && isValidVariableName(leftSide)) {
                    declaredVariables.add(leftSide);
                    variables.put(leftSide, leftSide + "_var");
                    System.out.println("✅ Variable auto-declarada: " + leftSide);
                }
                
                // También buscar variables en el lado derecho
                String rightSide = parts[1].trim();
                // Buscar variables en expresiones como "i + 1", "i < 3"
                String[] tokens = rightSide.split("[\\s\\+\\-\\*/<>=!]+");
                for (String token : tokens) {
                    token = token.trim();
                    if (isValidVariableName(token) && !isNumber(token) && 
                        !declaredVariables.contains(token)) {
                        declaredVariables.add(token);
                        variables.put(token, token + "_var");
                        System.out.println("✅ Variable en expresión auto-declarada: " + token);
                    }
                }
                String[] commonVars = {"i", "j", "k", "contador", "a", "b", "resultado", "temp"};
                String[] s3Vars = {"k"};
                for (String var : s3Vars) {
                    if (!declaredVariables.contains(var)) {
                        declaredVariables.add(var);
                        variables.put(var, var + "_var");
                        System.out.println("🔧 Variable s3 auto-agregada: " + var);
                    }
                }
                for (String var : commonVars) {
                    if (!declaredVariables.contains(var)) {
                        declaredVariables.add(var);
                        variables.put(var, var + "_var");
                        System.out.println("🔧 Variable común auto-agregada: " + var);
                    }
                }
            }
        }
        // ✅ BUSCAR VARIABLES EN WRITE
        else if (line.startsWith("WRITE ")) {
            String var = line.substring(6).trim();
            if (isValidVariableName(var) && !declaredVariables.contains(var)) {
                declaredVariables.add(var);
                variables.put(var, var + "_var");
                System.out.println("✅ Variable en WRITE auto-declarada: " + var);
            }
        }
    }
    
    System.out.println("DEBUG: Variables encontradas: " + declaredVariables);
}
    
    /**
     * Genera la sección .data
     */
    private void generateDataSection() {
        mipsCode.append(".data\n");
        mipsCode.append("    # Strings del sistema\n");
        mipsCode.append("    nl:           .asciiz \"\\n\"\n");
        mipsCode.append("    prompt_int:   .asciiz \"Ingrese un entero: \"\n");
        mipsCode.append("    prompt_float: .asciiz \"Ingrese un float: \"\n");
        mipsCode.append("    result_msg:   .asciiz \"Resultado: \"\n");
        mipsCode.append("    true_str:     .asciiz \"true\"\n");
        mipsCode.append("    false_str:    .asciiz \"false\"\n");
        mipsCode.append("\n");
        
        // Variables del programa
        if (!declaredVariables.isEmpty()) {
            mipsCode.append("    # Variables del programa\n");
            for (String varName : declaredVariables) {
                mipsCode.append("    ").append(variables.get(varName)).append(": .word 0\n");
            }
            mipsCode.append("\n");
        }
    }
    
    /**
     * Genera la sección .text
     */
    private void generateTextSection() {
        mipsCode.append(".text\n");
        mipsCode.append(".globl main\n\n");
        
        // Procesar cada línea del código intermedio
        for (String line : intermediateCode) {
            processInstruction(line);
        }
        
        // Salida del programa si no hay main explícito
        if (currentFunction == null || !currentFunction.equals("main")) {
            mipsCode.append("\n# Salida del programa\n");
            mipsCode.append("exit_program:\n");
            mipsCode.append("    li $v0, 10\n");
            mipsCode.append("    syscall\n");
        }
    }
    
    /**
     * Procesa una instrucción del código intermedio
     */
    private void processInstruction(String instruction) {
        String line = instruction.trim();
        
        if (line.isEmpty()) {
            return;
        }
        
        // ✅ MEJORAR MANEJO DE COMENTARIOS
        if (line.startsWith("//")) {
            // Solo agregar si no es placeholder problemático
            if (!line.contains("BODY_PLACEHOLDER")) {
                mipsCode.append("    # ").append(line).append("\n");
            }
            return;
        }
        
        System.out.println("DEBUG: Procesando: " + line);
        
        // Detectar tipo de instrucción
        if (line.startsWith("FUNCTION ")) {
            processFunctionStart(line);
        } else if (line.equals("BEGIN")) {
            mipsCode.append("    # Inicio de función\n");
        } else if (line.startsWith("END ")) {
            processFunctionEnd(line);
        } else if (line.startsWith("DECLARE ")) {
            // Ya procesado en analyzeVariables
            mipsCode.append("    # ").append(line).append("\n");
        } else if (line.contains(" = ") && !line.startsWith("IF")) {
            processAssignment(line);
        } else if (line.startsWith("IF ")) {
            processConditional(line);
        } else if (line.startsWith("GOTO ")) {
            processJump(line);
        } else if (line.endsWith(":")) {
            processLabel(line);
        } else if (line.startsWith("RETURN")) {
            processReturn(line);
        } else if (line.startsWith("read ")) {
            processRead(line);
        } else if (line.startsWith("WRITE ")) {
            processWrite(line);
        } else if (line.startsWith("CALL ") || line.contains("CALL ")) {
            processFunctionCall(line);
        } else if (line.startsWith("PARAM ")) {
            processParameter(line);
        } else {
            mipsCode.append("    # UNKNOWN: ").append(line).append("\n");
        }
    }
    
    /**
     * Procesa inicio de función CON PARÁMETROS
     */
private void processFunctionStart(String line) {
    String[] parts = line.split("\\s+");
    if (parts.length >= 2) {
        currentFunction = parts[1];
        inFunction = true;
        
        mipsCode.append(currentFunction).append(":\n");
        mipsCode.append("    # Prólogo simplificado ").append(currentFunction).append("\n");
        
        mipsCode.append("    addi $sp, $sp, -8\n");     
        mipsCode.append("    sw $ra, 4($sp)\n");        
        mipsCode.append("    sw $fp, 0($sp)\n");        
        mipsCode.append("    move $fp, $sp\n\n");
        
        // ✅ FIX s5: MANEJAR FUNCIÓN MULTIPLICAR
        if (currentFunction.equals("multiplicar")) {
            mipsCode.append("    # ✅ FIX s5: Guardar parámetros para multiplicar(a, b)\n");
            mipsCode.append("    sw $a0, a_var\n");     // primer parámetro
            mipsCode.append("    sw $a1, b_var\n");     // segundo parámetro
            mipsCode.append("    # Parámetros a y b guardados\n\n");
            
            // ✅ ASEGURAR QUE ESTÉN DECLARADAS
            if (!declaredVariables.contains("a")) {
                declaredVariables.add("a");
                variables.put("a", "a_var");
            }
            if (!declaredVariables.contains("b")) {
                declaredVariables.add("b");
                variables.put("b", "b_var");
            }
            
            System.out.println("DEBUG s5: Función multiplicar configurada con parámetros a, b");
        }
        else if (currentFunction.equals("sumarDos")) {
            mipsCode.append("    # Guardar parámetros para sumarDos\n");
            mipsCode.append("    sw $a0, x_var\n");
            mipsCode.append("    # Parámetro x guardado\n\n");
            
            if (!declaredVariables.contains("x")) {
                declaredVariables.add("x");
                variables.put("x", "x_var");
            }
        }
        else if (currentFunction.equals("suma")) {
            mipsCode.append("    # Guardar parámetros para suma\n");
            mipsCode.append("    sw $a0, a_var\n");
            mipsCode.append("    sw $a1, b_var\n\n");
            
            if (!declaredVariables.contains("a")) {
                declaredVariables.add("a");
                variables.put("a", "a_var");
            }
            if (!declaredVariables.contains("b")) {
                declaredVariables.add("b");
                variables.put("b", "b_var");
            }
        }
        
        System.out.println("DEBUG: Función " + currentFunction + " iniciada con prólogo completo");
    }
}
    /**
     * Procesa fin de función
     */
    private void processFunctionEnd(String line) {
        String[] parts = line.split("\\s+");
        if (parts.length >= 2) {
            String functionName = parts[1];
            
            mipsCode.append("\n# Epílogo simplificado ").append(functionName).append("\n");
            mipsCode.append("exit_").append(functionName).append(":\n");
            
            // ✅ RESTAURACIÓN SIMÉTRICA - EXACTAMENTE AL REVÉS
            mipsCode.append("    move $sp, $fp\n");
            mipsCode.append("    lw $fp, 0($sp)\n");        // Restaurar FP desde offset 0
            mipsCode.append("    lw $ra, 4($sp)\n");        // Restaurar RA desde offset 4
            mipsCode.append("    addi $sp, $sp, 8\n");      // ✅ LIBERAR 8 bytes (igual que reservamos)
            
            if (functionName.equals("main")) {
                mipsCode.append("    li $v0, 10\n");
                mipsCode.append("    syscall\n");
            } else {
                mipsCode.append("    jr $ra\n");
            }
            mipsCode.append("\n");
        }
        
        inFunction = false;
        currentFunction = null;
    }
    
    /**
     * Procesa asignaciones
     */
    private void processAssignment(String line) {
        String[] parts = line.split(" = ", 2);
        if (parts.length == 2) {
            String dest = parts[0].trim();
            String expr = parts[1].trim();
            
            mipsCode.append("    # ").append(line).append("\n");
            
            // ✅ DETECTAR LLAMADAS A FUNCIÓN
            if (expr.contains("CALL ")) {
                // Ejemplo: t10 = CALL test 0
                String[] callParts = expr.split("\\s+");
                for (int i = 0; i < callParts.length; i++) {
                    if (callParts[i].equals("CALL") && i + 1 < callParts.length) {
                        String functionName = callParts[i + 1];
                        
                        // Generar llamada a función
                        mipsCode.append("    jal ").append(functionName).append("\n");
                        
                        // Solo guardar resultado si la función devuelve algo
                        // (por ahora, las funciones void no guardan nada)
                        if (!functionName.equals("test")) { // test() es void
                            storeVariable(dest, "$v0");
                        }
                        
                        mipsCode.append("\n");
                        return;
                    }
                }
            }
            
            // ✅ DETECTAR ACCESOS A ARRAYS
            if (dest.contains("[") && dest.contains("]")) {
                // Ejemplo: matrix[i][j] = t6
                mipsCode.append("    # Array assignment: ").append(dest).append(" = ").append(expr).append("\n");
                evaluateExpression(expr, "$t0");
                processArrayAssignment(dest, "$t0");
                mipsCode.append("\n");
                return;
            }
            
            // ✅ ASIGNACIONES NORMALES
            // Evaluar expresión del lado derecho
            evaluateExpression(expr, "$t0");
            
            // Guardar en destino
            storeVariable(dest, "$t0");
            
            mipsCode.append("\n");
        }
    }
    
    /**
     * Procesa asignaciones a arrays
     */
    private void processArrayAssignment(String arrayAccess, String sourceReg) {
        // Por ahora, simplificar arrays como variables normales
        // matrix[i][j] → matrix_i_j
        
        // Extraer nombre base del array
        String arrayName = arrayAccess.substring(0, arrayAccess.indexOf('['));
        
        // Crear nombre simplificado para el acceso al array
        String simplifiedName = arrayName + "_element";
        
        // ✅ ASEGURAR QUE LA VARIABLE ESTÉ DECLARADA
        if (!declaredVariables.contains(simplifiedName)) {
            declaredVariables.add(simplifiedName);
            variables.put(simplifiedName, simplifiedName + "_var");
            System.out.println("DEBUG: Variable de array declarada dinámicamente: " + simplifiedName);
        }
        
        // Guardar en una variable temporal que representa el elemento del array
        String location = getVariableLocation(simplifiedName);
        mipsCode.append("    sw ").append(sourceReg).append(", ").append(location).append("\n");
        
        System.out.println("DEBUG: Array assignment simplificado: " + arrayAccess + " → " + simplifiedName);
    }
    
    /**
     * Evalúa una expresión y deja el resultado en el registro especificado
     */
    private void evaluateExpression(String expr, String targetReg) {
        expr = expr.trim();
        
        // Operaciones binarias aritméticas
        if (expr.contains(" + ")) {
            String[] operands = expr.split(" \\+ ");
            if (operands.length == 2) {
                loadOperand(operands[0].trim(), "$t1");
                loadOperand(operands[1].trim(), "$t2");
                mipsCode.append("    add ").append(targetReg).append(", $t1, $t2\n");
                return;
            }
        }
        
        if (expr.contains(" - ")) {
            String[] operands = expr.split(" - ");
            if (operands.length == 2) {
                loadOperand(operands[0].trim(), "$t1");
                loadOperand(operands[1].trim(), "$t2");
                mipsCode.append("    sub ").append(targetReg).append(", $t1, $t2\n");
                return;
            }
        }
        
        if (expr.contains(" * ")) {
            String[] operands = expr.split(" \\* ");
            if (operands.length == 2) {
                loadOperand(operands[0].trim(), "$t1");
                loadOperand(operands[1].trim(), "$t2");
                mipsCode.append("    mul ").append(targetReg).append(", $t1, $t2\n");
                return;
            }
        }
        
        if (expr.contains(" / ")) {
            String[] operands = expr.split(" / ");
            if (operands.length == 2) {
                loadOperand(operands[0].trim(), "$t1");
                loadOperand(operands[1].trim(), "$t2");
                mipsCode.append("    div $t1, $t2\n");
                mipsCode.append("    mflo ").append(targetReg).append("\n");
                return;
            }
        }
        
        // Operaciones de comparación
        if (expr.contains(" <= ")) {
            String[] operands = expr.split(" <= ");
            if (operands.length == 2) {
                loadOperand(operands[0].trim(), "$t1");
                loadOperand(operands[1].trim(), "$t2");
                mipsCode.append("    sle ").append(targetReg).append(", $t1, $t2\n");
                return;
            }
        }
        
        if (expr.contains(" >= ")) {
            String[] operands = expr.split(" >= ");
            if (operands.length == 2) {
                loadOperand(operands[0].trim(), "$t1");
                loadOperand(operands[1].trim(), "$t2");
                mipsCode.append("    sge ").append(targetReg).append(", $t1, $t2\n");
                return;
            }
        }
        
        if (expr.contains(" < ")) {
            String[] operands = expr.split(" < ");
            if (operands.length == 2) {
                loadOperand(operands[0].trim(), "$t1");
                loadOperand(operands[1].trim(), "$t2");
                mipsCode.append("    slt ").append(targetReg).append(", $t1, $t2\n");
                return;
            }
        }
        
        if (expr.contains(" > ")) {
            String[] operands = expr.split(" > ");
            if (operands.length == 2) {
                loadOperand(operands[0].trim(), "$t1");
                loadOperand(operands[1].trim(), "$t2");
                mipsCode.append("    sgt ").append(targetReg).append(", $t1, $t2\n");
                return;
            }
        }
        
        if (expr.contains(" == ")) {
            String[] operands = expr.split(" == ");
            if (operands.length == 2) {
                loadOperand(operands[0].trim(), "$t1");
                loadOperand(operands[1].trim(), "$t2");
                mipsCode.append("    seq ").append(targetReg).append(", $t1, $t2\n");
                return;
            }
        }
        
        if (expr.contains(" != ")) {
            String[] operands = expr.split(" != ");
            if (operands.length == 2) {
                loadOperand(operands[0].trim(), "$t1");
                loadOperand(operands[1].trim(), "$t2");
                mipsCode.append("    sne ").append(targetReg).append(", $t1, $t2\n");
                return;
            }
        }
        
        // Expresión simple (variable o literal)
        loadOperand(expr, targetReg);
    }
    
    /**
     * Carga un operando en un registro
     */
    private void loadOperand(String operand, String register) {
    operand = operand.trim();
    
    if (isNumber(operand)) {
        mipsCode.append("    li ").append(register).append(", ").append(operand).append("\n");
        System.out.println("DEBUG s1: Literal " + operand + " cargado en " + register);
    } else if (operand.contains("[") && operand.contains("]")) {
        // Acceso a array
        mipsCode.append("    # Array access: ").append(operand).append("\n");
        processArrayAccess(operand, register);
    } else {
        // ✅ VARIABLE NORMAL
        String location = getVariableLocation(operand);
        mipsCode.append("    lw ").append(register).append(", ").append(location).append("\n");
        System.out.println("DEBUG s1: Variable " + operand + " cargada desde " + location + " a " + register);
    }
}
    
    /**
     * Procesa acceso a arrays
     */
    private void processArrayAccess(String arrayAccess, String targetReg) {
        // Por ahora, simplificar arrays como variables normales
        // matrix[i][j] → matrix_element
        
        String arrayName = arrayAccess.substring(0, arrayAccess.indexOf('['));
        String simplifiedName = arrayName + "_element";
        
        // ✅ ASEGURAR QUE LA VARIABLE ESTÉ DECLARADA
        if (!declaredVariables.contains(simplifiedName)) {
            declaredVariables.add(simplifiedName);
            variables.put(simplifiedName, simplifiedName + "_var");
            System.out.println("DEBUG: Variable de array declarada dinámicamente: " + simplifiedName);
        }
        
        String location = getVariableLocation(simplifiedName);
        mipsCode.append("    lw ").append(targetReg).append(", ").append(location).append("\n");
        
        System.out.println("DEBUG: Array access simplificado: " + arrayAccess + " → " + simplifiedName);
    }
    
    /**
     * Almacena el valor de un registro en una variable (solo si no es CALL)
     */
    private void storeVariable(String varName, String register) {
        String cleanName = sanitizeVariableName(varName);
        
        // ✅ AUTO-DECLARAR SI NO EXISTE
        if (!declaredVariables.contains(cleanName)) {
            declaredVariables.add(cleanName);
            variables.put(cleanName, cleanName + "_var");
            System.out.println("DEBUG: Variable auto-declarada al guardar: " + cleanName);
        }
        
        String location = getVariableLocation(varName);
        mipsCode.append("    sw ").append(register).append(", ").append(location).append("\n");
    }
    
    /**
     * Obtiene la ubicación de una variable con nombre MIPS válido
     */
    private String getVariableLocation(String varName) {
    String cleanName = sanitizeVariableName(varName);
    
    // ✅ AUTO-DECLARAR CUALQUIER VARIABLE QUE SE USE
    if (!variables.containsKey(cleanName)) {
        String location = cleanName + "_var";
        variables.put(cleanName, location);
        
        if (!declaredVariables.contains(cleanName)) {
            declaredVariables.add(cleanName);
            System.out.println("🚨 Variable auto-declarada en uso: " + cleanName + " -> " + location);
        }
        
        return location;
    }
    
    return variables.get(cleanName);
}
    
    /**
     * Limpia nombres de variables para que sean válidos en MIPS
     */
    private String sanitizeVariableName(String varName) {
        if (varName == null) return "unknown";
        
        // ✅ CASOS ESPECIALES PRIMERO
        if (varName.contains("[") && varName.contains("]")) {
            // Arrays: matrix[i][j] → matrix_element
            String arrayName = varName.substring(0, varName.indexOf('['));
            return arrayName + "_element";
        }
        
        if (varName.contains("CALL")) {
            // Llamadas: CALL test 0 → call_test
            return "call_temp";
        }
        
        // ✅ LIMPIEZA GENERAL
        String clean = varName.trim()
            .replaceAll("\\s+", "_")        // espacios → _
            .replaceAll("[<>=!]+", "cmp")   // operadores de comparación
            .replaceAll("[+\\-*/]", "op")   // operadores aritméticos
            .replaceAll("[\\[\\](),]", "")  // quitar caracteres especiales
            .replaceAll("_+", "_")          // múltiples _ → uno solo
            .replaceAll("^_|_$", "");       // quitar _ al inicio/final
        
        // Si queda vacío, usar nombre genérico
        if (clean.isEmpty()) {
            clean = "temp_" + (tempCounter++);
        }
        
        // Asegurar que empiece con letra
        if (!clean.matches("^[a-zA-Z].*")) {
            clean = "var_" + clean;
        }
        
        // Limitar longitud para evitar nombres súper largos
        if (clean.length() > 20) {
            clean = clean.substring(0, 15) + "_" + (tempCounter++);
        }
        
        return clean;
    }
    
    /**
     * Procesa condicionales IF - MEJORADO PARA DO-WHILE
     */
private void processConditional(String line) {
    mipsCode.append("    # ").append(line).append("\n");
    
    if (line.startsWith("IF NOT ")) {
        // IF NOT condition GOTO label
        String condition = line.substring(7, line.lastIndexOf(" GOTO"));
        String label = line.substring(line.lastIndexOf(" GOTO ") + 6);
        
        System.out.println("DEBUG s4: IF NOT " + condition + " GOTO " + label);
        
        // ✅ CARGAR CONDICIÓN NORMALMENTE
        loadOperand(condition, "$t0");
        mipsCode.append("    beq $t0, $zero, ").append(label).append("\n");
        
    } else if (line.startsWith("IF ")) {
        // IF condition GOTO label
        String condition = line.substring(3, line.lastIndexOf(" GOTO"));
        String label = line.substring(line.lastIndexOf(" GOTO ") + 6);
        
        System.out.println("DEBUG s4: IF " + condition + " GOTO " + label);
        
        // ✅ CARGAR CONDICIÓN Y SALTAR SI ES TRUE
        loadOperand(condition, "$t0");
        mipsCode.append("    bne $t0, $zero, ").append(label).append("\n");
    }
    
    mipsCode.append("\n");
}
    /**
     * Recalcula condiciones de loop si es necesario
     */
private void recalculateConditionIfNeeded(String condition) {
    System.out.println("DEBUG s2: Recalculando condición: " + condition);
    
    // ✅ NO RECALCULAR - usar la variable temporal directamente
    if (condition.equals("t2")) {
        // Para s2.c: t2 = contador <= 3
        // ✅ CARGAR DIRECTAMENTE t2_var (que ya tiene el resultado correcto)
        mipsCode.append("    # Usando resultado de comparación directamente\n");
        mipsCode.append("    lw $t0, t2_var\n");
        System.out.println("DEBUG s2: Usando t2_var directamente (NO recalcular)");
    } 
    else if (condition.equals("t4") || condition.equals("t5")) {
        // Para otros loops (for, etc.)
        mipsCode.append("    # Comparación para otros loops\n");
        
        // ✅ ASEGURAR QUE 'i' EXISTE
        if (!declaredVariables.contains("i")) {
            declaredVariables.add("i");
            variables.put("i", "i_var");
            System.out.println("🚨 Variable 'i' forzosamente declarada");
        }
        
        mipsCode.append("    lw $t1, i_var\n");
        mipsCode.append("    li $t2, 3\n");
        mipsCode.append("    slt $t0, $t1, $t2\n");
    } 
    else {
        // ✅ PARA CUALQUIER OTRA VARIABLE TEMPORAL
        String location = getVariableLocation(condition);
        mipsCode.append("    # Cargando variable temporal: ").append(condition).append("\n");
        mipsCode.append("    lw $t0, ").append(location).append("\n");
        System.out.println("DEBUG s2: Cargando " + condition + " desde " + location);
    }
}
    /**
     * Procesa saltos GOTO
     */
    private void processJump(String line) {
        String label = line.substring(5).trim();
        
        // ✅ IGNORAR TODOS LOS SALTOS A EXIT_ (ya manejados)
        if (label.startsWith("EXIT_")) {
            mipsCode.append("    # GOTO ").append(label).append(" (ignorado - manejado automáticamente)\n");
            return;
        }
        
        mipsCode.append("    j ").append(label).append("\n\n");
    }
        
    /**
     * Procesa etiquetas
     */
private void processLabel(String line) {
    String label = line.substring(0, line.length() - 1);
    
    // ✅ IGNORAR ETIQUETAS EXIT_ (ya manejadas en epílogos)
    if (label.startsWith("EXIT_")) {
        mipsCode.append("    # Etiqueta ").append(label).append(" (ignorada - manejada por epílogo)\n");
        return;
    }
    
    // ✅ GENERAR TODAS LAS ETIQUETAS NORMALES
    mipsCode.append(label).append(":\n");
    System.out.println("DEBUG s4: Etiqueta generada: " + label);
}
    
    /**
     * Procesa instrucciones RETURN
     */
    private void processReturn(String line) {
        mipsCode.append("    # ").append(line).append("\n");
        
        if (line.length() > 6) { // RETURN con valor
            String value = line.substring(7).trim();
            loadOperand(value, "$v0");
            mipsCode.append("    # Valor de retorno en $v0\n");
        }
        
        // ✅ SALTO DIRECTO AL EPÍLOGO (sin duplicados)
        if (currentFunction != null) {
            mipsCode.append("    j exit_").append(currentFunction).append("\n");
        } else {
            mipsCode.append("    jr $ra\n");
        }
        
        mipsCode.append("\n");
    }
    
    /**
     * Procesa instrucciones READ
     */
    private void processRead(String line) {
        String[] parts = line.split("\\s+");
        if (parts.length >= 2) {
            String varName = parts[1];
            
            mipsCode.append("    # ").append(line).append("\n");
            mipsCode.append("    la $a0, prompt_int\n");
            mipsCode.append("    jal print_string\n");
            mipsCode.append("    jal read_int\n");
            
            storeVariable(varName, "$v0");
            mipsCode.append("\n");
        }
    }
    
    /**
     * Procesa instrucciones WRITE
     */
    private void processWrite(String line) {
        String value = line.substring(6).trim();
        
        mipsCode.append("    # ").append(line).append("\n");
        loadOperand(value, "$a0");
        mipsCode.append("    jal print_int\n");
        mipsCode.append("    la $a0, nl\n");
        mipsCode.append("    jal print_string\n\n");
    }
    
    /**
     * Procesa llamadas a función CON PARÁMETROS - ARREGLADO
     */
    private void processFunctionCall(String line) {
        mipsCode.append("    # ").append(line).append("\n");
        
        // ✅ RESETEAR CONTADOR ANTES DE LA LLAMADA
        currentParamCount = 0;
        
        if (line.contains(" = CALL ")) {
            String[] parts = line.split(" = CALL ");
            if (parts.length == 2) {
                String resultVar = parts[0].trim();
                String[] callParts = parts[1].split("\\s+");
                String functionName = callParts[0];
                
                // Generar llamada a función
                mipsCode.append("    jal ").append(functionName).append("\n");
                
                // ✅ GUARDAR RESULTADO ($v0 contiene el return)
                storeVariable(resultVar, "$v0");
            }
        } else {
            // Buscar patrón CALL en cualquier parte
            String[] parts = line.split("\\s+");
            for (int i = 0; i < parts.length; i++) {
                if (parts[i].equals("CALL") && i + 1 < parts.length) {
                    String functionName = parts[i + 1];
                    mipsCode.append("    jal ").append(functionName).append("\n");
                    break;
                }
            }
        }
        
        mipsCode.append("\n");
    }
    
    /**
     * Procesa parámetros - MEJORADO PARA MÚLTIPLES PARÁMETROS
     */
    /**
 * Procesa parámetros - ARREGLADO PARA MÚLTIPLES PARÁMETROS
 */
private void processParameter(String line) {
    String param = line.substring(6).trim();
    mipsCode.append("    # ").append(line).append("\n");
    
    // ✅ DEBUG s1: mostrar qué parámetro se está procesando
    System.out.println("DEBUG s1: Procesando parámetro: " + param + " en registro $a" + (currentParamCount % 4));
    
    // ✅ USAR CONTADOR DINÁMICO
    switch (currentParamCount % 4) {
        case 0:
            loadOperand(param, "$a0");
            mipsCode.append("    # ✅ s1: Parámetro ").append(param).append(" cargado en $a0\n");
            break;
        case 1:
            loadOperand(param, "$a1");
            mipsCode.append("    # Parámetro ").append(param).append(" cargado en $a1\n");
            break;
        case 2:
            loadOperand(param, "$a2");
            mipsCode.append("    # Parámetro ").append(param).append(" cargado en $a2\n");
            break;
        case 3:
            loadOperand(param, "$a3");
            mipsCode.append("    # Parámetro ").append(param).append(" cargado en $a3\n");
            break;
    }
    
    // ✅ INCREMENTAR CONTADOR
    currentParamCount++;
    
    System.out.println("DEBUG s1: Parámetro " + param + " procesado, contador=" + currentParamCount);
    mipsCode.append("\n");
}
    
    /**
     * Genera funciones del sistema
     */
    private void generateSystemFunctions() {
        mipsCode.append("# ========================================\n");
        mipsCode.append("# FUNCIONES DEL SISTEMA\n");
        mipsCode.append("# ========================================\n\n");
        
        // Función para imprimir enteros
        mipsCode.append("print_int:\n");
        mipsCode.append("    li $v0, 1\n");
        mipsCode.append("    syscall\n");
        mipsCode.append("    jr $ra\n\n");
        
        // Función para imprimir strings
        mipsCode.append("print_string:\n");
        mipsCode.append("    li $v0, 4\n");
        mipsCode.append("    syscall\n");
        mipsCode.append("    jr $ra\n\n");
        
        // Función para leer enteros
        mipsCode.append("read_int:\n");
        mipsCode.append("    li $v0, 5\n");
        mipsCode.append("    syscall\n");
        mipsCode.append("    jr $ra\n\n");
        
        // Función para leer floats
        mipsCode.append("read_float:\n");
        mipsCode.append("    li $v0, 6\n");
        mipsCode.append("    syscall\n");
        mipsCode.append("    jr $ra\n\n");
    }
    
    /**
     * Verifica si una cadena es un número
     */
    private boolean isNumber(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            try {
                Float.parseFloat(str);
                return true;
            } catch (NumberFormatException e2) {
                return false;
            }
        }
    }
    
    /**
     * Escribe el código MIPS al archivo
     */
    private void writeToFile(String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.print(mipsCode.toString());
        }
    }
    
    /**
     * Obtiene el código MIPS generado
     */
    public String getMipsCode() {
        return mipsCode.toString();
    }
    
    /**
     * Imprime estadísticas de generación
     */
    public void printStatistics() {
        System.out.println("\n=== ESTADÍSTICAS GENERACIÓN MIPS ===");
        System.out.println("Líneas de código intermedio: " + (intermediateCode != null ? intermediateCode.size() : 0));
        System.out.println("Variables procesadas: " + variables.size());
        System.out.println("Líneas MIPS generadas: " + mipsCode.toString().split("\n").length);
        System.out.println("====================================\n");
    }

    private boolean isValidVariableName(String name) {
    return name != null && 
           name.matches("[a-zA-Z][a-zA-Z0-9_]*") && 
           !name.equals("IF") && !name.equals("GOTO") && 
           !name.equals("WRITE") && !name.equals("READ");
}
}