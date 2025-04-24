/* Sección de código de usuario */
package lexer;

import java_cup.runtime.*;
import parser.sym;

%%

/* Opciones y declaraciones de JFlex */
%class Scanner
%unicode
%cup
%line
%column
%public

/* Patrones básicos */
LineTerminator = \r|\n|\r\n
WhiteSpace     = {LineTerminator} | [ \t\f]
Identifier     = [a-zA-Z][a-zA-Z0-9_]*
IntegerLiteral = 0 | [1-9][0-9]*
FloatLiteral   = {IntegerLiteral}\.[0-9]+
CharLiteral    = '.'
StringLiteral  = \"[^\"]*\"
OneLineComment = @[^\r\n]*
MultiLineComment = \{[^}]*\}

%%

/* Reglas léxicas */
"main"          { return new Symbol(sym.MAIN, yyline, yycolumn, yytext()); }
"if"            { return new Symbol(sym.IF, yyline, yycolumn, yytext()); }
"elif"          { return new Symbol(sym.ELIF, yyline, yycolumn, yytext()); }
"else"          { return new Symbol(sym.ELSE, yyline, yycolumn, yytext()); }
"do"            { return new Symbol(sym.DO, yyline, yycolumn, yytext()); }
"while"         { return new Symbol(sym.WHILE, yyline, yycolumn, yytext()); }
"for"           { return new Symbol(sym.FOR, yyline, yycolumn, yytext()); }
"return"        { return new Symbol(sym.RETURN, yyline, yycolumn, yytext()); }
"break"         { return new Symbol(sym.BREAK, yyline, yycolumn, yytext()); }

"int"           { return new Symbol(sym.INT, yyline, yycolumn, yytext()); }
"float"         { return new Symbol(sym.FLOAT, yyline, yycolumn, yytext()); }
"char"          { return new Symbol(sym.CHAR, yyline, yycolumn, yytext()); }
"string"        { return new Symbol(sym.STRING, yyline, yycolumn, yytext()); }
"boolean"       { return new Symbol(sym.BOOLEAN, yyline, yycolumn, yytext()); }
"void"          { return new Symbol(sym.VOID, yyline, yycolumn, yytext()); }

"luna"          { return new Symbol(sym.TRUE, yyline, yycolumn, yytext()); }
"sol"           { return new Symbol(sym.FALSE, yyline, yycolumn, yytext()); }

"+"             { return new Symbol(sym.PLUS, yyline, yycolumn, yytext()); }
"-"             { return new Symbol(sym.MINUS, yyline, yycolumn, yytext()); }
"*"             { return new Symbol(sym.TIMES, yyline, yycolumn, yytext()); }
"//"            { return new Symbol(sym.DIV, yyline, yycolumn, yytext()); }
"~"             { return new Symbol(sym.MOD, yyline, yycolumn, yytext()); }
"**"            { return new Symbol(sym.POW, yyline, yycolumn, yytext()); }
"++"            { return new Symbol(sym.INCREMENT, yyline, yycolumn, yytext()); }
"--"            { return new Symbol(sym.DECREMENT, yyline, yycolumn, yytext()); }

"<"             { return new Symbol(sym.LT, yyline, yycolumn, yytext()); }
"<="            { return new Symbol(sym.LE, yyline, yycolumn, yytext()); }
">"             { return new Symbol(sym.GT, yyline, yycolumn, yytext()); }
">="            { return new Symbol(sym.GE, yyline, yycolumn, yytext()); }
"=="            { return new Symbol(sym.EQ, yyline, yycolumn, yytext()); }
"!="            { return new Symbol(sym.NE, yyline, yycolumn, yytext()); }

"^"             { return new Symbol(sym.AND, yyline, yycolumn, yytext()); }
"#"             { return new Symbol(sym.OR, yyline, yycolumn, yytext()); }
"!"             { return new Symbol(sym.NOT, yyline, yycolumn, yytext()); }

"="             { return new Symbol(sym.ASSIGN, yyline, yycolumn, yytext()); }
"?"             { return new Symbol(sym.SEMI, yyline, yycolumn, yytext()); }
"&"             { return new Symbol(sym.PAREN, yyline, yycolumn, yytext()); }
"|"             { return new Symbol(sym.ARRAY_SEP, yyline, yycolumn, yytext()); }
"\\"            { return new Symbol(sym.BLOCK_START, yyline, yycolumn, yytext()); }
"/"             { return new Symbol(sym.BLOCK_END, yyline, yycolumn, yytext()); }
","             { return new Symbol(sym.COMMA, yyline, yycolumn, yytext()); }
"("             { return new Symbol(sym.LPAREN, yyline, yycolumn, yytext()); }
")"             { return new Symbol(sym.RPAREN, yyline, yycolumn, yytext()); }
"["             { return new Symbol(sym.LBRACK, yyline, yycolumn, yytext()); }
"]"             { return new Symbol(sym.RBRACK, yyline, yycolumn, yytext()); }

{Identifier}     { return new Symbol(sym.ID, yyline, yycolumn, yytext()); }
{IntegerLiteral} { return new Symbol(sym.INT_LITERAL, yyline, yycolumn, Integer.parseInt(yytext())); }
{FloatLiteral}   { return new Symbol(sym.FLOAT_LITERAL, yyline, yycolumn, Float.parseFloat(yytext())); }
{CharLiteral}    { return new Symbol(sym.CHAR_LITERAL, yyline, yycolumn, yytext().charAt(1)); }
{StringLiteral}  { return new Symbol(sym.STRING_LITERAL, yyline, yycolumn, yytext().substring(1, yytext().length()-1)); }

{OneLineComment}  { /* ignorar comentarios de una línea */ }
{MultiLineComment} { /* ignorar comentarios multilínea */ }
{WhiteSpace}      { /* ignorar espacios en blanco */ }

/* Error fallback */
[^]              { 
                    System.err.println("Error léxico: carácter ilegal '" + yytext() + 
                                       "' en línea " + (yyline+1) + ", columna " + (yycolumn+1));
                 }