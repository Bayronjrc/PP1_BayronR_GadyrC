/* 
 * Analizador Léxico para el Compilador
 * -----------------------------------
 * 
 * OBJETIVO: Reconocer los tokens del lenguaje y convertirlos en símbolos
 * para el análisis sintáctico.
 * 
 * ENTRADAS: 
 * - Stream de caracteres del código fuente
 * 
 * SALIDAS: 
 * - Secuencia de tokens (objetos Symbol) con información sobre tipo,
 *   valor, línea y columna
 * 
 * RESTRICCIONES:
 * - Los identificadores deben comenzar con letra y pueden contener letras,
 *   números y guiones bajos
 * - Los tokens se clasifican en palabras reservadas, operadores, literales
 *   e identificadores
 */
import java_cup.runtime.*;
import parser.sym;

%%

/* Opciones y declaraciones */
%class Scanner
%unicode
%cup
%line
%column
%public

%{
  /* 
   * OBJETIVO: Generar objetos Symbol para el parser
   * ENTRADAS: Tipo de token y opcionalmente su valor
   * SALIDAS: Objeto Symbol con información de tipo, posición y valor
   */
  private Symbol symbol(int type) {
    return new Symbol(type, yyline+1, yycolumn+1);
  }

  private Symbol symbol(int type, Object value) {
    return new Symbol(type, yyline+1, yycolumn+1, value);
  }
%}

/* 
 * Macros para expresiones regulares
 * OBJETIVO: Definir patrones comunes para simplificar las reglas léxicas
 */
LineTerminator = \r|\n|\r\n
WhiteSpace     = {LineTerminator} | [ \t\f]
Identifier     = [a-zA-Z][a-zA-Z0-9_]*
IntLiteral     = [0-9]+
FloatLiteral   = [0-9]+\.[0-9]+
CharLiteral    = \'[^\']\'
StringLiteral  = \"[^\"]*\"
CommentLine    = "@".*
CommentBlock   = "{"[^}]*"}"

%%

/* 
 * Reglas léxicas
 * OBJETIVO: Asociar patrones de texto con tokens del lenguaje
 */

/* Palabras reservadas */
"if"        { return symbol(sym.IF); }
"elif"      { return symbol(sym.ELIF); }
"else"      { return symbol(sym.ELSE); }
"do"        { return symbol(sym.DO); }
"while"     { return symbol(sym.WHILE); }
"for"       { return symbol(sym.FOR); }
"break"     { return symbol(sym.BREAK); }
"return"    { return symbol(sym.RETURN); }
"int"       { return symbol(sym.INT); }
"float"     { return symbol(sym.FLOAT); }
"bool"      { return symbol(sym.BOOL); }
"char"      { return symbol(sym.CHAR); }
"string"    { return symbol(sym.STRING); }
"void"      { return symbol(sym.VOID); }
"read"      { return symbol(sym.READ); }
"write"     { return symbol(sym.WRITE); }
"main"      { return symbol(sym.MAIN); }
"luna"      { return symbol(sym.TRUE, Boolean.TRUE); }
"sol"       { return symbol(sym.FALSE, Boolean.FALSE); }
"switch"    { return symbol(sym.SWITCH); }
"case"      { return symbol(sym.CASE); }
"default"   { return symbol(sym.DEFAULT); }

/* 
 * Operadores aritméticos
 * OBJETIVO: Reconocer símbolos para operaciones matemáticas
 */
"+"         { return symbol(sym.PLUS); }
"-"         { return symbol(sym.MINUS); }
"*"         { return symbol(sym.TIMES); }
"//"        { return symbol(sym.DIVIDE); }
"~"         { return symbol(sym.MOD); }
"**"        { return symbol(sym.POW); }
"++"        { return symbol(sym.INCREMENT); }
"--"        { return symbol(sym.DECREMENT); }

/* 
 * Operadores relacionales
 * OBJETIVO: Reconocer símbolos para comparaciones
 */
"<"         { return symbol(sym.LT); }
"<="        { return symbol(sym.LTE); }
">"         { return symbol(sym.GT); }
">="        { return symbol(sym.GTE); }
"=="        { return symbol(sym.EQ); }
"!="        { return symbol(sym.NEQ); }

/* 
 * Operadores lógicos
 * OBJETIVO: Reconocer símbolos para operaciones lógicas
 */
"^"         { return symbol(sym.AND); }
"#"         { return symbol(sym.OR); }
"!"         { return symbol(sym.NOT); }

/* 
 * Delimitadores
 * OBJETIVO: Reconocer símbolos que definen estructura y ámbitos
 */
"ʃ"         { return symbol(sym.LPAREN); }
"ʅ"         { return symbol(sym.RPAREN); }
"\\"        { return symbol(sym.LBLOCK); }
"/"         { return symbol(sym.RBLOCK); }
"["         { return symbol(sym.LBRACKET); }
"]"         { return symbol(sym.RBRACKET); }
"?"         { return symbol(sym.QUESTION); }
","         { return symbol(sym.COMMA); }
"|"         { return symbol(sym.ASSIGN); }
":"         { return symbol(sym.COLON); }

/* 
 * Comentarios - IGNORADOS por el scanner
 * OBJETIVO: Reconocer comentarios pero no pasarlos al parser
 * NOTA: Los comentarios se procesan aquí pero no se envían tokens al parser
 */
{CommentLine}    { 
                   System.out.println("Comentario de línea encontrado en línea " + (yyline+1) + ": " + yytext()); 
                   /* No retorna token - se ignora */ 
                 }

{CommentBlock}   { 
                   System.out.println("Comentario de bloque encontrado en línea " + (yyline+1) + ": " + yytext()); 
                   /* No retorna token - se ignora */ 
                 }

/* 
 * Valores literales
 * OBJETIVO: Reconocer constantes en el código fuente
 */
{IntLiteral}    { return symbol(sym.LIT_INT, Integer.parseInt(yytext())); }
{FloatLiteral}  { return symbol(sym.LIT_FLOAT, Float.parseFloat(yytext())); }
{CharLiteral}   { return symbol(sym.LIT_CHAR, yytext().charAt(1)); }
{StringLiteral} { return symbol(sym.LIT_STRING, yytext().substring(1, yytext().length() - 1)); }

/* 
 * Identificadores
 * OBJETIVO: Reconocer nombres de variables, funciones, etc.
 */
{Identifier}    { return symbol(sym.ID, yytext()); }

/* 
 * Espacios en blanco
 * OBJETIVO: Ignorar espacios, tabulaciones y saltos de línea
 */
{WhiteSpace}    { /* ignorar */ }

/* 
 * Error para cualquier otro carácter
 * OBJETIVO: Detectar caracteres no permitidos en el lenguaje
 */
[^]             { 
                  System.err.println("Error léxico: Carácter ilegal <" + yytext() + "> en línea " + (yyline+1) + ", columna " + (yycolumn+1)); 
                }