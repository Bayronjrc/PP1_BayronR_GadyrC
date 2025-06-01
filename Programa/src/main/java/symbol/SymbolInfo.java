package main.java.symbol;

import java.util.*;

/**
 * Estructura para almacenar información detallada de cada simbolo en el compilador.
 * 
 * Esta clase encapsula todos los atributos asociados a un simbolo: su lexema,
 * tipo de token, tipo de variable, posición en el código fuente, valor (si corresponde),
 * clasificación (variable, función o constante) y parámetros (en caso de funciones).
 * 
 * @author Compilador
 * @version 2.0 - Mejorado para análisis semántico
 */
public class SymbolInfo {
    /** Texto original del simbolo en el codigo fuente */
    private String lexema;
    
    /** Tipo de token segun el analizador lexico (ID, INT_LITERAL, etc.) */
    private String tipo;
    
    /** 
     * Tipo de datos para variables y funciones (INT, FLOAT, BOOL, etc.)
     * Solo aplica a variables y funciones, no a otros tipos de tokens
     */
    private String tipoVariable;
    
    /** Número de linea donde se encuentra el simbolo en el codigo fuente */
    private int linea;
    
    /** Número de columna donde se encontro el simbolo en el codigo fuente */
    private int columna;
    
    /** 
     * Valor asociado al simbolo.
     * - Para constantes: valor literal (entero, flotante, booleano, etc.)
     * - Para variables inicializadas: valor asignado
     */
    private Object valor;
    
    /** Indica si el simbolo es una variable */
    private boolean esVariable;
    
    /** Indica si el simbolo es una función */
    private boolean esFuncion;
    
    /** Indica si el simbolo es una constante o literal */
    private boolean esConstante;
    
    /** 
     * Lista de parametros para funciones.
     * Almacena los tipos de datos de los parametros en orden de declaracion
     */
    private List<String> parametros;
    
    /** 
     * Útil para verificaciones semánticas y debugging
     */
    private int alcance;
    
    /** 
     * Importante para detectar uso de variables no inicializadas
     */
    private boolean inicializada;
    
    /** 
     * Para variables, indica si fue declarada pero no usada
     */
    private boolean utilizada;
    
    /** 
     * Almacena las dimensiones del array
     */
    private List<Integer> dimensiones;
    
    /**
     * Constructor que inicializa un objeto SymbolInfo con la informacion del simbolo.
     * y la lista de parametros se inicializa vacia.
     * 
     * @param lexema Texto del simbolo (identificador, literal, etc.)
     * @param tipo Tipo de token segun el analizador lexico
     * @param linea Numero de linea donde se encontuentra el simbolo
     * @param columna Numero de columna donde se encuentra el simbolo
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
        this.alcance = 0;
        this.inicializada = false;
        this.utilizada = false;
        this.dimensiones = new ArrayList<>();
    }
    
    /**
     * Constructor adicional que incluye el alcance
     */
    public SymbolInfo(String lexema, String tipo, int linea, int columna, int alcance) {
        this(lexema, tipo, linea, columna);
        this.alcance = alcance;
    }
    
    // ============= GETTERS Y SETTERS EXISTENTES =============
    
    public String getLexema() { return lexema; }
    public String getTipo() { return tipo; }
    public String getTipoVariable() { return tipoVariable; }
    public void setTipoVariable(String tipoVariable) { this.tipoVariable = tipoVariable; }
    public int getLinea() { return linea; }
    public int getColumna() { return columna; }
    public Object getValor() { return valor; }
    public void setValor(Object valor) { this.valor = valor; }
    public boolean esVariable() { return esVariable; }
    public void setEsVariable(boolean esVariable) { this.esVariable = esVariable; }
    public boolean esFuncion() { return esFuncion; }
    public void setEsFuncion(boolean esFuncion) { this.esFuncion = esFuncion; }
    public boolean esConstante() { return esConstante; }
    public void setEsConstante(boolean esConstante) { this.esConstante = esConstante; }
    public List<String> getParametros() { return parametros; }
    public void addParametro(String param) { this.parametros.add(param); }
    
    // ============= NUEVOS GETTERS Y SETTERS =============
    
    public int getAlcance() { return alcance; }
    public void setAlcance(int alcance) { this.alcance = alcance; }
    
