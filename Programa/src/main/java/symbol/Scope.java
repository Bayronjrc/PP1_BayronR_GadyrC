package main.java.symbol;

import java.util.*;



/**
 * Scope para manejar analisis semantico
 * 
 * Esta clase maneja multiples alcances (scopes) organizados
 * 
 * @author Compilador
 * @version 1.0
 */

public class Scope {
    private int nivel;
    private Scope parent;
    private Map<String, SymbolInfo> symbols;
    private String tipoAlcance;
    private String nombreAlcance;
    
    /**
     * Constructor que inicializa los scopes
    */

    public Scope(int nivel, Scope parent, String tipoAlcance, String nombreAlcance) {
        this.nivel = nivel;
        this.parent = parent;
        this.symbols = new HashMap<>();
        this.tipoAlcance = tipoAlcance;
        this.nombreAlcance = nombreAlcance;
    }
    
    public Scope(int nivel, Scope parent) {
        this(nivel, parent, "BLOCK", "scope_" + nivel);
    }
    
    public SymbolInfo getLocal(String name) {
        return symbols.get(name);
    }
    
    public SymbolInfo lookup(String name) {
        SymbolInfo symbol = symbols.get(name);
        if (symbol != null) {
            symbol.setUtilizada(true);
            return symbol;
        }
        
        if (parent != null) {
            return parent.lookup(name);
        }
        
        return null;
    }
    
    public boolean declare(String name, SymbolInfo symbol) {
        if (symbols.containsKey(name)) {
            return false;
        }
        
        symbol.updateScope(this.nivel, this.nombreAlcance, this.tipoAlcance);
        symbols.put(name, symbol);
        return true;
    }
    
    public boolean existsLocal(String name) {
        return symbols.containsKey(name);
    }
    
    public boolean exists(String name) {
        return lookup(name) != null;
    }
    
    public Map<String, SymbolInfo> getAllLocalSymbols() {
        return new HashMap<>(symbols);
    }
    
    public List<SymbolInfo> getVariablesNoUtilizadas() {
        List<SymbolInfo> noUtilizadas = new ArrayList<>();
        for (SymbolInfo symbol : symbols.values()) {
            if (symbol.esVariable() && !symbol.estaUtilizada()) {
                noUtilizadas.add(symbol);
            }
        }
        return noUtilizadas;
    }
    
    public List<SymbolInfo> getVariablesNoInicializadas() {
        List<SymbolInfo> noInicializadas = new ArrayList<>();
        for (SymbolInfo symbol : symbols.values()) {
            if (symbol.esVariable() && !symbol.estaInicializada()) {
                noInicializadas.add(symbol);
            }
        }
        return noInicializadas;
    }
    
    public SymbolInfo lookupFunction(String name) {
        SymbolInfo symbol = lookup(name);
        if (symbol != null && symbol.esFuncion()) {
            return symbol;
        }
        return null;
    }
    
    public int getNivel() { return nivel; }
    public Scope getParent() { return parent; }
    public String getTipoAlcance() { return tipoAlcance; }
    public String getNombreAlcance() { return nombreAlcance; }
    public int getCantidadSimbolos() { return symbols.size(); }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Scope[").append(nombreAlcance)
        .append(", nivel=").append(nivel)
        .append(", tipo=").append(tipoAlcance)
        .append(", simbolos=").append(symbols.size())
        .append("]");
        return sb.toString();
    }
    
    public void printSymbols() {
        System.out.println("=== Simbolos en " + toString() + " ===");
        for (Map.Entry<String, SymbolInfo> entry : symbols.entrySet()) {
            System.out.println("  " + entry.getKey() + ": " + entry.getValue());
        }
    }
}