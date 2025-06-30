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
    t7_var: .word 0
    t8_var: .word 0
    resultado1_var: .word 0
    resultado2_var: .word 0
    t_cond_var: .word 0
    t_inc_var: .word 0
    a_var: .word 0
    b_var: .word 0
    temp_var: .word 0
    resultado_var: .word 0
    d_var: .word 0
    k_var: .word 0
    n_var: .word 0
    r_var: .word 0
    recurse_var: .word 0
    t1_var: .word 0
    t2_var: .word 0
    t3_var: .word 0

.text
.globl main

    # // Código Intermedio Generado
    # // Archivo: src/main/resources/s1_intermediate.txt
atun:
    # UNIVERSAL: Prólogo para atun
    addi $sp, $sp, -8
    sw $ra, 4($sp)
    sw $fp, 0($sp)
    move $fp, $sp
    # Reservar espacio para variables locales
    addi $sp, $sp, -16

    # UNIVERSAL: Guardar parámetros de atun
    sw $a0, -4($fp)   # a local
    sw $a0, a_var     # a global
    sw $a1, -8($fp)   # b local
    sw $a1, b_var     # b global

    # Inicio de función
    # DECLARE a INT
    # DECLARE b INT
    # t1 = a + b
    lw $t1, -4($fp)   # a local
    lw $t2, -8($fp)   # b local
    add $t0, $t1, $t2
    sw $t0, t1_var

    # DECLARE resultado INT
    # resultado = t1
    lw $t0, t1_var
    sw $t0, resultado_var

    # RETURN resultado
    lw $v0, resultado_var
    # Valor de retorno en $v0
    j exit_atun


#  UNIVERSAL: Epílogo para atun
exit_atun:
    # Limpiar variables locales
    addi $sp, $sp, 16    # Liberar espacio de variables locales
    # Restaurar frame pointer y return address
    move $sp, $fp
    lw $fp, 0($sp)
    lw $ra, 4($sp)
    addi $sp, $sp, 8
    jr $ra

pepe:
    # UNIVERSAL: Prólogo para pepe
    addi $sp, $sp, -8
    sw $ra, 4($sp)
    sw $fp, 0($sp)
    move $fp, $sp
    # Reservar espacio para variables locales
    addi $sp, $sp, -16

    # UNIVERSAL: Guardar parámetros de pepe
    sw $a0, -4($fp)   # n local
    sw $a0, n_var     # n global

    # Inicio de función
    # DECLARE n INT
    # t2 = n <= 1
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
    sw $t0, t2_var

    # IF NOT t2 GOTO L1
    lw $t0, t2_var
    beq $t0, $zero, L1

    # RETURN 1
    li $v0, 1
    # Valor de retorno en $v0
    j exit_pepe

L1:
    # t3 = n - 1
    lw $t1, -4($fp)   # n local
    li $t2, 1
    sub $t0, $t1, $t2
    sw $t0, t3_var

    # DECLARE temp INT
    # temp = t3
    lw $t0, t3_var
    sw $t0, temp_var

    # PARAM temp
    lw $a0, temp_var
    #  UNIVERSAL: Parámetro temp cargado en $a0

    # t4 = CALL pepe 1
    jal pepe
    sw $v0, t4_var

    # DECLARE recurse INT
    # recurse = t4
    lw $t0, t4_var
    sw $t0, recurse_var

    # t5 = n * recurse
    lw $t1, -4($fp)   # n local
    lw $t2, recurse_var
    mult $t1, $t2
    mflo $t0
    sw $t0, t5_var

    # DECLARE resultado INT
    # resultado = t5
    lw $t0, t5_var
    sw $t0, resultado_var

    # RETURN resultado
    lw $v0, resultado_var
    # Valor de retorno en $v0
    j exit_pepe


#  UNIVERSAL: Epílogo para pepe
exit_pepe:
    # Limpiar variables locales
    addi $sp, $sp, 16    # Liberar espacio de variables locales
    # Restaurar frame pointer y return address
    move $sp, $fp
    lw $fp, 0($sp)
    lw $ra, 4($sp)
    addi $sp, $sp, 8
    jr $ra

testBasico:
    # UNIVERSAL: Prólogo para testBasico
    addi $sp, $sp, -8
    sw $ra, 4($sp)
    sw $fp, 0($sp)
    move $fp, $sp
    # Reservar espacio para variables locales
    addi $sp, $sp, -16

    # Función sin parámetros o no detectados

    # Inicio de función
    # DECLARE d INT
    # d = 4
    li $t0, 4
    sw $t0, d_var

    # DECLARE r INT
    # r = 4
    li $t0, 4
    sw $t0, r_var

    # PARAM d
    lw $a0, d_var
    #  UNIVERSAL: Parámetro d cargado en $a0

    # PARAM r
    lw $a1, r_var
    #  UNIVERSAL: Parámetro r cargado en $a1

    # t6 = CALL atun 2
    jal atun
    sw $v0, t6_var

    # DECLARE resultado1 INT
    # resultado1 = t6
    lw $t0, t6_var
    sw $t0, resultado1_var

    # WRITE resultado1
    lw $a0, resultado1_var
    jal print_int
    la $a0, nl
    jal print_string

    # PARAM 5
    li $a0, 5
    #  UNIVERSAL: Parámetro 5 cargado en $a0

    # t7 = CALL pepe 1
    jal pepe
    sw $v0, t7_var

    # DECLARE resultado2 INT
    # resultado2 = t7
    lw $t0, t7_var
    sw $t0, resultado2_var

    # WRITE resultado2
    lw $a0, resultado2_var
    jal print_int
    la $a0, nl
    jal print_string

    # DECLARE k INT
    # k = 0
    li $t0, 0
    sw $t0, k_var

    # k = 0
    li $t0, 0
    sw $t0, k_var

    # t8 = k < 7
    lw $t1, k_var
    li $t2, 7
    # k < 7
    sub $t3, $t1, $t2    # t1 - t2
    bltz $t3, set_true_lt_2
    li $t0, 0
    j end_lt_2
set_true_lt_2:
    li $t0, 1
end_lt_2:
    sw $t0, t8_var

    # k = k + 1
    lw $t1, k_var
    li $t2, 1
    add $t0, $t1, $t2
    sw $t0, k_var

    # WRITE k
    lw $a0, k_var
    jal print_int
    la $a0, nl
    jal print_string

L2:
    # t_cond = k < 7
    lw $t1, k_var
    li $t2, 7
    # k < 7
    sub $t3, $t1, $t2    # t1 - t2
    bltz $t3, set_true_lt_3
    li $t0, 0
    j end_lt_3
set_true_lt_3:
    li $t0, 1
end_lt_3:
    sw $t0, t_cond_var

    # IF NOT t_cond GOTO L3
    lw $t0, t_cond_var
    beq $t0, $zero, L3

    # WRITE k
    lw $a0, k_var
    jal print_int
    la $a0, nl
    jal print_string

    # t_inc = k + 1
    lw $t1, k_var
    li $t2, 1
    add $t0, $t1, $t2
    sw $t0, t_inc_var

    # k = t_inc
    lw $t0, t_inc_var
    sw $t0, k_var

    j L2

L3:

#  UNIVERSAL: Epílogo para testBasico
exit_testBasico:
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
    # CALL testBasico 0
    jal testBasico


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

