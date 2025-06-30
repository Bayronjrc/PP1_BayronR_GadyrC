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
    t9_var: .word 0
    matrix_var: .word 0
    t10_var: .word 0
    t12_var: .word 0
    t11_var: .word 0
    t_cond_var: .word 0
    t13_var: .word 0
    t_inc_var: .word 0
    matrixij_var: .word 0
    i_var: .word 0
    j_var: .word 0
    k_var: .word 0
    n_var: .word 0
    t1_var: .word 0
    t2_var: .word 0
    t3_var: .word 0

.text
.globl main

    # // Código Intermedio Generado
    # // Archivo: src/main/resources/a_intermediate.txt
fibonnaci:
    # UNIVERSAL: Prólogo para fibonnaci
    addi $sp, $sp, -8
    sw $ra, 4($sp)
    sw $fp, 0($sp)
    move $fp, $sp
    # Reservar espacio para variables locales
    addi $sp, $sp, -16

    # UNIVERSAL: Guardar parámetros de fibonnaci
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

    # RETURN n
    lw $v0, -4($fp)   # n local
    # Valor de retorno en $v0
    j exit_fibonnaci

L1:
    # t2 = n - 1
    lw $t1, -4($fp)   # n local
    li $t2, 1
    sub $t0, $t1, $t2
    sw $t0, t2_var

    # PARAM t2
    lw $a0, t2_var
    #  UNIVERSAL: Parámetro t2 cargado en $a0

    # t3 = CALL fibonnaci 1
    jal fibonnaci
    sw $v0, t3_var

    # t4 = n - 2
    lw $t1, -4($fp)   # n local
    li $t2, 2
    sub $t0, $t1, $t2
    sw $t0, t4_var

    # PARAM t4
    lw $a0, t4_var
    #  UNIVERSAL: Parámetro t4 cargado en $a0

    # t5 = CALL fibonnaci 1
    jal fibonnaci
    sw $v0, t5_var

    # t6 = t3 + t5
    lw $t1, t3_var
    lw $t2, t5_var
    add $t0, $t1, $t2
    sw $t0, t6_var

    # RETURN t6
    lw $v0, t6_var
    # Valor de retorno en $v0
    j exit_fibonnaci


#  UNIVERSAL: Epílogo para fibonnaci
exit_fibonnaci:
    # Limpiar variables locales
    addi $sp, $sp, 16    # Liberar espacio de variables locales
    # Restaurar frame pointer y return address
    move $sp, $fp
    lw $fp, 0($sp)
    lw $ra, 4($sp)
    addi $sp, $sp, 8
    jr $ra

test:
    # UNIVERSAL: Prólogo para test
    addi $sp, $sp, -8
    sw $ra, 4($sp)
    sw $fp, 0($sp)
    move $fp, $sp
    # Reservar espacio para variables locales
    addi $sp, $sp, -16

    # Función sin parámetros o no detectados

    # Inicio de función
    # DECLARE matrix INT[][]
    # DECLARE i INT
    # i = 0
    li $t0, 0
    sw $t0, i_var

    # i = 0
    li $t0, 0
    sw $t0, i_var

    # t7 = i < 4
    lw $t1, i_var
    li $t2, 4
    # i < 4
    sub $t3, $t1, $t2    # t1 - t2
    bltz $t3, set_true_lt_2
    li $t0, 0
    j end_lt_2
set_true_lt_2:
    li $t0, 1
end_lt_2:
    sw $t0, t7_var

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

    # t8 = j < 4
    lw $t1, j_var
    li $t2, 4
    # j < 4
    sub $t3, $t1, $t2    # t1 - t2
    bltz $t3, set_true_lt_3
    li $t0, 0
    j end_lt_3
set_true_lt_3:
    li $t0, 1
end_lt_3:
    sw $t0, t8_var

    # j = j + 1
    lw $t1, j_var
    li $t2, 1
    add $t0, $t1, $t2
    sw $t0, j_var

    # t9 = i + j
    lw $t1, i_var
    lw $t2, j_var
    add $t0, $t1, $t2
    sw $t0, t9_var

    # PARAM t9
    lw $a0, t9_var
    #  UNIVERSAL: Parámetro t9 cargado en $a0

    # t10 = CALL fibonnaci 1
    jal fibonnaci
    sw $v0, t10_var

    # matrix[i][j] = t10
    lw $t0, t10_var
    sw $t0, matrixij_var

    # t11 = matrix[i][j]
    li $t0, 0    # ERROR: No se pudo procesar 'matrix[i][j]'
    sw $t0, t11_var

    # t12 = t11 > 6
    lw $t1, t11_var
    li $t2, 6
    # t11 > 6
    sub $t3, $t2, $t1    # t2 - t1
    bltz $t3, set_true_gt_4
    li $t0, 0
    j end_gt_4
set_true_gt_4:
    li $t0, 1
end_gt_4:
    sw $t0, t12_var

    # IF NOT t12 GOTO L6
    lw $t0, t12_var
    beq $t0, $zero, L6

    j L5

L6:
    # t13 = matrix[i][j]
    li $t0, 0    # ERROR: No se pudo procesar 'matrix[i][j]'
    sw $t0, t13_var

    # WRITE t13
    lw $a0, t13_var
    jal print_int
    la $a0, nl
    jal print_string

L4:
    # t_cond = k <= 2
    lw $t1, k_var
    li $t2, 2
    # k <= 2
    sub $t3, $t2, $t1    # t2 - t1
    bgez $t3, set_true_le_5
    li $t0, 0
    j end_le_5
set_true_le_5:
    li $t0, 1
end_le_5:
    sw $t0, t_cond_var

    # IF NOT t_cond GOTO L5
    lw $t0, t_cond_var
    beq $t0, $zero, L5

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

    j L4

L5:
L2:
    # t_cond = k <= 2
    lw $t1, k_var
    li $t2, 2
    # k <= 2
    sub $t3, $t2, $t1    # t2 - t1
    bgez $t3, set_true_le_6
    li $t0, 0
    j end_le_6
set_true_le_6:
    li $t0, 1
end_le_6:
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

#  UNIVERSAL: Epílogo para test
exit_test:
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
    # CALL test 0
    jal test


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

