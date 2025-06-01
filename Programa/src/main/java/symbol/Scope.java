package main.java.symbol;

import java.util.*;

/**
 * Clase que representa un alcance (scope) específico en el programa.
 * 
 * Cada alcance mantiene su propia tabla de simbolos y una referencia
 * al alcance padre, permitiendo la búsqueda jerárquica de simbolos
 * siguiendo las reglas de visibilidad del lenguaje.
 * 
 * @author Compilador
 * @version 1.0
 */
public class Scope {
    /** Nivel del alcance (0 = global, 1 = primer nivel anidado, etc.) */
    private int nivel;
    
    /** Referencia al alcance padre (null para alcance global) */
    private Scope parent;
    
    /** Tabla de simbolos local de este alcance */
    private Map<String, SymbolInfo> symbols;
    
    /** Tipo de alcance (GLOBAL, FUNCTION, BLOCK, etc.) */
    private String tipoAlcance;
    
    /** Nombre del alcance (nombre de funcion, "main", "block_1", etc.) */
    private String nombreAlcance;
    
    /**
     * Constructor para crear un nuevo alcance
     * 
     * @param nivel Nivel de anidamiento (0 para global)
     * @param parent Referencia al alcance padre (null para global)
     * @param tipoAlcance Tipo del alcance ("GLOBAL", "FUNCTION", "BLOCK")
     * @param nombreAlcance Nombre identificativo del alcance
     */
    public Scope(int nivel, Scope parent, String tipoAlcance, String nombreAlcance) {
        this.nivel = nivel;
        this.parent = parent;
        this.symbols = new HashMap<>();
        this.tipoAlcance = tipoAlcance;
        this.nombreAlcance = nombreAlcance;
    }
    
    /**
     * Constructor simplificado para alcances básicos
     */
    public Scope(int nivel, Scope parent) {
        this(nivel, parent, "BLOCK", "scope_" + nivel);
    }
    
    /**
     * Busca un simbolo solo en este alcance (búsqueda local)
     * 
     * @param name Nombre del simbolo a buscar
     * @return SymbolInfo del simbolo o null si no se encuentra
     */
    public SymbolInfo getLocal(String name) {
        return symbols.get(name);
    }
    
    /**
     * Busca un simbolo en este alcance y en los alcances padre (busqueda jerarquica)
     * 
     * @param name Nombre del simbolo a buscar
     * @return SymbolInfo del simbolo o null si no se encuentra en ningun alcance
     */
    public SymbolInfo lookup(String name) {
        // Buscar primero en este alcance
        SymbolInfo symbol = symbols.get(name);
        if (symbol != null) {
            symbol.setUtilizada(true); // Marcar como utilizada
            return symbol;
        }
        
        // Si no se encuentra y hay alcance padre, buscar 
        if (parent != null) {
            return parent.lookup(name);
        }
        
        return null;
    }
}
