# ========================================
# CÓDIGO MIPS GENERADO AUTOMÁTICAMENTE
# Compilador - Proyecto 3
# Autores: Bayron Rodríguez & Gadir Calderón
# ========================================

.data
    # Strings del sistema
    nl:           .asciiz "\n"
    prompt_int:   .asciiz "Ingrese un entero: "
    prompt_float: .asciiz "Ingrese un float: "
    result_msg:   .asciiz "Resultado: "
    true_str:     .asciiz "true"
    false_str:    .asciiz "false"

    # Variables del programa
    t4_var: .word 0
    t5_var: .word 0
    t6_var: .word 0
    fact4_var: .word 0
    t7_var: .word 0
    t8_var: .word 0
    t9_var: .word 0
    pot23_var: .word 0
    gcd12_8_var: .word 0
    temp2_var: .word 0
    temp1_var: .word 0
    t10_var: .word 0
    t12_var: .word 0
    t11_var: .word 0
    t14_var: .word 0
    t13_var: .word 0
    t16_var: .word 0
    t15_var: .word 0
    t18_var: .word 0
    t17_var: .word 0
    exp_var: .word 0
    t19_var: .word 0
    a_var: .word 0
    temp_var: .word 0
    b_var: .word 0
    resultado_var: .word 0
    resto_var: .word 0
    n_var: .word 0
    t21_var: .word 0
    t20_var: .word 0
    recurse_var: .word 0
    fib5_var: .word 0
    fib2_var: .word 0
    t1_var: .word 0
    fib1_var: .word 0
    t2_var: .word 0
    t3_var: .word 0
    base_var: .word 0

.text
.globl main

    # // Código Intermedio Generado
    # // Archivo: src/main/resources/s4_intermediate.txt
factorial:
    # Prólogo estándar factorial
    addi $sp, $sp, -8
    sw $ra, 4($sp)
    sw $fp, 0($sp)
    move $fp, $sp
    # Reservar espacio para variables locales
    addi $sp, $sp, -16

    # Función genérica - guardar hasta 4 parámetros en stack frame
    sw $a0, -4($fp)   # param1 local
    sw $a1, -8($fp)   # param2 local
    sw $a2, -12($fp)  # param3 local
    sw $a3, -16($fp)  # param4 local

    # Inicio de función
    # DECLARE n INT
    # t1 = n <= 1
    lw $t1, -4($fp)   # n local
    li $t2, 1
    sle $t0, $t1, $t2
    sw $t0, t1_var

    # IF NOT t1 GOTO L1
    lw $t0, t1_var
    beq $t0, $zero, L1

    # RETURN 1
    li $v0, 1
    # Valor de retorno en $v0
    j exit_factorial

L1:
    # t2 = n - 1
    lw $t1, -4($fp)   # n local
    li $t2, 1
    sub $t0, $t1, $t2
    sw $t0, t2_var

    # DECLARE temp INT
    # temp = t2
    lw $t0, t2_var
    sw $t0, temp_var

    # PARAM temp
    lw $a0, temp_var
    # ✅ FIXED: Parámetro temp cargado en $a0

    # t3 = CALL factorial 1
    jal factorial
    sw $v0, t3_var

    # DECLARE recurse INT
    # recurse = t3
    lw $t0, t3_var
    sw $t0, recurse_var

    # t4 = n * recurse
    lw $t1, -4($fp)   # n local
    lw $t2, recurse_var
    mul $t0, $t1, $t2
    sw $t0, t4_var

    # DECLARE resultado INT
    # resultado = t4
    lw $t0, t4_var
    sw $t0, resultado_var

    # RETURN resultado
    lw $v0, resultado_var
    # Valor de retorno en $v0
    j exit_factorial


