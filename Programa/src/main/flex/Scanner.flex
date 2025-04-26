/* JFlex para el lenguaje imperativo */
import java_cup.runtime.*;

%%

%class Scanner
%cup
%unicode
%line
%column

%{
    /* Código a incluir en el scanner */
    
    private Symbol symbol(int type) {
        return new Symbol(type, yyline, yycolumn);
    }
    
    private Symbol symbol(int type, Object value) {
        return new Symbol(type, yyline, yycolumn, value);
    }
    
    StringBuffer string = new StringBuffer();
%}

/* Definiciones léxicas */

LineTerminator = \r|\n|\r\n
WhiteSpace     = {LineTerminator} | [ \t\f]
Digit          = [0-9]
Letter         = [a-zA-Z_]
Identifier     = {Letter}({Letter}|{Digit})*
IntLiteral     = {Digit}+
FloatLiteral   = {Digit}+\.{Digit}+
CharLiteral    = \'[^\']\'
StringLiteral  = \"[^\"]*\"

/* Comentarios */
CommentLine    = "@"[^\r\n]*{LineTerminator}?
CommentBlock   = "{"[^}]*"}"

%%

/* Palabras reservadas */
"if"        { return symbol(sym.IF); }
"elif"      { return symbol(sym.ELIF); }
"else"      { return symbol(sym.ELSE); }
"do"        { return symbol(sym.DO); }
"while"     { return symbol(sym.WHILE); }
"for"       { return symbol(sym.FOR); }
"break"     { return symbol(sym.BREAK); }
"return"    { return symbol(sym.RETURN); }

/* Tipos de datos */
"int"       { return symbol(sym.INT); }
"float"     { return symbol(sym.FLOAT); }
"bool"      { return symbol(sym.BOOL); }
"char"      { return symbol(sym.CHAR); }
"string"    { return symbol(sym.STRING); }
"void"      { return symbol(sym.VOID); }

/* Funciones de entrada/salida y main */
"read"      { return symbol(sym.READ); }
"write"     { return symbol(sym.WRITE); }
"main"      { return symbol(sym.MAIN); }

/* Valores booleanos */
"true"      { return symbol(sym.TRUE, Boolean.TRUE); }
"false"     { return symbol(sym.FALSE, Boolean.FALSE); }

/* Operadores aritméticos */
"+"         { return symbol(sym.PLUS); }
"-"         { return symbol(sym.MINUS); }
"*"         { return symbol(sym.TIMES); }
"//"        { return symbol(sym.DIVIDE); }
"~"         { return symbol(sym.MOD); }
"**"        { return symbol(sym.POW); }
"++"        { return symbol(sym.INCREMENT); }
"--"        { return symbol(sym.DECREMENT); }

/* Operadores relacionales */
"<"         { return symbol(sym.LT); }
"<="        { return symbol(sym.LTE); }
">"         { return symbol(sym.GT); }
">="        { return symbol(sym.GTE); }
"=="        { return symbol(sym.EQ); }
"!="        { return symbol(sym.NEQ); }

/* Operadores lógicos */
"^"         { return symbol(sym.AND); }
"#"         { return symbol(sym.OR); }
"!"         { return symbol(sym.NOT); }

/* Delimitadores */
"("         { return symbol(sym.LPAREN); }
")"         { return symbol(sym.RPAREN); }
"\\"        { return symbol(sym.LBLOCK); }
"/"         { return symbol(sym.RBLOCK); }
"?"         { return symbol(sym.QUESTION); }
","         { return symbol(sym.COMMA); }
"|"         { return symbol(sym.ASSIGN); }
"["         { return symbol(sym.LBRACKET); }
"]"         { return symbol(sym.RBRACKET); }

/* Comentarios */
{CommentLine}    { return symbol(sym.COMMENT_LINE); }
{CommentBlock}   { return symbol(sym.LCOMMENT_BLOCK); /* Simplificado para este ejemplo */ }

/* Literales */
{IntLiteral}     { return symbol(sym.LIT_INT, Integer.parseInt(yytext())); }
{FloatLiteral}   { return symbol(sym.LIT_FLOAT, Float.parseFloat(yytext())); }
{CharLiteral}    { return symbol(sym.LIT_CHAR, yytext().charAt(1)); }
{StringLiteral}  { return symbol(sym.LIT_STRING, yytext().substring(1, yytext().length() - 1)); }

/* Identificadores */
{Identifier}     { return symbol(sym.ID, yytext()); }

/* Espacios en blanco */
{WhiteSpace}     { /* ignorar */ }

/* Error en caso de no coincidir con ninguna regla */
[^]              { throw new Error("Caracter ilegal <" + yytext() + "> en línea " + yyline + ", columna " + yycolumn); }