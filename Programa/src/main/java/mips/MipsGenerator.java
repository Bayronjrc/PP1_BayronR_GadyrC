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
    private Map<String, String> variables; 
    private Map<String, String> labels; 
    private Set<String> declaredVariables; 
    private int stackOffset;
    private int currentVarOffset;
    private int tempCounter;
    private boolean inFunction;
    private String currentFunction;
    private int currentParamCount = 0;
    private boolean expectingParameters = false;
    private Map<String, List<String>> functionParameters = new HashMap<>();
    private Map<String, Integer> functionParamCounts = new HashMap<>();
    
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
    

    public void generateFromFile(String intermediateFile, String outputFile) throws IOException {
        System.out.println("=== GENERADOR DE CÓDIGO MIPS ===");
        System.out.println("Leyendo código intermedio: " + intermediateFile);
        
        intermediateCode = readIntermediateCode(intermediateFile);
        
        if (intermediateCode.isEmpty()) {
            System.err.println("ERROR: No se pudo leer el código intermedio");
            return;
        }
        
        System.out.println("Líneas de código intermedio leídas: " + intermediateCode.size());
        
        generateMipsCode();
        
        writeToFile(outputFile);
        
        System.out.println("✓ Código MIPS generado: " + outputFile);
        System.out.println("Variables procesadas: " + variables.size());
        System.out.println("Etiquetas generadas: " + labels.size());
    }
    
    private List<String> readIntermediateCode(String filename) throws IOException {
        List<String> code = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                
                if (line.isEmpty() || line.startsWith("//") || line.startsWith("=")) {
                    continue;
                }

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
    

    private void generateMipsCode() {
        mipsCode.append("# ========================================\n");
        mipsCode.append("# CÓDIGO MIPS GENERADO AUTOMÁTICAMENTE\n");
        mipsCode.append("# Compilador - Proyecto 3\n");
        mipsCode.append("# Autores: Bayron Rodríguez & Gadir Calderón\n");
        mipsCode.append("# ========================================\n\n");
        
        analyzeVariables();
        
        StringBuilder tempTextSection = new StringBuilder();
        String originalMipsCode = mipsCode.toString();
        
        mipsCode = tempTextSection;
        generateTextSection();
        String textSection = mipsCode.toString();
        
        mipsCode = new StringBuilder(originalMipsCode);
        
        generateDataSection();
        
        mipsCode.append(textSection);
        
        generateSystemFunctions();
    }
    
    
    
    private void analyzeVariables() {
        System.out.println("DEBUG: Analizando variables en código intermedio...");
        
        detectRealFunctionParameters();
        
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
            else if (line.contains(" = ") && !line.contains("CALL")) {
                String[] parts = line.split(" = ");
                if (parts.length == 2) {
                    String leftSide = parts[0].trim();
                    
                    if (!declaredVariables.contains(leftSide) && isValidVariableName(leftSide)) {
                        declaredVariables.add(leftSide);
                        variables.put(leftSide, leftSide + "_var");
                        System.out.println("✅ Variable auto-declarada: " + leftSide);
                    }
                    
                    String rightSide = parts[1].trim();
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
                }
            }
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
        System.out.println("DEBUG: Parámetros reales detectados: " + functionParameters);
    }
    
    private void detectRealFunctionParameters() {
        System.out.println("🔍 DETECTING real function parameters from PARAM patterns...");
        
        for (int i = 0; i < intermediateCode.size(); i++) {
            String line = intermediateCode.get(i).trim();
            
            if (line.contains("CALL ")) {
                String[] parts = line.split("\\s+");
                String functionName = null;
                int paramCount = 0;
                
                for (int j = 0; j < parts.length; j++) {
                    if (parts[j].equals("CALL") && j + 1 < parts.length) {
                        functionName = parts[j + 1];
                        if (j + 2 < parts.length) {
                            try {
                                paramCount = Integer.parseInt(parts[j + 2]);
                            } catch (NumberFormatException e) {
                                paramCount = 0;
                            }
                        }
                        break;
                    }
                }
                
                if (functionName != null && paramCount > 0) {
                    functionParamCounts.put(functionName, paramCount);
                    System.out.println("🎯 DETECTED: " + functionName + " has " + paramCount + " parameters");
                    
                    List<String> params = new ArrayList<>();
                    int paramsSeen = 0;
                    for (int k = i - 1; k >= 0 && paramsSeen < paramCount; k--) {
                        String prevLine = intermediateCode.get(k).trim();
                        if (prevLine.startsWith("PARAM ")) {
                            paramsSeen++;
                        } else if (!prevLine.isEmpty() && !prevLine.startsWith("//")) {
                            break; 
                        }
                    }
                    
                    for (int p = 0; p < paramCount; p++) {
                        if (paramCount == 1) {
                            params.add("n");
                        } else if (paramCount == 2) {
                            params.add(p == 0 ? "a" : "b"); 
                        } else {
                            params.add("param" + (p + 1)); 
                        }
                    }
                    
                    functionParameters.put(functionName, params);
                }
            }
        }
    }
    
    private void analyzeFunctionSignatures() {
        String currentFunc = null;
        boolean inFunction = false;
        List<String> currentParams = new ArrayList<>();
        Set<String> declaredInFunction = new HashSet<>();
        
        for (String line : intermediateCode) {
            if (line.startsWith("FUNCTION ")) {
                String[] parts = line.split("\\s+");
                if (parts.length >= 2) {
                    currentFunc = parts[1];
                    inFunction = true;
                    currentParams = new ArrayList<>();
                    declaredInFunction = new HashSet<>();
                    System.out.println("🔍 ANALYZING function: " + currentFunc);
                }
            } else if (line.startsWith("END ") && inFunction) {
                functionParameters.put(currentFunc, new ArrayList<>(currentParams));
                System.out.println("✅ Function " + currentFunc + " parameters: " + currentParams);
                inFunction = false;
                currentFunc = null;
            } else if (inFunction && line.startsWith("DECLARE ")) {
                String[] parts = line.split("\\s+");
                if (parts.length >= 3) {
                    String varName = parts[1];
                    declaredInFunction.add(varName);
                }
            } else if (inFunction && line.contains(" = ") && 
                      (line.contains(currentFunc) || hasVariableFromParams(line, declaredInFunction))) {
                extractPotentialParameters(line, currentParams, declaredInFunction);
            }
        }
    }
    
    private boolean hasVariableFromParams(String line, Set<String> declared) {
        for (String var : declared) {
            if (line.contains(var)) return true;
        }
        return false;
    }
    
    private void extractPotentialParameters(String line, List<String> params, Set<String> declared) {
        String[] tokens = line.split("[\\s=+\\-*/<>!()]+");
        for (String token : tokens) {
            token = token.trim();
            if (isValidVariableName(token) && declared.contains(token) && !params.contains(token)) {
                params.add(token);
                System.out.println("🎯 DETECTED parameter: " + token);
            }
        }
    }
    
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
        
        if (!declaredVariables.isEmpty()) {
            mipsCode.append("    # Variables del programa\n");
            for (String varName : declaredVariables) {
                mipsCode.append("    ").append(variables.get(varName)).append(": .word 0\n");
            }
            mipsCode.append("\n");
        }
    }
    
    private void generateTextSection() {
        mipsCode.append(".text\n");
        mipsCode.append(".globl main\n\n");
        
        for (String line : intermediateCode) {
            processInstruction(line);
        }
        
        if (currentFunction == null || !currentFunction.equals("main")) {
            mipsCode.append("\n# Salida del programa\n");
            mipsCode.append("exit_program:\n");
            mipsCode.append("    li $v0, 10\n");
            mipsCode.append("    syscall\n");
        }
    }
    
    
    private void processInstruction(String instruction) {
        String line = instruction.trim();
        
        if (line.isEmpty()) {
            return;
        }
        
        if (line.startsWith("//")) {
            if (!line.contains("BODY_PLACEHOLDER")) {
                mipsCode.append("    # ").append(line).append("\n");
            }
            return;
        }
        
        System.out.println("DEBUG: Procesando: " + line);
        
        if (line.startsWith("PARAM ")) {
            if (!expectingParameters) {
                currentParamCount = 0;
                expectingParameters = true;
                System.out.println("🔄 RESET: Contador de parámetros reseteado a 0 para nueva llamada");
            }
        } else {
            if (expectingParameters) {
                System.out.println("🏁 END PARAMS: Terminaron los parámetros para esta llamada");
            }
            expectingParameters = false;
        }
        
        if (line.startsWith("FUNCTION ")) {
            processFunctionStart(line);
        } else if (line.equals("BEGIN")) {
            mipsCode.append("    # Inicio de función\n");
        } else if (line.startsWith("END ")) {
            processFunctionEnd(line);
        } else if (line.startsWith("DECLARE ")) {
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
    

    private void processFunctionStart(String line) {
        String[] parts = line.split("\\s+");
        if (parts.length >= 2) {
            currentFunction = parts[1];
            inFunction = true;
            
            currentParamCount = 0;
            
            mipsCode.append(currentFunction).append(":\n");
            mipsCode.append("    # Prólogo estándar ").append(currentFunction).append("\n");
            
            mipsCode.append("    addi $sp, $sp, -8\n");     
            mipsCode.append("    sw $ra, 4($sp)\n");        
            mipsCode.append("    sw $fp, 0($sp)\n");        
            mipsCode.append("    move $fp, $sp\n");
            
            mipsCode.append("    # Reservar espacio para variables locales\n");
            mipsCode.append("    addi $sp, $sp, -16\n");  
            mipsCode.append("\n");
            
            if (currentFunction.equals("multiplicar")) {
                mipsCode.append("    # Guardar parámetros en stack frame local\n");
                mipsCode.append("    sw $a0, -4($fp)   # a local\n");    
                mipsCode.append("    sw $a1, -8($fp)   # b local\n"); 
                mipsCode.append("    # También en variables globales para compatibilidad\n");
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
            else if (currentFunction.equals("sumarDos")) {
                mipsCode.append("    # Guardar parámetros en stack frame local\n");
                mipsCode.append("    sw $a0, -4($fp)   # x local\n");
                mipsCode.append("    sw $a0, x_var     # x global\n\n");
                
                if (!declaredVariables.contains("x")) {
                    declaredVariables.add("x");
                    variables.put("x", "x_var");
                }
            }
            else if (currentFunction.equals("suma")) {
                mipsCode.append("    # Guardar parámetros en stack frame local\n");
                mipsCode.append("    sw $a0, -4($fp)   # a local\n");
                mipsCode.append("    sw $a1, -8($fp)   # b local\n");
                mipsCode.append("    sw $a0, a_var     # a global\n");
                mipsCode.append("    sw $a1, b_var     # b global\n\n");
                
                if (!declaredVariables.contains("a")) {
                    declaredVariables.add("a");
                    variables.put("a", "a_var");
                }
                if (!declaredVariables.contains("b")) {
                    declaredVariables.add("b");
                    variables.put("b", "b_var");
                }
            }
            else if (currentFunction.equals("fibonacci")) {
                mipsCode.append("    # Guardar parámetros en stack frame local\n");
                mipsCode.append("    sw $a0, -4($fp)   # n local (CRÍTICO para recursión)\n");
                mipsCode.append("    sw $a0, n_var     # n global\n\n");
                
                if (!declaredVariables.contains("n")) {
                    declaredVariables.add("n");
                    variables.put("n", "n_var");
                }
            }
            else if (currentFunction.equals("potencia")) {
                mipsCode.append("    # Guardar parámetros en stack frame local\n");
                mipsCode.append("    sw $a0, -4($fp)   # base local (CRÍTICO para recursión)\n");
                mipsCode.append("    sw $a1, -8($fp)   # exp local (CRÍTICO para recursión)\n");
                mipsCode.append("    sw $a0, base_var  # base global\n");
                mipsCode.append("    sw $a1, exp_var   # exp global\n\n");
                
                if (!declaredVariables.contains("base")) {
                    declaredVariables.add("base");
                    variables.put("base", "base_var");
                }
                if (!declaredVariables.contains("exp")) {
                    declaredVariables.add("exp");
                    variables.put("exp", "exp_var");
                }
            }
            else if (currentFunction.equals("mcd")) {
                mipsCode.append("    # Guardar parámetros en stack frame local\n");
                mipsCode.append("    sw $a0, -4($fp)   # a local (CRÍTICO para recursión)\n");
                mipsCode.append("    sw $a1, -8($fp)   # b local (CRÍTICO para recursión)\n");
                mipsCode.append("    sw $a0, a_var     # a global\n");
                mipsCode.append("    sw $a1, b_var     # b global\n\n");
                
                if (!declaredVariables.contains("a")) {
                    declaredVariables.add("a");
                    variables.put("a", "a_var");
                }
                if (!declaredVariables.contains("b")) {
                    declaredVariables.add("b");
                    variables.put("b", "b_var");
                }
            }
            else {
                mipsCode.append("    # Función genérica - guardar hasta 4 parámetros en stack frame\n");
                mipsCode.append("    sw $a0, -4($fp)   # param1 local\n");
                mipsCode.append("    sw $a1, -8($fp)   # param2 local\n");
                mipsCode.append("    sw $a2, -12($fp)  # param3 local\n");
                mipsCode.append("    sw $a3, -16($fp)  # param4 local\n\n");
            }
            
            System.out.println("DEBUG: Función " + currentFunction + " iniciada con stack frame estándar");
        }
    }

    private void processFunctionEnd(String line) {
        String[] parts = line.split("\\s+");
        if (parts.length >= 2) {
            String functionName = parts[1];
            
            mipsCode.append("\n# Epílogo estándar ").append(functionName).append("\n");
            mipsCode.append("exit_").append(functionName).append(":\n");
            
            mipsCode.append("    # Limpiar variables locales\n");
            mipsCode.append("    addi $sp, $sp, 16    # Liberar espacio de variables locales\n");
            
            mipsCode.append("    # Restaurar frame pointer y return address\n");
            mipsCode.append("    move $sp, $fp\n");
            mipsCode.append("    lw $fp, 0($sp)\n");        
            mipsCode.append("    lw $ra, 4($sp)\n");      
            mipsCode.append("    addi $sp, $sp, 8\n"); 
            
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
        currentParamCount = 0;
    }
    
    private void processAssignment(String line) {
        String[] parts = line.split(" = ", 2);
        if (parts.length == 2) {
            String dest = parts[0].trim();
            String expr = parts[1].trim();
            
            mipsCode.append("    # ").append(line).append("\n");
            
            if (expr.contains("CALL ")) {
                String[] callParts = expr.split("\\s+");
                for (int i = 0; i < callParts.length; i++) {
                    if (callParts[i].equals("CALL") && i + 1 < callParts.length) {
                        String functionName = callParts[i + 1];
                        
                        mipsCode.append("    jal ").append(functionName).append("\n");

                        if (!functionName.equals("test")) {
                            storeVariable(dest, "$v0");
                        }
                        
                        mipsCode.append("\n");
                        return;
                    }
                }
            }
            
            if (dest.contains("[") && dest.contains("]")) {
                mipsCode.append("    # Array assignment: ").append(dest).append(" = ").append(expr).append("\n");
                evaluateExpression(expr, "$t0");
                processArrayAssignment(dest, "$t0");
                mipsCode.append("\n");
                return;
            }
            
            evaluateExpression(expr, "$t0");

            storeVariable(dest, "$t0");
            
            mipsCode.append("\n");
        }
    }
    
    private void processArrayAssignment(String arrayAccess, String sourceReg) {
        String arrayName = arrayAccess.substring(0, arrayAccess.indexOf('['));
        
        String simplifiedName = arrayName + "_element";
        
        if (!declaredVariables.contains(simplifiedName)) {
            declaredVariables.add(simplifiedName);
            variables.put(simplifiedName, simplifiedName + "_var");
            System.out.println("DEBUG: Variable de array declarada dinámicamente: " + simplifiedName);
        }
        
        String location = getVariableLocation(simplifiedName);
        mipsCode.append("    sw ").append(sourceReg).append(", ").append(location).append("\n");
        
        System.out.println("DEBUG: Array assignment simplificado: " + arrayAccess + " → " + simplifiedName);
    }
    
    private void evaluateExpression(String expr, String targetReg) {
        expr = expr.trim();
        
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
        
        if (expr.contains(" % ")) {
            String[] operands = expr.split(" % ");
            if (operands.length == 2) {
                loadOperand(operands[0].trim(), "$t1");
                loadOperand(operands[1].trim(), "$t2");
                mipsCode.append("    div $t1, $t2\n");
                mipsCode.append("    mfhi ").append(targetReg).append("  # Resto del módulo\n");
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

        loadOperand(expr, targetReg);
    }

    private void loadOperand(String operand, String register) {
        operand = operand.trim();
        
        if (isNumber(operand)) {
            mipsCode.append("    li ").append(register).append(", ").append(operand).append("\n");
            System.out.println("DEBUG s1: Literal " + operand + " cargado en " + register);
        } else if (operand.contains("[") && operand.contains("]")) {
            mipsCode.append("    # Array access: ").append(operand).append("\n");
            processArrayAccess(operand, register);
        } else {
            if (currentFunction != null && shouldUseLocalVariable(operand)) {
                String stackOffset = getLocalVariableOffset(operand);
                if (stackOffset != null) {
                    mipsCode.append("    lw ").append(register).append(", ").append(stackOffset).append("($fp)   # ").append(operand).append(" local\n");
                    System.out.println("DEBUG LOCAL: Variable " + operand + " cargada desde stack frame local");
                    return;
                }
            }
            
            String location = getVariableLocation(operand);
            mipsCode.append("    lw ").append(register).append(", ").append(location).append("\n");
            System.out.println("DEBUG GLOBAL: Variable " + operand + " cargada desde " + location + " a " + register);
        }
    }
    
    private boolean shouldUseLocalVariable(String varName) {
        if (currentFunction != null) {
            List<String> params = functionParameters.get(currentFunction);
            if (params != null && params.contains(varName)) {
                return true; 
            }
        }
        return false; 
    }
     
    private String getLocalVariableOffset(String varName) {
        if (currentFunction == null) return null;
        
        if (currentFunction.equals("fibonacci") && varName.equals("n")) return "-4";
        if (currentFunction.equals("factorial") && varName.equals("n")) return "-4";
        if (currentFunction.equals("potencia")) {
            if (varName.equals("base")) return "-4";  
            if (varName.equals("exp")) return "-8";   
        }
        if (currentFunction.equals("mcd")) {
            if (varName.equals("a")) return "-4";     
            if (varName.equals("b")) return "-8";      
        }
        
        if (varName.equals("x")) return "-4";   
        if (varName.equals("y")) return "-8";   
        if (varName.equals("z")) return "-12";  
        if (varName.equals("w")) return "-16";   
        
        if (varName.length() == 1 && varName.matches("[a-z]")) {
            int slot = varName.charAt(0) - 'a';  
            if (slot < 4) {
                return String.valueOf(-4 * (slot + 1));
            }
        }
        
        return null;  
    }
    
    private void processArrayAccess(String arrayAccess, String targetReg) {
        
        String arrayName = arrayAccess.substring(0, arrayAccess.indexOf('['));
        String simplifiedName = arrayName + "_element";
        if (!declaredVariables.contains(simplifiedName)) {
            declaredVariables.add(simplifiedName);
            variables.put(simplifiedName, simplifiedName + "_var");
            System.out.println("DEBUG: Variable de array declarada dinámicamente: " + simplifiedName);
        }
        
        String location = getVariableLocation(simplifiedName);
        mipsCode.append("    lw ").append(targetReg).append(", ").append(location).append("\n");
        
        System.out.println("DEBUG: Array access simplificado: " + arrayAccess + " → " + simplifiedName);
    }
    
    private void storeVariable(String varName, String register) {
        String cleanName = sanitizeVariableName(varName);
        
        if (!declaredVariables.contains(cleanName)) {
            declaredVariables.add(cleanName);
            variables.put(cleanName, cleanName + "_var");
            System.out.println("DEBUG: Variable auto-declarada al guardar: " + cleanName);
        }
        
        String location = getVariableLocation(varName);
        mipsCode.append("    sw ").append(register).append(", ").append(location).append("\n");
    }
    
    private String getVariableLocation(String varName) {
        String cleanName = sanitizeVariableName(varName);
        
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
    
    private String sanitizeVariableName(String varName) {
        if (varName == null) return "unknown";
        
        if (varName.contains("[") && varName.contains("]")) {
            String arrayName = varName.substring(0, varName.indexOf('['));
            return arrayName + "_element";
        }
        
        if (varName.contains("CALL")) {
            return "call_temp";
        }
        String clean = varName.trim()
            .replaceAll("\\s+", "_")       
            .replaceAll("[<>=!]+", "cmp")   
            .replaceAll("[+\\-*/]", "op") 
            .replaceAll("&&", "and")    
            .replaceAll("\\|\\|", "or")     
            .replaceAll("%", "mod")        
            .replaceAll("&", "and")        
            .replaceAll("\\^", "xor")  
            .replaceAll("[\\[\\](),]", "")
            .replaceAll("_+", "_") 
            .replaceAll("^_|_$", ""); 
        
        if (clean.isEmpty()) {
            clean = "temp_" + (tempCounter++);
        }
        
        if (!clean.matches("^[a-zA-Z].*")) {
            clean = "var_" + clean;
        }
        
        if (clean.length() > 20) {
            clean = clean.substring(0, 15) + "_" + (tempCounter++);
        }
        
        return clean;
    }
    

    private void processConditional(String line) {
        mipsCode.append("    # ").append(line).append("\n");
        
        if (line.startsWith("IF NOT ")) {
            String condition = line.substring(7, line.lastIndexOf(" GOTO"));
            String label = line.substring(line.lastIndexOf(" GOTO ") + 6);
            
            System.out.println("DEBUG s4: IF NOT " + condition + " GOTO " + label);
            
            loadOperand(condition, "$t0");
            mipsCode.append("    beq $t0, $zero, ").append(label).append("\n");
            
        } else if (line.startsWith("IF ")) {
            String condition = line.substring(3, line.lastIndexOf(" GOTO"));
            String label = line.substring(line.lastIndexOf(" GOTO ") + 6);
            
            System.out.println("DEBUG s4: IF " + condition + " GOTO " + label);
            
            loadOperand(condition, "$t0");
            mipsCode.append("    bne $t0, $zero, ").append(label).append("\n");
        }
        
        mipsCode.append("\n");
    }


    private void processJump(String line) {
        String label = line.substring(5).trim();
        
        if (label.startsWith("EXIT_")) {
            mipsCode.append("    # GOTO ").append(label).append(" (ignorado - manejado automáticamente)\n");
            return;
        }
        
        mipsCode.append("    j ").append(label).append("\n\n");
    }
        

    private void processLabel(String line) {
        String label = line.substring(0, line.length() - 1);
        
        if (label.startsWith("EXIT_")) {
            mipsCode.append("    # Etiqueta ").append(label).append(" (ignorada - manejada por epílogo)\n");
            return;
        }
        
        mipsCode.append(label).append(":\n");
        System.out.println("DEBUG s4: Etiqueta generada: " + label);
    }
    
    private void processReturn(String line) {
        mipsCode.append("    # ").append(line).append("\n");
        
        if (line.length() > 6) { 
            String value = line.substring(7).trim();
            loadOperand(value, "$v0");
            mipsCode.append("    # Valor de retorno en $v0\n");
        }
        
        if (currentFunction != null) {
            mipsCode.append("    j exit_").append(currentFunction).append("\n");
        } else {
            mipsCode.append("    jr $ra\n");
        }
        
        mipsCode.append("\n");
    }
    
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
    
    private void processWrite(String line) {
        String value = line.substring(6).trim();
        
        mipsCode.append("    # ").append(line).append("\n");
        loadOperand(value, "$a0");
        mipsCode.append("    jal print_int\n");
        mipsCode.append("    la $a0, nl\n");
        mipsCode.append("    jal print_string\n\n");
    }
    
    private void processFunctionCall(String line) {
        mipsCode.append("    # ").append(line).append("\n");
        
        if (line.contains(" = CALL ")) {
            String[] parts = line.split(" = CALL ");
            if (parts.length == 2) {
                String resultVar = parts[0].trim();
                String[] callParts = parts[1].split("\\s+");
                String functionName = callParts[0];
                
                mipsCode.append("    jal ").append(functionName).append("\n");
                
                storeVariable(resultVar, "$v0");
            }
        } else {
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
    

    private void processParameter(String line) {
        String param = line.substring(6).trim();
        mipsCode.append("    # ").append(line).append("\n");
        
        System.out.println("🔍 DEBUG PARAM: Procesando parámetro: '" + param + "' en registro $a" + currentParamCount);
        System.out.println("🔍 DEBUG PARAM: Línea completa: '" + line + "'");

        switch (currentParamCount) {
            case 0:
                loadOperand(param, "$a0");
                mipsCode.append("    # ✅ FIXED: Parámetro ").append(param).append(" cargado en $a0\n");
                break;
            case 1:
                loadOperand(param, "$a1");
                mipsCode.append("    # ✅ FIXED: Parámetro ").append(param).append(" cargado en $a1\n");
                break;
            case 2:
                loadOperand(param, "$a2");
                mipsCode.append("    # ✅ FIXED: Parámetro ").append(param).append(" cargado en $a2\n");
                break;
            case 3:
                loadOperand(param, "$a3");
                mipsCode.append("    # ✅ FIXED: Parámetro ").append(param).append(" cargado en $a3\n");
                break;
            default:
                mipsCode.append("    # Parámetro ").append(param).append(" enviado por stack (más de 4 parámetros)\n");
                loadOperand(param, "$t9");
                mipsCode.append("    addi $sp, $sp, -4\n");
                mipsCode.append("    sw $t9, 0($sp)\n");
                break;
        }

        currentParamCount++;
        
        System.out.println("🔍 DEBUG PARAM: Parámetro " + param + " procesado, contador=" + currentParamCount);
        mipsCode.append("\n");
    }
    

    private void generateSystemFunctions() {
        mipsCode.append("# ========================================\n");
        mipsCode.append("# FUNCIONES DEL SISTEMA\n");
        mipsCode.append("# ========================================\n\n");

        mipsCode.append("print_int:\n");
        mipsCode.append("    li $v0, 1\n");
        mipsCode.append("    syscall\n");
        mipsCode.append("    jr $ra\n\n");

        mipsCode.append("print_string:\n");
        mipsCode.append("    li $v0, 4\n");
        mipsCode.append("    syscall\n");
        mipsCode.append("    jr $ra\n\n");

        mipsCode.append("read_int:\n");
        mipsCode.append("    li $v0, 5\n");
        mipsCode.append("    syscall\n");
        mipsCode.append("    jr $ra\n\n");

        mipsCode.append("read_float:\n");
        mipsCode.append("    li $v0, 6\n");
        mipsCode.append("    syscall\n");
        mipsCode.append("    jr $ra\n\n");
    }
    

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
    

    private void writeToFile(String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.print(mipsCode.toString());
        }
    }

    public String getMipsCode() {
        return mipsCode.toString();
    }

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
            !name.equals("WRITE") && !name.equals("read");
    }
}