# Epílogo estándar factorial
exit_factorial:
    # Limpiar variables locales
    addi $sp, $sp, 16    # Liberar espacio de variables locales
    # Restaurar frame pointer y return address
    move $sp, $fp
    lw $fp, 0($sp)
    lw $ra, 4($sp)
    addi $sp, $sp, 8
    jr $ra

fibonacci:
    # Prólogo estándar fibonacci
    addi $sp, $sp, -8
    sw $ra, 4($sp)
    sw $fp, 0($sp)
    move $fp, $sp
    # Reservar espacio para variables locales
    addi $sp, $sp, -16

    # Guardar parámetros en stack frame local
    sw $a0, -4($fp)   # n local (CRÍTICO para recursión)
    sw $a0, n_var     # n global

    # Inicio de función
    # DECLARE n INT
    # t5 = n <= 1
    lw $t1, -4($fp)   # n local
    li $t2, 1
    sle $t0, $t1, $t2
    sw $t0, t5_var

    # IF NOT t5 GOTO L2
    lw $t0, t5_var
    beq $t0, $zero, L2

    # RETURN n
    lw $v0, -4($fp)   # n local
    # Valor de retorno en $v0
    j exit_fibonacci

L2:
    # t6 = n - 1
    lw $t1, -4($fp)   # n local
    li $t2, 1
    sub $t0, $t1, $t2
    sw $t0, t6_var

    # DECLARE temp1 INT
    # temp1 = t6
    lw $t0, t6_var
    sw $t0, temp1_var

    # t7 = n - 2
    lw $t1, -4($fp)   # n local
    li $t2, 2
    sub $t0, $t1, $t2
    sw $t0, t7_var

    # DECLARE temp2 INT
    # temp2 = t7
    lw $t0, t7_var
    sw $t0, temp2_var

    # PARAM temp1
    lw $a0, temp1_var
    # ✅ FIXED: Parámetro temp1 cargado en $a0

    # t8 = CALL fibonacci 1
    jal fibonacci
    sw $v0, t8_var

    # DECLARE fib1 INT
    # fib1 = t8
    lw $t0, t8_var
    sw $t0, fib1_var

    # PARAM temp2
    lw $a0, temp2_var
    # ✅ FIXED: Parámetro temp2 cargado en $a0

    # t9 = CALL fibonacci 1
    jal fibonacci
    sw $v0, t9_var

    # DECLARE fib2 INT
    # fib2 = t9
    lw $t0, t9_var
    sw $t0, fib2_var

    # t10 = fib1 + fib2
    lw $t1, fib1_var
    lw $t2, fib2_var
    add $t0, $t1, $t2
    sw $t0, t10_var

    # DECLARE resultado INT
    # resultado = t10
    lw $t0, t10_var
    sw $t0, resultado_var

    # RETURN resultado
    lw $v0, resultado_var
    # Valor de retorno en $v0
    j exit_fibonacci


# Epílogo estándar fibonacci
exit_fibonacci:
    # Limpiar variables locales
    addi $sp, $sp, 16    # Liberar espacio de variables locales
    # Restaurar frame pointer y return address
    move $sp, $fp
    lw $fp, 0($sp)
    lw $ra, 4($sp)
    addi $sp, $sp, 8
    jr $ra

potencia:
    # Prólogo estándar potencia
    addi $sp, $sp, -8
    sw $ra, 4($sp)
    sw $fp, 0($sp)
    move $fp, $sp
    # Reservar espacio para variables locales
    addi $sp, $sp, -16

    # Guardar parámetros en stack frame local
    sw $a0, -4($fp)   # base local (CRÍTICO para recursión)
    sw $a1, -8($fp)   # exp local (CRÍTICO para recursión)
    sw $a0, base_var  # base global
    sw $a1, exp_var   # exp global

    # Inicio de función
    # DECLARE base INT
    # DECLARE exp INT
    # t11 = exp <= 0
    lw $t1, exp_var
    li $t2, 0
    sle $t0, $t1, $t2
    sw $t0, t11_var

    # IF NOT t11 GOTO L3
    lw $t0, t11_var
    beq $t0, $zero, L3

    # RETURN 1
    li $v0, 1
    # Valor de retorno en $v0
    j exit_potencia

