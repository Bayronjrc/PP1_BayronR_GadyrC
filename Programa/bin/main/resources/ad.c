// 🎯 PRUEBA SIMPLE - ARRAYS, STRINGS Y CHARS
// Sin recursión, sin funciones complejas, solo lo básico

void testArrays ʃ ʅ \ 
    // Arrays básicos
    int numeros[3] | 0?
    
    // Asignaciones simples
    numeros[0] | 10?
    numeros[1] | 20?
    numeros[2] | 30?
    
    // Lectura de arrays
    int primero | numeros[0]?
    int segundo | numeros[1]?
    int tercero | numeros[2]?
    
    // Imprimir valores
    write ʃ "Array[0]:" ʅ ?
    write ʃ primero ʅ ?
    
    write ʃ "Array[1]:" ʅ ?
    write ʃ segundo ʅ ?
    
    write ʃ "Array[2]:" ʅ ?
    write ʃ tercero ʅ ?
/ 

void testStrings ʃ ʅ \ 
    // Strings literales
    string mensaje | "Hola_mundo!" ?
    string saludo | "Buenos_dias" ?
    
    // Chars básicos
    char letra | 'A' ?
    char numero | '5' ?
    char simbolo | '@' ?
    
    // Imprimir strings
    write ʃ "Mensaje:" ʅ ?
    write ʃ mensaje ʅ ?
    
    write ʃ "Saludo:" ʅ ?
    write ʃ saludo ʅ ?
    
    // Imprimir chars
    write ʃ "Letra:" ʅ ?
    write ʃ letra ʅ ?
    
    write ʃ "Numero:" ʅ ?
    write ʃ numero ʅ ?
    
    write ʃ "Simbolo:" ʅ ?
    write ʃ simbolo ʅ ?
/ 

void testMixed ʃ ʅ \ 
    // Array de diferentes tipos
    int valores[2] | 0?
    valores[0] | 100?
    valores[1] | 200?
    
    // String con caracteres especiales
    string especial | "Test_123_!@#" ?
    
    // Char con ASCII
    char espacio | ' ' ?
    char punto | '.' ?
    
    // Operaciones simples
    int suma | valores[0] + valores[1]?
    
    // Output mixto
    write ʃ "=== PRUEBA MIXTA ===" ʅ ?
    write ʃ especial ʅ ?
    write ʃ "Suma:" ʅ ?
    write ʃ suma ʅ ?
    write ʃ "Caracteres:" ʅ ?
    write ʃ espacio ʅ ?
    write ʃ punto ʅ ?
/ 

void main ʃ ʅ \ 
    write ʃ "=== INICIANDO PRUEBAS ===" ʅ ?
    
    write ʃ "1. Probando arrays..." ʅ ?
    testArrays ʃ ʅ ?
    
    write ʃ "2. Probando strings y chars..." ʅ ?
    testStrings ʃ ʅ ?
    
    write ʃ "3. Probando mixto..." ʅ ?
    testMixed ʃ ʅ ?
    
    write ʃ "=== PRUEBAS COMPLETADAS ===" ʅ ?
/