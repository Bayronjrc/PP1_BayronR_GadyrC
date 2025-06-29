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
    t7_var: .word 0
    t8_var: .word 0
    t9_var: .word 0
    matrix_var: .word 0
    t10_var: .word 0
    t12_var: .word 0
    contador_var: .word 0
    t11_var: .word 0
    t_cond_var: .word 0
    t13_var: .word 0
    t_inc_var: .word 0
    a_var: .word 0
    b_var: .word 0
    temp_var: .word 0
    resultado_var: .word 0
    i_var: .word 0
    j_var: .word 0
    k_var: .word 0
    n_var: .word 0
    matrix_element_var: .word 0
    t2_var: .word 0
    t3_var: .word 0

.text
.globl main

    # // Código Intermedio Generado
    # // Archivo: src/main/resources/a_intermediate.txt
fibonnaci:
    # Prólogo simplificado fibonnaci
    addi $sp, $sp, -8
    sw $ra, 4($sp)
    sw $fp, 0($sp)
    move $fp, $sp

    # Inicio de función
    # DECLARE n INT
L1:
    # t2 = n - 1
    lw $t1, n_var
    li $t2, 1
    sub $t0, $t1, $t2
    sw $t0, t2_var

    # PARAM t2
    lw $a0, t2_var
    # ✅ s1: Parámetro t2 cargado en $a0

    # t3 = CALL fibonnaci 1
    jal fibonnaci
    sw $v0, t3_var

    # t4 = n - 2
    lw $t1, n_var
    li $t2, 2
    sub $t0, $t1, $t2
    sw $t0, t4_var

    # PARAM t4
    lw $a1, t4_var
    # Parámetro t4 cargado en $a1

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


# Epílogo simplificado fibonnaci
exit_fibonnaci:
    move $sp, $fp
    lw $fp, 0($sp)
    lw $ra, 4($sp)
    addi $sp, $sp, 8
    jr $ra

test:
    # Prólogo simplificado test
    addi $sp, $sp, -8
    sw $ra, 4($sp)
    sw $fp, 0($sp)
    move $fp, $sp

    # Inicio de función
    # DECLARE matrix INT[][]
    # DECLARE i INT
    # i = 0
    li $t0, 0
    sw $t0, i_var

    # i = 0
    li $t0, 0
    sw $t0, i_var

    # t7 = i < 3
    lw $t1, i_var
    li $t2, 3
    slt $t0, $t1, $t2
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

    # t8 = j < 3
    lw $t1, j_var
    li $t2, 3
    slt $t0, $t1, $t2
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
    lw $a2, t9_var
    # Parámetro t9 cargado en $a2

    # t10 = CALL fibonnaci 1
    jal fibonnaci
    sw $v0, t10_var

    # matrix[i][j] = t10
    # Array assignment: matrix[i][j] = t10
    lw $t0, t10_var
    sw $t0, matrix_element_var

    # t11 = matrix[i][j]
    # Array access: matrix[i][j]
    lw $t0, matrix_element_var
    sw $t0, t11_var

    # t12 = t11 > 5
    lw $t1, t11_var
    li $t2, 5
    sgt $t0, $t1, $t2
    sw $t0, t12_var

    # IF NOT t12 GOTO L6
    lw $t0, t12_var
    beq $t0, $zero, L6

    j L5

L6:
    # t13 = matrix[i][j]
    # Array access: matrix[i][j]
    lw $t0, matrix_element_var
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
    sle $t0, $t1, $t2
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
    sle $t0, $t1, $t2
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

# Epílogo simplificado test
exit_test:
    move $sp, $fp
    lw $fp, 0($sp)
    lw $ra, 4($sp)
    addi $sp, $sp, 8
    jr $ra

main:
    # Prólogo simplificado main
    addi $sp, $sp, -8
    sw $ra, 4($sp)
    sw $fp, 0($sp)
    move $fp, $sp

    # Inicio de función
    # CALL test 0
    jal test


# Epílogo simplificado main
exit_main:
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

