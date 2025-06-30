int fibonnaci ʃ int n ʅ \
    if ʃ n <= 1 ʅ \
        return n ?
    /
    return fibonnaci ʃ n - 1 ʅ + fibonnaci ʃ n - 2 ʅ ?
/

void test ʃ ʅ \
    int matrix[4][4] ?
    for ʃ int i|0? i < 4? ++i ʅ \
        for ʃ int j|0? j < 4 ? ++j ʅ \
            matrix[i][j] | fibonnaci ʃ i + j ʅ ?

            if ʃ matrix[i][j] > 6 ʅ \
                break ?
            /
            write ʃ matrix[i][j] ʅ ?
        /
    /
/

void main ʃ ʅ \
    test ʃ ʅ ?
/