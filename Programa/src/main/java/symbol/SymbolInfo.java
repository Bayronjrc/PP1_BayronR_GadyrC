package main.java.symbol;

import java.util.*;

public class SymbolInfo {
    private String lexema;
    private String tipo;
    private String tipoVariable;
    private int linea;
    private int columna;
    private Object valor;
    private boolean esVariable;
    private boolean esFuncion;
    private boolean esConstante;
    private List<String> parametros;
    private int alcance;
    private String scopeName;
    private String scopeType;
    private String scopePath;
    private boolean inicializada;
    private boolean utilizada;
    private List<Integer> dimensiones;
    
    public SymbolInfo(String lexema, String tipo, int linea, int columna, int alcance, String scopeName, String scopeType) {
        this.lexema = lexema;
        this.tipo = tipo;
        this.linea = linea;
        this.columna = columna;
        this.alcance = alcance;
        this.scopeName = scopeName;
        this.scopeType = scopeType;
        this.scopePath = buildScopePath();
        this.esVariable = false;
        this.esFuncion = false;
        this.esConstante = false;
        this.parametros = new ArrayList<>();
        this.inicializada = false;
        this.utilizada = false;
        this.dimensiones = new ArrayList<>();
    }
    
    public SymbolInfo(String lexema, String tipo, int linea, int columna) {
        this(lexema, tipo, linea, columna, 0, "global", "GLOBAL");
    }
    
    public SymbolInfo(String lexema, String tipo, int linea, int columna, int alcance) {
        this(lexema, tipo, linea, columna, alcance, "scope_" + alcance, "BLOCK");
    }
    
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
    
    public int getAlcance() { return alcance; }
    public void setAlcance(int alcance) { 
        this.alcance = alcance;
        this.scopePath = buildScopePath();
    }
    
    public String getScopeName() { return scopeName; }
    public void setScopeName(String scopeName) { 
        this.scopeName = scopeName;
        this.scopePath = buildScopePath();
    }
    
    public String getScopeType() { return scopeType; }
    public void setScopeType(String scopeType) { 
        this.scopeType = scopeType;
        this.scopePath = buildScopePath();
    }
    
    public String getScopePath() { return scopePath; }
    public void setScopePath(String scopePath) { this.scopePath = scopePath; }
    
    public boolean estaInicializada() { return inicializada; }
    public void setInicializada(boolean inicializada) { this.inicializada = inicializada; }
    
    public boolean estaUtilizada() { return utilizada; }
    public void setUtilizada(boolean utilizada) { this.utilizada = utilizada; }
    
    public List<Integer> getDimensiones() { return dimensiones; }
    public void addDimension(int dimension) { this.dimensiones.add(dimension); }
    public boolean esArray() { return !dimensiones.isEmpty(); }
    
    private String buildScopePath() {
        if (scopeType == null || scopeName == null) {
            return "unknown(" + alcance + ")";
        }
        return scopeType + ":" + scopeName + "(" + alcance + ")";
    }
    
    public void updateScope(int alcance, String scopeName, String scopeType) {
        this.alcance = alcance;
        this.scopeName = scopeName;
        this.scopeType = scopeType;
        this.scopePath = buildScopePath();
    }
    
    public boolean esCompatibleCon(String otroTipo) {
        if (this.tipoVariable == null || otroTipo == null) {
            return false;
        }
        
        if (this.tipoVariable.equals(otroTipo)) {
            return true;
        }
        
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
    
    public boolean esNumerico() {
        return tipoVariable != null && 
               (tipoVariable.equals("INT") || tipoVariable.equals("FLOAT"));
    }
    
    public boolean esComparable() {
        return tipoVariable != null && 
               (tipoVariable.equals("INT") || tipoVariable.equals("FLOAT") || 
                tipoVariable.equals("CHAR"));
    }
    
    public String tipoResultanteConOperacion(String otroTipo) {
        if (!this.esNumerico() || !(otroTipo.equals("INT") || otroTipo.equals("FLOAT"))) {
            return null;
        }
        
        if (this.tipoVariable.equals("FLOAT") || otroTipo.equals("FLOAT")) {
            return "FLOAT";
        }
        
        return "INT";
    }
    
    public SymbolInfo clonar() {
        SymbolInfo copia = new SymbolInfo(this.lexema, this.tipo, this.linea, this.columna, 
                                          this.alcance, this.scopeName, this.scopeType);
        copia.setTipoVariable(this.tipoVariable);
        copia.setValor(this.valor);
        copia.setEsVariable(this.esVariable);
        copia.setEsFuncion(this.esFuncion);
        copia.setEsConstante(this.esConstante);
        copia.setInicializada(this.inicializada);
        copia.setUtilizada(this.utilizada);
        
        for (String param : this.parametros) {
            copia.addParametro(param);
        }
        for (Integer dim : this.dimensiones) {
            copia.addDimension(dim);
        }
        
        return copia;
    }
    
    public boolean estEnMismoScope(SymbolInfo otro) {
        return this.alcance == otro.alcance && 
               this.scopeName.equals(otro.scopeName);
    }
    
    public boolean estEnScopeHijo(SymbolInfo padre) {
        return this.alcance > padre.alcance;
    }
    
    public String getScopeInfo() {
        return String.format("Scope[%s:%s(%d)]", scopeType, scopeName, alcance);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Lexema: ").append(lexema);
        sb.append(", Tipo: ").append(tipo);
        
        if (tipoVariable != null) {
            sb.append(", Tipo Variable: ").append(tipoVariable);
        }
        
        sb.append(", Linea: ").append(linea);
        sb.append(", Columna: ").append(columna);
        sb.append(", Alcance: ").append(alcance);
        
        if (scopeName != null) {
            sb.append(", Scope: ").append(scopeName);
        }
        if (scopeType != null) {
            sb.append(", Tipo_Scope: ").append(scopeType);
        }
        if (scopePath != null) {
            sb.append(", Ruta_Scope: ").append(scopePath);
        }
        
        if (valor != null) {
            sb.append(", Valor: ").append(valor);
        }
        
        if (esFuncion) {
            sb.append(", Funcion: Si");
            if (!parametros.isEmpty()) {
                sb.append(", Parametros: ").append(parametros);
            }
            sb.append(", Utilizada: ").append(utilizada ? "Si" : "No");
        }
        
        if (esVariable) {
            sb.append(", Variable: Si");
            sb.append(", Inicializada: ").append(inicializada ? "Si" : "No");
            sb.append(", Utilizada: ").append(utilizada ? "Si" : "No");
            if (esArray()) {
                sb.append(", Array: ").append(dimensiones);
            }
        }
        
        if (esConstante) {
            sb.append(", Constante: Si");
        }
        
        return sb.toString();
    }
    
    public String toShortString() {
        return String.format("%s(%s) en %s", lexema, 
                           tipoVariable != null ? tipoVariable : tipo, 
                           getScopeInfo());
    }
}