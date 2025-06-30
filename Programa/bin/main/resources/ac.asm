# ========================================
# CÓDIGO MIPS UNIVERSAL - SIN HARDCODEO
# Funciona con cualquier nombre de función
# Autores: Bayron Rodríguez & Gadir Calderón
# ========================================

.data
    # Strings del sistema
    nl:           .asciiz "\n"
    prompt_int:   .asciiz "Ingrese un entero: "
    prompt_float: .asciiz "Ingrese un float: "
    prompt_string: .asciiz "Ingrese texto: "
    result_msg:   .asciiz "Resultado: "
    true_str:     .asciiz "true"
    false_str:    .asciiz "false"

    # Constantes booleanas del lenguaje
    true_const:   .word 1
    false_const:  .word 0
    luna_const:   .word 1    # luna = true
    sol_const:    .word 0    # sol = false

    # Variables del programa
    floatResult_var: .word 0
    not_result_var: .word 0
    fibResult_var: .word 0
    resultadoArray_var: .word 0
    t10_var: .word 0
    t12_var: .word 0
    t11_var: .word 0
    t14_var: .word 0
    t13_var: .word 0
    t16_var: .word 0
    boolResult_var: .word 0
    t15_var: .word 0
    t18_var: .word 0
    t17_var: .word 0
    t19_var: .word 0
    explicitoTrue_var: .word 0
    textoResult_var: .word 0
    mayor_var: .word 0
    letra_var: .word 0
    especial_var: .word 0
    t21_var: .word 0
    t20_var: .word 0
    t23_var: .word 0
    t22_var: .word 0
    t25_var: .word 0
    t24_var: .word 0
    fib2_var: .word 0
    fib1_var: .word 0
    t1_var: .word 0
    t2_var: .word 0
    t3_var: .word 0
    suma_var: .word 0
    t4_var: .word 0
    t5_var: .word 0
    t6_var: .word 0
    t7_var: .word 0
    numero_var: .word 0
    t8_var: .word 0
    t9_var: .word 0
    verdadero_var: .word 0
    contador_var: .word 0
    or_result_var: .word 0
    X_var: .word 0
    negativo_var: .word 0
    resultado_var: .word 0
    saludo_var: .word 0
    falso_var: .word 0
    i_var: .word 0
    diferente_var: .word 0
    j_var: .word 0
    and_result_var: .word 0
    n_var: .word 0
    negativoFloat_var: .word 0
    controlResult_var: .word 0
    igual_var: .word 0
    mensaje_var: .word 0
    explicitoFalse_var: .word 0

.text
.globl main

main:
    # Función main generada automáticamente
    jal procesarArrays
    li $v0, 10
    syscall

    # // Código Intermedio Generado
    # // Archivo: src/main/resources/ac_intermediate.txt
procesarArrays:
    # UNIVERSAL: Prólogo para procesarArrays
    addi $sp, $sp, -8
    sw $ra, 4($sp)
    sw $fp, 0($sp)
    move $fp, $sp
    # Reservar espacio para variables locales
    addi $sp, $sp, -16

    # Función sin parámetros o no detectados

    # Inicio de función
    # DECLARE n INT
    # DECLARE i INT
    # i = 0
    li $t0, 0
    sw $t0, i_var

    # i = 0
    li $t0, 0
    sw $t0, i_var

    # DECLARE j INT
    # j = 0
    li $t0, 0
    sw $t0, j_var

    # j = 0
    li $t0, 0
    sw $t0, j_var

    # i = 0
    li $t0, 0
    sw $t0, i_var

    # j = 0
    li $t0, 0
    sw $t0, j_var

    # i = 0
    li $t0, 0
    sw $t0, i_var

    # DECLARE suma INT
    # suma = 0
    li $t0, 0
    sw $t0, suma_var

    # i = 0
    li $t0, 0
    sw $t0, i_var

    # j = 0
    li $t0, 0
    sw $t0, j_var

    # DECLARE mensaje STRING
    # DECLARE saludo STRING
    # DECLARE letra CHAR
    # DECLARE numero CHAR
    # numero = 7
    li $t0, 7
    sw $t0, numero_var

    # DECLARE especial STRING
    # DECLARE resultado FLOAT
    # DECLARE mayor BOOL
    # DECLARE igual BOOL
    # DECLARE diferente BOOL
    # DECLARE verdadero BOOL
    # DECLARE falso BOOL
    # DECLARE explicitoTrue BOOL
    # DECLARE explicitoFalse BOOL
    # DECLARE and_result BOOL
    # DECLARE or_result BOOL
    # DECLARE not_result BOOL
    # DECLARE contador INT
    # contador = 0
    li $t0, 0
    sw $t0, contador_var

    # i = 0
    li $t0, 0
    sw $t0, i_var

    # t19 = n <= 1
    lw $t1, n_var
    li $t2, 1
    # n <= 1
    sub $t3, $t2, $t1    # t2 - t1
    bgez $t3, set_true_le_1
    li $t0, 0
    j end_le_1
