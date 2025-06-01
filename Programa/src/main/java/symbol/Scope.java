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

    
    /**
     * Declara un nuevo simbolo en este alcance
     * 
     * @param name Nombre del simbolo
     * @param symbol Información del simbolo
     * @return true si se declara exitosamente, false si ya existe
     */
    public boolean declare(String name, SymbolInfo symbol) {
        // Verificar si ya existe en este alcance
        if (symbols.containsKey(name)) {
            return false; 
        }
        
        // Establecer el alcance del simbolo
        symbol.setAlcance(this.nivel);
        symbols.put(name, symbol);
        return true;
    }
    
    /**
     * Verifica si un simbolo existe en este alcance especifico
     * 
     * @param name Nombre del simbolo
     * @return true si existe en este alcance, false en caso contrario
     */
    public boolean existsLocal(String name) {
        return symbols.containsKey(name);
    }
    
    /**
     * Verifica si un simbolo existe en este alcance o en los alcances padre
     * 
     * @param name Nombre del simbolo
     * @return true si existe en cualquier alcance accesible
     */
    public boolean exists(String name) {
        return lookup(name) != null;
    }

    
    /**
     * Obtiene todos los simbolos declarados en este alcance
     * 
     * @return Mapa de todos los simbolos locales
     */
    public Map<String, SymbolInfo> getAllLocalSymbols() {
        return new HashMap<>(symbols);
    }
    
    /**
     * Obtiene todas las variables no utilizadas en este alcance
     * (util para advertencias del compilador)
     * 
     * @return Lista de variables no utilizadas
     */
    public List<SymbolInfo> getVariablesNoUtilizadas() {
        List<SymbolInfo> noUtilizadas = new ArrayList<>();
        for (SymbolInfo symbol : symbols.values()) {
            if (symbol.esVariable() && !symbol.estaUtilizada()) {
                noUtilizadas.add(symbol);
            }
        }
        return noUtilizadas;
    }
    
    /**
     * Obtiene todas las variables no inicializadas en este alcance
     * (util para detectar errores semánticos)
     * 
     * @return Lista de variables no inicializadas
     */
    public List<SymbolInfo> getVariablesNoInicializadas() {
        List<SymbolInfo> noInicializadas = new ArrayList<>();
        for (SymbolInfo symbol : symbols.values()) {
            if (symbol.esVariable() && !symbol.estaInicializada()) {
                noInicializadas.add(symbol);
            }
        }
        return noInicializadas;
    }

    
    /**
     * Busca especuficamente una funcion en la jerarquia de alcances
     * Las funciones se declaran en el alcance global
     * 
     * @param name Nombre de la funcion
     * @return SymbolInfo de la funcion o null si no se encuentra
     */
    public SymbolInfo lookupFunction(String name) {
        SymbolInfo symbol = lookup(name);
        if (symbol != null && symbol.esFuncion()) {
            return symbol;
        }
        return null;
    }

    
    // ============= GETTERS =============
    
    public int getNivel() { return nivel; }
    public Scope getParent() { return parent; }
    public String getTipoAlcance() { return tipoAlcance; }
    public String getNombreAlcance() { return nombreAlcance; }
    public int getCantidadSimbolos() { return symbols.size(); }
    
    /**
     * Representacion textual del alcance para debugging
     */
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
    
    /**
     * Imprime todos los simbolos de este alcance s(para debugging)
     */
    public void printSymbols() {
        System.out.println("=== Simbolos en " + toString() + " ===");
        for (Map.Entry<String, SymbolInfo> entry : symbols.entrySet()) {
            System.out.println("  " + entry.getKey() + ": " + entry.getValue());
        }
    }
}
