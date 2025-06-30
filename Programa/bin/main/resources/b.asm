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
    t_case_var: .word 0
    t5_var: .word 0
    t6_var: .word 0
    t7_var: .word 0
    t8_var: .word 0
    t9_var: .word 0
    t10_var: .word 0
    contador_var: .word 0
    t_cond_var: .word 0
    arr_element_var: .word 0
    t_inc_var: .word 0
    a_var: .word 0
    b_var: .word 0
    temp_var: .word 0
    resultado_var: .word 0
    i_var: .word 0
    j_var: .word 0
    k_var: .word 0
    n_var: .word 0
    x_var: .word 0
    t1_var: .word 0
    t2_var: .word 0
    t3_var: .word 0
    option_var: .word 0

.text
.globl main

    # // Código Intermedio Generado
    # // Archivo: src/main/resources/b_intermediate.txt
factorial:
    # Prólogo estándar factorial
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

    # PARAM t2
    lw $a0, t2_var
    # ✅ FIXED: Parámetro t2 cargado en $a0

    # t3 = CALL factorial 1
    jal factorial
    sw $v0, t3_var

    # t4 = n * t3
    lw $t1, -4($fp)   # n local
    lw $t2, t3_var
    mul $t0, $t1, $t2
    sw $t0, t4_var

    # RETURN t4
    lw $v0, t4_var
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

testLopps:
    # Prólogo estándar testLopps
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
    seq $t0, $t1, $t2
    sw $t0, t6_var

    # IF NOT t6 GOTO L4
    lw $t0, t6_var
    beq $t0, $zero, L4

    j L2

L4:
    # t7 = x == 6
    lw $t1, x_var
    li $t2, 6
    seq $t0, $t1, $t2
    sw $t0, t7_var

    # IF NOT t7 GOTO L5
    lw $t0, t7_var
    beq $t0, $zero, L5

    j L3

L5:
    # t8 = x > 0
    lw $t1, x_var
    li $t2, 0
    sgt $t0, $t1, $t2
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
    seq $t0, $t1, $t2
    sw $t0, t_case_var

    # IF t_case GOTO L6
    lw $t0, t_case_var
    bne $t0, $zero, L6

    # t_case = option == 2
    lw $t1, option_var
    li $t2, 2
    seq $t0, $t1, $t2
    sw $t0, t_case_var

    # IF t_case GOTO L7
    lw $t0, t_case_var
    bne $t0, $zero, L7

    # t_case = option == 3
    lw $t1, option_var
    li $t2, 3
    seq $t0, $t1, $t2
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

# Epílogo estándar testLopps
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
    # Prólogo estándar testArrays
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
    slt $t0, $t1, $t2
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
    slt $t0, $t1, $t2
    sw $t0, t10_var

    # j = j + 1
    lw $t1, j_var
    li $t2, 1
    add $t0, $t1, $t2
    sw $t0, j_var

    # arr[j][i] = j
    # Array assignment: arr[j][i] = j
    lw $t0, j_var
    sw $t0, arr_element_var

L13:
    # t_cond = k <= 2
    lw $t1, k_var
    li $t2, 2
    sle $t0, $t1, $t2
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
    sle $t0, $t1, $t2
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

# Epílogo estándar testArrays
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
    # CALL testArrays 0
    jal testArrays


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

