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
    dif_var: .word 0
    t4_var: .word 0
    t5_var: .word 0
    t6_var: .word 0
    t7_var: .word 0
    t8_var: .word 0
    t9_var: .word 0
    b1_var: .word 0
    t10_var: .word 0
    t11_var: .word 0
    t_cond_var: .word 0
    var2_var: .word 0
    t_inc_var: .word 0
    s1_var: .word 0
    otra_var: .word 0
    var_var: .word 0
    i_var: .word 0
    k_var: .word 0
    str_var: .word 0
    t1_var: .word 0
    t2_var: .word 0
    t3_var: .word 0

.text
.globl main

    # // Código Intermedio Generado
    # // Archivo: src/main/resources/haha_intermediate.txt
mi:
    # UNIVERSAL: Prólogo para mi
    addi $sp, $sp, -8
    sw $ra, 4($sp)
    sw $fp, 0($sp)
    move $fp, $sp
    # Reservar espacio para variables locales
    addi $sp, $sp, -16

    # Función sin parámetros o no detectados

    # Inicio de función
    # DECLARE dif INT
    # DECLARE otra CHAR
    # DECLARE var INT
    # var = 0
    li $t0, 0
    sw $t0, var_var

    # DECLARE var2 INT
    # DECLARE str STRING
    # DECLARE i INT
    # i = 0
    li $t0, 0
    sw $t0, i_var

    # i = 0
    li $t0, 0
    sw $t0, i_var

    # var2 = 1
    li $t0, 1
    sw $t0, var2_var

    # IF NOT true GOTO L6
    lw $t0, true_const
    beq $t0, $zero, L6

    j L7

L6:
    # t1 = 3 ** 4
    li $t1, 3
    li $t2, 4
    # Potencia: 3 ** 4
    move $a0, $t1
    move $a1, $t2
    jal power_function
    move $t0, $v0
    sw $t0, t1_var

    # t2 = 2 * t1
    li $t1, 2
    lw $t2, t1_var
    mult $t1, $t2
    mflo $t0
    sw $t0, t2_var

    # t3 = 1 + t2
    li $t1, 1
    lw $t2, t2_var
    add $t0, $t1, $t2
    sw $t0, t3_var

    # t4 = i < 4
    lw $t1, i_var
    li $t2, 4
    # i < 4
    sub $t3, $t1, $t2    # t1 - t2
    bltz $t3, set_true_lt_1
    li $t0, 0
    j end_lt_1
set_true_lt_1:
    li $t0, 1
end_lt_1:
    sw $t0, t4_var

    # t5 = var2 > 122
    lw $t1, var2_var
    li $t2, 122
    # var2 > 122
    sub $t3, $t2, $t1    # t2 - t1
    bltz $t3, set_true_gt_2
    li $t0, 0
    j end_gt_2
set_true_gt_2:
    li $t0, 1
end_gt_2:
    sw $t0, t5_var

    # t6 = 34 + 35
    li $t1, 34
    li $t2, 35
    add $t0, $t1, $t2
    sw $t0, t6_var

    # t7 = 12 > t6
    li $t1, 12
    lw $t2, t6_var
    # 12 > t6
    sub $t3, $t2, $t1    # t2 - t1
    bltz $t3, set_true_gt_3
    li $t0, 0
    j end_gt_3
set_true_gt_3:
    li $t0, 1
end_gt_3:
    sw $t0, t7_var

    # t8 = t5 && t7
    lw $t1, t5_var
    lw $t2, t7_var
    # AND lógico
    and $t0, $t1, $t2
    sw $t0, t8_var

    # t9 = var == 0
    lw $t1, var_var
    li $t2, 0
    # var == 0
    sub $t3, $t1, $t2    # t1 - t2
    beq $t3, $zero, set_true_eq_4
    li $t0, 0
    j end_eq_4
set_true_eq_4:
    li $t0, 1
end_eq_4:
    sw $t0, t9_var

L7:
L1:
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
    li $t2, 1
    add $t0, $t1, $t2
    sw $t0, t_inc_var

    # k = t_inc
    lw $t0, t_inc_var
    sw $t0, k_var

    j L1

L2:
    # RETURN 1.0
    li $v0, 100    # Float 1.0 (*100)
    # Valor de retorno en $v0
    j exit_mi


#  UNIVERSAL: Epílogo para mi
exit_mi:
    # Limpiar variables locales
    addi $sp, $sp, 16    # Liberar espacio de variables locales
    # Restaurar frame pointer y return address
    move $sp, $fp
    lw $fp, 0($sp)
    lw $ra, 4($sp)
    addi $sp, $sp, 8
    jr $ra

miOtraFun:
    # UNIVERSAL: Prólogo para miOtraFun
    addi $sp, $sp, -8
    sw $ra, 4($sp)
    sw $fp, 0($sp)
    move $fp, $sp
    # Reservar espacio para variables locales
    addi $sp, $sp, -16

    # Función sin parámetros o no detectados

    # Inicio de función
    # RETURN true
    lw $v0, true_const
    # Valor de retorno en $v0
    j exit_miOtraFun


#  UNIVERSAL: Epílogo para miOtraFun
exit_miOtraFun:
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
    # DECLARE i INT
    # i = 0
    li $t0, 0
    sw $t0, i_var

    # i = 1
    li $t0, 1
    sw $t0, i_var

    # t10 = 67 + i
    li $t1, 67
    lw $t2, i_var
    add $t0, $t1, $t2
    sw $t0, t10_var

    # DECLARE b1 BOOL
    # b1 = true
    lw $t0, true_const
    sw $t0, b1_var

    # DECLARE s1 STRING
    # s1 = luna
    lw $t0, luna_const    # luna = true
    sw $t0, s1_var

    # UNKNOWN: READ s1
    # WRITE b1
    lw $a0, b1_var
    jal print_int
    la $a0, nl
    jal print_string

    # WRITE true
    lw $a0, true_const
    jal print_int
    la $a0, nl
    jal print_string

    # t11 = -6.7
    li $t0, -670    # Float -6.7 (*100)
    sw $t0, t11_var

    # WRITE t11
    lw $a0, t11_var
    jal print_float_decimal
    la $a0, nl
    jal print_string


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