set_true_le_1:
    li $t0, 1
end_le_1:
    sw $t0, t19_var

    # DECLARE fib1 INT
    # DECLARE fib2 INT
    # IF NOT t19 GOTO L1
    lw $t0, t19_var
    beq $t0, $zero, L1

    # RETURN suma
    lw $v0, suma_var
    # Valor de retorno en $v0
    j exit_procesarArrays

    # RETURN mensaje
    lw $v0, mensaje_var
    # Valor de retorno en $v0
    j exit_procesarArrays

    # RETURN resultado
    lw $v0, resultado_var
    # Valor de retorno en $v0
    j exit_procesarArrays

    # RETURN verdadero
    lw $v0, verdadero_var
    # Valor de retorno en $v0
    j exit_procesarArrays

    # RETURN contador
    lw $v0, contador_var
    # Valor de retorno en $v0
    j exit_procesarArrays

    # RETURN n
    lw $v0, n_var
    # Valor de retorno en $v0
    j exit_procesarArrays

    # RETURN t22
    lw $v0, t22_var
    # Valor de retorno en $v0
    j exit_procesarArrays

    j L2

L1:
    # t1 = i < 5
    lw $t1, i_var
    li $t2, 5
    # i < 5
    sub $t3, $t1, $t2    # t1 - t2
    bltz $t3, set_true_lt_2
    li $t0, 0
    j end_lt_2
set_true_lt_2:
    li $t0, 1
end_lt_2:
    sw $t0, t1_var

    # t2 = j < 3
    lw $t1, j_var
    li $t2, 3
    # j < 3
    sub $t3, $t1, $t2    # t1 - t2
    bltz $t3, set_true_lt_3
    li $t0, 0
    j end_lt_3
set_true_lt_3:
    li $t0, 1
end_lt_3:
    sw $t0, t2_var

    # t3 = i * j
    lw $t1, i_var
    lw $t2, j_var
    mult $t1, $t2
    mflo $t0
    sw $t0, t3_var

    # t4 = t3 + 1
    lw $t1, t3_var
    li $t2, 1
    add $t0, $t1, $t2
    sw $t0, t4_var

    # t5 = i < 10
    lw $t1, i_var
    li $t2, 10
    # i < 10
    sub $t3, $t1, $t2    # t1 - t2
    bltz $t3, set_true_lt_4
    li $t0, 0
    j end_lt_4
set_true_lt_4:
    li $t0, 1
end_lt_4:
    sw $t0, t5_var

    # t6 = i ** 2
    lw $t1, i_var
    li $t2, 2
    # Potencia: i ** 2
    move $a0, $t1
    move $a1, $t2
    jal power_function
    move $t0, $v0
    sw $t0, t6_var

    # t7 = i < 5
    lw $t1, i_var
    li $t2, 5
    # i < 5
    sub $t3, $t1, $t2    # t1 - t2
    bltz $t3, set_true_lt_5
    li $t0, 0
    j end_lt_5
set_true_lt_5:
    li $t0, 1
end_lt_5:
    sw $t0, t7_var

    # t8 = j < 3
    lw $t1, j_var
    li $t2, 3
    # j < 3
    sub $t3, $t1, $t2    # t1 - t2
    bltz $t3, set_true_lt_6
    li $t0, 0
    j end_lt_6
set_true_lt_6:
    li $t0, 1
