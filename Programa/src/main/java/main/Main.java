package main.java.main;

import java.io.*;
import main.java.lexer.Scanner;
import main.java.parser.parser;
import main.java.parser.sym;
import main.java.symbol.SymbolTable;
import main.java.symbol.SymbolInfo;
import java_cup.runtime.Symbol;

/**
 * Clase principal del compilador que coordina el análisis léxico y sintáctico.
 * 
 * Esta clase realiza las siguientes funciones:
 * - Procesa argumentos de línea de comandos
 * - Inicializa el analizador léxico y el analizador sintáctico
 * - Ejecuta el análisis léxico completo para generar tokens
 * - Construye las tablas de símbolos
 * - Ejecuta el análisis sintáctico con recuperación de errores
 * - Genera archivos de salida con los resultados
 * 
 * @author Compilador
 * @version 1.0
 */
public class Main {
    /**
     * Método principal que ejecuta el proceso de compilación.
     * 
     * @param args Argumentos de línea de comandos. Se espera un único argumento
     *             con la ruta al archivo fuente a compilar.
     */
    public static void main(String[] args) {
        try {
            // Verificar que se haya proporcionado un archivo fuente
            if (args.length != 1) {
                System.out.println("Uso: java Main <archivo_fuente>");
                return;
            }
            
            // Obtener ruta del archivo fuente
            String sourceFile = args[0];
            
            // Definir archivos de salida (tokens y tabla de símbolos)
            String tokenOutputFile = sourceFile.substring(0, sourceFile.lastIndexOf('.')) + "_tokens.txt";
            PrintWriter tokenWriter = new PrintWriter(new FileWriter(tokenOutputFile));
            
            String symbolTableFile = sourceFile.substring(0, sourceFile.lastIndexOf('.')) + "_symbols.txt";
            PrintWriter symbolWriter = new PrintWriter(new FileWriter(symbolTableFile));
            
            // Inicializar tabla de símbolos
            SymbolTable symbolTable = new SymbolTable();
            
            // Inicializar el analizador léxico con el archivo fuente
            Scanner scanner = new Scanner(new FileReader(sourceFile));
            
            // === ANÁLISIS LÉXICO ===
            System.out.println("Analizando léxicamente el archivo: " + sourceFile);
            
            // Procesar todos los tokens del archivo
            Symbol token;
            int tokenCount = 0;
            
            while (true) {
                // Obtener siguiente token
                token = scanner.next_token();
                
                // Si es fin de archivo, terminar
                if (token.sym == 0) {
                    break;
                }
                
                tokenCount++;
                
                // Convertir el código numérico del token a su nombre simbólico
                String symbolName = symbolToString(token.sym);
                
                // Obtener el lexema del token
                String lexema = (token.value != null) ? token.value.toString() : symbolName;
                
                // Determinar en qué tabla debe almacenarse el token
                String tabla = symbolTable.determinarTabla(symbolName, lexema);
                
                // Escribir información del token en el archivo de salida
                tokenWriter.println("Token: " + symbolName + 
                                   ", Lexema: " + lexema + 
                                   ", Línea: " + token.left + 
                                   ", Columna: " + token.right + 
                                   ", Tabla: " + (tabla.equals("NINGUNO") ? "N/A" : tabla));
                
                // Insertar el token en la tabla de símbolos correspondiente
                if (!tabla.equals("NINGUNO")) {
                    symbolTable.insertarSimbolo(lexema, symbolName, token.left, token.right, token.value);
                }
            }
            
            // Cerrar el archivo de tokens
            tokenWriter.close();
            System.out.println("Análisis léxico completado. " + tokenCount + " tokens procesados.");
            System.out.println("Tokens escritos en: " + tokenOutputFile);
            
            // Escribir las tablas de símbolos en el archivo correspondiente
            symbolTable.escribirTablas(symbolTableFile);
            System.out.println("Tablas de símbolos escritas en: " + symbolTableFile);
            
            // === ANÁLISIS SINTÁCTICO ===
            
            // Reiniciar el scanner para el análisis sintáctico
            scanner = new Scanner(new FileReader(sourceFile));
            
            // Crear el analizador sintáctico
            parser p = new parser(scanner);
            p.setSymbolTable(symbolTable);  // Pasar la tabla de símbolos al parser
            
            System.out.println("Iniciando análisis sintáctico del archivo: " + sourceFile);
            
            // Iniciar el análisis sintáctico con manejo de errores
            Symbol result = p.parse();
            
            // Obtener el número de errores encontrados
            int errorCount = p.getErrorCount();
            
            if (errorCount == 0) {
                System.out.println("✓ Análisis sintáctico completado sin errores.");
            } else {
                System.err.println("✗ Análisis sintáctico completado con " + errorCount + " errores.");
                System.err.println("Revise los mensajes de error mostrados anteriormente.");
            }
            
            // Cerrar el archivo de símbolos
            symbolWriter.close();
            
        } catch (Exception e) {
            // Manejar cualquier error durante el proceso de compilación
            System.err.println("Error durante la compilación: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Convierte un código numérico de token (definido en sym.java generado por CUP)
     * a su nombre simbólico correspondiente.
     * 
     * @param sym Código numérico del token según CUP
     * @return Nombre simbólico del token como cadena
     */
    public static String symbolToString(int sym) {
        // Mapeo de códigos numéricos a nombres simbólicos según parser.cup
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
            case main.java.parser.sym.UMINUS: return "UMINUS";
            case main.java.parser.sym.SWITCH: return "SWITCH";
            case main.java.parser.sym.CASE: return "CASE";
            case main.java.parser.sym.DEFAULT: return "DEFAULT";
            case main.java.parser.sym.COLON: return "COLON";
            default: return "SYMBOL_" + sym;
        }
    }
}