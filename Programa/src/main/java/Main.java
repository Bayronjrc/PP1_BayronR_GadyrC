package main;

import java.io.*;
import lexer.Scanner;
import parser.parser;
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
        // Aquí deberás mapear manualmente los números de símbolo a nombres
        // Esto es solo un ejemplo básico:
        switch(sym) {
            case parser.sym.ID: return "ID";
            case parser.sym.INT_LITERAL: return "INT_LITERAL";
            case parser.sym.FLOAT_LITERAL: return "FLOAT_LITERAL";
            // ... agregar más casos según sea necesario
            default: return "SYMBOL_" + sym;
        }
    }
}