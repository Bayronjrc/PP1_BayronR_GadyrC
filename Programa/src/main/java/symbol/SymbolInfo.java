package main.java.symbol;

import java.util.*;

/**
 * Estructura para almacenar información detallada de cada símbolo en el compilador.
 * 
 * Esta clase encapsula todos los atributos asociados a un símbolo: su lexema,
 * tipo de token, tipo de variable, posición en el código fuente, valor (si corresponde),
 * clasificación (variable, función o constante) y parámetros (en caso de funciones).
 * 
 * @author Compilador
 * @version 1.0
 */
public class SymbolInfo {
    /** Texto original del símbolo en el código fuente */
    private String lexema;
    
    /** Tipo de token según el analizador léxico (ID, INT_LITERAL, etc.) */
    private String tipo;
    
    /** 
     * Tipo de datos para variables y funciones (INT, FLOAT, BOOL, etc.)
     * Solo aplica a variables y funciones, no a otros tipos de tokens
     */
    private String tipoVariable;
    
    /** Número de línea donde se encontró el símbolo en el código fuente */
    private int linea;
    
    /** Número de columna donde se encontró el símbolo en el código fuente */
    private int columna;
    
    /** 
     * Valor asociado al símbolo.
     * - Para constantes: valor literal (entero, flotante, booleano, etc.)
     * - Para variables inicializadas: valor asignado
     */
    private Object valor;
    
    /** Indica si el símbolo es una variable */
    private boolean esVariable;
    
    /** Indica si el símbolo es una función */
    private boolean esFuncion;
    
    /** Indica si el símbolo es una constante o literal */
    private boolean esConstante;
    
    /** 
     * Lista de parámetros para funciones.
     * Almacena los tipos de datos de los parámetros en orden de declaración
     */
    private List<String> parametros;
    
    /**
     * Constructor que inicializa un objeto SymbolInfo con la información básica del símbolo.
     * Por defecto, los valores booleanos (esVariable, esFuncion, esConstante) se inicializan en falso
     * y la lista de parámetros se inicializa vacía.
     * 
     * @param lexema Texto del símbolo (identificador, literal, etc.)
     * @param tipo Tipo de token según el analizador léxico
     * @param linea Número de línea donde se encontró el símbolo
     * @param columna Número de columna donde se encontró el símbolo
     */
    public SymbolInfo(String lexema, String tipo, int linea, int columna) {
        this.lexema = lexema;
        this.tipo = tipo;
        this.linea = linea;
        this.columna = columna;
        this.esVariable = false;
        this.esFuncion = false;
        this.esConstante = false;
        this.parametros = new ArrayList<>();
    }
    
    /**
     * Obtiene el texto original del símbolo.
     * 
     * @return Lexema del símbolo
     */
    public String getLexema() { return lexema; }
    
    /**
     * Obtiene el tipo de token según el analizador léxico.
     * 
     * @return Tipo del token (ID, INT_LITERAL, etc.)
     */
    public String getTipo() { return tipo; }
    
    /**
     * Obtiene el tipo de datos de la variable o función.
     * 
     * @return Tipo de datos (INT, FLOAT, BOOL, etc.) o null si no es aplicable
     */
    public String getTipoVariable() { return tipoVariable; }
    
    /**
     * Establece el tipo de datos de la variable o función.
     * 
     * @param tipoVariable Tipo de datos a asignar
     */
    public void setTipoVariable(String tipoVariable) { this.tipoVariable = tipoVariable; }
    
    /**
     * Obtiene el número de línea donde se encontró el símbolo.
     * 
     * @return Número de línea en el código fuente
     */
    public int getLinea() { return linea; }
    
    /**
     * Obtiene el número de columna donde se encontró el símbolo.
     * 
     * @return Número de columna en el código fuente
     */
    public int getColumna() { return columna; }
    
    /**
     * Obtiene el valor asociado al símbolo.
     * 
     * @return Valor del símbolo o null si no tiene valor asociado
     */
    public Object getValor() { return valor; }
    
    /**
     * Establece el valor asociado al símbolo.
     * 
     * @param valor Valor a asignar
     */
    public void setValor(Object valor) { this.valor = valor; }
    
    /**
     * Verifica si el símbolo es una variable.
     * 
     * @return true si es una variable, false en caso contrario
     */
    public boolean esVariable() { return esVariable; }
    
    /**
     * Establece si el símbolo es una variable.
     * 
     * @param esVariable Valor booleano a asignar
     */
    public void setEsVariable(boolean esVariable) { this.esVariable = esVariable; }
    
    /**
     * Verifica si el símbolo es una función.
     * 
     * @return true si es una función, false en caso contrario
     */
    public boolean esFuncion() { return esFuncion; }
    
    /**
     * Establece si el símbolo es una función.
     * 
     * @param esFuncion Valor booleano a asignar
     */
    public void setEsFuncion(boolean esFuncion) { this.esFuncion = esFuncion; }
    
    /**
     * Verifica si el símbolo es una constante o literal.
     * 
     * @return true si es una constante, false en caso contrario
     */
    public boolean esConstante() { return esConstante; }
    
    /**
     * Establece si el símbolo es una constante o literal.
     * 
     * @param esConstante Valor booleano a asignar
     */
    public void setEsConstante(boolean esConstante) { this.esConstante = esConstante; }
    
    /**
     * Obtiene la lista de parámetros para funciones.
     * 
     * @return Lista de tipos de datos de los parámetros o lista vacía si no es una función
     */
    public List<String> getParametros() { return parametros; }
    
    /**
     * Agrega un parámetro a la lista de parámetros de una función.
     * 
     * @param param Tipo de datos del parámetro a agregar
     */
    public void addParametro(String param) { this.parametros.add(param); }
    
    /**
     * Genera una representación textual del símbolo con todos sus atributos.
     * Esta representación se usa al escribir las tablas de símbolos en archivos.
     * 
     * @return Cadena con la información completa del símbolo
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Lexema: ").append(lexema);
        sb.append(", Tipo: ").append(tipo);
        if (tipoVariable != null) {
            sb.append(", Tipo Variable: ").append(tipoVariable);
        }
        sb.append(", Línea: ").append(linea);
        sb.append(", Columna: ").append(columna);
        if (valor != null) {
            sb.append(", Valor: ").append(valor);
        }
        if (esFuncion) {
            sb.append(", Función: Sí");
            sb.append(", Parámetros: ").append(parametros);
        }
        if (esVariable) {
            sb.append(", Variable: Sí");
        }
        if (esConstante) {
            sb.append(", Constante: Sí");
        }
        return sb.toString();
    }
}