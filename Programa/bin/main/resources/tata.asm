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
    hola_var: .word 0
    haciendo_var: .word 0
    b1_var: .word 0
    x50_var: .word 0
    t10_var: .word 0
    t12_var: .word 0
    x51_var: .word 0
    t11_var: .word 0
    str1_var: .word 0
    t14_var: .word 0
    str2_var: .word 0
    t13_var: .word 0
    un_var: .word 0
    t15_var: .word 0
    t_inc_var: .word 0
    var_3.7_opop_fl1_var: .word 0
    bl1_var: .word 0
    bl0_var: .word 0
    fl1_var: .word 0
    ch33_var: .word 0
    in2_var: .word 0
    in1_var: .word 0
    fl2_var: .word 0
    in2_mod_7_var: .word 0
    x23_var: .word 0
    true_var: .word 0
    x22_var: .word 0
    x24_var: .word 0
    Mi_var: .word 0
    t1_var: .word 0
    t2_var: .word 0
    t3_var: .word 0
    que_var: .word 0
    t4_var: .word 0
    compilador_var: .word 0
    t5_var: .word 0
    t6_var: .word 0
    t7_var: .word 0
    string_var: .word 0
    t8_var: .word 0
    t9_var: .word 0
    cmp_var: .word 0
    los_var: .word 0
    x30_var: .word 0
    contador_var: .word 0
    t_cond_var: .word 0
    arr_element_var: .word 0
    arr_var: .word 0
    a_var: .word 0
    b_var: .word 0
    temp_var: .word 0
    resultado_var: .word 0
    sdff_var: .word 0
    false_var: .word 0
    i_var: .word 0
    j_var: .word 0
    Mi_string_1_var: .word 0
    k_var: .word 0
    Hola a todos los que est_element_var: .word 0
    miChar2_var: .word 0
    miChar_var: .word 0
    x40_var: .word 0
    y_var: .word 0
    z_var: .word 0
    todos_var: .word 0
    Hola_a_todos_lo_1_var: .word 0
    Hola_var: .word 0

.text
.globl main

    # // Código Intermedio Generado
    # // Archivo: src/main/resources/tata_intermediate.txt
