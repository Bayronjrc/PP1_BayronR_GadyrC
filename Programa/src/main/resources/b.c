int factorial ʃ int n ʅ \
    if ʃ n <= 1 ʅ \
        return 1 ?
    /
    return n * factorial ʃ n - 1 ʅ ?
/

void testLopps ʃ ʅ \
    int x | 10?
    do \
        write ʃ x ʅ ?
        x | x - 1 ?

        if ʃ x == 5 ʅ \
            continue ?
        /

        if ʃ x == 2 ʅ \
            break ?
        /

    / while ʃ x > 0ʅ ?

    int option | 3?
    switch ʃ option ʅ \
        case 1: 
            write ʃ 100 ʅ ? 
            break ? 
        case 2: 
            write ʃ 200 ʅ ? 
            break ? 
        case 3: 
            write ʃ 300 ʅ ?
            break ?
        default: 
            write ʃ 999 ʅ ? 
            break ?
    /
/

void testArrays ʃ ʅ \
    int arr[3][3] | [[1,2,3],[4,5,6],[7,8,9]]?
    for ʃ int i|0? i < 3? ++i ʅ \
        for ʃ int j|0? j < 3 ? ++j ʅ \
            read ʃ arr[i][j] ʅ ?

            if ʃ arr[i][j] > 5 ʅ \
                arr[i][j] | factorial ʃ arr[i][j] ʅ?
            /
            write ʃ arr[i][j] ʅ ?
        /
    /
/

void main ʃ ʅ \
    testLopps ʃ ʅ ?
    testArrays ʃ ʅ ?
/