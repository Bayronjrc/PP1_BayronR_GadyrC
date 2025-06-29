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
    a_var: .word 0
    b_var: .word 0
    temp_var: .word 0
    resultado_var: .word 0
    contador_var: .word 0
    t_cond_var: .word 0
    i_var: .word 0
    j_var: .word 0
    k_var: .word 0
    t_inc_var: .word 0

.text
.globl main

    # // Código Intermedio Generado
    # // Archivo: src/main/resources/s3_intermediate.txt
main:
    # Prólogo simplificado main
    addi $sp, $sp, -8
    sw $ra, 4($sp)
    sw $fp, 0($sp)
    move $fp, $sp

    # Inicio de función
    # DECLARE k INT
    # k = 1
    li $t0, 1
    sw $t0, k_var

L1:
    # t_cond = k <= 3
    lw $t1, k_var
    li $t2, 3
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
    li $t2, 1
    add $t0, $t1, $t2
    sw $t0, t_inc_var

    # k = t_inc
    lw $t0, t_inc_var
    sw $t0, k_var

    j L1

L2:

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

