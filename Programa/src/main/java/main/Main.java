package main.java.main;

import java.io.*;
import main.java.lexer.Scanner;
import main.java.parser.parser;
import main.java.parser.sym;
import main.java.symbol.SymbolTable;
import main.java.symbol.SymbolInfo;
import main.java.mips.MipsGenerator;
import main.java.intermedio.IntermediateCodeGenerator;
import java_cup.runtime.Symbol;

/**
 * Main - Preserva análisis semántico + código intermedio opcional
 * 
 * 
 * @author Bayron Rodríguez & Gadir Calderón
 * @version 2.0 - No invasiva
 */
public class Main {
    
    public static void main(String[] args) {
        try {
            if (args.length < 1 || args.length > 2) {
                printUsageUpdated();
                return;
            }
            
            String sourceFile = args[0];
            String mode = args.length > 1 ? args[1].toLowerCase() : "semantic";
            
            System.out.println("=== COMPILADOR - PROYECTO 3 ===");
            System.out.println("Autores: Bayron Rodríguez & Gadir Calderón");
            System.out.println("Archivo: " + sourceFile);
            System.out.println("Modo: " + mode.toUpperCase());
            System.out.println();
            
            // Verificar que el archivo existe
            File file = new File(sourceFile);
            if (!file.exists()) {
                System.err.println("ERROR: El archivo '" + sourceFile + "' no existe.");
                return;
            }
            
            // En tu método main(), el switch debería ser:
            switch (mode) {
                case "semantic":
                    runSemanticOnly(sourceFile);
                    break;
                case "code":
                    runCodeGenerationPass(sourceFile);
                    break;
                case "mips":           // ← ESTE CASE DEBE EXISTIR
                    runMipsGeneration(sourceFile);
                    break;
                case "both":
                    runBothSeparately(sourceFile);
                    break;
                case "full":
                    runFullAnalysis(sourceFile);
                    break;
                case "complete":
                default:
                    runCompleteCompilation(sourceFile);
                    break;
            }
            
        } catch (Exception e) {
            System.err.println("Error durante la compilación: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void printUsageUpdated() {
    System.out.println("=== COMPILADOR - PROYECTO 3 ===");
    System.out.println("Uso: java Main <archivo_fuente> [modo]");
    System.out.println();
    System.out.println("Modos disponibles:");
    System.out.println("  semantic  - Solo análisis semántico");
    System.out.println("  code      - Solo generación de código intermedio");
    System.out.println("  mips      - Código intermedio + MIPS");
    System.out.println("  both      - Semántico + código por separado");
    System.out.println("  full      - Análisis completo en una pasada");
    System.out.println("  complete  - Compilación completa (DEFAULT)");
    System.out.println();
    System.out.println("Ejemplos:");
    System.out.println("  java Main programa.txt           # Compilación completa");
    System.out.println("  java Main programa.txt mips      # Solo generar MIPS");
    System.out.println("  java Main programa.txt semantic  # Solo análisis semántico");
}
    
    /**
     * MODO 1: Solo análisis semántico
     */
    private static void runSemanticOnly(String sourceFile) throws Exception {
        System.out.println("=== MODO: SOLO ANÁLISIS SEMÁNTICO ===");
        System.out.println("Usando tu implementación original que ya funciona");
        System.out.println();
        
        runOriginalAnalysis(sourceFile);
        
        System.out.println();
        System.out.println("✓ Análisis semántico completado (sin modificaciones)");
        System.out.println("📁 Archivos generados:");
        System.out.println("  - " + getTokenFile(sourceFile) + " (tokens)");
        System.out.println("  - " + getSymbolFile(sourceFile) + " (símbolos)");
        System.out.println("  - semantic_analysis.txt (análisis semántico)");
    }
    
    /**
     * MODO 2: Solo generación de código intermedio
     */
    private static void runCodeGenerationPass(String sourceFile) throws Exception {
        System.out.println("=== MODO: SOLO CÓDIGO INTERMEDIO ===");
        System.out.println();
        
        String outputFile = getIntermediateFile(sourceFile);
        IntermediateCodeGenerator codeGen = new IntermediateCodeGenerator(outputFile);
        
        generateBasicIntermediateCode(sourceFile, codeGen);
        
        codeGen.printCode();
        codeGen.printStatistics();
        codeGen.writeToFile();
        
        System.out.println();
        System.out.println(" Código intermedio generado");
        System.out.println(" Archivo generado:");
        System.out.println("  - " + outputFile + " (código intermedio)");
    }
    
    /**
     * MODO 3: Ambos en pasadas separadas
     */
    private static void runBothSeparately(String sourceFile) throws Exception {
        System.out.println("=== MODO: PASADAS SEPARADAS ===");
        System.out.println();
        
        System.out.println("--- PASADA 1: ANÁLISIS SEMÁNTICO ---");
        runSemanticOnly(sourceFile);
        
        System.out.println();
        
        System.out.println("--- PASADA 2: GENERACIÓN DE CÓDIGO ---");
        runCodeGenerationPass(sourceFile);
        
        System.out.println();
        System.out.println("Ambas pasadas completadas");
    }
    
    /**
     * MODO 4: Análisis completo 
     */
    private static void runFullAnalysis(String sourceFile) throws Exception {
        System.out.println("=== MODO: ANÁLISIS COMPLETO ===");
        System.out.println("Análisis semántico + Generación de código intermedio");
        System.out.println();
        
        String tokenOutputFile = getTokenFile(sourceFile);
        String symbolTableFile = getSymbolFile(sourceFile);
        String intermediateFile = getIntermediateFile(sourceFile);
        
        PrintWriter tokenWriter = new PrintWriter(new FileWriter(tokenOutputFile));
        SymbolTable symbolTable = new SymbolTable();
        Scanner scanner = new Scanner(new FileReader(sourceFile));
        
        System.out.println("Ejecutando análisis léxico...");
        
        Symbol token;
        int tokenCount = 0;
        
        while (true) {
            token = scanner.next_token();
            if (token.sym == 0) break;
            
            tokenCount++;
            String symbolName = symbolToString(token.sym);
            String lexema = (token.value != null) ? token.value.toString() : symbolName;
            String tabla = symbolTable.determinarTabla(symbolName, lexema);
            
            tokenWriter.println("Token: " + symbolName + 
                            ", Lexema: " + lexema + 
                            ", Línea: " + token.left + 
                            ", Columna: " + token.right + 
                            ", Tabla: " + (tabla.equals("NINGUNO") ? "N/A" : tabla));
            
            if (!tabla.equals("NINGUNO")) {
                symbolTable.insertarSimbolo(lexema, symbolName, token.left, token.right, token.value);
            }
        }
        
        tokenWriter.close();
        symbolTable.escribirTablas(symbolTableFile);
        System.out.println("✓ Análisis léxico completado. " + tokenCount + " tokens procesados.");
        
        scanner = new Scanner(new FileReader(sourceFile));
        parser p = new parser(scanner);
        p.setSymbolTable(symbolTable);
        
        p.enableCodeGeneration(intermediateFile);
        
        System.out.println("Ejecutando análisis híbrido...");
        
        Symbol result = p.parse();
        
        int errorCount = p.getErrorCount();
        
        if (errorCount == 0) {
            System.out.println("✓ Análisis completado sin errores.");
            
            p.getCodeGenerator().printCode();
            p.getCodeGenerator().printStatistics();
            p.getCodeGenerator().writeToFile();
            
            System.out.println("Archivos generados:");
            System.out.println("  - " + tokenOutputFile + " (tokens)");
            System.out.println("  - " + symbolTableFile + " (símbolos)");
            System.out.println("  - semantic_analysis.txt (análisis semántico)");
            System.out.println("  - " + intermediateFile + " (código intermedio)");
            
        } else {
            System.out.println("Análisis completado con " + errorCount + " errores.");
            System.out.println("No se generó código intermedio debido a errores semánticos.");
        }
    }
    

    private static void runOriginalAnalysis(String sourceFile) throws Exception {
        // === ANÁLISIS LÉXICO ===
        String tokenOutputFile = getTokenFile(sourceFile);
        PrintWriter tokenWriter = new PrintWriter(new FileWriter(tokenOutputFile));
        
        String symbolTableFile = getSymbolFile(sourceFile);
        PrintWriter symbolWriter = new PrintWriter(new FileWriter(symbolTableFile));
        
        SymbolTable symbolTable = new SymbolTable();
        Scanner scanner = new Scanner(new FileReader(sourceFile));
        
        System.out.println("Ejecutando análisis léxico...");
        
        Symbol token;
        int tokenCount = 0;
        
        while (true) {
            token = scanner.next_token();
            
            if (token.sym == 0) { // EOF
                break;
            }
            
            tokenCount++;
            
            String symbolName = symbolToString(token.sym);
            String lexema = (token.value != null) ? token.value.toString() : symbolName;
            String tabla = symbolTable.determinarTabla(symbolName, lexema);
            
            tokenWriter.println("Token: " + symbolName + 
                               ", Lexema: " + lexema + 
                               ", Línea: " + token.left + 
                               ", Columna: " + token.right + 
                               ", Tabla: " + (tabla.equals("NINGUNO") ? "N/A" : tabla));
            
            if (!tabla.equals("NINGUNO")) {
                symbolTable.insertarSimbolo(lexema, symbolName, token.left, token.right, token.value);
            }
        }
        
        tokenWriter.close();
        System.out.println("✓ Análisis léxico completado. " + tokenCount + " tokens procesados.");
        
        symbolTable.escribirTablas(symbolTableFile);
        System.out.println("✓ Tablas de símbolos generadas.");
        
        scanner = new Scanner(new FileReader(sourceFile));
        parser p = new parser(scanner);
        p.setSymbolTable(symbolTable);
        
        System.out.println("Ejecutando análisis sintáctico y semántico...");
        
        Symbol result = p.parse();
        
        int errorCount = p.getErrorCount();
        
        if (errorCount == 0) {
            System.out.println("✓ Análisis completado sin errores.");
        } else {
            System.out.println("⚠ Análisis completado con " + errorCount + " errores.");
        }
        
        symbolWriter.close();
    }
    
    /**
     * Generación básica de código intermedio
     */
    private static void generateBasicIntermediateCode(String sourceFile, IntermediateCodeGenerator codeGen) throws Exception {
        
        codeGen.addComment("Generación básica de código intermedio");
        codeGen.addComment("Archivo fuente: " + sourceFile);
        codeGen.addComment("");
        
        
        codeGen.startFunction("main", "VOID");
        codeGen.addComment("Función principal detectada");
        codeGen.generateReturn(null);
        codeGen.endFunction("main");
        
        codeGen.addComment("Fin del código generado");
    }
    // === GENERACIÓN DE CÓDIGO MIPS ===
    /**
 * NUEVO MODO: Generación de código MIPS
 */
    private static void runMipsGeneration(String sourceFile) throws Exception {
        System.out.println("=== MODO: GENERACIÓN CÓDIGO MIPS ===");
        System.out.println();
        
        // Paso 1: Generar código intermedio COMPLETO (usando análisis full)
        String intermediateFile = getIntermediateFile(sourceFile);
        System.out.println("Paso 1: Generando código intermedio completo...");
        
        // Usar runFullAnalysis en lugar de runCodeGenerationPass para obtener código completo
        runFullAnalysis(sourceFile);
        
        // Verificar que el archivo de código intermedio existe y tiene contenido útil
        File intermFile = new File(intermediateFile);
        if (!intermFile.exists()) {
            System.err.println("ERROR: No se generó código intermedio");
            return;
        }
        
        // Paso 2: Generar código MIPS desde el intermedio
        String mipsFile = getMipsFile(sourceFile);
        System.out.println("\nPaso 2: Generando código MIPS...");
        
        MipsGenerator mipsGen = new MipsGenerator();
        mipsGen.generateFromFile(intermediateFile, mipsFile);
        mipsGen.printStatistics();
        
        System.out.println();
        System.out.println("✓ Código MIPS generado exitosamente");
        System.out.println("📁 Archivos generados:");
        System.out.println("  - " + intermediateFile + " (código intermedio)");
        System.out.println("  - " + mipsFile + " (código MIPS)");
        System.out.println();
        System.out.println("💡 Para probar:");
        System.out.println("  1. Abrir QtSpim");
        System.out.println("  2. Cargar archivo: " + mipsFile);
        System.out.println("  3. Ejecutar (F10)");
    }

    /**
 * MODO COMPLETO: Análisis + Código Intermedio + MIPS
 */
    private static void runCompleteCompilation(String sourceFile) throws Exception {
        System.out.println("=== MODO: COMPILACIÓN COMPLETA ===");
        System.out.println("Análisis semántico + Código intermedio + Código MIPS");
        System.out.println();
        
        // Paso 1: Análisis completo (semántico + intermedio)
        System.out.println("--- PASO 1: ANÁLISIS Y CÓDIGO INTERMEDIO ---");
        runFullAnalysis(sourceFile);
        
        // Paso 2: Generar MIPS solo si no hubo errores
        System.out.println("\n--- PASO 2: GENERACIÓN CÓDIGO MIPS ---");
        String intermediateFile = getIntermediateFile(sourceFile);
        String mipsFile = getMipsFile(sourceFile);
        
        // Verificar que existe el archivo intermedio
        File intermFile = new File(intermediateFile);
        if (!intermFile.exists()) {
            System.err.println("ERROR: No se pudo generar código MIPS - código intermedio no disponible");
            return;
        }
        
        MipsGenerator mipsGen = new MipsGenerator();
        mipsGen.generateFromFile(intermediateFile, mipsFile);
        mipsGen.printStatistics();
        
        System.out.println();
        System.out.println("🎉 COMPILACIÓN COMPLETA EXITOSA");
        System.out.println("📁 Archivos generados:");
        System.out.println("  - " + getTokenFile(sourceFile) + " (tokens)");
        System.out.println("  - " + getSymbolFile(sourceFile) + " (símbolos)");
        System.out.println("  - semantic_analysis.txt (análisis semántico)");
        System.out.println("  - " + intermediateFile + " (código intermedio)");
        System.out.println("  - " + mipsFile + " (código MIPS)");
        System.out.println();
        System.out.println("🚀 Listo para ejecutar en QtSpim: " + mipsFile);
    }
    // === MÉTODOS HELPER ===
    
    private static String getTokenFile(String sourceFile) {
        return sourceFile.substring(0, sourceFile.lastIndexOf('.')) + "_tokens.txt";
    }
    
    private static String getSymbolFile(String sourceFile) {
        return sourceFile.substring(0, sourceFile.lastIndexOf('.')) + "_symbols.txt";
    }
    
    private static String getIntermediateFile(String sourceFile) {
        return sourceFile.substring(0, sourceFile.lastIndexOf('.')) + "_intermediate.txt";
    }

    private static String getMipsFile(String sourceFile) {
        return sourceFile.substring(0, sourceFile.lastIndexOf('.')) + ".asm";
    }
    
    /**
     * método original para convertir símbolos
     */
    public static String symbolToString(int sym) {
        switch(sym) {
            case main.java.parser.sym.ID: return "ID";
            case main.java.parser.sym.LIT_INT: return "INT_LITERAL";
            case main.java.parser.sym.LIT_FLOAT: return "FLOAT_LITERAL";
            case main.java.parser.sym.LIT_CHAR: return "CHAR_LITERAL";
            case main.java.parser.sym.LIT_STRING: return "STRING_LITERAL";
            case main.java.parser.sym.LIT_BOOL: return "BOOL_LITERAL";
            case main.java.parser.sym.TRUE: return "TRUE";
            case main.java.parser.sym.FALSE: return "FALSE";
            case main.java.parser.sym.IF: return "IF";
            case main.java.parser.sym.ELIF: return "ELIF";
            case main.java.parser.sym.ELSE: return "ELSE";
            case main.java.parser.sym.DO: return "DO";
            case main.java.parser.sym.WHILE: return "WHILE";
            case main.java.parser.sym.FOR: return "FOR";
            case main.java.parser.sym.BREAK: return "BREAK";
            case main.java.parser.sym.RETURN: return "RETURN";
            case main.java.parser.sym.INT: return "INT";
            case main.java.parser.sym.FLOAT: return "FLOAT";
            case main.java.parser.sym.BOOL: return "BOOL";
            case main.java.parser.sym.CHAR: return "CHAR";
            case main.java.parser.sym.STRING: return "STRING";
            case main.java.parser.sym.VOID: return "VOID";
            case main.java.parser.sym.READ: return "read";
            case main.java.parser.sym.WRITE: return "WRITE";
            case main.java.parser.sym.MAIN: return "MAIN";
            case main.java.parser.sym.PLUS: return "PLUS";
            case main.java.parser.sym.MINUS: return "MINUS";
            case main.java.parser.sym.TIMES: return "TIMES";
            case main.java.parser.sym.DIVIDE: return "DIVIDE";
            case main.java.parser.sym.MOD: return "MOD";
            case main.java.parser.sym.POW: return "POW";
            case main.java.parser.sym.INCREMENT: return "INCREMENT";
            case main.java.parser.sym.DECREMENT: return "DECREMENT";
            case main.java.parser.sym.LT: return "LT";
            case main.java.parser.sym.LTE: return "LTE";
            case main.java.parser.sym.GT: return "GT";
            case main.java.parser.sym.GTE: return "GTE";
            case main.java.parser.sym.EQ: return "EQ";
            case main.java.parser.sym.NEQ: return "NEQ";
            case main.java.parser.sym.AND: return "AND";
            case main.java.parser.sym.OR: return "OR";
            case main.java.parser.sym.NOT: return "NOT";
            case main.java.parser.sym.LPAREN: return "LPAREN";
            case main.java.parser.sym.RPAREN: return "RPAREN";
            case main.java.parser.sym.LBLOCK: return "LBLOCK";
            case main.java.parser.sym.RBLOCK: return "RBLOCK";
            case main.java.parser.sym.QUESTION: return "QUESTION";
            case main.java.parser.sym.COMMA: return "COMMA";
            case main.java.parser.sym.ASSIGN: return "ASSIGN";
            case main.java.parser.sym.LBRACKET: return "LBRACKET";
            case main.java.parser.sym.RBRACKET: return "RBRACKET";
            case main.java.parser.sym.SWITCH: return "SWITCH";
            case main.java.parser.sym.CASE: return "CASE";
            case main.java.parser.sym.DEFAULT: return "DEFAULT";
            case main.java.parser.sym.COLON: return "COLON";
            default: return "SYMBOL_" + sym;
        }
    }
}