package main.java.intermedio;

import java.io.*;
import java.util.*;

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
    
    public void emitLabel(String label) {
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
}