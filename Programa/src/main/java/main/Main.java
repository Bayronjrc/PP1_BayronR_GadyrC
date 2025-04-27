package main.java.main;

import java.io.*;
import main.java.lexer.Scanner;
import main.java.parser.parser;
import java_cup.runtime.Symbol;

public class Main {
    public static void main(String[] args) {
        try {
            // Verificar argumentos
            if (args.length != 1) {
                System.out.println("Uso: java Main <archivo_fuente>");
                return;
            }
            
            // Archivo fuente
            String sourceFile = args[0];
            
            // Archivo de salida para tokens
            String tokenOutputFile = sourceFile.substring(0, sourceFile.lastIndexOf('.')) + "_tokens.txt";
            PrintWriter tokenWriter = new PrintWriter(new FileWriter(tokenOutputFile));
            
            // Crear scanner
            Scanner scanner = new Scanner(new FileReader(sourceFile));
            
            // Procesar tokens
            System.out.println("Analizando léxicamente el archivo: " + sourceFile);
            
            Symbol token;
            while (true) {
                token = scanner.next_token();
                if (token.sym == 0) { // EOF
                    break;
                }
                // Escribir token en archivo
                tokenWriter.println("Token: " + symbolToString(token.sym) + 
                                   ", Lexema: " + token.value + 
                                   ", Línea: " + (token.left+1) + 
                                   ", Columna: " + (token.right+1));
            }
            tokenWriter.close();
            System.out.println("Análisis léxico completado. Tokens escritos en: " + tokenOutputFile);
            
            // Reiniciar scanner para el análisis sintáctico
            scanner = new Scanner(new FileReader(sourceFile));
            
            // Crear parser
            parser p = new parser(scanner);
            System.out.println("Analizando sintácticamente el archivo: " + sourceFile);
            
            // Iniciar análisis sintáctico
            p.parse();
            
            System.out.println("Análisis sintáctico completado sin errores.");
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Método auxiliar para convertir número de símbolo a su nombre
    public static String symbolToString(int sym) {
        // Mapeo de símbolos a nombres según parser.cup
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
            case main.java.parser.sym.READ: return "READ";
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
            case main.java.parser.sym.COMMENT_LINE: return "COMMENT_LINE";
            case main.java.parser.sym.LCOMMENT_BLOCK: return "LCOMMENT_BLOCK";
            case main.java.parser.sym.RCOMMENT_BLOCK: return "RCOMMENT_BLOCK";
            case main.java.parser.sym.LBRACKET: return "LBRACKET";
            case main.java.parser.sym.RBRACKET: return "RBRACKET";
            case main.java.parser.sym.UMINUS: return "UMINUS";
            default: return "SYMBOL_" + sym;
        }
    }
}