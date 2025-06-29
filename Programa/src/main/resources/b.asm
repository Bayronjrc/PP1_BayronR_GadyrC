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
    t_cond_var: .word 0
    x_var: .word 0
    i_var: .word 0
    j_var: .word 0
    t_inc_var: .word 0
    n_var: .word 0
    option_var: .word 0

.text
.globl main

    # // Código Intermedio Generado
    # // Archivo: src/main/resources/b_intermediate.txt
factorial:
    # Prólogo simplificado factorial
    addi $sp, $sp, -8
    sw $ra, 4($sp)
    sw $fp, 0($sp)
    move $fp, $sp

    # i = 0
    li $t0, 0
    sw $t0, i_var

L11:
    # t_cond = i < 3
    lw $t1, i_var
    li $t2, 3
    slt $t0, $t1, $t2
    sw $t0, t_cond_var

    # IF NOT t_cond GOTO L12
    lw $t0, t_cond_var
    beq $t0, $zero, L12

    # WRITE i
    lw $a0, i_var
    jal print_int
    la $a0, nl
    jal print_string

    # t_inc = i + 1
    lw $t1, i_var
    li $t2, 1
    add $t0, $t1, $t2
    sw $t0, t_inc_var

    # i = t_inc
    lw $t0, t_inc_var
    sw $t0, i_var

    j L11

L12:
    # Inicio de función
    # DECLARE n INT

# Epílogo simplificado factorial
exit_factorial:
    move $sp, $fp
    lw $fp, 0($sp)
    lw $ra, 4($sp)
    addi $sp, $sp, 8
    jr $ra

testLopps:
    # Prólogo simplificado testLopps
    addi $sp, $sp, -8
    sw $ra, 4($sp)
    sw $fp, 0($sp)
    move $fp, $sp

    # Inicio de función
    # DECLARE x INT
    # DECLARE option INT

# Epílogo simplificado testLopps
exit_testLopps:
    move $sp, $fp
    lw $fp, 0($sp)
    lw $ra, 4($sp)
    addi $sp, $sp, 8
    jr $ra

testArrays:
    # Prólogo simplificado testArrays
    addi $sp, $sp, -8
    sw $ra, 4($sp)
    sw $fp, 0($sp)
    move $fp, $sp

    # Inicio de función
    # DECLARE i INT
    # DECLARE j INT

# Epílogo simplificado testArrays
exit_testArrays:
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
    # CALL testLopps 0
    jal testLopps

    # CALL testArrays 0
    jal testArrays


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

