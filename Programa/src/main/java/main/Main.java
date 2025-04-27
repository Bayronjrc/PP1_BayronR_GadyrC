package main;

import java.io.*;
import lexer.Scanner;
import parser.parser;
import parser.sym;
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
    private static String symbolToString(int sym) {
        // Mapeo de símbolos a nombres según parser.cup
        switch(sym) {
            case parser.sym.ID: return "ID";
            case parser.sym.LIT_INT: return "INT_LITERAL";
            case parser.sym.LIT_FLOAT: return "FLOAT_LITERAL";
            case parser.sym.LIT_CHAR: return "CHAR_LITERAL";
            case parser.sym.LIT_STRING: return "STRING_LITERAL";
            case parser.sym.LIT_BOOL: return "BOOL_LITERAL";
            case parser.sym.TRUE: return "TRUE";
            case parser.sym.FALSE: return "FALSE";
            case parser.sym.IF: return "IF";
            case parser.sym.ELIF: return "ELIF";
            case parser.sym.ELSE: return "ELSE";
            case parser.sym.DO: return "DO";
            case parser.sym.WHILE: return "WHILE";
            case parser.sym.FOR: return "FOR";
            case parser.sym.BREAK: return "BREAK";
            case parser.sym.RETURN: return "RETURN";
            case parser.sym.INT: return "INT";
            case parser.sym.FLOAT: return "FLOAT";
            case parser.sym.BOOL: return "BOOL";
            case parser.sym.CHAR: return "CHAR";
            case parser.sym.STRING: return "STRING";
            case parser.sym.VOID: return "VOID";
            case parser.sym.READ: return "READ";
            case parser.sym.WRITE: return "WRITE";
            case parser.sym.MAIN: return "MAIN";
            case parser.sym.PLUS: return "PLUS";
            case parser.sym.MINUS: return "MINUS";
            case parser.sym.TIMES: return "TIMES";
            case parser.sym.DIVIDE: return "DIVIDE";
            case parser.sym.MOD: return "MOD";
            case parser.sym.POW: return "POW";
            case parser.sym.INCREMENT: return "INCREMENT";
            case parser.sym.DECREMENT: return "DECREMENT";
            case parser.sym.LT: return "LT";
            case parser.sym.LTE: return "LTE";
            case parser.sym.GT: return "GT";
            case parser.sym.GTE: return "GTE";
            case parser.sym.EQ: return "EQ";
            case parser.sym.NEQ: return "NEQ";
            case parser.sym.AND: return "AND";
            case parser.sym.OR: return "OR";
            case parser.sym.NOT: return "NOT";
            case parser.sym.LPAREN: return "LPAREN";
            case parser.sym.RPAREN: return "RPAREN";
            case parser.sym.LBLOCK: return "LBLOCK";
            case parser.sym.RBLOCK: return "RBLOCK";
            case parser.sym.QUESTION: return "QUESTION";
            case parser.sym.COMMA: return "COMMA";
            case parser.sym.ASSIGN: return "ASSIGN";
            case parser.sym.COMMENT_LINE: return "COMMENT_LINE";
            case parser.sym.LCOMMENT_BLOCK: return "LCOMMENT_BLOCK";
            case parser.sym.RCOMMENT_BLOCK: return "RCOMMENT_BLOCK";
            case parser.sym.LBRACKET: return "LBRACKET";
            case parser.sym.RBRACKET: return "RBRACKET";
            case parser.sym.UMINUS: return "UMINUS";
            default: return "SYMBOL_" + sym;
        }
    }
}