func1:
    # Prólogo simplificado func1
    addi $sp, $sp, -8
    sw $ra, 4($sp)
    sw $fp, 0($sp)
    move $fp, $sp

    # Inicio de función
    # DECLARE x22 CHAR
    # DECLARE x23 CHAR
    # t1 = -0.01
    li $t0, 0  # Float: -0.01
    sw $t0, t1_var

    # DECLARE z FLOAT
    # z = t1
    lw $t0, t1_var
    sw $t0, z_var

    # DECLARE x24 CHAR
    # x24 = a
    lw $t0, a_var
    sw $t0, x24_var

    # DECLARE miChar CHAR
    # miChar = !
    lw $t0, cmp_var
    sw $t0, miChar_var

    # DECLARE miChar2 CHAR
    # miChar2 = !
    lw $t0, cmp_var
    sw $t0, miChar2_var

    # t2 = -1
    li $t0, 0  # Float: -1
    sw $t0, t2_var

    # DECLARE x30 INT
    # x30 = t2
    lw $t0, t2_var
    sw $t0, x30_var

    # DECLARE x40 BOOL
    # x40 = false
    lw $t0, false_var
    sw $t0, x40_var

    # DECLARE x50 CHAR[][]
    # // Array inicializado con matriz
    # DECLARE x51 STRING
    # x51 = Hola a todos los que est[a] haciendo un compilador nuevo\n
    # Array access: Hola a todos los que est[a] haciendo un compilador nuevo\n
    lw $t0, Hola_a_todos_lo_1_var
    sw $t0, x51_var

    # t3 = z > 5.6
    lw $t1, z_var
    li $t2, 0  # Float: 5.6
    sgt $t0, $t1, $t2
    sw $t0, t3_var

    # DECLARE y INT
    # x30 = 10
    li $t0, 0  # Float: 10
    sw $t0, x30_var

    # DECLARE ch33 CHAR
    # ch33 = a
    lw $t0, a_var
    sw $t0, ch33_var

    # x30 = 10
    li $t0, 0  # Float: 10
    sw $t0, x30_var

    # ch33 = a
    lw $t0, a_var
    sw $t0, ch33_var

    # DECLARE str2 STRING
    # str2 = sdff
    lw $t0, sdff_var
    sw $t0, str2_var

    # DECLARE i INT
    # i = 0
    li $t0, 0  # Float: 0
    sw $t0, i_var

    # i = 0
    li $t0, 0  # Float: 0
    sw $t0, i_var

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

    # i = i + 1
    lw $t1, i_var
    li $t2, 0  # Float: 1
    add $t0, $t1, $t2
    sw $t0, i_var

    # WRITE i
    lw $a0, i_var
    jal print_int
    la $a0, nl
    jal print_string

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
    # WRITE miChar
    lw $a0, miChar_var
    jal print_int
    la $a0, nl
    jal print_string

    # UNKNOWN: READ x40
    # RETURN 5.6
    li $v0, 0  # Float: 5.6
    # Valor de retorno en $v0
    j exit_func1

    # DECLARE b1 STRING
    # b1 = hola
    lw $t0, hola_var
    sw $t0, b1_var

    # RETURN false
    lw $v0, false_var
    # Valor de retorno en $v0
    j exit_func1

    # RETURN 1
    li $v0, 0  # Float: 1
    # Valor de retorno en $v0
    j exit_func1

    # DECLARE str1 STRING
    # str1 = Mi string 1
    lw $t0, Mi_string_1_var
    sw $t0, str1_var

    # DECLARE fl1 FLOAT
    # fl1 = 56.6
    li $t0, 0  # Float: 56.6
    sw $t0, fl1_var

    # DECLARE in2 INT
    # in2 = 2
    li $t0, 0  # Float: 2
    sw $t0, in2_var

    # in2 = in2 + 1
    lw $t1, in2_var
    li $t2, 0  # Float: 1
    add $t0, $t1, $t2
    sw $t0, in2_var

    # t6 = in2 - 14
    lw $t1, in2_var
    li $t2, 0  # Float: 14
    sub $t0, $t1, $t2
    sw $t0, t6_var

    # t7 = in2 % 7
    lw $t0, in2_mod_7_var
    sw $t0, t7_var

    # t8 = t7 / 15
    lw $t1, t7_var
    li $t2, 0  # Float: 15
    div $t1, $t2
    mflo $t0
    sw $t0, t8_var

    # t9 = t6 + t8
    lw $t1, t6_var
    lw $t2, t8_var
    add $t0, $t1, $t2
    sw $t0, t9_var

    # DECLARE in1 INT
    # in1 = t9
    lw $t0, t9_var
    sw $t0, in1_var

    # t10 = 3.7 ** fl1
    lw $t0, var_3.7_opop_fl1_var
    sw $t0, t10_var

    # DECLARE fl2 FLOAT
    # fl2 = 76
    li $t0, 0  # Float: 76
    sw $t0, fl2_var

    # DECLARE arr INT[][]
    # // Array inicializado con matriz
    # t11 = arr[67][67]
    # Array access: arr[67][67]
    lw $t0, arr_element_var
    sw $t0, t11_var

    # t12 = -0.005
    li $t0, 0  # Float: -0.005
    sw $t0, t12_var

    # t13 = 6.7 != 8.9
    li $t1, 0  # Float: 6.7
    li $t2, 0  # Float: 8.9
    sne $t0, $t1, $t2
    sw $t0, t13_var

    # DECLARE bl0 BOOL
    # bl0 = t13
    lw $t0, t13_var
    sw $t0, bl0_var

    # t14 = true != false
    lw $t1, true_var
    lw $t2, false_var
    sne $t0, $t1, $t2
    sw $t0, t14_var

    # bl0 = t14
    lw $t0, t14_var
    sw $t0, bl0_var

    # t15 = in1 >= fl1
    lw $t1, in1_var
    lw $t2, fl1_var
    sge $t0, $t1, $t2
    sw $t0, t15_var

    # DECLARE bl1 BOOL
    # bl1 = 56
    li $t0, 0  # Float: 56
    sw $t0, bl1_var

    # RETURN bl1
    lw $v0, bl1_var
    # Valor de retorno en $v0
    j exit_func1


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