end_lt_6:
    sw $t0, t8_var

    # t9 = 2.5 == 1.0
    li $t1, 250    # Float 2.5 (*100)
    li $t2, 100    # Float 1.0 (*100)
    # 2.5 == 1.0
    sub $t3, $t1, $t2    # t1 - t2
    beq $t3, $zero, set_true_eq_7
    li $t0, 0
    j end_eq_7
set_true_eq_7:
    li $t0, 1
end_eq_7:
    sw $t0, t9_var

    # t10 = i != j
    lw $t1, i_var
    lw $t2, j_var
    # i != j
    sub $t3, $t1, $t2    # t1 - t2
    bne $t3, $zero, set_true_ne_8
    li $t0, 0
    j end_ne_8
set_true_ne_8:
    li $t0, 1
end_ne_8:
    sw $t0, t10_var

    # t11 = verdadero && falso
    lw $t1, verdadero_var
    lw $t2, falso_var
    # AND lógico
    and $t0, $t1, $t2
    sw $t0, t11_var

    # t12 = verdadero || falso
    lw $t1, verdadero_var
    lw $t2, falso_var
    # OR lógico
    or $t0, $t1, $t2
    sw $t0, t12_var

    # t13 = !verdadero
    lw $t1, verdadero_var
    xori $t0, $t1, 1  # NOT lógico
    sw $t0, t13_var

    # t14 = contador < 5
    lw $t1, contador_var
    li $t2, 5
    # contador < 5
    sub $t3, $t1, $t2    # t1 - t2
    bltz $t3, set_true_lt_9
    li $t0, 0
    j end_lt_9
set_true_lt_9:
    li $t0, 1
end_lt_9:
    sw $t0, t14_var

    # t15 = contador + 1
    lw $t1, contador_var
    li $t2, 1
    add $t0, $t1, $t2
    sw $t0, t15_var

    # t16 = i < 10
    lw $t1, i_var
    li $t2, 10
    # i < 10
    sub $t3, $t1, $t2    # t1 - t2
    bltz $t3, set_true_lt_10
    li $t0, 0
    j end_lt_10
set_true_lt_10:
    li $t0, 1
end_lt_10:
    sw $t0, t16_var

    # t17 = i + 2
    lw $t1, i_var
    li $t2, 2
    add $t0, $t1, $t2
    sw $t0, t17_var

    # t18 = 2 == 0
    li $t1, 2
    li $t2, 0
    # 2 == 0
    sub $t3, $t1, $t2    # t1 - t2
    beq $t3, $zero, set_true_eq_11
    li $t0, 0
    j end_eq_11
set_true_eq_11:
    li $t0, 1
end_eq_11:
    sw $t0, t18_var

    # t20 = n - 1
    lw $t1, n_var
    li $t2, 1
    sub $t0, $t1, $t2
    sw $t0, t20_var

    # t21 = n - 2
    lw $t1, n_var
    li $t2, 2
    sub $t0, $t1, $t2
    sw $t0, t21_var

    # t22 = fib1 + fib2
    lw $t1, fib1_var
    lw $t2, fib2_var
    add $t0, $t1, $t2
    sw $t0, t22_var