L3:
    # t12 = exp - 1
    lw $t1, exp_var
    li $t2, 1
    sub $t0, $t1, $t2
    sw $t0, t12_var

    # DECLARE temp INT
    # temp = t12
    lw $t0, t12_var
    sw $t0, temp_var

    # PARAM base
    lw $a0, base_var
    # ✅ FIXED: Parámetro base cargado en $a0

    # PARAM temp
    lw $a1, temp_var
    # ✅ FIXED: Parámetro temp cargado en $a1

    # t13 = CALL potencia 2
    jal potencia
    sw $v0, t13_var

    # DECLARE recurse INT
    # recurse = t13
    lw $t0, t13_var
    sw $t0, recurse_var

    # t14 = base * recurse
    lw $t1, base_var
    lw $t2, recurse_var
    mul $t0, $t1, $t2
    sw $t0, t14_var

    # DECLARE resultado INT
    # resultado = t14
    lw $t0, t14_var
    sw $t0, resultado_var

    # RETURN resultado
    lw $v0, resultado_var
    # Valor de retorno en $v0
    j exit_potencia


# Epílogo estándar potencia
exit_potencia:
    # Limpiar variables locales
    addi $sp, $sp, 16    # Liberar espacio de variables locales
    # Restaurar frame pointer y return address
    move $sp, $fp
    lw $fp, 0($sp)
    lw $ra, 4($sp)
    addi $sp, $sp, 8
    jr $ra

mcd:
    # Prólogo estándar mcd
    addi $sp, $sp, -8
    sw $ra, 4($sp)
    sw $fp, 0($sp)
    move $fp, $sp
    # Reservar espacio para variables locales
    addi $sp, $sp, -16

    # Guardar parámetros en stack frame local
    sw $a0, -4($fp)   # a local (CRÍTICO para recursión)
    sw $a1, -8($fp)   # b local (CRÍTICO para recursión)
    sw $a0, a_var     # a global
    sw $a1, b_var     # b global

    # Inicio de función
    # DECLARE a INT
    # DECLARE b INT
    # t15 = b == 0
    lw $t1, -8($fp)   # b local
    li $t2, 0
    seq $t0, $t1, $t2
    sw $t0, t15_var

    # IF NOT t15 GOTO L4
    lw $t0, t15_var
    beq $t0, $zero, L4

    # RETURN a
    lw $v0, -4($fp)   # a local
    # Valor de retorno en $v0
    j exit_mcd

L4:
    # t16 = a % b
    lw $t1, -4($fp)   # a local
    lw $t2, -8($fp)   # b local
    div $t1, $t2
    mfhi $t0  # Resto del módulo
    sw $t0, t16_var

    # DECLARE resto INT
    # resto = t16
    lw $t0, t16_var
    sw $t0, resto_var

    # PARAM b
    lw $a0, -8($fp)   # b local
    # ✅ FIXED: Parámetro b cargado en $a0

    # PARAM resto
    lw $a1, resto_var
    # ✅ FIXED: Parámetro resto cargado en $a1

    # t17 = CALL mcd 2
    jal mcd
    sw $v0, t17_var

    # DECLARE resultado INT
    # resultado = t17
    lw $t0, t17_var
    sw $t0, resultado_var

    # RETURN resultado
    lw $v0, resultado_var
    # Valor de retorno en $v0
    j exit_mcd


# Epílogo estándar mcd
exit_mcd:
    # Limpiar variables locales
    addi $sp, $sp, 16    # Liberar espacio de variables locales
    # Restaurar frame pointer y return address
    move $sp, $fp
    lw $fp, 0($sp)
    lw $ra, 4($sp)
    addi $sp, $sp, 8
    jr $ra

