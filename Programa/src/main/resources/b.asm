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
    t_case_var: .word 0
    t5_var: .word 0
    t6_var: .word 0
    t7_var: .word 0
    t8_var: .word 0
    t9_var: .word 0
    i_var: .word 0
    j_var: .word 0
    k_var: .word 0
    arrji_var: .word 0
    n_var: .word 0
    t10_var: .word 0
    t_cond_var: .word 0
    x_var: .word 0
    t_inc_var: .word 0
    t1_var: .word 0
    t2_var: .word 0
    t3_var: .word 0
    option_var: .word 0

.text
.globl main

    # // Código Intermedio Generado
    # // Archivo: src/main/resources/b_intermediate.txt
factorial:
    # UNIVERSAL: Prólogo para factorial
    addi $sp, $sp, -8
    sw $ra, 4($sp)
    sw $fp, 0($sp)
    move $fp, $sp
    # Reservar espacio para variables locales
    addi $sp, $sp, -16

    # UNIVERSAL: Guardar parámetros de factorial
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
    j exit_factorial

L1:
    # t2 = n - 1
    lw $t1, -4($fp)   # n local
    li $t2, 1
    sub $t0, $t1, $t2
    sw $t0, t2_var

    # PARAM t2
    lw $a0, t2_var
    #  UNIVERSAL: Parámetro t2 cargado en $a0

    # t3 = CALL factorial 1
    jal factorial
    sw $v0, t3_var

    # t4 = n * t3
    lw $t1, -4($fp)   # n local
    lw $t2, t3_var
    mult $t1, $t2
    mflo $t0
    sw $t0, t4_var

    # RETURN t4
    lw $v0, t4_var
    # Valor de retorno en $v0
    j exit_factorial


#  UNIVERSAL: Epílogo para factorial
exit_factorial:
    # Limpiar variables locales
    addi $sp, $sp, 16    # Liberar espacio de variables locales
    # Restaurar frame pointer y return address
    move $sp, $fp
    lw $fp, 0($sp)
    lw $ra, 4($sp)
    addi $sp, $sp, 8
    jr $ra

testLopps:
    # UNIVERSAL: Prólogo para testLopps
    addi $sp, $sp, -8
    sw $ra, 4($sp)
    sw $fp, 0($sp)
    move $fp, $sp
    # Reservar espacio para variables locales
    addi $sp, $sp, -16

    # Función sin parámetros o no detectados

    # Inicio de función
    # DECLARE x INT
    # x = 10
    li $t0, 10
    sw $t0, x_var

L2:
    # WRITE x
    lw $a0, x_var
    jal print_int
    la $a0, nl
    jal print_string

    # t5 = x - 1
    lw $t1, x_var
    li $t2, 1
    sub $t0, $t1, $t2
    sw $t0, t5_var

    # x = t5
    lw $t0, t5_var
    sw $t0, x_var

    # t6 = x == 5
    lw $t1, x_var
    li $t2, 5
    # x == 5
    sub $t3, $t1, $t2    # t1 - t2
    beq $t3, $zero, set_true_eq_2
    li $t0, 0
    j end_eq_2
set_true_eq_2:
    li $t0, 1
end_eq_2:
    sw $t0, t6_var

    # IF NOT t6 GOTO L4
    lw $t0, t6_var
    beq $t0, $zero, L4

    j L2

L4:
    # t7 = x == 6
    lw $t1, x_var
    li $t2, 6
    # x == 6
    sub $t3, $t1, $t2    # t1 - t2
    beq $t3, $zero, set_true_eq_3
    li $t0, 0
    j end_eq_3
set_true_eq_3:
    li $t0, 1
end_eq_3:
    sw $t0, t7_var

    # IF NOT t7 GOTO L5
    lw $t0, t7_var
    beq $t0, $zero, L5

    j L3

L5:
    # t8 = x > 0
    lw $t1, x_var
    li $t2, 0
    # x > 0
    sub $t3, $t2, $t1    # t2 - t1
    bltz $t3, set_true_gt_4
    li $t0, 0
    j end_gt_4
set_true_gt_4:
    li $t0, 1
end_gt_4:
    sw $t0, t8_var

    # IF t8 GOTO L2
    lw $t0, t8_var
    bne $t0, $zero, L2

L3:
    # DECLARE option INT
    # option = 3
    li $t0, 3
    sw $t0, option_var

    # WRITE 100
    li $a0, 100
    jal print_int
    la $a0, nl
    jal print_string

    # WRITE 200
    li $a0, 200
    jal print_int
    la $a0, nl
    jal print_string

    # WRITE 300
    li $a0, 300
    jal print_int
    la $a0, nl
    jal print_string

    # WRITE 999
    li $a0, 999
    jal print_int
    la $a0, nl
    jal print_string

    # t_case = option == 1
    lw $t1, option_var
    li $t2, 1
    # option == 1
    sub $t3, $t1, $t2    # t1 - t2
    beq $t3, $zero, set_true_eq_5
    li $t0, 0
    j end_eq_5
