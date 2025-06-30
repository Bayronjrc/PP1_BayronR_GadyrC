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
    dif_var: .word 0
    t4_var: .word 0
    t5_var: .word 0
    t6_var: .word 0
    t7_var: .word 0
    t8_var: .word 0
    t9_var: .word 0
    luna_var: .word 0
    b1_var: .word 0
    t10_var: .word 0
    t12_var: .word 0
    contador_var: .word 0
    t11_var: .word 0
    t_cond_var: .word 0
    t13_var: .word 0
    var2_var: .word 0
    t_inc_var: .word 0
    s1_var: .word 0
    otra_var: .word 0
    a_var: .word 0
    b_var: .word 0
    temp_var: .word 0
    resultado_var: .word 0
    var_var: .word 0
    i_var: .word 0
    j_var: .word 0
    k_var: .word 0
    var_3_opop_4_var: .word 0
    str_var: .word 0
    true_var: .word 0
    t1_var: .word 0
    t6_and_t8_var: .word 0
    t2_var: .word 0
    t3_var: .word 0

.text
.globl main

    # // Código Intermedio Generado
    # // Archivo: src/main/resources/haha_intermediate.txt
mi:
    # Prólogo simplificado mi
    addi $sp, $sp, -8
    sw $ra, 4($sp)
    sw $fp, 0($sp)
    move $fp, $sp

    # Inicio de función
    # DECLARE dif INT
    # DECLARE otra CHAR
    # DECLARE var INT
    # var = 0
    li $t0, 0  # Float: 0
    sw $t0, var_var

    # DECLARE var2 INT
    # DECLARE str STRING
    # DECLARE i INT
    # i = 0
    li $t0, 0  # Float: 0
    sw $t0, i_var

    # i = 0
    li $t0, 0  # Float: 0
    sw $t0, i_var

    # var2 = 1
    li $t0, 0  # Float: 1
    sw $t0, var2_var

    # IF NOT true GOTO L6
    lw $t0, true_var
    beq $t0, $zero, L6

    j L7

L6:
    # t1 = 3 ** 4
    lw $t0, var_3_opop_4_var
    sw $t0, t1_var

    # t2 = 2 * t1
    li $t1, 0  # Float: 2
    lw $t2, t1_var
    mul $t0, $t1, $t2
    sw $t0, t2_var

    # t3 = 1 + t2
    li $t1, 0  # Float: 1
    lw $t2, t2_var
    add $t0, $t1, $t2
    sw $t0, t3_var

    # t4 = 4 + i
    li $t1, 0  # Float: 4
    lw $t2, i_var
    add $t0, $t1, $t2
    sw $t0, t4_var

    # t5 = i < t4
    lw $t1, i_var
    lw $t2, t4_var
    slt $t0, $t1, $t2
    sw $t0, t5_var

    # t6 = var2 > 122
    lw $t1, var2_var
    li $t2, 0  # Float: 122
    sgt $t0, $t1, $t2
    sw $t0, t6_var

    # t7 = 34 + 35
    li $t1, 0  # Float: 34
    li $t2, 0  # Float: 35
    add $t0, $t1, $t2
    sw $t0, t7_var

    # t8 = 12 > t7
    li $t1, 0  # Float: 12
    lw $t2, t7_var
    sgt $t0, $t1, $t2
    sw $t0, t8_var

    # t9 = t6 && t8
    lw $t0, t6_and_t8_var
    sw $t0, t9_var

    # t10 = var == 0
    lw $t1, var_var
    li $t2, 0  # Float: 0
    seq $t0, $t1, $t2
    sw $t0, t10_var

L7:
L1:
    # t_cond = k <= 2
    lw $t1, k_var
    li $t2, 0  # Float: 2
    sle $t0, $t1, $t2
    sw $t0, t_cond_var

    # IF NOT t_cond GOTO L2
    lw $t0, t_cond_var
    beq $t0, $zero, L2

    # WRITE k
    lw $a0, k_var
    jal print_int
    la $a0, nl
    jal print_string

    # t_inc = k + 1
    lw $t1, k_var
    li $t2, 0  # Float: 1
    add $t0, $t1, $t2
    sw $t0, t_inc_var

    # k = t_inc
    lw $t0, t_inc_var
    sw $t0, k_var

    j L1

L2:
    # RETURN 1
    li $v0, 0  # Float: 1
    # Valor de retorno en $v0
    j exit_mi


# Epílogo simplificado mi
exit_mi:
    move $sp, $fp
    lw $fp, 0($sp)
    lw $ra, 4($sp)
    addi $sp, $sp, 8
    jr $ra

miOtraFun:
    # Prólogo simplificado miOtraFun
    addi $sp, $sp, -8
    sw $ra, 4($sp)
    sw $fp, 0($sp)
    move $fp, $sp

    # Inicio de función
    # RETURN true
    lw $v0, true_var
    # Valor de retorno en $v0
    j exit_miOtraFun


# Epílogo simplificado miOtraFun
exit_miOtraFun:
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
    # DECLARE i INT
    # i = 0
    li $t0, 0  # Float: 0
    sw $t0, i_var

    # i = 1
    li $t0, 0  # Float: 1
    sw $t0, i_var

    # t11 = 67 + i
    li $t1, 0  # Float: 67
    lw $t2, i_var
    add $t0, $t1, $t2
    sw $t0, t11_var

    # t12 = -0.01
    li $t0, 0  # Float: -0.01
    sw $t0, t12_var

    # DECLARE b1 BOOL
    # b1 = true
    lw $t0, true_var
    sw $t0, b1_var

    # DECLARE s1 STRING
    # s1 = luna
    lw $t0, luna_var
    sw $t0, s1_var

    # UNKNOWN: READ s1
    # WRITE b1
    lw $a0, b1_var
    jal print_int
    la $a0, nl
    jal print_string

    # WRITE true
    lw $a0, true_var
    jal print_int
    la $a0, nl
    jal print_string

    # t13 = -6.7
    li $t0, 0  # Float: -6.7
    sw $t0, t13_var

    # WRITE t13
    lw $a0, t13_var
    jal print_int
    la $a0, nl
    jal print_string


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

