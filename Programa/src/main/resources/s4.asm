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
filo:
    # UNIVERSAL: Prólogo para filo
    addi $sp, $sp, -8
    sw $ra, 4($sp)
    sw $fp, 0($sp)
    move $fp, $sp
    # Reservar espacio para variables locales
    addi $sp, $sp, -16

    # UNIVERSAL: Guardar parámetros de filo
    sw $a0, -4($fp)   # n local
    sw $a0, n_var     # n global

    # Inicio de función
    # DECLARE n INT
    # t1 = n <= 1
    lw $t1, -4($fp)   # n local
    li $t2, 1
    # n <= 1
    sub $t3, $t2, $t1    # t2 - t1
    bgez $t3, set_true_le_1
    li $t0, 0
    j end_le_1
set_true_le_1:
    li $t0, 1
end_le_1:
    sw $t0, t1_var

    # IF NOT t1 GOTO L1
    lw $t0, t1_var
    beq $t0, $zero, L1

    # RETURN 1
    li $v0, 1
    # Valor de retorno en $v0
    j exit_filo

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
    #  UNIVERSAL: Parámetro temp cargado en $a0

    # t3 = CALL filo 1
    jal filo
    sw $v0, t3_var

    # DECLARE recurse INT
    # recurse = t3
    lw $t0, t3_var
    sw $t0, recurse_var

    # t4 = n * recurse
    lw $t1, -4($fp)   # n local
    lw $t2, recurse_var
    mult $t1, $t2
    mflo $t0
    sw $t0, t4_var

    # DECLARE resultado INT
    # resultado = t4
    lw $t0, t4_var
    sw $t0, resultado_var

    # RETURN resultado
    lw $v0, resultado_var
    # Valor de retorno en $v0
    j exit_filo


#  UNIVERSAL: Epílogo para filo
exit_filo:
    # Limpiar variables locales
    addi $sp, $sp, 16    # Liberar espacio de variables locales
    # Restaurar frame pointer y return address
    move $sp, $fp
    lw $fp, 0($sp)
    lw $ra, 4($sp)
    addi $sp, $sp, 8
    jr $ra

azul:
    # UNIVERSAL: Prólogo para azul
    addi $sp, $sp, -8
    sw $ra, 4($sp)
    sw $fp, 0($sp)
    move $fp, $sp
    # Reservar espacio para variables locales
    addi $sp, $sp, -16

    # UNIVERSAL: Guardar parámetros de azul
    sw $a0, -4($fp)   # n local
    sw $a0, n_var     # n global

    # Inicio de función
    # DECLARE n INT
    # t5 = n <= 1
    lw $t1, -4($fp)   # n local
    li $t2, 1
    # n <= 1
    sub $t3, $t2, $t1    # t2 - t1
    bgez $t3, set_true_le_2
    li $t0, 0
    j end_le_2
set_true_le_2:
    li $t0, 1
end_le_2:
    sw $t0, t5_var

    # IF NOT t5 GOTO L2
    lw $t0, t5_var
    beq $t0, $zero, L2

    # RETURN n
    lw $v0, -4($fp)   # n local
    # Valor de retorno en $v0
    j exit_azul

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
    #  UNIVERSAL: Parámetro temp1 cargado en $a0

    # t8 = CALL azul 1
    jal azul
    sw $v0, t8_var

    # DECLARE fib1 INT
    # fib1 = t8
    lw $t0, t8_var
    sw $t0, fib1_var

    # PARAM temp2
    lw $a0, temp2_var
    #  UNIVERSAL: Parámetro temp2 cargado en $a0

    # t9 = CALL azul 1
    jal azul
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
    j exit_azul


#  UNIVERSAL: Epílogo para azul
exit_azul:
    # Limpiar variables locales
    addi $sp, $sp, 16    # Liberar espacio de variables locales
    # Restaurar frame pointer y return address
    move $sp, $fp
    lw $fp, 0($sp)
    lw $ra, 4($sp)
    addi $sp, $sp, 8
    jr $ra

pocoyo:
    # UNIVERSAL: Prólogo para pocoyo
    addi $sp, $sp, -8
    sw $ra, 4($sp)
    sw $fp, 0($sp)
    move $fp, $sp
    # Reservar espacio para variables locales
    addi $sp, $sp, -16

    # UNIVERSAL: Guardar parámetros de pocoyo
    sw $a0, -4($fp)   # base local
    sw $a0, base_var     # base global
    sw $a1, -8($fp)   # exp local
    sw $a1, exp_var     # exp global

    # Inicio de función
    # DECLARE base INT
    # DECLARE exp INT
    # t11 = exp <= 0
    lw $t1, -8($fp)   # exp local
    li $t2, 0
    # exp <= 0
    sub $t3, $t2, $t1    # t2 - t1
    bgez $t3, set_true_le_3
    li $t0, 0
    j end_le_3
