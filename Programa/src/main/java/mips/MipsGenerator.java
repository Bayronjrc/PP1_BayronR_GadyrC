package main.java.mips;

import java.io.*;
import java.util.*;

/**
 * Generador de Código MIPS UNIVERSAL
 * 
 * Detecta automáticamente parámetros de CUALQUIER función sin hardcodeo
 * Funciona con fibonacci, factorial, paco, abeja, funct1, etc.
 * 
 * @author Bayron Rodríguez & Gadir Calderón
 * @version 2.0 UNIVERSAL
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
    
    private Map<String, String> stringLiterals = new HashMap<>();
    private int stringCounter = 1;
    private int labelCounter = 1;
    private Set<String> floatVariables = new HashSet<>();

    
    public MipsGenerator() {
        this.mipsCode = new StringBuilder();
        this.variables = new HashMap<>();
        this.labels = new HashMap<>();
        this.declaredVariables = new HashSet<>();
        this.stringLiterals = new HashMap<>(); 
        this.stackOffset = 0;
        this.currentVarOffset = 0;
        this.tempCounter = 1;
        this.stringCounter = 1;  
        this.inFunction = false;
        this.currentFunction = null;
        this.currentParamCount = 0;
        this.labelCounter = 1; 
        this.floatVariables = new HashSet<>(); 
        
        initializeConstants();
    }
    
    private void initializeConstants() {
        declaredVariables.add("true");
        variables.put("true", "true_const");
        declaredVariables.add("false");
        variables.put("false", "false_const");
        
        declaredVariables.add("sol");
        variables.put("sol", "sol_const");  
        declaredVariables.add("luna");
        variables.put("luna", "luna_const");
    }
    
    private boolean isSystemConstant(String varName) {
        return varName.equals("true") || varName.equals("false") || 
               varName.equals("luna") || varName.equals("sol");
    }

    public void generateFromFile(String intermediateFile, String outputFile) throws IOException {
        System.out.println("=== GENERADOR DE CÓDIGO MIPS UNIVERSAL ===");
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
        System.out.println("Strings literales: " + stringLiterals.size());
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
        mipsCode.append("# CÓDIGO MIPS UNIVERSAL - SIN HARDCODEO\n");
        mipsCode.append("# Funciona con cualquier nombre de función\n");
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
        System.out.println(" UNIVERSAL: Analizando variables sin hardcodeo...");
        
        detectUniversalFunctionParameters();
        
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
                        
                        if (varType.equals("FLOAT")) {
                            floatVariables.add(varName);
                            System.out.println(" Variable FLOAT declarada: " + varName);
                        }
                        
                        System.out.println(" Variable encontrada: " + varName + " tipo " + varType);
                    }
                }
            }
            else if (line.contains(" = ") && !line.contains("CALL")) {
                String[] parts = line.split(" = ");
                if (parts.length == 2) {
                    String leftSide = parts[0].trim();
                    String rightSide = parts[1].trim();
                    
                    if (containsFloatLiteral(rightSide)) {
                        floatVariables.add(leftSide);
                        System.out.println("Variable marcada como FLOAT por literal: " + leftSide + " = " + rightSide);
                    }
                    else if (containsFloatVariable(rightSide)) {
                        floatVariables.add(leftSide);
                        System.out.println(" Variable marcada como FLOAT por propagación: " + leftSide);
                    }
                    
                    if (!declaredVariables.contains(leftSide) && isValidVariableName(leftSide)) {
                        declaredVariables.add(leftSide);
                        variables.put(leftSide, leftSide + "_var");
                        System.out.println(" Variable auto-declarada: " + leftSide);
                    }
                    
                    if (!rightSide.startsWith("\"")) {
                        String[] tokens = rightSide.split("[\\s\\+\\-\\*/<>=!]+");
                        for (String token : tokens) {
                            token = token.trim();
                            if (isValidVariableName(token) && !isNumber(token) && 
                                !token.startsWith("\"") && !token.startsWith("'") &&
                                !declaredVariables.contains(token)) {
                                declaredVariables.add(token);
                                variables.put(token, token + "_var");
                                System.out.println(" Variable en expresión auto-declarada: " + token);
                            }
                        }
                    }
                }
            }
            else if (line.startsWith("WRITE ")) {
                String var = line.substring(6).trim();
                if (isValidVariableName(var) && !declaredVariables.contains(var)) {
                    declaredVariables.add(var);
                    variables.put(var, var + "_var");
                    System.out.println(" Variable en WRITE auto-declarada: " + var);
                }
            }
        }
        
        System.out.println(" UNIVERSAL: Variables encontradas: " + declaredVariables);
        System.out.println(" UNIVERSAL: Parámetros detectados: " + functionParameters);
    }
    
    private void detectUniversalFunctionParameters() {
        System.out.println("🔍 UNIVERSAL: Detectando parámetros de CUALQUIER función...");
        
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
                    System.out.println(" DETECTED: " + functionName + " tiene " + paramCount + " parámetros");
                }
            }
        }
        
        analyzeUniversalFunctionBodies();
    }
    
    private void analyzeUniversalFunctionBodies() {
        String currentFunc = null;
        boolean inFunction = false;
        List<String> declaredInFunction = new ArrayList<>();
        
        for (int i = 0; i < intermediateCode.size(); i++) {
            String line = intermediateCode.get(i).trim();
            
            if (line.startsWith("FUNCTION ")) {
                String[] parts = line.split("\\s+");
                if (parts.length >= 2) {
                    currentFunc = parts[1];
                    inFunction = true;
                    declaredInFunction = new ArrayList<>();
                    System.out.println("🔍 ANALYZING: " + currentFunc);
                }
            } 
            else if (line.startsWith("END ") && inFunction) {
                Integer expectedParamCount = functionParamCounts.get(currentFunc);
                if (expectedParamCount != null && expectedParamCount > 0) {
                    List<String> detectedParams = detectParametersFromDeclares(
                        declaredInFunction, expectedParamCount);
                    functionParameters.put(currentFunc, detectedParams);
                    System.out.println(" " + currentFunc + " parámetros: " + detectedParams);
                }
                inFunction = false;
                currentFunc = null;
            }
            else if (inFunction && line.startsWith("DECLARE ")) {
                String[] parts = line.split("\\s+");
                if (parts.length >= 3) {
                    String varName = parts[1];
                    declaredInFunction.add(varName);
                    System.out.println(" " + currentFunc + ": DECLARE " + varName);
                }
            }
        }
    }
    
    private List<String> detectParametersFromDeclares(List<String> declares, int expectedCount) {
        List<String> params = new ArrayList<>();
        
        for (int i = 0; i < Math.min(declares.size(), expectedCount); i++) {
            params.add(declares.get(i));
        }
        
        while (params.size() < expectedCount) {
            if (expectedCount == 1) {
                params.add("param1"); 
            } else {
                params.add("param" + (params.size() + 1));
            }
        }
        
        return params;
    }
    
    private boolean containsFloatLiteral(String expr) {
        String[] tokens = expr.split("[\\s\\+\\-\\*/<>=!]+");
        for (String token : tokens) {
            token = token.trim();
            if (token.contains(".") && isNumber(token)) {
                return true;
            }
        }
        return false;
    }

    private boolean containsFloatVariable(String expr) {
        String[] tokens = expr.split("[\\s\\+\\-\\*/<>=!]+");
        for (String token : tokens) {
            token = token.trim();
            if (floatVariables.contains(token)) {
                return true;
            }
        }
        return false;
    }
    
    private void generateDataSection() {
        mipsCode.append(".data\n");
        mipsCode.append("    # Strings del sistema\n");
        mipsCode.append("    nl:           .asciiz \"\\n\"\n");
        mipsCode.append("    prompt_int:   .asciiz \"Ingrese un entero: \"\n");
        mipsCode.append("    prompt_float: .asciiz \"Ingrese un float: \"\n");
        mipsCode.append("    prompt_string: .asciiz \"Ingrese texto: \"\n");
        mipsCode.append("    result_msg:   .asciiz \"Resultado: \"\n");
        mipsCode.append("    true_str:     .asciiz \"true\"\n");
        mipsCode.append("    false_str:    .asciiz \"false\"\n");
        mipsCode.append("\n");
        
        mipsCode.append("    # Constantes booleanas del lenguaje\n");
        mipsCode.append("    true_const:   .word 1\n");
        mipsCode.append("    false_const:  .word 0\n");
        mipsCode.append("    luna_const:   .word 1    # luna = true\n");
        mipsCode.append("    sol_const:    .word 0    # sol = false\n");
        mipsCode.append("\n");
        
        if (!declaredVariables.isEmpty()) {
            mipsCode.append("    # Variables del programa\n");
            for (String varName : declaredVariables) {
                if (!isSystemConstant(varName)) {
                    mipsCode.append("    ").append(variables.get(varName)).append(": .word 0\n");
                }
            }
            mipsCode.append("\n");
        }
    }
    
    private void generateTextSection() {
        mipsCode.append(".text\n");
        mipsCode.append(".globl main\n\n");
        
        boolean hasMain = false;
        
        for (String line : intermediateCode) {
            if (line.startsWith("FUNCTION main")) {
                hasMain = true;
                break;
            }
        }
        
        if (!hasMain) {
            mipsCode.append("main:\n");
            mipsCode.append("    # Función main generada automáticamente\n");
            
            String firstFunction = null;
            for (String line : intermediateCode) {
                if (line.startsWith("FUNCTION ")) {
                    String[] parts = line.split("\\s+");
                    if (parts.length >= 2) {
                        firstFunction = parts[1];
                        break;
                    }
                }
            }
            
            if (firstFunction != null) {
                mipsCode.append("    jal ").append(firstFunction).append("\n");
            }
            
            mipsCode.append("    li $v0, 10\n");
            mipsCode.append("    syscall\n\n");
        }
        
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
                System.out.println(" RESET: Contador de parámetros reseteado a 0 para nueva llamada");
            }
        } else {
            if (expectingParameters) {
                System.out.println(" END PARAMS: Terminaron los parámetros para esta llamada");
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
        } else if (line.startsWith("read ") || line.startsWith("read\t") || line.startsWith("read(")) {
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
            mipsCode.append("    # UNIVERSAL: Prólogo para ").append(currentFunction).append("\n");
            
            mipsCode.append("    addi $sp, $sp, -8\n");     
            mipsCode.append("    sw $ra, 4($sp)\n");        
            mipsCode.append("    sw $fp, 0($sp)\n");        
            mipsCode.append("    move $fp, $sp\n");
            
            mipsCode.append("    # Reservar espacio para variables locales\n");
            mipsCode.append("    addi $sp, $sp, -16\n");  
            mipsCode.append("\n");
            
            List<String> params = functionParameters.get(currentFunction);
            if (params != null && !params.isEmpty()) {
                mipsCode.append("    # UNIVERSAL: Guardar parámetros de ").append(currentFunction).append("\n");
                
                for (int i = 0; i < Math.min(params.size(), 4); i++) {
                    String paramName = params.get(i);
                    String reg = "$a" + i;
                    int offset = -4 * (i + 1);
                    
                    mipsCode.append("    sw ").append(reg).append(", ").append(offset).append("($fp)   # ")
                           .append(paramName).append(" local\n");
                    mipsCode.append("    sw ").append(reg).append(", ").append(paramName).append("_var     # ")
                           .append(paramName).append(" global\n");
                    
                    if (!declaredVariables.contains(paramName)) {
                        declaredVariables.add(paramName);
                        variables.put(paramName, paramName + "_var");
                    }
                }
                mipsCode.append("\n");
                
                System.out.println(" UNIVERSAL: " + currentFunction + " configurado con parámetros: " + params);
            } else {
                mipsCode.append("    # Función sin parámetros o no detectados\n\n");
            }
        }
    }

    private void processFunctionEnd(String line) {
        String[] parts = line.split("\\s+");
        if (parts.length >= 2) {
            String functionName = parts[1];
            
            mipsCode.append("\n#  UNIVERSAL: Epílogo para ").append(functionName).append("\n");
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
            
            evaluateExpression(expr, "$t0");
            storeVariable(dest, "$t0");
            
            mipsCode.append("\n");
        }
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
                mipsCode.append("    mult $t1, $t2\n");
                mipsCode.append("    mflo ").append(targetReg).append("\n");
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
                int labelId = labelCounter++;
                loadOperand(operands[0].trim(), "$t1");
                loadOperand(operands[1].trim(), "$t2");
                mipsCode.append("    # ").append(operands[0]).append(" <= ").append(operands[1]).append("\n");
                mipsCode.append("    sub $t3, $t2, $t1    # t2 - t1\n");
                mipsCode.append("    bgez $t3, set_true_le_").append(labelId).append("\n");
                mipsCode.append("    li ").append(targetReg).append(", 0\n");
                mipsCode.append("    j end_le_").append(labelId).append("\n");
                mipsCode.append("set_true_le_").append(labelId).append(":\n");
                mipsCode.append("    li ").append(targetReg).append(", 1\n");
                mipsCode.append("end_le_").append(labelId).append(":\n");
                return;
            }
        }
        
        if (expr.contains(" >= ")) {
            String[] operands = expr.split(" >= ");
            if (operands.length == 2) {
                int labelId = labelCounter++;  
                loadOperand(operands[0].trim(), "$t1");
                loadOperand(operands[1].trim(), "$t2");
                mipsCode.append("    # ").append(operands[0]).append(" >= ").append(operands[1]).append("\n");
                mipsCode.append("    sub $t3, $t1, $t2    # t1 - t2\n");
                mipsCode.append("    bgez $t3, set_true_ge_").append(labelId).append("\n");
                mipsCode.append("    li ").append(targetReg).append(", 0\n");
                mipsCode.append("    j end_ge_").append(labelId).append("\n");
                mipsCode.append("set_true_ge_").append(labelId).append(":\n");
                mipsCode.append("    li ").append(targetReg).append(", 1\n");
                mipsCode.append("end_ge_").append(labelId).append(":\n");
                return;
            }
        }
        
        if (expr.contains(" < ")) {
            String[] operands = expr.split(" < ");
            if (operands.length == 2) {
                int labelId = labelCounter++; 
                loadOperand(operands[0].trim(), "$t1");
                loadOperand(operands[1].trim(), "$t2");
                mipsCode.append("    # ").append(operands[0]).append(" < ").append(operands[1]).append("\n");
                mipsCode.append("    sub $t3, $t1, $t2    # t1 - t2\n");
                mipsCode.append("    bltz $t3, set_true_lt_").append(labelId).append("\n");
                mipsCode.append("    li ").append(targetReg).append(", 0\n");
                mipsCode.append("    j end_lt_").append(labelId).append("\n");
                mipsCode.append("set_true_lt_").append(labelId).append(":\n");
                mipsCode.append("    li ").append(targetReg).append(", 1\n");
                mipsCode.append("end_lt_").append(labelId).append(":\n");
                return;
            }
        }
        
        if (expr.contains(" > ")) {
            String[] operands = expr.split(" > ");
            if (operands.length == 2) {
                int labelId = labelCounter++;  
                loadOperand(operands[0].trim(), "$t1");
                loadOperand(operands[1].trim(), "$t2");
                mipsCode.append("    # ").append(operands[0]).append(" > ").append(operands[1]).append("\n");
                mipsCode.append("    sub $t3, $t2, $t1    # t2 - t1\n");
                mipsCode.append("    bltz $t3, set_true_gt_").append(labelId).append("\n");
                mipsCode.append("    li ").append(targetReg).append(", 0\n");
                mipsCode.append("    j end_gt_").append(labelId).append("\n");
                mipsCode.append("set_true_gt_").append(labelId).append(":\n");
                mipsCode.append("    li ").append(targetReg).append(", 1\n");
                mipsCode.append("end_gt_").append(labelId).append(":\n");
                return;
            }
        }

        if (expr.contains(" && ")) {
            String[] operands = expr.split(" && ");
            if (operands.length == 2) {
                loadOperand(operands[0].trim(), "$t1");
                loadOperand(operands[1].trim(), "$t2");
                mipsCode.append("    # AND lógico\n");
                mipsCode.append("    and ").append(targetReg).append(", $t1, $t2\n");
                return;
            }
        }

        if (expr.contains(" || ")) {
            String[] operands = expr.split(" \\|\\| ");
            if (operands.length == 2) {
                loadOperand(operands[0].trim(), "$t1");
                loadOperand(operands[1].trim(), "$t2");
                mipsCode.append("    # OR lógico\n");
                mipsCode.append("    or ").append(targetReg).append(", $t1, $t2\n");
                return;
            }
        }

        if (expr.contains(" ** ")) {
            String[] operands = expr.split(" \\*\\* ");
            if (operands.length == 2) {
                loadOperand(operands[0].trim(), "$t1");
                loadOperand(operands[1].trim(), "$t2");
                mipsCode.append("    # Potencia: ").append(operands[0]).append(" ** ").append(operands[1]).append("\n");
                mipsCode.append("    move $a0, $t1\n");
                mipsCode.append("    move $a1, $t2\n");
                mipsCode.append("    jal power_function\n");
                mipsCode.append("    move ").append(targetReg).append(", $v0\n");
                return;
            }
        }
                
        if (expr.contains(" == ")) {
            String[] operands = expr.split(" == ");
            if (operands.length == 2) {
                int labelId = labelCounter++;  
                loadOperand(operands[0].trim(), "$t1");
                loadOperand(operands[1].trim(), "$t2");
                mipsCode.append("    # ").append(operands[0]).append(" == ").append(operands[1]).append("\n");
                mipsCode.append("    sub $t3, $t1, $t2    # t1 - t2\n");
                mipsCode.append("    beq $t3, $zero, set_true_eq_").append(labelId).append("\n");
                mipsCode.append("    li ").append(targetReg).append(", 0\n");
                mipsCode.append("    j end_eq_").append(labelId).append("\n");
                mipsCode.append("set_true_eq_").append(labelId).append(":\n");
                mipsCode.append("    li ").append(targetReg).append(", 1\n");
                mipsCode.append("end_eq_").append(labelId).append(":\n");
                return;
            }
        }
        
        if (expr.contains(" != ")) {
            String[] operands = expr.split(" != ");
            if (operands.length == 2) {
                int labelId = labelCounter++; 
                loadOperand(operands[0].trim(), "$t1");
                loadOperand(operands[1].trim(), "$t2");
                mipsCode.append("    # ").append(operands[0]).append(" != ").append(operands[1]).append("\n");
                mipsCode.append("    sub $t3, $t1, $t2    # t1 - t2\n");
                mipsCode.append("    bne $t3, $zero, set_true_ne_").append(labelId).append("\n");
                mipsCode.append("    li ").append(targetReg).append(", 0\n");
                mipsCode.append("    j end_ne_").append(labelId).append("\n");
                mipsCode.append("set_true_ne_").append(labelId).append(":\n");
                mipsCode.append("    li ").append(targetReg).append(", 1\n");
                mipsCode.append("end_ne_").append(labelId).append(":\n");
                return;
            }
        }

        loadOperand(expr, targetReg);
    }

    private void loadOperand(String operand, String register) {
        operand = operand.trim();
        
        if (isNumber(operand)) {
            if (operand.contains(".")) {
                try {
                    float floatVal = Float.parseFloat(operand);
                    int intRepresentation = (int) (floatVal * 100); 
                    mipsCode.append("    li ").append(register).append(", ").append(intRepresentation).append("    # Float ").append(operand).append(" (*100)\n");
                    System.out.println("DEBUG: Float " + operand + " convertido a " + intRepresentation);
                } catch (NumberFormatException e) {
                    mipsCode.append("    li ").append(register).append(", 0    # Error parsing float ").append(operand).append("\n");
                }
            } else {
                mipsCode.append("    li ").append(register).append(", ").append(operand).append("\n");
                System.out.println("DEBUG: Entero " + operand + " cargado en " + register);
            }
            return;
        }
        
        if (operand.equals("luna")) {
            mipsCode.append("    lw ").append(register).append(", luna_const    # luna = true\n");
            return;
        }
        if (operand.equals("sol")) {
            mipsCode.append("    lw ").append(register).append(", sol_const     # sol = false\n");
            return;
        }
        if (operand.equals("true")) {
            mipsCode.append("    lw ").append(register).append(", true_const\n");
            return;
        }
        if (operand.equals("false")) {
            mipsCode.append("    lw ").append(register).append(", false_const\n");
            return;
        }
        
        if (currentFunction != null && shouldUseLocalVariable(operand)) {
            String stackOffset = getUniversalLocalVariableOffset(operand);
            if (stackOffset != null) {
                mipsCode.append("    lw ").append(register).append(", ").append(stackOffset).append("($fp)   # ").append(operand).append(" local\n");
                System.out.println(" UNIVERSAL LOCAL: Variable " + operand + " cargada desde stack frame local");
                return;
            }
        }
        
        if (declaredVariables.contains(operand) || isValidVariableName(operand)) {
            String location = getVariableLocation(operand);
            mipsCode.append("    lw ").append(register).append(", ").append(location).append("\n");
            System.out.println("DEBUG GLOBAL: Variable " + operand + " cargada desde " + location + " a " + register);
            return;
        }
        
        if (operand.length() == 1 && operand.matches("[a-zA-Z!@#$%^&*()_+=]") && 
            !declaredVariables.contains(operand)) {
            int asciiValue = (int) operand.charAt(0);
            mipsCode.append("    li ").append(register).append(", ").append(asciiValue).append("    # Char '").append(operand).append("' como ASCII\n");
            System.out.println("DEBUG: Caracter simple '" + operand + "' convertido a ASCII " + asciiValue);
            return;
        }
        
        mipsCode.append("    li ").append(register).append(", 0    # ERROR: No se pudo procesar '").append(operand).append("'\n");
        System.out.println("ERROR: No se pudo procesar operando: " + operand);
    }
    
    private boolean shouldUseLocalVariable(String varName) {
        if (currentFunction != null) {
            List<String> params = functionParameters.get(currentFunction);
            if (params != null && params.contains(varName)) {
                System.out.println(" UNIVERSAL: " + varName + " es parámetro de " + currentFunction);
                return true; 
            }
        }
        return false; 
    }
     
    private String getUniversalLocalVariableOffset(String varName) {
        if (currentFunction == null) return null;
        
        List<String> params = functionParameters.get(currentFunction);
        if (params != null) {
            for (int i = 0; i < params.size(); i++) {
                if (params.get(i).equals(varName)) {
                    int offset = -4 * (i + 1);
                    System.out.println(" UNIVERSAL: " + varName + " en " + currentFunction + " -> " + offset + "($fp)");
                    return String.valueOf(offset);
                }
            }
        }
        
        return null;  
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
                System.out.println(" Variable auto-declarada en uso: " + cleanName + " -> " + location);
            }
            
            return location;
        }
        
        return variables.get(cleanName);
    }
    
    private String sanitizeVariableName(String varName) {
        if (varName == null) return "unknown";
        
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
        
        if (isFloatVariable(value)) {
            loadOperand(value, "$a0");
            mipsCode.append("    jal print_float_decimal\n");
        } else {
            loadOperand(value, "$a0");
            mipsCode.append("    jal print_int\n");
        }
        
        mipsCode.append("    la $a0, nl\n");
        mipsCode.append("    jal print_string\n\n");
    }
    
    private boolean isFloatVariable(String varName) {
        if (floatVariables.contains(varName)) {
            return true;
        }
        
        return varName.startsWith("fl") || 
            varName.equals("z") || 
            varName.contains("float") ||
            varName.matches("t\\d+") && containsFloatInTemporary(varName);
    }

    private boolean containsFloatInTemporary(String tempVar) {
        for (String line : intermediateCode) {
            if (line.startsWith(tempVar + " = ")) {
                String expr = line.split(" = ")[1].trim();
                return containsFloatLiteral(expr) || containsFloatVariable(expr);
            }
        }
        return false;
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

        switch (currentParamCount) {
            case 0:
                loadOperand(param, "$a0");
                mipsCode.append("    #  UNIVERSAL: Parámetro ").append(param).append(" cargado en $a0\n");
                break;
            case 1:
                loadOperand(param, "$a1");
                mipsCode.append("    #  UNIVERSAL: Parámetro ").append(param).append(" cargado en $a1\n");
                break;
            case 2:
                loadOperand(param, "$a2");
                mipsCode.append("    #  UNIVERSAL: Parámetro ").append(param).append(" cargado en $a2\n");
                break;
            case 3:
                loadOperand(param, "$a3");
                mipsCode.append("    #  UNIVERSAL: Parámetro ").append(param).append(" cargado en $a3\n");
                break;
            default:
                mipsCode.append("    # Parámetro ").append(param).append(" enviado por stack (más de 4 parámetros)\n");
                loadOperand(param, "$t9");
                mipsCode.append("    addi $sp, $sp, -4\n");
                mipsCode.append("    sw $t9, 0($sp)\n");
                break;
        }

        currentParamCount++;
        
        System.out.println(" DEBUG PARAM: Parámetro " + param + " procesado, contador=" + currentParamCount);
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
        
        mipsCode.append("print_float_decimal:\n");
        mipsCode.append("    # $a0 contiene el flotante multiplicado por 100\n");
        mipsCode.append("    addi $sp, $sp, -4\n");
        mipsCode.append("    sw $a0, 0($sp)\n");
        mipsCode.append("    bgez $a0, positive_float\n");
        mipsCode.append("    li $v0, 11\n");
        mipsCode.append("    li $a0, 45    # ASCII de '-'\n");
        mipsCode.append("    syscall\n");
        mipsCode.append("    lw $a0, 0($sp)\n");
        mipsCode.append("    sub $a0, $zero, $a0\n");
        mipsCode.append("positive_float:\n");
        mipsCode.append("    li $t1, 100\n");
        mipsCode.append("    div $a0, $t1\n");
        mipsCode.append("    mflo $t2\n");
        mipsCode.append("    mfhi $t3\n");
        mipsCode.append("    move $a0, $t2\n");
        mipsCode.append("    li $v0, 1\n");
        mipsCode.append("    syscall\n");
        mipsCode.append("    li $v0, 11\n");
        mipsCode.append("    li $a0, 46    # ASCII de '.'\n");
        mipsCode.append("    syscall\n");
        mipsCode.append("    bge $t3, 10, print_decimal\n");
        mipsCode.append("    li $v0, 11\n");
        mipsCode.append("    li $a0, 48    # ASCII de '0'\n");
        mipsCode.append("    syscall\n");
        mipsCode.append("print_decimal:\n");
        mipsCode.append("    move $a0, $t3\n");
        mipsCode.append("    li $v0, 1\n");
        mipsCode.append("    syscall\n");
        mipsCode.append("    addi $sp, $sp, 4\n");
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
        System.out.println("\n=== ESTADÍSTICAS GENERACIÓN MIPS UNIVERSAL ===");
        System.out.println("Líneas de código intermedio: " + (intermediateCode != null ? intermediateCode.size() : 0));
        System.out.println("Variables procesadas: " + variables.size());
        System.out.println("Funciones detectadas: " + functionParameters.size());
        System.out.println("Líneas MIPS generadas: " + mipsCode.toString().split("\n").length);
        System.out.println("=============================================\n");
    }

    private boolean isValidVariableName(String name) {
        return name != null && 
            name.matches("[a-zA-Z][a-zA-Z0-9_]*") && 
            !name.equals("IF") && !name.equals("GOTO") && 
            !name.equals("WRITE") && !name.equals("read");
    }
}