set_true_eq_5:
    li $t0, 1
end_eq_5:
    sw $t0, t_case_var

    # IF t_case GOTO L6
    lw $t0, t_case_var
    bne $t0, $zero, L6

    # t_case = option == 2
    lw $t1, option_var
    li $t2, 2
    # option == 2
    sub $t3, $t1, $t2    # t1 - t2
    beq $t3, $zero, set_true_eq_6
    li $t0, 0
    j end_eq_6
set_true_eq_6:
    li $t0, 1
end_eq_6:
    sw $t0, t_case_var

    # IF t_case GOTO L7
    lw $t0, t_case_var
    bne $t0, $zero, L7

    # t_case = option == 3
    lw $t1, option_var
    li $t2, 3
    # option == 3
    sub $t3, $t1, $t2    # t1 - t2
    beq $t3, $zero, set_true_eq_7
    li $t0, 0
    j end_eq_7
set_true_eq_7:
    li $t0, 1
end_eq_7:
    sw $t0, t_case_var

    # IF t_case GOTO L8
    lw $t0, t_case_var
    bne $t0, $zero, L8

    j L9

L6:
    j L10

L7:
    j L10

L8:
    j L10

L9:
L10:

#  UNIVERSAL: Epílogo para testLopps
exit_testLopps:
    # Limpiar variables locales
    addi $sp, $sp, 16    # Liberar espacio de variables locales
    # Restaurar frame pointer y return address
    move $sp, $fp
    lw $fp, 0($sp)
    lw $ra, 4($sp)
    addi $sp, $sp, 8
    jr $ra

testArrays:
    # UNIVERSAL: Prólogo para testArrays
    addi $sp, $sp, -8
    sw $ra, 4($sp)
    sw $fp, 0($sp)
    move $fp, $sp
    # Reservar espacio para variables locales
    addi $sp, $sp, -16

    # Función sin parámetros o no detectados

    # Inicio de función
    # DECLARE i INT
    # i = 0
    li $t0, 0
    sw $t0, i_var

    # i = 0
    li $t0, 0
    sw $t0, i_var

    # t9 = i < 3
    lw $t1, i_var
    li $t2, 3
    # i < 3
    sub $t3, $t1, $t2    # t1 - t2
    bltz $t3, set_true_lt_8
    li $t0, 0
    j end_lt_8
set_true_lt_8:
    li $t0, 1
end_lt_8:
    sw $t0, t9_var

    # i = i + 1
    lw $t1, i_var
    li $t2, 1
    add $t0, $t1, $t2
    sw $t0, i_var

    # DECLARE j INT
    # j = 0
    li $t0, 0
    sw $t0, j_var

    # j = 0
    li $t0, 0
    sw $t0, j_var

    # t10 = j < 3
    lw $t1, j_var
    li $t2, 3
    # j < 3
    sub $t3, $t1, $t2    # t1 - t2
    bltz $t3, set_true_lt_9
    li $t0, 0
    j end_lt_9
set_true_lt_9:
    li $t0, 1
end_lt_9:
    sw $t0, t10_var

    # j = j + 1
    lw $t1, j_var
    li $t2, 1
    add $t0, $t1, $t2
    sw $t0, j_var

    # arr[j][i] = j
    lw $t0, j_var
    sw $t0, arrji_var

L13:
    # t_cond = k <= 2
    lw $t1, k_var
    li $t2, 2
    # k <= 2
    sub $t3, $t2, $t1    # t2 - t1
    bgez $t3, set_true_le_10
    li $t0, 0
    j end_le_10
set_true_le_10:
    li $t0, 1
end_le_10:
    sw $t0, t_cond_var

    # IF NOT t_cond GOTO L14
    lw $t0, t_cond_var
    beq $t0, $zero, L14

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

    j L13

L14:
L11:
    # t_cond = k <= 2
    lw $t1, k_var
    li $t2, 2
    # k <= 2
    sub $t3, $t2, $t1    # t2 - t1
    bgez $t3, set_true_le_11
    li $t0, 0
    j end_le_11
set_true_le_11:
    li $t0, 1
end_le_11:
    sw $t0, t_cond_var

    # IF NOT t_cond GOTO L12
    lw $t0, t_cond_var
    beq $t0, $zero, L12

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

    j L11

L12:

#  UNIVERSAL: Epílogo para testArrays
exit_testArrays:
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
    # CALL testLopps 0
    jal testLopps

    # CALL testArrays 0
    jal testArrays


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