set_true_le_3:
    li $t0, 1
end_le_3:
    sw $t0, t11_var

    # IF NOT t11 GOTO L3
    lw $t0, t11_var
    beq $t0, $zero, L3

    # RETURN 1
    li $v0, 1
    # Valor de retorno en $v0
    j exit_pocoyo

L3:
    # t12 = exp - 1
    lw $t1, -8($fp)   # exp local
    li $t2, 1
    sub $t0, $t1, $t2
    sw $t0, t12_var

    # DECLARE temp INT
    # temp = t12
    lw $t0, t12_var
    sw $t0, temp_var

    # PARAM base
    lw $a0, -4($fp)   # base local
    #  UNIVERSAL: Parámetro base cargado en $a0

    # PARAM temp
    lw $a1, temp_var
    #  UNIVERSAL: Parámetro temp cargado en $a1

    # t13 = CALL pocoyo 2
    jal pocoyo
    sw $v0, t13_var

    # DECLARE recurse INT
    # recurse = t13
    lw $t0, t13_var
    sw $t0, recurse_var

    # t14 = base * recurse
    lw $t1, -4($fp)   # base local
    lw $t2, recurse_var
    mult $t1, $t2
    mflo $t0
    sw $t0, t14_var

    # DECLARE resultado INT
    # resultado = t14
    lw $t0, t14_var
    sw $t0, resultado_var

    # RETURN resultado
    lw $v0, resultado_var
    # Valor de retorno en $v0
    j exit_pocoyo


#  UNIVERSAL: Epílogo para pocoyo
exit_pocoyo:
    # Limpiar variables locales
    addi $sp, $sp, 16    # Liberar espacio de variables locales
    # Restaurar frame pointer y return address
    move $sp, $fp
    lw $fp, 0($sp)
    lw $ra, 4($sp)
    addi $sp, $sp, 8
    jr $ra

mcd:
    # UNIVERSAL: Prólogo para mcd
    addi $sp, $sp, -8
    sw $ra, 4($sp)
    sw $fp, 0($sp)
    move $fp, $sp
    # Reservar espacio para variables locales
    addi $sp, $sp, -16

    # UNIVERSAL: Guardar parámetros de mcd
    sw $a0, -4($fp)   # a local
    sw $a0, a_var     # a global
    sw $a1, -8($fp)   # b local
    sw $a1, b_var     # b global

    # Inicio de función
    # DECLARE a INT
    # DECLARE b INT
    # t15 = b == 0
    lw $t1, -8($fp)   # b local
    li $t2, 0
    # b == 0
    sub $t3, $t1, $t2    # t1 - t2
    beq $t3, $zero, set_true_eq_4
    li $t0, 0
    j end_eq_4
set_true_eq_4:
    li $t0, 1
end_eq_4:
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
    #  UNIVERSAL: Parámetro b cargado en $a0

    # PARAM resto
    lw $a1, resto_var
    #  UNIVERSAL: Parámetro resto cargado en $a1

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


#  UNIVERSAL: Epílogo para mcd
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
    # UNIVERSAL: Prólogo para testRecursion
    addi $sp, $sp, -8
    sw $ra, 4($sp)
    sw $fp, 0($sp)
    move $fp, $sp
    # Reservar espacio para variables locales
    addi $sp, $sp, -16

    # Función sin parámetros o no detectados

    # Inicio de función
    # PARAM 5
    li $a0, 5
    #  UNIVERSAL: Parámetro 5 cargado en $a0

    # t18 = CALL azul 1
    jal azul
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
    #  UNIVERSAL: Parámetro 2 cargado en $a0

    # PARAM 3
    li $a1, 3
    #  UNIVERSAL: Parámetro 3 cargado en $a1

    # t19 = CALL pocoyo 2
    jal pocoyo
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
    #  UNIVERSAL: Parámetro 12 cargado en $a0

    # PARAM 8
    li $a1, 8
    #  UNIVERSAL: Parámetro 8 cargado en $a1

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
    #  UNIVERSAL: Parámetro 4 cargado en $a0

    # t21 = CALL filo 1
    jal filo
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


#  UNIVERSAL: Epílogo para testRecursion
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
    # UNIVERSAL: Prólogo para main
    addi $sp, $sp, -8
    sw $ra, 4($sp)
    sw $fp, 0($sp)
    move $fp, $sp
    # Reservar espacio para variables locales
    addi $sp, $sp, -16

    # Función sin parámetros o no detectados

    # Inicio de función
    # CALL testRecursion 0
    jal testRecursion


#  UNIVERSAL: Epílogo para main
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

