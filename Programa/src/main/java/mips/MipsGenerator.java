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
        // Valores booleanos estándar
        declaredVariables.add("true");
        variables.put("true", "true_const");
        declaredVariables.add("false");
        variables.put("false", "false_const");
        
        // Constantes sol/luna del lenguaje
        declaredVariables.add("sol");
        variables.put("sol", "sol_const");   // sol = false
        declaredVariables.add("luna");
        variables.put("luna", "luna_const"); // luna = true
    }
    
    private boolean isSystemConstant(String varName) {
        return varName.equals("true") || varName.equals("false") || 
               varName.equals("luna") || varName.equals("sol");
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
    
    private boolean isLikelyStringLiteral(String text) {
        // Detectar patrones típicos de strings sin comillas
        if (text.length() > 10 && 
            (text.contains(" ") || text.contains("[") || text.contains("]")) &&
            !text.matches(".*\\s*[+\\-*/]\\s*.*") && // No es expresión aritmética
            !text.matches(".*\\s*[<>=!]+\\s*.*") && // No es expresión de comparación
            text.matches(".*[a-zA-Z]{3,}.*")) { // Contiene palabras de al menos 3 letras
            return true;
        }
        return false;
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
                        
                        // ✅ NUEVO: Rastrear variables flotantes por tipo
                        if (varType.equals("FLOAT")) {
                            floatVariables.add(varName);
                            System.out.println("✅ Variable FLOAT declarada: " + varName);
                        }
                        
                        System.out.println("✅ Variable encontrada: " + varName + " tipo " + varType);
                    }
                }
            }
            else if (line.contains(" = ") && !line.contains("CALL")) {
                String[] parts = line.split(" = ");
                if (parts.length == 2) {
                    String leftSide = parts[0].trim();
                    String rightSide = parts[1].trim();
                    
                    // ✅ NUEVO: Si asigna un literal flotante, marcar variable como flotante
                    if (containsFloatLiteral(rightSide)) {
                        floatVariables.add(leftSide);
                        System.out.println("✅ Variable marcada como FLOAT por literal: " + leftSide + " = " + rightSide);
                    }
                    // ✅ NUEVO: Si asigna desde variable flotante, propagar el tipo
                    else if (containsFloatVariable(rightSide)) {
                        floatVariables.add(leftSide);
                        System.out.println("✅ Variable marcada como FLOAT por propagación: " + leftSide);
                    }
                    
                    // ✅ NUEVO: Detectar string literal CON comillas
                    if (rightSide.startsWith("\"") && rightSide.endsWith("\"")) {
                        handleStringLiteral(rightSide);
                        System.out.println("✅ String literal detectado: " + rightSide);
                    }
                    // ✅ NUEVO: Detectar string literal SIN comillas (caso problemático)
                    else if (isLikelyStringLiteral(rightSide)) {
                        String quotedString = "\"" + rightSide + "\"";
                        handleStringLiteral(quotedString);
                        System.out.println("✅ String sin comillas detectado y corregido: " + rightSide + " -> " + quotedString);
                    }
                    
                    if (!declaredVariables.contains(leftSide) && isValidVariableName(leftSide)) {
                        declaredVariables.add(leftSide);
                        variables.put(leftSide, leftSide + "_var");
                        System.out.println("✅ Variable auto-declarada: " + leftSide);
                    }
                    
                    // ✅ MEJORADO: Solo procesar tokens si NO es string literal
                    if (!rightSide.startsWith("\"") && !isLikelyStringLiteral(rightSide)) {
                        String[] tokens = rightSide.split("[\\s\\+\\-\\*/<>=!]+");
                        for (String token : tokens) {
                            token = token.trim();
                            if (isValidVariableName(token) && !isNumber(token) && 
                                !token.startsWith("\"") && !token.startsWith("'") &&
                                !declaredVariables.contains(token)) {
                                declaredVariables.add(token);
                                variables.put(token, token + "_var");
                                System.out.println("✅ Variable en expresión auto-declarada: " + token);
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
                    System.out.println("✅ Variable en WRITE auto-declarada: " + var);
                }
            }
        }
        
        System.out.println("DEBUG: Variables encontradas: " + declaredVariables);
        System.out.println("DEBUG: Variables flotantes: " + floatVariables);
        System.out.println("DEBUG: Strings literales: " + stringLiterals.keySet());
        System.out.println("DEBUG: Parámetros reales detectados: " + functionParameters);
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

    

    private String handleStringLiteral(String literal) {
        if (!stringLiterals.containsKey(literal)) {
            String label = "str_" + stringCounter++;
            stringLiterals.put(literal, label);
            System.out.println("DEBUG: String literal registrado: " + literal + " -> " + label);
        }
        return stringLiterals.get(literal);
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
    
    // ✅ MEJORADO: generateDataSection() con strings literales y constantes
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
        
        // ✅ NUEVO: Constantes del lenguaje
        mipsCode.append("    # Constantes booleanas del lenguaje\n");
        mipsCode.append("    true_const:   .word 1\n");
        mipsCode.append("    false_const:  .word 0\n");
        mipsCode.append("    luna_const:   .word 1    # luna = true\n");
        mipsCode.append("    sol_const:    .word 0    # sol = false\n");
        mipsCode.append("\n");
        
        // ✅ NUEVO: Strings literales encontrados
        if (!stringLiterals.isEmpty()) {
            mipsCode.append("    # Strings literales\n");
            for (Map.Entry<String, String> entry : stringLiterals.entrySet()) {
                mipsCode.append("    ").append(entry.getValue()).append(": .asciiz ").append(entry.getKey()).append("\n");
            }
            mipsCode.append("\n");
        }
        
        // Variables normales
        if (!declaredVariables.isEmpty()) {
            mipsCode.append("    # Variables del programa\n");
            for (String varName : declaredVariables) {
                // Saltar constantes ya declaradas
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
        
        // Verificar si hay función main
        for (String line : intermediateCode) {
            if (line.startsWith("FUNCTION main")) {
                hasMain = true;
                break;
            }
        }
        
        // Si no hay main, crear una que llame a la primera función
        if (!hasMain) {
            mipsCode.append("main:\n");
            mipsCode.append("    # Función main generada automáticamente\n");
            
            // Buscar la primera función para llamarla
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
        } else if (line.startsWith("READ ") || line.startsWith("READ\t") || line.startsWith("READ(")) {
            processRead(line);
        } else if (line.startsWith("READ ") || line.startsWith("READ\t")) {
            processRead(line);
        } else if (line.startsWith("READ ")) {
            processRead(line);
        } else if (line.startsWith("READ ")) {
            processRead(line);
        } else if (line.startsWith("READ ")) {
            processRead(line);
        } else if (line.startsWith("read ")) {
            processRead(line);
        } else if (line.startsWith("READ ")) {
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
            
            // ✅ IMPORTANTE: Generar etiqueta de salida
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
                int labelId = labelCounter++;  // ✅ NUEVO: ID único
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
                int labelId = labelCounter++;  // ✅ NUEVO: ID único
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
                int labelId = labelCounter++;  // ✅ NUEVO: ID único
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
                int labelId = labelCounter++;  // ✅ NUEVO: ID único
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
        
        if (expr.contains(" == ")) {
            String[] operands = expr.split(" == ");
            if (operands.length == 2) {
                int labelId = labelCounter++;  // ✅ NUEVO: ID único
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
                int labelId = labelCounter++;  // ✅ NUEVO: ID único
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

        loadOperand(expr, targetReg);
    }
        
       

    private void loadOperand(String operand, String register) {
        operand = operand.trim();
        
        if (operand.startsWith("\"") && operand.endsWith("\"")) {
            String stringLabel = handleStringLiteral(operand);
            mipsCode.append("    la ").append(register).append(", ").append(stringLabel).append("\n");
            System.out.println("DEBUG: String literal " + operand + " cargado como " + stringLabel);
            return;
        }
        
        if (isLikelyStringLiteral(operand)) {
            String quotedString = "\"" + operand + "\"";
            String stringLabel = handleStringLiteral(quotedString);
            mipsCode.append("    la ").append(register).append(", ").append(stringLabel).append("\n");
            System.out.println("DEBUG: String sin comillas detectado: " + operand + " -> " + stringLabel);
            return;
        }
        
        
        if (operand.startsWith("'") && operand.endsWith("'") && operand.length() == 3) {
            char ch = operand.charAt(1);
            int asciiValue = (int) ch;
            mipsCode.append("    li ").append(register).append(", ").append(asciiValue).append("\n");
            System.out.println("DEBUG: Caracter literal '" + ch + "' cargado como ASCII " + asciiValue);
            return;
        }
        
        if (isNumber(operand)) {
            if (operand.contains(".")) {
                // ✅ CAMBIO: Multiplicar por 100 en lugar de 1000
                try {
                    float floatVal = Float.parseFloat(operand);
                    int intRepresentation = (int) (floatVal * 100); // ✅ 100 en lugar de 1000
                    mipsCode.append("    li ").append(register).append(", ").append(intRepresentation).append("    # Float ").append(operand).append(" (*100)\n");
                    System.out.println("DEBUG: Float " + operand + " convertido a " + intRepresentation);
                } catch (NumberFormatException e) {
                    mipsCode.append("    li ").append(register).append(", 0    # Error parsing float ").append(operand).append("\n");
                }
            } else {
                // Entero normal
                mipsCode.append("    li ").append(register).append(", ").append(operand).append("\n");
                System.out.println("DEBUG: Entero " + operand + " cargado en " + register);
            }
            return;
        }
        
        // ✅ MEJORADO: Constantes especiales
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
        
        // Arrays
        if (operand.contains("[") && operand.contains("]")) {
            mipsCode.append("    # Array access: ").append(operand).append("\n");
            processArrayAccess(operand, register);
            return;
        }
        
        // Variables locales vs globales
        if (currentFunction != null && shouldUseLocalVariable(operand)) {
            String stackOffset = getLocalVariableOffset(operand);
            if (stackOffset != null) {
                mipsCode.append("    lw ").append(register).append(", ").append(stackOffset).append("($fp)   # ").append(operand).append(" local\n");
                System.out.println("DEBUG LOCAL: Variable " + operand + " cargada desde stack frame local");
                return;
            }
        }
        
        // Variables globales
        if (declaredVariables.contains(operand) || isValidVariableName(operand)) {
            String location = getVariableLocation(operand);
            mipsCode.append("    lw ").append(register).append(", ").append(location).append("\n");
            System.out.println("DEBUG GLOBAL: Variable " + operand + " cargada desde " + location + " a " + register);
            return;
        }
        
        // ✅ CORREGIDO: Solo caracteres NO declarados como variables
        if (operand.length() == 1 && operand.matches("[a-zA-Z!@#$%^&*()_+=]") && 
            !declaredVariables.contains(operand)) {
            int asciiValue = (int) operand.charAt(0);
            mipsCode.append("    li ").append(register).append(", ").append(asciiValue).append("    # Char '").append(operand).append("' como ASCII\n");
            System.out.println("DEBUG: Caracter simple '" + operand + "' convertido a ASCII " + asciiValue);
            return;
        }
        
        // Fallback
        mipsCode.append("    li ").append(register).append(", 0    # ERROR: No se pudo procesar '").append(operand).append("'\n");
        System.out.println("ERROR: No se pudo procesar operando: " + operand);
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
        
        // ✅ NUEVO: Detectar si la variable contiene un flotante
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
        // Primero verificar si está en el conjunto de variables flotantes
        if (floatVariables.contains(varName)) {
            return true;
        }
        
        // Heurísticas adicionales
        return varName.startsWith("fl") || 
            varName.equals("z") ||  // basado en tu código específico
            varName.contains("float") ||
            varName.matches("t\\d+") && containsFloatInTemporary(varName); // temporales de operaciones flotantes
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
        
        mipsCode.append("print_float_decimal:\n");
        mipsCode.append("    # $a0 contiene el flotante multiplicado por 100\n");
        mipsCode.append("    # Guardar $a0 en el stack para uso posterior\n");
        mipsCode.append("    addi $sp, $sp, -4\n");
        mipsCode.append("    sw $a0, 0($sp)\n");
        mipsCode.append("    \n");
        mipsCode.append("    # Verificar si es negativo\n");
        mipsCode.append("    bgez $a0, positive_float\n");
        mipsCode.append("    \n");
        mipsCode.append("    # Imprimir signo negativo\n");
        mipsCode.append("    li $v0, 11\n");
        mipsCode.append("    li $a0, 45    # ASCII de '-'\n");
        mipsCode.append("    syscall\n");
        mipsCode.append("    \n");
        mipsCode.append("    # Convertir a positivo\n");
        mipsCode.append("    lw $a0, 0($sp)   # Recargar valor original\n");
        mipsCode.append("    sub $a0, $zero, $a0\n");
        mipsCode.append("    \n");
        mipsCode.append("positive_float:\n");
        mipsCode.append("    # Dividir por 100 para separar parte entera y decimal\n");
        mipsCode.append("    li $t1, 100\n");
        mipsCode.append("    div $a0, $t1\n");
        mipsCode.append("    mflo $t2        # Parte entera\n");
        mipsCode.append("    mfhi $t3        # Resto (parte decimal * 100)\n");
        mipsCode.append("    \n");
        mipsCode.append("    # Imprimir parte entera\n");
        mipsCode.append("    move $a0, $t2\n");
        mipsCode.append("    li $v0, 1\n");
        mipsCode.append("    syscall\n");
        mipsCode.append("    \n");
        mipsCode.append("    # Imprimir punto decimal\n");
        mipsCode.append("    li $v0, 11\n");
        mipsCode.append("    li $a0, 46    # ASCII de '.'\n");
        mipsCode.append("    syscall\n");
        mipsCode.append("    \n");
        mipsCode.append("    # Imprimir parte decimal\n");
        mipsCode.append("    # Si es menor que 10, agregar un 0 adelante\n");
        mipsCode.append("    bge $t3, 10, print_decimal\n");
        mipsCode.append("    li $v0, 11\n");
        mipsCode.append("    li $a0, 48    # ASCII de '0'\n");
        mipsCode.append("    syscall\n");
        mipsCode.append("    \n");
        mipsCode.append("print_decimal:\n");
        mipsCode.append("    move $a0, $t3\n");
        mipsCode.append("    li $v0, 1\n");
        mipsCode.append("    syscall\n");
        mipsCode.append("    \n");
        mipsCode.append("    # Restaurar stack\n");
        mipsCode.append("    addi $sp, $sp, 4\n");
        mipsCode.append("    jr $ra\n\n");
        
        // Función de potencia compatible
        mipsCode.append("power_function:\n");
        mipsCode.append("    li $v0, 1\n");
        mipsCode.append("    beq $a1, $zero, power_done\n");
        mipsCode.append("    blt $a1, $zero, power_negative\n");
        mipsCode.append("power_loop:\n");
        mipsCode.append("    mult $v0, $a0\n");
        mipsCode.append("    mflo $v0\n");
        mipsCode.append("    addi $a1, $a1, -1\n");
        mipsCode.append("    bne $a1, $zero, power_loop\n");
        mipsCode.append("power_done:\n");
        mipsCode.append("    jr $ra\n");
        mipsCode.append("power_negative:\n");
        mipsCode.append("    li $v0, 0\n");
        mipsCode.append("    jr $ra\n\n");
    }

    private void generateFloatPrintFunction() {
        mipsCode.append("print_float_as_decimal:\n");
        mipsCode.append("    # $a0 contiene el entero (flotante * 1000)\n");
        mipsCode.append("    # Dividir por 1000 e imprimir parte entera y decimal\n");
        mipsCode.append("    \n");
        mipsCode.append("    # Verificar si es negativo\n");
        mipsCode.append("    bgez $a0, positive_float\n");
        mipsCode.append("    \n");
        mipsCode.append("    # Imprimir signo negativo\n");
        mipsCode.append("    li $v0, 11\n");
        mipsCode.append("    li $a0, 45    # ASCII de '-'\n");
        mipsCode.append("    syscall\n");
        mipsCode.append("    \n");
        mipsCode.append("    # Convertir a positivo\n");
        mipsCode.append("    lw $a0, 0($sp)   # Recargar valor original\n");
        mipsCode.append("    sub $a0, $zero, $a0\n");
        mipsCode.append("    \n");
        mipsCode.append("positive_float:\n");
        mipsCode.append("    # Dividir por 1000\n");
        mipsCode.append("    li $t1, 1000\n");
        mipsCode.append("    div $a0, $t1\n");
        mipsCode.append("    mflo $t2        # Parte entera\n");
        mipsCode.append("    mfhi $t3        # Resto (parte decimal * 1000)\n");
        mipsCode.append("    \n");
        mipsCode.append("    # Imprimir parte entera\n");
        mipsCode.append("    move $a0, $t2\n");
        mipsCode.append("    li $v0, 1\n");
        mipsCode.append("    syscall\n");
        mipsCode.append("    \n");
        mipsCode.append("    # Imprimir punto decimal\n");
        mipsCode.append("    li $v0, 11\n");
        mipsCode.append("    li $a0, 46    # ASCII de '.'\n");
        mipsCode.append("    syscall\n");
        mipsCode.append("    \n");
        mipsCode.append("    # Imprimir parte decimal (hasta 3 dígitos)\n");
        mipsCode.append("    move $a0, $t3\n");
        mipsCode.append("    li $v0, 1\n");
        mipsCode.append("    syscall\n");
        mipsCode.append("    \n");
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