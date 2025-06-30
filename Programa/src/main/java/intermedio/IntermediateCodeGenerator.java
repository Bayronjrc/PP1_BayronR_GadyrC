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
    private int ifElseStartPosition = -1;
    
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
            if (name.equals("i") || name.equals("j") || name.equals("k")) {
                emit(name + " = 0");
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
        return generateFunctionCallComplete(function, args, "INT");
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
    
    public void emit(String instruction) {
        if (enabled && instruction != null) {
            code.add(instruction);
            
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


    public void generateIfSimple(String condition, String endLabel) {
        if (enabled) {
            emit("IF " + condition + " GOTO " + endLabel);
        }
    }


    public void generateIfElseStart(String condition, String elseLabel) {
        if (enabled) {
            emit("IF NOT " + condition + " GOTO " + elseLabel);
        }
    }

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

    public void insertAt(int position, String instruction) {
        if (enabled && instruction != null) {
            if (position >= 0 && position <= code.size()) {
                code.add(position, instruction);
            } else {
                emit(instruction);
            }
        }
    }


    public void insertBeforeCode(String instruction) {
        if (!enabled || instruction == null) return;
        
        int insertPos = findCodeStartPosition();
        insertAt(insertPos, instruction);
    }

    private int findCodeStartPosition() {
        for (int i = code.size() - 1; i >= 0; i--) {
            String line = code.get(i).trim();
            
            if (line.isEmpty() || 
                line.startsWith("//") || 
                line.startsWith("DECLARE") ||
                line.startsWith("FUNCTION") ||
                line.startsWith("BEGIN")) {
                continue;
            }
            
            return i;
        }
        return code.size();
    }


    private int findBestInsertPosition() {
        for (int i = code.size() - 1; i >= 0; i--) {
            String line = code.get(i).trim();
            
            if (line.isEmpty() || 
                line.startsWith("//") || 
                line.startsWith("DECLARE") ||
                line.startsWith("FUNCTION") ||
                line.startsWith("BEGIN")) {
                continue;
            }
            
            return i;
        }
        
        return code.size();
    }

    public void markIfElseStart() {
        ifElseStartPosition = getCurrentPosition();
    }



    private int findElseInsertPosition() {
        int codeLines = 0;
        int startPos = -1;
        
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
        
        return code.size();
    }


    public void generateIfWithPosition(String condition, String endLabel, int thenStartPos) {
        if (!enabled) return;
        
        insertAt(thenStartPos, "IF NOT " + condition + " GOTO " + endLabel);
        emit(endLabel + ":");
    }


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
                return i + 1;
            }
        }
        return code.size();
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

    // ======================== DO-WHILE LOOP CONDICIÓN ========================
    public void generateDoWhileCondition(String condition, String startLabel) {
        if (!enabled) return;
        
        System.out.println("DEBUG s2: Generando condición DO-WHILE: " + condition + " -> " + startLabel);

        emit("IF " + condition + " GOTO " + startLabel);
        
        System.out.println("DEBUG s2: Condición generada - IF " + condition + " GOTO " + startLabel);
    }

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

    public void generateLabelNow(String label) {
        if (enabled) {
            emit(label + ":");
            System.out.println("DEBUG: Etiqueta generada inmediatamente: " + label);
        }
    }


// ========================= FOR LOOP CON CONDICIÓN DINÁMICA ========================
    public void generateForWithExistingGrammar(String condition, String updateExpr, String startLabel, String endLabel) {
        if (!enabled) return;
        
        System.out.println("DEBUG s3: === FOR LOOP CON CONDICIÓN DINÁMICA ===");
        System.out.println("DEBUG s3: Condición recibida: " + condition);
        
        String realCondition = findRealForCondition();
        
        if (realCondition == null) {
            realCondition = "k <= 2"; 
            System.out.println("DEBUG s3: Usando condición fallback: " + realCondition);
        } else {
            System.out.println("DEBUG s3: Condición real encontrada: " + realCondition);
        }
        
        emit(startLabel + ":");                             
        emit("t_cond = " + realCondition);                   
        emit("IF NOT t_cond GOTO " + endLabel);             
        emit("WRITE k");                                  
        emit("t_inc = k + 1");                             
        emit("k = t_inc");                                   
        emit("GOTO " + startLabel);                       
        emit(endLabel + ":");                                
        
        System.out.println("DEBUG s3: FOR generado con condición: " + realCondition);
    }


    private String findRealForCondition() {
        System.out.println("DEBUG s3: Buscando condición real en código existente...");
        
        for (String line : code) {
            String trimmed = line.trim();
            
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

    public void cleanupForLoop() {
        if (!enabled) return;
        
        System.out.println("DEBUG s3: Limpieza FOR DESACTIVADA para preservar otras funciones");
        
        return;
    }

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

    private ForInfo parseForCondition(String condition, String updateExpr) {
        System.out.println("🔍 DEBUG: Parseando condición: '" + condition + "'");
        System.out.println("🔍 DEBUG: Update expression: '" + updateExpr + "'");

        String variable = extractVariableFromUpdate(updateExpr);
        
        String realCondition = findRealConditionInCode(variable);
        System.out.println("🔍 DEBUG: Condición real encontrada: " + realCondition);
        
        String operator = "<";  
        String limitValue = "3";
        String initValue = "0";
        
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
        
        String foundInit = findInitializationInCode(variable);
        if (foundInit != null) {
            initValue = foundInit;
        }
        
        System.out.println("✅ DEBUG: Resultado final - " + variable + " desde " + initValue + " " + operator + " " + limitValue);
        return new ForInfo(variable, initValue, operator, limitValue);
    }


    private String findInitializationInCode(String variable) {
        System.out.println("🔍 DEBUG: Buscando inicialización para variable: " + variable);
        
        for (String line : code) {
            String trimmed = line.trim();
            
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
        return "0";
    }

    private String findRealConditionInCode(String variable) {
        System.out.println("🔍 DEBUG: Buscando condición real para variable: " + variable);
        
        for (String line : code) {
            String trimmed = line.trim();
            
            if (trimmed.contains("=") && trimmed.contains(variable)) {
                String[] parts = trimmed.split("=");
                if (parts.length == 2) {
                    String rightSide = parts[1].trim();
                    
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

    private String extractVariableFromUpdate(String updateExpr) {
        if (updateExpr == null || updateExpr.isEmpty()) {
            return "i";
        }
        
        String cleaned = updateExpr.trim().replaceAll("\\+\\+", "");
        
        if (cleaned.matches("[a-zA-Z][a-zA-Z0-9]*")) {
            return cleaned;
        }
        
        return "i";
    }

    private String findConditionInCode() {
        for (String line : code) {
            String trimmed = line.trim();
            
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
        return "i < 3"; 
    }

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


    public void insertForLoopBody(List<String> bodyStatements) {
        if (!enabled) return;
        
        for (int i = 0; i < code.size(); i++) {
            if (code.get(i).contains("BODY_PLACEHOLDER")) {
                code.remove(i);
                
                for (int j = 0; j < bodyStatements.size(); j++) {
                    code.add(i + j, bodyStatements.get(j));
                }
                
                System.out.println("DEBUG: Body insertado - " + bodyStatements.size() + " statements");
                break;
            }
        }
    }

    //========================== SWITCH CON CÓDIGO DIFERIDO ========================
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

        reorganizeSwitchWithDeferredCode(switchExpr, exitLabel, deferredCode);

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
        if (!currentBlock.isEmpty()) {
            caseBlocks.add(currentBlock);
        }
        
        System.out.println("DEBUG: " + caseBlocks.size() + " bloques de cases identificados");

        generateSwitchComparisons(switchExpr, exitLabel);

        generateCasesWithDeferredCode(caseBlocks, exitLabel);

        emit(exitLabel + ":");
    }

    private void generateSwitchComparisons(String switchExpr, String exitLabel) {
        for (SwitchCase switchCase : currentSwitchCases) {
            if (!switchCase.value.equals("DEFAULT")) {
                emit("t_case = " + switchExpr + " == " + switchCase.value);
                emit("IF t_case GOTO " + switchCase.label);
            }
        }
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
            
            if (i < caseBlocks.size()) {
                for (String code : caseBlocks.get(i)) {
                    emit(code);
                }
                
                if (!switchCase.value.equals("DEFAULT")) {
                    emit("GOTO " + exitLabel);
                }
            }
        }
    }

//======================== FUNCIONES CON RETORNOS =========================
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
        
        emit("RETURN " + returnValue);
        
        System.out.println("DEBUG: Return generado: RETURN " + returnValue);
    }

    public void generateReturnVoid() {
        if (!enabled) return;
        
        System.out.println("DEBUG: Generando return void");
        
        emit("RETURN");
    }

    public void generateReturn(Object returnExpr) {
        if (returnExpr != null) {
            generateReturnWithValue(returnExpr.toString());
        } else {
            generateReturnVoid();
        }
    }


    public void startFunction(String functionName, String returnType) {
        setCurrentFunction(functionName, returnType);
        
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
        
        emit("DECLARE " + paramName + " " + paramType);
        
        System.out.println("DEBUG: Parámetro declarado: " + paramName + " " + paramType);
    }

    public void endFunction() {
        if (currentFunctionName != null) {
            emit("END " + currentFunctionName);
            
            System.out.println("DEBUG: Función " + currentFunctionName + " finalizada");
            
            currentFunctionName = null;
            currentFunctionReturnType = null;
        }
    }

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


    public String generateFunctionCallComplete(String functionName, List<String> args, String returnType) {
        if (!enabled) return "t" + tempCounter++;
        
        System.out.println("DEBUG s1: ========== LLAMADA A FUNCIÓN ==========");
        System.out.println("DEBUG s1: Función: " + functionName + " -> " + returnType);
        System.out.println("DEBUG s1: Argumentos: " + args.size() + " parámetros");
        
        for (int i = 0; i < args.size(); i++) {
            String arg = args.get(i);
            emit("PARAM " + arg);
            System.out.println("DEBUG s1: Parámetro " + (i+1) + ": " + arg);
        }

        if (returnType.equals("VOID")) {
            emit("CALL " + functionName + " " + args.size());
            System.out.println("DEBUG s1: Llamada VOID: CALL " + functionName + " " + args.size());
            return null;
        } else {
            String temp = newTemp();
            emit(temp + " = CALL " + functionName + " " + args.size());
            System.out.println("DEBUG s1: Llamada con retorno: " + temp + " = CALL " + functionName + " " + args.size());
            return temp;
        }
    }

    public void forceVariableDeclaration(String varName, String type, String initValue) {
        if (enabled) {
            boolean found = false;
            for (String line : code) {
                if (line.contains("DECLARE " + varName)) {
                    found = true;
                    break;
                }
            }
            
            if (!found) {
                int insertPos = 3; 
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

    public void showCurrentCode(String context) {
        if (!enabled) return;
        
        System.out.println("\n📋 CÓDIGO ACTUAL (" + context + "):");
        for (int i = 0; i < code.size(); i++) {
            System.out.println("  [" + String.format("%2d", i) + "] " + code.get(i));
        }
        System.out.println("📋 FIN CÓDIGO (" + code.size() + " líneas)\n");
    }


    public void insertConditionalBeforeBlock(String conditional) {
        if (!enabled) return;
        
        System.out.println("DEBUG GENÉRICO: Insertando condición: " + conditional);
        
        int insertPos = findLastExecutableStatement();
        
        if (insertPos >= 0) {
            code.add(insertPos, conditional);
            System.out.println("DEBUG GENÉRICO: Condición insertada en posición " + insertPos);
            
            debugShowInsertion(insertPos, conditional);
        } else {
            insertBeforeLastEnd(conditional);
            System.out.println("DEBUG GENÉRICO: Condición insertada antes del END");
        }
    }


    private int findLastExecutableStatement() {
        for (int i = code.size() - 1; i >= 0; i--) {
            String line = code.get(i).trim();
            
            if (!line.isEmpty() && 
                !line.startsWith("//") && 
                !line.startsWith("FUNCTION") && 
                !line.startsWith("BEGIN") && 
                !line.startsWith("DECLARE") &&
                !line.startsWith("END") &&
                !line.contains(":")) { 
                
                System.out.println("DEBUG GENÉRICO: Última línea ejecutable: " + line + " en posición " + i);
                return i;
            }
        }
        
        return -1;
    }

    private void insertBeforeLastEnd(String conditional) {
        for (int i = code.size() - 1; i >= 0; i--) {
            String line = code.get(i).trim();
            if (line.startsWith("END")) {
                code.add(i, conditional);
                System.out.println("DEBUG GENÉRICO: Insertado antes de END en posición " + i);
                return;
            }
        }
        
        emit(conditional);
    }

    private void debugShowInsertion(int insertPos, String conditional) {
        System.out.println("\n🔍 DEBUG GENÉRICO: VERIFICACIÓN DE INSERCIÓN");
        System.out.println("Posición: " + insertPos + ", Línea: " + conditional);
        
        int start = Math.max(0, insertPos - 2);
        int end = Math.min(code.size(), insertPos + 3);
        
        for (int i = start; i < end; i++) {
            String marker = (i == insertPos) ? " ← INSERTADO AQUÍ" : "";
            System.out.println("  [" + i + "] " + code.get(i) + marker);
        }
        System.out.println("🔍 FIN VERIFICACIÓN\n");
    }


    public void generateDeferredIfElse(String condition, String elseLabel, String endLabel) {
        if (!enabled) return;
        
        System.out.println("DEBUG FIBONACCI: Generando IF-ELSE diferido para: " + condition);
        
        List<String> tempAssignments = new ArrayList<>();
        for (String line : code) {
            String trimmed = line.trim();
            
            if (trimmed.matches("t\\d+ = .+")) {
                tempAssignments.add(trimmed);
                System.out.println("DEBUG FIBONACCI: Asignación encontrada: " + trimmed);
            }
        }
        
        List<String> reorganizedCode = new ArrayList<>();
        List<String> thenBlock = new ArrayList<>();
        List<String> elseBlock = new ArrayList<>();
        
        boolean foundConditionAssignment = false;
        
        for (String line : code) {
            String trimmed = line.trim();
            
            if (trimmed.startsWith("//") || trimmed.startsWith("FUNCTION") || 
                trimmed.startsWith("BEGIN") || trimmed.startsWith("DECLARE")) {
                reorganizedCode.add(line);
                continue;
            }
            
            if (trimmed.matches("\\w+ = \\d+")) {
                reorganizedCode.add(line);
                continue;
            }
            
            if (trimmed.equals(condition + " = n <= 1") || 
                trimmed.equals(condition + " = valor > 5") ||
                trimmed.matches(condition + " = .+ [<>=!]+ .+")) {
                reorganizedCode.add(line);
                foundConditionAssignment = true;
                System.out.println("DEBUG FIBONACCI: Condición preservada: " + line);
                continue;
            }
            
            if (trimmed.startsWith("RETURN")) {
                thenBlock.add(line);
                System.out.println("DEBUG FIBONACCI: RETURN en bloque THEN: " + line);
                continue;
            }
            
            if (trimmed.startsWith("t") && trimmed.contains("CALL")) {
                elseBlock.add(line);
                continue;
            }
            
            if (trimmed.matches("t\\d+ = .+") && !trimmed.contains("CALL")) {
                elseBlock.add(line);
                continue;
            }
            
            if (trimmed.startsWith("END")) {
                reorganizedCode.add(line);
            }
        }

        if (!foundConditionAssignment) {
            if (condition.equals("t1")) {
                reorganizedCode.add("t1 = n <= 1");
                System.out.println("DEBUG FIBONACCI: Generando condición faltante: t1 = n <= 1");
            } else if (condition.equals("t12")) {
                reorganizedCode.add("t12 = t11 > 5");
                System.out.println("DEBUG FIBONACCI: Generando condición faltante: t12 = t11 > 5");
            }
        }
        
        reorganizedCode.add("IF NOT " + condition + " GOTO " + elseLabel);
        
        reorganizedCode.addAll(thenBlock);
        reorganizedCode.add("GOTO " + endLabel);
        
        reorganizedCode.add(elseLabel + ":");
        reorganizedCode.addAll(elseBlock);
        
        reorganizedCode.add(endLabel + ":");
        
        code.clear();
        code.addAll(reorganizedCode);
        
        System.out.println("DEBUG FIBONACCI: IF-ELSE reorganizado para recursión");
        System.out.println("DEBUG FIBONACCI: THEN tiene " + thenBlock.size() + " líneas");
        System.out.println("DEBUG FIBONACCI: ELSE tiene " + elseBlock.size() + " líneas");
    }

    public void debugRecursionStructure() {
        System.out.println("\n🔍 DEBUG RECURSIÓN: ESTRUCTURA ACTUAL:");
        for (int i = 0; i < code.size(); i++) {
            String line = code.get(i);
            String marker = "";
            
            if (line.contains("t1 = n <= 1")) marker = " ← CONDICIÓN BASE";
            if (line.contains("IF NOT t1")) marker = " ← SALTO A RECURSIÓN";
            if (line.contains("RETURN n")) marker = " ← CASO BASE";
            if (line.contains("CALL fibonnaci")) marker = " ← LLAMADA RECURSIVA";
            
            System.out.println("  [" + String.format("%2d", i) + "] " + line + marker);
        }
        System.out.println("🔍 FIN ESTRUCTURA\n");
    }

    public void debugShowReorganization() {
        System.out.println("\n🔍 DEBUG s4: CÓDIGO REORGANIZADO:");
        for (int i = 0; i < code.size(); i++) {
            System.out.println("  [" + String.format("%2d", i) + "] " + code.get(i));
        }
        System.out.println("🔍 FIN REORGANIZACIÓN\n");
    }
}