int procesarArrays ʃ int n ʅ \ 
    int matriz[5][3] | 0?
    int vector[10] | 0?
    int i | 0?
    int j | 0?
    
    for ʃ i|0? i<5? ++i ʅ \ 
        for ʃ j|0? j<3? ++j ʅ \ 
            matriz[i][j] | i * j + 1?
        / 
    / 
    
    for ʃ i|0? i<10? ++i ʅ \ 
        vector[i] | i ** 2?  
    / 
    
    int suma | 0?
    for ʃ i|0? i<5? ++i ʅ \ 
        for ʃ j|0? j<3? ++j ʅ \ 
            suma | suma + matriz[i][j]?
        / 
    / 
    
    write ʃ "Suma de matriz:" ʅ ?
    write ʃ suma ʅ ?
    
    return suma?
/ 

string manejarTexto ʃ char inicial ʅ \ 
    string mensaje | "Hola_mundo_desde_MIPS!" ?
    string saludo | "Buenos_dias" ?
    char letra | 'A' ?
    char numero | '7' ?
    
    write ʃ "Mensaje original:" ʅ ?
    write ʃ mensaje ʅ ?
    
    write ʃ "Caracter inicial:" ʅ ?
    write ʃ inicial ʅ ?
    
    write ʃ "Letra:" ʅ ?
    write ʃ letra ʅ ?
    
    write ʃ "Numero como char:" ʅ ?
    write ʃ numero ʅ ?
    
    string especial | "Símbolos: @#$%^&*()_+-=[]{}|;:,.<>?" ?
    write ʃ especial ʅ ?
    
    return mensaje?
/ 

float calcularComplejo ʃ float a, float b ʅ \ 
    float resultado | 0.0?
    
    resultado | a + b * 2.5 - 1.0?
    write ʃ "Suma compleja:" ʅ ?
    write ʃ resultado ʅ ?
    
    resultado | a ** 3.0?
    write ʃ "Potencia:" ʅ ?
    write ʃ resultado ʅ ?
    
    bool mayor | a > b?
    bool igual | a == b?
    bool diferente | a != b?
    
    write ʃ "a > b:" ʅ ?
    write ʃ mayor ʅ ?
    
    bool logico | mayor ^ igual || diferente?
    write ʃ "Resultado lógico:" ʅ ?
    write ʃ logico ʅ ?
    
    return resultado?
/ 

bool probarBooleanos ʃ ʅ \ 
    bool verdadero | luna?  
    bool falso | sol?       
    bool explicitoTrue | true?
    bool explicitoFalse | false?
    
    write ʃ "Luna (true):" ʅ ?
    write ʃ verdadero ʅ ?
    
    write ʃ "Sol (false):" ʅ ?
    write ʃ falso ʅ ?
    
    bool and_result | verdadero ^ falso?
    bool or_result | verdadero # falso?
    bool not_result | !verdadero?
    
    write ʃ "AND:" ʅ ?
    write ʃ and_result ʅ ?
    
    write ʃ "OR:" ʅ ?
    write ʃ or_result ʅ ?
    
    return verdadero?
/ 

int estructurasControl ʃ int limite ʅ \ 
    int contador | 0?
    int i | 0?
    
    if ʃ limite > 10 ʅ \ 
        write ʃ "Limite alto" ʅ ?
        if ʃ limite > 20 ʅ \ 
            write ʃ "Limite muy alto" ʅ ?
        / 
        else \ 
            write ʃ "Limite medio" ʅ ?
        / 
    / 
    else \ 
        write ʃ "Limite bajo" ʅ ?
    / 
    
    while ʃ contador < limite ^ contador < 5 ʅ \ 
        write ʃ "Contador:" ʅ ?
        write ʃ contador ʅ ?
        contador | contador + 1?
    / 
    
    for ʃ i|0? i<limite && i<10? i|i+2 ʅ \ 
        if ʃ i % 2 == 0 ʅ \ 
            write ʃ "Par:" ʅ ?
            write ʃ i ʅ ?
        / 
    / 
    
    return contador?
/ 

int fibonacciModificado ʃ int n, int multiplicador ʅ \ 
    if ʃ n <= 1 ʅ \ 
        return n * multiplicador?
    / 
    else \ 
        int fib1 | fibonacciModificado ʃ n-1, multiplicador ʅ ?
        int fib2 | fibonacciModificado ʃ n-2, multiplicador ʅ ?
        return fib1 + fib2?
    / 
/ 

void main ʃ ʅ \ 
    write ʃ "=== PRUEBA COMPLETA DEL GENERADOR UNIVERSAL ===" ʅ ?
    
    write ʃ "1. Probando arrays..." ʅ ?
    int resultadoArray | procesarArrays ʃ 5 ʅ ?
    
    write ʃ "2. Probando strings y chars..." ʅ ?
    string textoResult | manejarTexto ʃ 'X' ʅ ?
    
    write ʃ "3. Probando operadores complejos..." ʅ ?
    float floatResult | calcularComplejo ʃ 3.14, 2.71 ʅ ?
    
    write ʃ "4. Probando booleanos..." ʅ ?
    bool boolResult | probarBooleanos ʃ ʅ ?
    
    write ʃ "5. Probando estructuras de control..." ʅ ?
    int controlResult | estructurasControl ʃ 15 ʅ ?
    
    write ʃ "6. Probando recursión..." ʅ ?
    int fibResult | fibonacciModificado ʃ 6, 2 ʅ ?
    write ʃ "Fibonacci modificado:" ʅ ?
    write ʃ fibResult ʅ ?
    
    int numeros[3] | 0?
    numeros[0] | 100?
    numeros[1] | 200?
    numeros[2] | 300?
    
    write ʃ "Array[0]:" ʅ ?
    write ʃ numeros[0] ʅ ?
    write ʃ "Array[1]:" ʅ ?
    write ʃ numeros[1] ʅ ?
    write ʃ "Array[2]:" ʅ ?
    write ʃ numeros[2] ʅ ?
    
    int negativo | -42?
    float negativoFloat | -3.14159?
    
    write ʃ "Entero negativo:" ʅ ?
    write ʃ negativo ʅ ?
    write ʃ "Float negativo:" ʅ ?
    write ʃ negativoFloat ʅ ?
    
    write ʃ "=== TODAS LAS PRUEBAS COMPLETADAS ===" ʅ ?
/