testRecursion:
    # Prólogo estándar testRecursion
    addi $sp, $sp, -8
    sw $ra, 4($sp)
    sw $fp, 0($sp)
    move $fp, $sp
    # Reservar espacio para variables locales
    addi $sp, $sp, -16

    # Función genérica - guardar hasta 4 parámetros en stack frame
    sw $a0, -4($fp)   # param1 local
    sw $a1, -8($fp)   # param2 local
    sw $a2, -12($fp)  # param3 local
    sw $a3, -16($fp)  # param4 local

    # Inicio de función
    # PARAM 5
    li $a0, 5
    # ✅ FIXED: Parámetro 5 cargado en $a0

    # t18 = CALL fibonacci 1
    jal fibonacci
    sw $v0, t18_var

    # DECLARE fib5 INT
    # fib5 = t18
    lw $t0, t18_var
    sw $t0, fib5_var

    # WRITE fib5
    lw $a0, fib5_var
    jal print_int
    la $a0, nl
    jal print_string

    # PARAM 2
    li $a0, 2
    # ✅ FIXED: Parámetro 2 cargado en $a0

    # PARAM 3
    li $a1, 3
    # ✅ FIXED: Parámetro 3 cargado en $a1

    # t19 = CALL potencia 2
    jal potencia
    sw $v0, t19_var

    # DECLARE pot23 INT
    # pot23 = t19
    lw $t0, t19_var
    sw $t0, pot23_var

    # WRITE pot23
    lw $a0, pot23_var
    jal print_int
    la $a0, nl
    jal print_string

    # PARAM 12
    li $a0, 12
    # ✅ FIXED: Parámetro 12 cargado en $a0

    # PARAM 8
    li $a1, 8
    # ✅ FIXED: Parámetro 8 cargado en $a1

    # t20 = CALL mcd 2
    jal mcd
    sw $v0, t20_var

    # DECLARE gcd12_8 INT
    # gcd12_8 = t20
    lw $t0, t20_var
    sw $t0, gcd12_8_var

    # WRITE gcd12_8
    lw $a0, gcd12_8_var
    jal print_int
    la $a0, nl
    jal print_string

    # PARAM 4
    li $a0, 4
    # ✅ FIXED: Parámetro 4 cargado en $a0

    # t21 = CALL factorial 1
    jal factorial
    sw $v0, t21_var

    # DECLARE fact4 INT
    # fact4 = t21
    lw $t0, t21_var
    sw $t0, fact4_var

    # WRITE fact4
    lw $a0, fact4_var
    jal print_int
    la $a0, nl
    jal print_string


# Epílogo estándar testRecursion
exit_testRecursion:
    # Limpiar variables locales
    addi $sp, $sp, 16    # Liberar espacio de variables locales
    # Restaurar frame pointer y return address
    move $sp, $fp
    lw $fp, 0($sp)
    lw $ra, 4($sp)
    addi $sp, $sp, 8
    jr $ra

main:
    # Prólogo estándar main
    addi $sp, $sp, -8
    sw $ra, 4($sp)
    sw $fp, 0($sp)
    move $fp, $sp
    # Reservar espacio para variables locales
    addi $sp, $sp, -16

    # Función genérica - guardar hasta 4 parámetros en stack frame
    sw $a0, -4($fp)   # param1 local
    sw $a1, -8($fp)   # param2 local
    sw $a2, -12($fp)  # param3 local
    sw $a3, -16($fp)  # param4 local

    # Inicio de función
    # CALL testRecursion 0
    jal testRecursion


# Epílogo estándar main
exit_main:
    # Limpiar variables locales
    addi $sp, $sp, 16    # Liberar espacio de variables locales
    # Restaurar frame pointer y return address
    move $sp, $fp
    lw $fp, 0($sp)
    lw $ra, 4($sp)
    addi $sp, $sp, 8
    li $v0, 10
    syscall


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

