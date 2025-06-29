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
    a_var: .word 0
    t5_var: .word 0
    b_var: .word 0
    temp_var: .word 0
    t6_var: .word 0
    t7_var: .word 0
    resultado_var: .word 0
    t8_var: .word 0
    i_var: .word 0
    j_var: .word 0
    k_var: .word 0
    n_var: .word 0
    resultado1_var: .word 0
    resultado2_var: .word 0
    contador_var: .word 0
    recurse_var: .word 0
    t_cond_var: .word 0
    x_var: .word 0
    y_var: .word 0
    t_inc_var: .word 0
    t1_var: .word 0
    t2_var: .word 0
    t3_var: .word 0

.text
.globl main

    # // Código Intermedio Generado
    # // Archivo: src/main/resources/s1_intermediate.txt
suma:
    # Prólogo simplificado suma
    addi $sp, $sp, -8
    sw $ra, 4($sp)
    sw $fp, 0($sp)
    move $fp, $sp

    # Guardar parámetros para suma
    sw $a0, a_var
    sw $a1, b_var

    # Inicio de función
    # DECLARE a INT
    # DECLARE b INT
    # t1 = a + b
    lw $t1, a_var
    lw $t2, b_var
    add $t0, $t1, $t2
    sw $t0, t1_var

    # DECLARE resultado INT
    # resultado = t1
    lw $t0, t1_var
    sw $t0, resultado_var

    # RETURN resultado
    lw $v0, resultado_var
    # Valor de retorno en $v0
    j exit_suma


# Epílogo simplificado suma
exit_suma:
    move $sp, $fp
    lw $fp, 0($sp)
    lw $ra, 4($sp)
    addi $sp, $sp, 8
    jr $ra

factorial:
    # Prólogo simplificado factorial
    addi $sp, $sp, -8
    sw $ra, 4($sp)
    sw $fp, 0($sp)
    move $fp, $sp

    # Inicio de función
    # DECLARE n INT
    # t2 = n <= 1
    lw $t1, n_var
    li $t2, 1
    sle $t0, $t1, $t2
    sw $t0, t2_var

    # IF NOT t2 GOTO L1
    lw $t0, t2_var
    beq $t0, $zero, L1

    # RETURN 1
    li $v0, 1
    # Valor de retorno en $v0
    j exit_factorial

L1:
    # t3 = n - 1
    lw $t1, n_var
    li $t2, 1
    sub $t0, $t1, $t2
    sw $t0, t3_var

    # DECLARE temp INT
    # temp = t3
    lw $t0, t3_var
    sw $t0, temp_var

    # PARAM temp
    lw $a0, temp_var
    # ✅ s1: Parámetro temp cargado en $a0

    # t4 = CALL factorial 1
    jal factorial
    sw $v0, t4_var

    # DECLARE recurse INT
    # recurse = t4
    lw $t0, t4_var
    sw $t0, recurse_var

    # t5 = n * recurse
    lw $t1, n_var
    lw $t2, recurse_var
    mul $t0, $t1, $t2
    sw $t0, t5_var

    # DECLARE resultado INT
    # resultado = t5
    lw $t0, t5_var
    sw $t0, resultado_var

    # RETURN resultado
    lw $v0, resultado_var
    # Valor de retorno en $v0
    j exit_factorial


# Epílogo simplificado factorial
exit_factorial:
    move $sp, $fp
    lw $fp, 0($sp)
    lw $ra, 4($sp)
    addi $sp, $sp, 8
    jr $ra

testBasico:
    # Prólogo simplificado testBasico
    addi $sp, $sp, -8
    sw $ra, 4($sp)
    sw $fp, 0($sp)
    move $fp, $sp

    # Inicio de función
    # DECLARE x INT
    # x = 5
    li $t0, 5
    sw $t0, x_var

    # DECLARE y INT
    # y = 3
    li $t0, 3
    sw $t0, y_var

    # PARAM x
    lw $a1, x_var
    # Parámetro x cargado en $a1

    # PARAM y
    lw $a2, y_var
    # Parámetro y cargado en $a2

    # t6 = CALL suma 2
    jal suma
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

    # PARAM 4
    li $a3, 4
    # Parámetro 4 cargado en $a3

    # t7 = CALL factorial 1
    jal factorial
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

    # DECLARE i INT
    # i = 0
    li $t0, 0
    sw $t0, i_var

    # i = 0
    li $t0, 0
    sw $t0, i_var

    # t8 = i < 3
    lw $t1, i_var
    li $t2, 3
    slt $t0, $t1, $t2
    sw $t0, t8_var

    # i = i + 1
    lw $t1, i_var
    li $t2, 1
    add $t0, $t1, $t2
    sw $t0, i_var

    # WRITE i
    lw $a0, i_var
    jal print_int
    la $a0, nl
    jal print_string

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

# Epílogo simplificado testBasico
exit_testBasico:
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
    # CALL testBasico 0
    jal testBasico


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

