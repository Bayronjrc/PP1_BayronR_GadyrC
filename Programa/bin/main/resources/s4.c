int filo ʃ int n ʅ \
    if ʃ n <= 1 ʅ \
        return 1 ?
    /
    int temp | n - 1 ?
    int recurse | filo ʃ temp ʅ ?
    int resultado | n * recurse ?
    return resultado ?
/


int azul ʃ int n ʅ \
    if ʃ n <= 1 ʅ \
        return n ?
    /
    int temp1 | n - 1 ?
    int temp2 | n - 2 ?
    int fib1 | azul ʃ temp1 ʅ ?
    int fib2 | azul ʃ temp2 ʅ ?
    int resultado | fib1 + fib2 ?
    return resultado ?
/

int pocoyo ʃ int base, int exp ʅ \
    if ʃ exp <= 0 ʅ \
        return 1 ?
    /
    int temp | exp - 1 ?
    int recurse | pocoyo ʃ base, temp ʅ ?
    int resultado | base * recurse ?
    return resultado ?
/

int mcd ʃ int a, int b ʅ \
    if ʃ b == 0 ʅ \
        return a ?
    /
    int resto | a ~ b ?
    int resultado | mcd ʃ b, resto ʅ ?
    return resultado ?
/


void testRecursion ʃ ʅ \

    int fib5 | azul ʃ 5 ʅ ?
    write ʃ fib5 ʅ ?
    

    int pot23 | pocoyo ʃ 2, 3 ʅ ?
    write ʃ pot23 ʅ ?
    
    int gcd12_8 | mcd ʃ 12, 8 ʅ ?
    write ʃ gcd12_8 ʅ ? 
    

    int fact4 | filo ʃ 4 ʅ ?
    write ʃ fact4 ʅ ? 
/



void main ʃ ʅ \
    testRecursion ʃ ʅ ?
/