    public boolean estaInicializada() { return inicializada; }
    public void setInicializada(boolean inicializada) { this.inicializada = inicializada; }
    
    public boolean estaUtilizada() { return utilizada; }
    public void setUtilizada(boolean utilizada) { this.utilizada = utilizada; }
    
    public List<Integer> getDimensiones() { return dimensiones; }
    public void addDimension(int dimension) { this.dimensiones.add(dimension); }
    public boolean esArray() { return !dimensiones.isEmpty(); }
    
    // ============= METODOS DE VERIFICACION SEMANTICA =============
    
    /**
     * Verifica si es compatible con otro tipo para asignaciones
     */
    public boolean esCompatibleCon(String otroTipo) {
        if (this.tipoVariable == null || otroTipo == null) {
            return false;
        }
        
        // Mismos tipos son compatibles
        if (this.tipoVariable.equals(otroTipo)) {
            return true;
        }
        
        // Reglas de compatibilidad especificas del lenguaje
        // Ejemplo: int puede recibir char, float puede recibir int, etc.
        switch (this.tipoVariable) {
            case "FLOAT":
                return otroTipo.equals("INT") || otroTipo.equals("CHAR");
            case "INT":
                return otroTipo.equals("CHAR");
            case "STRING":
                return otroTipo.equals("CHAR");
            default:
                return false;
        }
    }
    
    /**
     * Verifica si es un tipo numérico (para operaciones aritmeticas)
     */
    public boolean esNumerico() {
        return tipoVariable != null && 
               (tipoVariable.equals("INT") || tipoVariable.equals("FLOAT"));
    }
    
    /**
     * Verifica si es un tipo comparable (para operaciones relacionales)
     */
    public boolean esComparable() {
        return tipoVariable != null && 
               (tipoVariable.equals("INT") || tipoVariable.equals("FLOAT") || 
                tipoVariable.equals("CHAR"));
    }
    
    /**
     * Obtiene el tipo resultante de una operacion aritmetica con otro tipo
     */
    public String tipoResultanteConOperacion(String otroTipo) {
        if (!this.esNumerico() || !(otroTipo.equals("INT") || otroTipo.equals("FLOAT"))) {
            return null; // Operación inválida
        }
        
        // Si cualquiera es FLOAT, el resultado es FLOAT
        if (this.tipoVariable.equals("FLOAT") || otroTipo.equals("FLOAT")) {
            return "FLOAT";
        }
        
        // Si ambos son INT, el resultado es INT
        return "INT";
    }
    
    /**
     * Clona el simbolo para crear una copia en otro alcance
     */
    public SymbolInfo clonar() {
        SymbolInfo copia = new SymbolInfo(this.lexema, this.tipo, this.linea, this.columna, this.alcance);
        copia.setTipoVariable(this.tipoVariable);
        copia.setValor(this.valor);
        copia.setEsVariable(this.esVariable);
        copia.setEsFuncion(this.esFuncion);
        copia.setEsConstante(this.esConstante);
        copia.setInicializada(this.inicializada);
        copia.setUtilizada(this.utilizada);
        
        // Copiar parámetros y dimensiones
        for (String param : this.parametros) {
            copia.addParametro(param);
        }
        for (Integer dim : this.dimensiones) {
            copia.addDimension(dim);
        }
        
        return copia;
    }
    
    /**
     * Genera una representacion textual del simbolo con todos sus atributos.
     * Esta representación se usa al escribir las tablas de simbolos en archivos.
     * 
     * @return Cadena con la información completa del simbolo
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
        sb.append(", Alcance: ").append(alcance);
        
        if (valor != null) {
            sb.append(", Valor: ").append(valor);
        }
        
        if (esFuncion) {
            sb.append(", Función: Sí");
            sb.append(", Parámetros: ").append(parametros);
            sb.append(", Utilizada: ").append(utilizada ? "Sí" : "No");
        }
        
        if (esVariable) {
            sb.append(", Variable: Sí");
            sb.append(", Inicializada: ").append(inicializada ? "Sí" : "No");
            sb.append(", Utilizada: ").append(utilizada ? "Sí" : "No");
            if (esArray()) {
                sb.append(", Array: ").append(dimensiones);
            }
        }
        
        if (esConstante) {
            sb.append(", Constante: Sí");
        }
        
        return sb.toString();
    }
}