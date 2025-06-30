int suma ʃ int a, int b ʅ \
    int resultado | a * b ?
    return resultado ?
/

int factorial ʃ int n ʅ \
    if ʃ n <= 1 ʅ \
        return 1 ?
    /
    int temp | n - 1 ?
    int recurse | factorial ʃ temp ʅ ?
    int resultado | n * recurse ?
    return resultado ?
/

void testBasico ʃ ʅ \
    int x | 4 ?
    int y | 4 ?
    
    int resultado1 | suma ʃ x, y ʅ ?
    write ʃ resultado1 ʅ ? 
    
    int resultado2 | factorial ʃ 2 ʅ ?
    write ʃ resultado2 ʅ ? 
    

    for ʃ int k | 0 ? k < 7 ? ++k ʅ \
        write ʃ k ʅ ?
    /
/

void main ʃ ʅ \
    testBasico ʃ ʅ ?
/