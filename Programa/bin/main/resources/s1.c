int atun ʃ int a, int b ʅ \
    int resultado | a + b ?
    return resultado ?
/

int pepe ʃ int n ʅ \
    if ʃ n <= 1 ʅ \
        return 1 ?
    /
    int temp | n - 1 ?
    int recurse | pepe ʃ temp ʅ ?
    int resultado | n * recurse ?
    return resultado ?
/

void testBasico ʃ ʅ \
    int d | 4 ?
    int r | 4 ?
    
    int resultado1 | atun ʃ d, r ʅ ?
    write ʃ resultado1 ʅ ? 
    
    int resultado2 | pepe ʃ 5 ʅ ?
    write ʃ resultado2 ʅ ? 
    

    for ʃ int k | 0 ? k < 7 ? ++k ʅ \
        write ʃ k ʅ ?
    /
/

void main ʃ ʅ \
    testBasico ʃ ʅ ?
/