L2:
    # WRITE === PRUEBA COMPLETA DEL GENERADOR UNIVERSAL ===
    li $a0, 0    # ERROR: No se pudo procesar '=== PRUEBA COMPLETA DEL GENERADOR UNIVERSAL ==='
    jal print_int
    la $a0, nl
    jal print_string

    # WRITE 1. Probando arrays...
    la $a0, str_3
    jal print_string
    la $a0, nl
    jal print_string

    # PARAM 5
    li $a0, 5
    #  UNIVERSAL: Parámetro 5 cargado en $a0

    # t23 = CALL procesarArrays 1
    jal procesarArrays
    sw $v0, t23_var

    # DECLARE resultadoArray INT
    # resultadoArray = t23
    lw $t0, t23_var
    sw $t0, resultadoArray_var

    # WRITE 2. Probando strings y chars...
    la $a0, str_4
    jal print_string
    la $a0, nl
    jal print_string

    # DECLARE textoResult STRING
    # textoResult = X
    lw $t0, X_var
    sw $t0, textoResult_var

    # WRITE 3. Probando operadores complejos...
    la $a0, str_5
    jal print_string
    la $a0, nl
    jal print_string

    # DECLARE floatResult FLOAT
    # floatResult = 2.71
    li $t0, 271    # Float 2.71 (*100)
    sw $t0, floatResult_var

    # WRITE 4. Probando booleanos...
    la $a0, str_6
    jal print_string
    la $a0, nl
    jal print_string

    # DECLARE boolResult BOOL
    # boolResult = 3.14
    li $t0, 314    # Float 3.14 (*100)
    sw $t0, boolResult_var

    # WRITE 5. Probando estructuras de control...
    la $a0, str_7
    jal print_string
    la $a0, nl
    jal print_string

    # DECLARE controlResult INT
    # controlResult = 15
    li $t0, 15
    sw $t0, controlResult_var

    # WRITE 6. Probando recursión...
    la $a0, str_8
    jal print_string
    la $a0, nl
    jal print_string

    # DECLARE fibResult INT
    # fibResult = 2
    li $t0, 2
    sw $t0, fibResult_var

    # WRITE Fibonacci modificado
    la $a0, str_15
    jal print_string
    la $a0, nl
    jal print_string

    # WRITE fibResult
    lw $a0, fibResult_var
    jal print_int
    la $a0, nl
    jal print_string

    # WRITE Array[0]
    la $a0, str_16
    jal print_string
    la $a0, nl
    jal print_string

    # WRITE Array[1]
    la $a0, str_17
    jal print_string
    la $a0, nl
    jal print_string

    # WRITE Array[2]
    la $a0, str_18
    jal print_string
    la $a0, nl
    jal print_string

    # t24 = -42
    li $t0, -42
    sw $t0, t24_var

    # DECLARE negativo INT
    # negativo = t24
    lw $t0, t24_var
    sw $t0, negativo_var

    # t25 = -3.14159
    li $t0, -314    # Float -3.14159 (*100)
    sw $t0, t25_var

    # DECLARE negativoFloat FLOAT
    # negativoFloat = t25
    lw $t0, t25_var
    sw $t0, negativoFloat_var

    # WRITE Entero negativo
    la $a0, str_19
    jal print_string
    la $a0, nl
    jal print_string

    # WRITE negativo
    lw $a0, negativo_var
    jal print_int
    la $a0, nl
    jal print_string

    # WRITE Float negativo
    la $a0, str_20
    jal print_string
    la $a0, nl
    jal print_string

    # WRITE negativoFloat
    lw $a0, negativoFloat_var
    jal print_float_decimal
    la $a0, nl
    jal print_string

    # WRITE === TODAS LAS PRUEBAS COMPLETADAS ===
    li $a0, 0    # ERROR: No se pudo procesar '=== TODAS LAS PRUEBAS COMPLETADAS ==='
    jal print_int
    la $a0, nl
    jal print_string


# Salida del programa
exit_program:
    li $v0, 10
    syscall
# ========================================
# FUNCIONES DEL SISTEMA
# ========================================

print_int:
    li $v0, 1
    syscall
    jr $ra

print_string:
    li $v0, 4
    syscall
    jr $ra

read_int:
    li $v0, 5
    syscall
    jr $ra

read_float:
    li $v0, 6
    syscall
    jr $ra

print_float_decimal:
    # $a0 contiene el flotante multiplicado por 100
    addi $sp, $sp, -4
    sw $a0, 0($sp)
    bgez $a0, positive_float
    li $v0, 11
    li $a0, 45    # ASCII de '-'
    syscall
    lw $a0, 0($sp)
    sub $a0, $zero, $a0
positive_float:
    li $t1, 100
    div $a0, $t1
    mflo $t2
    mfhi $t3
    move $a0, $t2
    li $v0, 1
    syscall
    li $v0, 11
    li $a0, 46    # ASCII de '.'
    syscall
    bge $t3, 10, print_decimal
    li $v0, 11
    li $a0, 48    # ASCII de '0'
    syscall
print_decimal:
    move $a0, $t3
    li $v0, 1
    syscall
    addi $sp, $sp, 4
    jr $ra

